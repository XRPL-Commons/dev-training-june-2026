// ============================================================
// Day 2 - Step 2 Solution: Issue a Custom Token
// Learn: How to create a trust line and issue tokens on XRPL.
// The holder must trust the issuer before receiving tokens.
// ============================================================

const xrpl = require('xrpl');
const fs = require('fs');

const TESTNET_URL = 'wss://s.altnet.rippletest.net:51233';

async function main() {
  const { wallets } = JSON.parse(fs.readFileSync('../../wallets.json', 'utf-8'));
  const issuerWallet = xrpl.Wallet.fromSeed(wallets[0].seed);
  const holderWallet = xrpl.Wallet.fromSeed(wallets[1].seed);

  const currencyCode = 'ACD';

  const client = new xrpl.Client(TESTNET_URL);
  await client.connect();
  console.log('Connected to XRPL Testnet');

  // Create trust line
  const trustSetTx = {
    TransactionType: 'TrustSet',
    Account: holderWallet.address,
    LimitAmount: {
      currency: currencyCode,
      issuer: issuerWallet.address,
      value: '1000000'
    }
  };
  const trustResult = await client.submitAndWait(trustSetTx, { wallet: holderWallet });
  console.log('Trust line created!', trustResult.result.hash);

  // Issue tokens
  const paymentTx = {
    TransactionType: 'Payment',
    Account: issuerWallet.address,
    Destination: holderWallet.address,
    Amount: {
      currency: currencyCode,
      issuer: issuerWallet.address,
      value: '500'
    }
  };
  const payResult = await client.submitAndWait(paymentTx, { wallet: issuerWallet });
  console.log('Tokens issued!', payResult.result.hash);

  console.log(`\nExplorer: https://testnet.xrpl.org/accounts/${issuerWallet.address}`);
  await client.disconnect();
}

main().catch(console.error);
