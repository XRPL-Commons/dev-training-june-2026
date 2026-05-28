// ============================================================
// Day 1 Solution: Send XRP
// Learn: How to construct and submit a Payment transaction
// on the XRP Ledger testnet.
// ============================================================

const xrpl = require('xrpl');
const fs = require('fs');

const TESTNET_URL = 'wss://s.altnet.rippletest.net:51233';

async function main() {
  const { wallets } = JSON.parse(fs.readFileSync('../../wallets.json', 'utf-8'));
  const senderWallet = xrpl.Wallet.fromSeed(wallets[0].seed);
  const receiverWallet = xrpl.Wallet.fromSeed(wallets[1].seed);

  const client = new xrpl.Client(TESTNET_URL);
  await client.connect();
  console.log('Connected to XRPL Testnet');

  const balanceBefore = await client.getXrpBalance(receiverWallet.address);
  console.log(`Receiver balance before: ${balanceBefore} XRP`);

  const paymentTx = {
    TransactionType: 'Payment',
    Account: senderWallet.address,
    Amount: xrpl.xrpToDrops('10'),
    Destination: receiverWallet.address
  };

  const result = await client.submitAndWait(paymentTx, { wallet: senderWallet });
  console.log(result);

  const balanceAfter = await client.getXrpBalance(receiverWallet.address);
  console.log(`Receiver balance after: ${balanceAfter} XRP`);

  console.log(`\nExplorer: https://testnet.xrpl.org/accounts/${senderWallet.address}`);
  await client.disconnect();
}

main().catch(console.error);
