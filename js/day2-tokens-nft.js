/**
 * XRPL Academy - Day 2: Tokens (IOUs) and NFTs
 * ==============================================
 *
 * WHAT YOU WILL LEARN:
 * - How custom tokens (IOUs) work on XRPL
 * - What "Default Ripple" means and why issuers need it
 * - How trust lines work (permission to hold a token)
 * - How to issue and transfer custom tokens
 * - How to mint an NFT with transfer fees (royalties)
 *
 * BACKGROUND - TOKENS ON XRPL:
 * Unlike XRP (the native currency), custom tokens require a trust relationship:
 *   1. An "issuer" account creates the token by sending it
 *   2. A "holder" account must first create a "trust line" to the issuer
 *   3. The trust line says: "I trust this issuer for up to X units of this token"
 *   4. Only then can the issuer send tokens to the holder
 *
 * This prevents spam -- no one can force unwanted tokens into your account.
 *
 * Token names (currency codes) are 3 characters for standard codes (like "USD",
 * "EUR", "ACD") or up to 20 characters in hex for longer names.
 *
 * BACKGROUND - NFTs ON XRPL:
 * XRPL has native NFT support built into the protocol (no smart contracts needed).
 * NFTs are minted with NFTokenMint and can include:
 *   - A URI pointing to off-chain metadata (image, description, etc.)
 *   - A transfer fee (royalty) paid to the original creator on every resale
 *   - Flags controlling transferability and burnability
 *
 * PREREQUISITES:
 * - Run "node scripts/mint-wallets.js" first to create wallets.json
 * - Complete Day 1 to understand basic transaction flow
 *
 * RUN: node day2-tokens-nft.js
 */

const xrpl = require("xrpl");
const fs = require("fs");

const TESTNET_URL = "wss://s.altnet.rippletest.net:51233";

