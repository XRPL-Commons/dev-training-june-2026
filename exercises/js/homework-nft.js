// ============================================================
// Homework Exercise: NFT Lifecycle
// Learn: How to mint, query, and burn NFTs on the XRP Ledger.
// You'll experience the full lifecycle of an NFToken.
// ============================================================

const xrpl = require('xrpl');
const fs = require('fs');

const TESTNET_URL = 'wss://s.altnet.rippletest.net:51233';

async function main() {
  const { wallets } = JSON.parse(fs.readFileSync('../../wallets.json', 'utf-8'));
  const wallet = xrpl.Wallet.fromSeed(wallets[0].seed);

  const client = new xrpl.Client(TESTNET_URL);
  await client.connect();
  console.log('Connected to XRPL Testnet');

  // ╔══════════════════════════════════════════════════════════════════╗
  // ║ TODO 1: Mint an NFT                                             ║
  // ║                                                                  ║
  // ║ HINT: {                                                          ║
  // ║   TransactionType: 'NFTokenMint',                                ║
  // ║   Account: wallet.address,                                       ║
  // ║   NFTokenTaxon: 0,                                               ║
  // ║   Flags: 8,                                                      ║
  // ║   TransferFee: 5000,                                             ║
  // ║   URI: xrpl.convertStringToHex('ipfs://YOUR_NAME_HERE')          ║
  // ║ }                                                                ║
  // ║                                                                  ║
  // ║ Submit: client.submitAndWait(mintTx, { wallet })                  ║
  // ╚══════════════════════════════════════════════════════════════════╝
  // YOUR CODE HERE

  console.log('NFT Minted!');

  // ╔══════════════════════════════════════════════════════════════════╗
  // ║ TODO 2: Get account NFTs and extract the NFTokenID              ║
  // ║                                                                  ║
  // ║ HINT: const nfts = await client.request({                        ║
  // ║         command: 'account_nfts',                                  ║
  // ║         account: wallet.address                                   ║
  // ║       });                                                         ║
  // ║       const tokenId = nfts.result.account_nfts[0].NFTokenID;      ║
  // ╚══════════════════════════════════════════════════════════════════╝
  // YOUR CODE HERE

  // ╔══════════════════════════════════════════════════════════════════╗
  // ║ TODO 3: Burn the NFT                                            ║
  // ║                                                                  ║
  // ║ HINT: {                                                          ║
  // ║   TransactionType: 'NFTokenBurn',                                ║
  // ║   Account: wallet.address,                                       ║
  // ║   NFTokenID: tokenId                                              ║
  // ║ }                                                                ║
  // ║                                                                  ║
  // ║ Submit: client.submitAndWait(burnTx, { wallet })                  ║
  // ╚══════════════════════════════════════════════════════════════════╝
  // YOUR CODE HERE

  console.log('NFT Burned!');

  // ╔══════════════════════════════════════════════════════════════════╗
  // ║ TODO 4: Query account_nfts again to verify it's gone            ║
  // ║                                                                  ║
  // ║ HINT: Same request as TODO 2, then log the count                 ║
  // ╚══════════════════════════════════════════════════════════════════╝
  // YOUR CODE HERE

  console.log(`\nExplorer: https://testnet.xrpl.org/accounts/${wallet.address}`);
  await client.disconnect();
}

main().catch(console.error);
