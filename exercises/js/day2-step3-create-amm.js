// ============================================================
// Day 2 - Step 3 Exercise: Create an AMM Pool
// Learn: How to create an Automated Market Maker (AMM) pool
// that pairs your custom token with XRP.
//
// PREREQUISITE: Run step1 (Default Ripple) and step2 (issue
// tokens) first! The issuer needs tokens to deposit into the pool.
//
// AMM Concept: An AMM pool holds two assets and allows anyone
// to swap between them. The TradingFee (in basis points) goes
// to liquidity providers.
// ============================================================

const xrpl = require('xrpl');
const fs = require('fs');

const TESTNET_URL = 'wss://s.altnet.rippletest.net:51233';

async function main() {
  const { wallets } = JSON.parse(fs.readFileSync('../../wallets.json', 'utf-8'));
  const issuerWallet = xrpl.Wallet.fromSeed(wallets[0].seed);

  // Must match the currency code from step2
  const currencyCode = 'ACD';

  const client = new xrpl.Client(TESTNET_URL);
  await client.connect();
  console.log('Connected to XRPL Testnet');

  // ╔══════════════════════════════════════════════════════════════════╗
  // ║ TODO 1: Create an AMMCreate transaction                         ║
  // ║                                                                  ║
  // ║ HINT: {                                                          ║
  // ║   TransactionType: 'AMMCreate',                                  ║
  // ║   Account: issuerWallet.address,                                 ║
  // ║   Amount: {                                                      ║
  // ║     currency: currencyCode,                                      ║
  // ║     issuer: issuerWallet.address,                                ║
  // ║     value: '100'                                                 ║
  // ║   },                                                             ║
  // ║   Amount2: xrpl.xrpToDrops('10'),                                ║
  // ║   TradingFee: 500                                                ║
  // ║ }                                                                ║
  // ║                                                                  ║
  // ║ Submit: client.submitAndWait(tx, { wallet: issuerWallet })       ║
  // ╚══════════════════════════════════════════════════════════════════╝
  // YOUR CODE HERE

  console.log('AMM Pool created!');

  // ╔══════════════════════════════════════════════════════════════════╗
  // ║ TODO 2: Query the AMM info                                      ║
  // ║                                                                  ║
  // ║ HINT: const ammInfo = await client.request({                     ║
  // ║   command: 'amm_info',                                            ║
  // ║   asset: {                                                        ║
  // ║     currency: currencyCode,                                       ║
  // ║     issuer: issuerWallet.address                                  ║
  // ║   },                                                              ║
  // ║   asset2: { currency: 'XRP' }                                     ║
  // ║ });                                                               ║
  // ║ console.log(JSON.stringify(ammInfo.result, null, 2));              ║
  // ╚══════════════════════════════════════════════════════════════════╝
  // YOUR CODE HERE

  console.log(`\nExplorer: https://testnet.xrpl.org/accounts/${issuerWallet.address}`);
  await client.disconnect();
}

main().catch(console.error);