async function main() {
  // Load wallets -- Wallet 1 will be the token issuer, Wallet 2 the holder
  const { wallets } = JSON.parse(fs.readFileSync("../wallets.json", "utf-8"));
  const issuerWallet = xrpl.Wallet.fromSeed(wallets[0].seed);
  const holderWallet = xrpl.Wallet.fromSeed(wallets[1].seed);

  const client = new xrpl.Client(TESTNET_URL);
  await client.connect();

  console.log("XRPL Academy - Day 2: Tokens and NFTs");
  console.log("======================================");
  console.log("");
  console.log("Issuer address: " + issuerWallet.address);
  console.log("Holder address: " + holderWallet.address);
  console.log("Connected to: " + TESTNET_URL);

  // =========================================================================
  // PART 1: CREATE A CUSTOM TOKEN (IOU)
  // =========================================================================
  console.log("");
  console.log("--- PART 1: Creating a Custom Token ---");
  console.log("");

  // -------------------------------------------------------------------------
  // STEP 1: Enable "Default Ripple" on the issuer account
  // -------------------------------------------------------------------------
  // "Rippling" allows tokens to flow through an account to reach others.
  // An issuer MUST enable Default Ripple so that holders can send the token
  // to each other (not just back to the issuer).
  //
  // Without this flag, tokens would be "stuck" -- holders could only send
  // them back to the issuer, not to other holders.
  //
  // AccountSet is a transaction type that modifies account settings.
  // SetFlag specifies which flag to enable.

  console.log("Step 1: Enabling Default Ripple on issuer account...");
  console.log("  (This allows the token to flow freely between holders)");

  const accountSetResult = await client.submitAndWait(
    {
      TransactionType: "AccountSet",
      Account: issuerWallet.address,
      SetFlag: xrpl.AccountSetAsfFlags.asfDefaultRipple,
    },
    { wallet: issuerWallet }
  );
  console.log("  Result: " + accountSetResult.result.meta.TransactionResult);

  // -------------------------------------------------------------------------
  // STEP 2: Create a Trust Line from the holder to the issuer
  // -------------------------------------------------------------------------
  // A trust line is the holder saying: "I agree to hold up to 1,000,000 units
  // of this token issued by this specific account."
  //
  // The TrustSet transaction creates this relationship.
  // LimitAmount specifies:
  //   - currency: the 3-character token code (you can change "ACD" to anything)
  //   - issuer: who issues this token
  //   - value: maximum amount the holder is willing to hold
  //
  // IMPORTANT: The trust line costs 2 XRP in reserve (locked, not spent).
  // This reserve is returned if the trust line is later deleted.

  const currencyCode = "ACD"; // Change this to your own 3-letter token name

  console.log("");
  console.log("Step 2: Creating trust line from holder to issuer...");
  console.log("  Token: " + currencyCode);
  console.log("  Limit: 1,000,000 " + currencyCode);
  console.log("  (Holder is agreeing to accept this token from the issuer)");

  const trustSetResult = await client.submitAndWait(
    {
      TransactionType: "TrustSet",
      Account: holderWallet.address,
      LimitAmount: {
        currency: currencyCode,
        issuer: issuerWallet.address,
        value: "1000000",
      },
    },
    { wallet: holderWallet }
  );
  console.log("  Result: " + trustSetResult.result.meta.TransactionResult);

  // -------------------------------------------------------------------------
  // STEP 3: Issue tokens (issuer sends tokens to holder)
  // -------------------------------------------------------------------------
  // Token issuance on XRPL is simply a Payment from the issuer to a holder.
  // The issuer "creates" tokens by sending them -- there is no separate
  // minting step. The issuer's balance goes negative (representing obligation)
  // and the holder's balance goes positive.
  //
  // The Amount field for token payments uses an object (not a string like XRP):
  //   { currency: "ACD", issuer: "rIssuerAddress...", value: "500" }

  console.log("");
  console.log("Step 3: Issuing 500 " + currencyCode + " tokens to holder...");
  console.log("  (Issuer creates tokens by sending a Payment)");

  const issueResult = await client.submitAndWait(
    {
      TransactionType: "Payment",
      Account: issuerWallet.address,
      Destination: holderWallet.address,
      Amount: {
        currency: currencyCode,
        issuer: issuerWallet.address,
        value: "500",
      },
    },
    { wallet: issuerWallet }
  );
  console.log("  Result: " + issueResult.result.meta.TransactionResult);
  console.log("  Holder now has 500 " + currencyCode);
  console.log("  View: https://testnet.xrpl.org/accounts/" + holderWallet.address);

  // =========================================================================
  // PART 2: MINT AN NFT
  // =========================================================================
  console.log("");
  console.log("--- PART 2: Minting an NFT ---");
  console.log("");

  // -------------------------------------------------------------------------
  // STEP 4: Mint an NFT using NFTokenMint
  // -------------------------------------------------------------------------
  // NFTokenMint creates a new non-fungible token on the ledger.
  //
  // Key fields:
  //   - NFTokenTaxon: a numeric category/collection ID (0 is fine for testing)
  //   - Flags: controls NFT behavior
  //       8 = tfTransferable (the NFT can be sold/transferred to others)
  //   - TransferFee: royalty percentage in basis points (5000 = 5%)
  //       The creator earns this fee on every secondary sale
  //   - URI: a hex-encoded link to the NFT's metadata (image, description, etc.)
  //       We use convertStringToHex() to encode a URL
  //
  // The NFT is stored directly on the ledger (not in a smart contract).
  // It belongs to the minting account until transferred or burned.

  const metadataUri = "https://xrpl.org/img/logo.svg";

  console.log("Step 4: Minting NFT...");
  console.log("  Minter     : " + issuerWallet.address);
  console.log("  Taxon      : 0 (default collection)");
  console.log("  Transferable: yes");
  console.log("  Royalty    : 5% on every resale");
  console.log("  URI        : " + metadataUri);

  const nftMintResult = await client.submitAndWait(
    {
      TransactionType: "NFTokenMint",
      Account: issuerWallet.address,
      NFTokenTaxon: 0,
      Flags: 8, // tfTransferable -- allows the NFT to be sold/transferred
      TransferFee: 5000, // 5% royalty (in basis points: 5000/100000 = 5%)
      URI: xrpl.convertStringToHex(metadataUri),
    },
    { wallet: issuerWallet }
  );

  console.log("");
  console.log("  Result: " + nftMintResult.result.meta.TransactionResult);
  console.log("  View NFTs: https://testnet.xrpl.org/accounts/" + issuerWallet.address + "/nfts");

  // -------------------------------------------------------------------------
  // DONE
  // -------------------------------------------------------------------------
  await client.disconnect();
  console.log("");
  console.log("Day 2 complete.");
  console.log("You have created a custom token and minted an NFT on the XRPL Testnet.");
}

main().catch(console.error);
