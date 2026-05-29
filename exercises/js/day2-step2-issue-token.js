// ============================================================
// Day 2 - Step 2 Exercise: Issue a Custom Token
// Learn: How to create a trust line and issue tokens on XRPL.
// The holder must trust the issuer before receiving tokens.
// Reference materials:
// - https://learn.xrpl-commons.org/course/code-with-the-xrpl/lesson/create-trustline-and-send-currency/
// - https://xrpl.org/docs/references/protocol/transactions/types/trustset
// - https://xrpl.org/docs/references/protocol/transactions/types/payment
// - https://js.xrpl.org/
// ============================================================

const xrpl = require('xrpl');
const fs = require('fs');

const TESTNET_URL = 'wss://s.altnet.rippletest.net:51233';

async function main() {
  const { wallets } = JSON.parse(fs.readFileSync('../../wallets.json', 'utf-8'));
  const issuerWallet = xrpl.Wallet.fromSeed(wallets[0].seed);
  const holderWallet = xrpl.Wallet.fromSeed(wallets[1].seed);

  // Replace with your initials (3 characters, e.g. 'ACD')
  const currencyCode = 'YOUR_INITIALS';

  const client = new xrpl.Client(TESTNET_URL);
  await client.connect();
  console.log('Connected to XRPL Testnet');

  // ╔══════════════════════════════════════════════════════════════════╗
  // ║ TODO 1: Create a TrustSet transaction (holder trusts issuer)    ║
  // ║                                                                  ║
  // ║ HINT: {                                                          ║
  // ║   TransactionType: 'TrustSet',                                   ║
  // ║   Account: holderWallet.address,                                 ║
  // ║   LimitAmount: {                                                 ║
  // ║     currency: currencyCode,                                      ║
  // ║     issuer: issuerWallet.address,                                ║
  // ║     value: '1000000'                                             ║
  // ║   }                                                              ║
  // ║ }                                                                ║
  // ║                                                                  ║
  // ║ Submit: client.submitAndWait(tx, { wallet: holderWallet })       ║
  // ╚══════════════════════════════════════════════════════════════════╝
  // YOUR CODE HERE

  console.log('Trust line created!');

  // ╔══════════════════════════════════════════════════════════════════╗
  // ║ TODO 2: Issue tokens (issuer sends tokens to holder)            ║
  // ║                                                                  ║
  // ║ HINT: {                                                          ║
  // ║   TransactionType: 'Payment',                                    ║
  // ║   Account: issuerWallet.address,                                 ║
  // ║   Destination: holderWallet.address,                             ║
  // ║   Amount: {                                                      ║
  // ║     currency: currencyCode,                                      ║
  // ║     issuer: issuerWallet.address,                                ║
  // ║     value: '500'                                                 ║
  // ║   }                                                              ║
  // ║ }                                                                ║
  // ║                                                                  ║
  // ║ Submit: client.submitAndWait(tx, { wallet: issuerWallet })       ║
  // ╚══════════════════════════════════════════════════════════════════╝
  // YOUR CODE HERE

  console.log('Tokens issued!');
  console.log(`\nExplorer: https://testnet.xrpl.org/accounts/${issuerWallet.address}`);
  await client.disconnect();
}

main().catch(console.error);
