/**
 * XRPL Academy - Day 1: Your First Transaction
 * ==============================================
 *
 * WHAT YOU WILL LEARN:
 * - How to connect to the XRPL network using a WebSocket client
 * - How to load a wallet from a secret seed
 * - How to check an account's XRP balance
 * - How to construct a Payment transaction
 * - How to sign and submit a transaction
 * - How to verify the result on the Testnet Explorer
 *
 * BACKGROUND:
 * The XRP Ledger processes transactions in "ledger versions" every 3-5 seconds.
 * When you submit a transaction, it goes through this lifecycle:
 *   1. You construct the transaction object (specifying type, sender, amount, recipient)
 *   2. The library signs it with your secret key (proving you authorized it)
 *   3. It is submitted to a connected XRPL node
 *   4. The network's consensus process validates it
 *   5. It is included in the next closed ledger (confirmed)
 *
 * PREREQUISITES:
 * - Run "node scripts/mint-wallets.js" first to create wallets.json
 *
 * RUN: node day1-first-transaction.js
 */

const xrpl = require("xrpl");
const fs = require("fs");

// The Testnet WebSocket endpoint. WebSocket is used (instead of HTTP) because
// it maintains a persistent connection, allowing the client to wait for
// transaction confirmation in real time.
const TESTNET_URL = "wss://s.altnet.rippletest.net:51233";

async function main() {
  // -------------------------------------------------------------------------
  // STEP 1: Load wallets from the JSON file created by mint-wallets.js
  // -------------------------------------------------------------------------
  // Each wallet has a "seed" (secret key). The Wallet.fromSeed() method
  // derives the full key pair and address from that seed.
  // Think of the seed like a master password that controls the account.

  const walletsFile = fs.readFileSync("../wallets.json", "utf-8");
  const { wallets } = JSON.parse(walletsFile);

  const senderWallet = xrpl.Wallet.fromSeed(wallets[0].seed);
  const receiverWallet = xrpl.Wallet.fromSeed(wallets[1].seed);

  console.log("XRPL Academy - Day 1: First Transaction");
  console.log("========================================");
  console.log("");
  console.log("Sender address  : " + senderWallet.address);
  console.log("Receiver address: " + receiverWallet.address);

  // -------------------------------------------------------------------------
  // STEP 2: Connect to the XRPL Testnet
  // -------------------------------------------------------------------------
  // The Client object manages the WebSocket connection to an XRPL node.
  // Once connected, you can query account info, submit transactions, and
  // subscribe to real-time ledger events.

  const client = new xrpl.Client(TESTNET_URL);
  await client.connect();
  console.log("");
  console.log("Connected to XRPL Testnet (" + TESTNET_URL + ")");

  // -------------------------------------------------------------------------
  // STEP 3: Check the sender's balance before the transaction
  // -------------------------------------------------------------------------
  // getXrpBalance returns the balance in XRP (not drops).
  // Remember: 1 XRP = 1,000,000 drops.

  const balanceBefore = await client.getXrpBalance(senderWallet.address);
  console.log("Sender balance before: " + balanceBefore + " XRP");

  // -------------------------------------------------------------------------
  // STEP 4: Construct the Payment transaction
  // -------------------------------------------------------------------------
  // A Payment transaction moves XRP (or tokens) from one account to another.
  //
  // Required fields:
  //   - TransactionType: "Payment" (tells the ledger what kind of operation)
  //   - Account: the sender's address (who is paying)
  //   - Amount: how much to send, in "drops" (1 XRP = 1,000,000 drops)
  //   - Destination: the recipient's address
  //
  // The xrpToDrops() helper converts human-readable XRP to drops.
  // We send 10 XRP in this example.

  const paymentTransaction = {
    TransactionType: "Payment",
    Account: senderWallet.address,
    Amount: xrpl.xrpToDrops("10"), // 10 XRP = 10,000,000 drops
    Destination: receiverWallet.address,
  };

  console.log("");
  console.log("Submitting payment of 10 XRP...");
  console.log("  From: " + senderWallet.address);
  console.log("  To  : " + receiverWallet.address);

  // -------------------------------------------------------------------------
  // STEP 5: Sign and submit the transaction, then wait for validation
  // -------------------------------------------------------------------------
  // submitAndWait() does three things:
  //   1. Auto-fills missing fields (like Sequence number and Fee)
  //   2. Signs the transaction with the sender's private key
  //   3. Submits it and waits until the network confirms it (3-5 seconds)
  //
  // The result contains the full transaction record including the outcome.

  const result = await client.submitAndWait(paymentTransaction, {
    wallet: senderWallet,
  });

  // -------------------------------------------------------------------------
  // STEP 6: Check the result
  // -------------------------------------------------------------------------
  // The transaction result code tells you what happened:
  //   - "tesSUCCESS" means the payment went through
  //   - "tecUNFUNDED_PAYMENT" means insufficient balance
  //   - Other "tec" codes indicate various failures
  //
  // The transaction hash is a unique identifier you can look up on the explorer.

  const txResult = result.result.meta.TransactionResult;
  const txHash = result.result.hash;

  console.log("");
  console.log("Transaction result: " + txResult);
  console.log("Transaction hash  : " + txHash);
  console.log("Explorer link     : https://testnet.xrpl.org/transactions/" + txHash);

  // -------------------------------------------------------------------------
  // STEP 7: Verify the balance changed
  // -------------------------------------------------------------------------
  // The sender's balance should be reduced by:
  //   - 10 XRP (the payment amount)
  //   - ~0.000012 XRP (the transaction fee, burned permanently)

  const balanceAfter = await client.getXrpBalance(senderWallet.address);
  console.log("");
  console.log("Sender balance after: " + balanceAfter + " XRP");
  console.log("Difference: " + (balanceBefore - balanceAfter).toFixed(6) + " XRP (payment + fee)");

  // -------------------------------------------------------------------------
  // CLEANUP: Disconnect from the network
  // -------------------------------------------------------------------------
  await client.disconnect();
  console.log("");
  console.log("Disconnected. Day 1 complete.");
}

main().catch(console.error);
