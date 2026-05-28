// ============================================================
// Homework Solution: NFT Lifecycle
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

  // Mint NFT
  const mintTx = {
    TransactionType: 'NFTokenMint',
    Account: wallet.address,
    NFTokenTaxon: 0,
    Flags: 8,
    TransferFee: 5000,
    URI: xrpl.convertStringToHex('ipfs://YOUR_NAME_HERE')
  };
  const mintResult = await client.submitAndWait(mintTx, { wallet });
  console.log('NFT Minted!', mintResult.result.hash);

  // Query NFTs
  const nfts = await client.request({
    command: 'account_nfts',
    account: wallet.address
  });
  const tokenId = nfts.result.account_nfts[0].NFTokenID;
  console.log('NFTokenID:', tokenId);

  // Burn NFT
  const burnTx = {
    TransactionType: 'NFTokenBurn',
    Account: wallet.address,
    NFTokenID: tokenId
  };
  const burnResult = await client.submitAndWait(burnTx, { wallet });
  console.log('NFT Burned!', burnResult.result.hash);

  // Verify it's gone
  const nftsAfter = await client.request({
    command: 'account_nfts',
    account: wallet.address
  });
  console.log('NFTs remaining:', nftsAfter.result.account_nfts.length);

  console.log(`\nExplorer: https://testnet.xrpl.org/accounts/${wallet.address}`);
  await client.disconnect();
}

main().catch(console.error);
