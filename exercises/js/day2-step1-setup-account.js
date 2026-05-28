// ============================================================
// Day 2 - Step 1 Exercise: Setup Account (Enable Default Ripple)
// Learn: How to configure account flags on the XRP Ledger.
// Default Ripple is required before issuing tokens.
// ============================================================

const xrpl = require('xrpl');
const fs = require('fs');

const TESTNET_URL = 'wss://s.altnet.rippletest.net:51233';

async function main() {
  const { wallets } = JSON.parse(fs.readFileSync('../../wallets.json', 'utf-8'));
  const issuerWallet = xrpl.Wallet.fromSeed(wallets[0].seed);

  const client = new xrpl.Client(TESTNET_URL);
  await client.connect();
  console.log('Connected to XRPL Testnet');

  // ╔══════════════════════════════════════════════════════════════════╗
  // ║ TODO 1: Submit an AccountSet transaction to enable Default       ║
  // ║         Ripple on the issuer account                             ║
  // ║                                                                  ║
  // ║ HINT: {                                                          ║
  // ║   TransactionType: 'AccountSet',                                 ║
  // ║   Account: issuerWallet.address,                                 ║
  // ║   SetFlag: xrpl.AccountSetAsfFlags.asfDefaultRipple              ║
  // ║ }                                                                ║
  // ║                                                                  ║
  // ║ Submit: client.submitAndWait(tx, { wallet: issuerWallet })       ║
  // ╚══════════════════════════════════════════════════════════════════╝
  // YOUR CODE HERE

  console.log('Default Ripple enabled on issuer account!');
  console.log(`\nExplorer: https://testnet.xrpl.org/accounts/${issuerWallet.address}`);
  await client.disconnect();
}

main().catch(console.error);
