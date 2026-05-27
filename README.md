# XRPL Academy - Hands-On Blockchain Development Workshop

[![Open in GitHub Codespaces](https://github.com/codespaces/badge.svg)](https://codespaces.new/xrpl-commons/xrpl-academy)

A two-day hands-on workshop that teaches you to build on the XRP Ledger (XRPL) from scratch.
No prior blockchain experience required. Supports JavaScript, Python, and Java.

---

## What is the XRP Ledger?

The XRP Ledger (XRPL) is a decentralized, open-source blockchain designed for fast and
low-cost financial transactions. Unlike blockchains that use mining (Proof of Work), XRPL
uses a unique consensus protocol where trusted validators agree on the order of transactions
every 3-5 seconds.

Key characteristics:
- Transactions settle in 3-5 seconds (compared to minutes or hours on other chains)
- Transaction cost is approximately 0.00001 XRP (fractions of a cent)
- Built-in decentralized exchange (DEX) for trading any issued token
- Native support for tokens (IOUs), NFTs, and automated market makers (AMMs)
- No smart contract language needed -- features are built into the protocol itself

---

## What is Testnet?

XRPL Testnet is a separate network that mirrors the real (Mainnet) ledger but uses
worthless test XRP. It exists so developers can experiment freely without risking real money.

- Test XRP is free and unlimited via the Testnet Faucet
- The Testnet resets periodically, so do not store anything permanent there
- All exercises in this workshop use Testnet exclusively

---

## Quick Start

1. Click the "Open in GitHub Codespaces" badge above (requires a free GitHub account)
2. Wait approximately 60 seconds for the environment to set up
3. The setup script automatically creates two funded Testnet wallets saved to `wallets.json`
4. Choose your language and start with Day 1

If you prefer to run locally, see the "Local Setup" section below.

---

## Workshop Structure

### Day 1 - Fundamentals: Wallets, Connections, and Transactions

You will learn:
- How XRPL accounts (wallets) work -- addresses, seeds, and key pairs
- How to connect to the XRPL network via WebSocket
- How to construct, sign, and submit a payment transaction
- How to verify transactions on the Testnet Explorer

Run the Day 1 exercise:

```bash
# JavaScript
cd js && node day1-first-transaction.js

# Python
cd python && python day1-first-transaction.py

# Java
cd java && mvn compile exec:java -Dexec.mainClass="academy.xrpl.Day1FirstTransaction"
```

### Day 2 - Tokens and NFTs: Issuing Digital Assets

You will learn:
- How tokens (IOUs) work on XRPL -- issuers, trust lines, and balances
- How to configure an account to issue tokens
- How to create a trust line (permission to hold a token)
- How to issue and transfer custom tokens
- How to mint NFTs (non-fungible tokens) with royalties

Run the Day 2 exercise:

```bash
# JavaScript
cd js && node day2-tokens-nft.js

# Python
cd python && python day2-tokens-nft.py

# Java
cd java && mvn compile exec:java -Dexec.mainClass="academy.xrpl.Day2TokensNft"
```

---

## Your Testnet Wallets

After the environment sets up, open `wallets.json` in the project root. It contains
two pre-funded wallets with approximately 100 XRP each (Testnet only, no real value).

Each wallet has:
- `address` -- your public account identifier on the ledger (starts with "r")
- `seed` -- your secret key used to sign transactions (starts with "s")
- `publicKey` -- the cryptographic public key derived from your seed
- `balance` -- current XRP balance on Testnet

IMPORTANT: Seeds are secret keys. Anyone with your seed can control your account.
On Testnet this does not matter (the XRP is worthless), but on Mainnet you must
never share your seed.

To generate fresh wallets at any time:
```bash
node scripts/mint-wallets.js
```

---

## Key XRPL Concepts

### Accounts and Wallets

An XRPL account is identified by its address (e.g., rN7n3473SaZBCG4dFL83w7p1W9cgZw6). To
create an account on the ledger, it must receive a minimum reserve of 10 XRP. On Testnet,
the faucet handles this automatically.

### Transactions

Every action on XRPL is a transaction: sending payments, creating trust lines, minting NFTs,
etc. Each transaction must be:
1. Constructed with the correct fields
2. Signed with the sender's secret key
3. Submitted to the network
4. Validated by consensus (3-5 seconds)

### Drops

XRP amounts in transactions are specified in "drops" -- the smallest unit of XRP.
1 XRP = 1,000,000 drops. Libraries provide helper functions to convert between XRP and drops.

### Trust Lines

Unlike XRP (the native currency), custom tokens require explicit permission to hold.
A trust line is a declaration that says "I trust this issuer to hold up to X amount of
their token." This prevents spam -- no one can force tokens into your account.

### NFTs on XRPL

XRPL has native NFT support (no smart contracts needed). NFTs are minted with NFTokenMint
transactions and can include:
- A URI pointing to metadata/media
- A transfer fee (royalty) paid to the creator on every resale
- Flags controlling whether the NFT can be transferred or burned

---

## Network Information

| Setting    | Value                                          |
|------------|------------------------------------------------|
| Network    | Testnet                                        |
| WebSocket  | `wss://s.altnet.rippletest.net:51233`          |
| Faucet     | https://faucet.altnet.rippletest.net/accounts  |
| Explorer   | https://testnet.xrpl.org                       |

---

## Languages and Libraries

| Language   | Library   | Documentation                          |
|------------|-----------|----------------------------------------|
| JavaScript | xrpl.js   | https://js.xrpl.org                    |
| Python     | xrpl-py   | https://xrpl-py.readthedocs.io         |
| Java       | xrpl4j    | https://github.com/XRPLF/xrpl4j       |

---

## Project Structure

```
xrpl-academy/
├── .devcontainer/
│   ├── devcontainer.json    -- Codespaces configuration
│   └── setup.sh             -- Automated environment setup
├── scripts/
│   └── mint-wallets.js      -- Generates funded Testnet wallets
├── js/
│   ├── package.json         -- Node.js dependencies
│   ├── day1-first-transaction.js
│   └── day2-tokens-nft.js
├── python/
│   ├── requirements.txt     -- Python dependencies
│   ├── day1-first-transaction.py
│   └── day2-tokens-nft.py
├── java/
│   ├── pom.xml              -- Maven dependencies (xrpl4j)
│   └── src/main/java/academy/xrpl/
│       ├── Day1FirstTransaction.java
│       └── Day2TokensNft.java
├── wallets.json             -- Auto-generated Testnet wallets (gitignored)
└── README.md
```

---

## Local Setup (Without Codespaces)

If you prefer to run locally instead of using Codespaces:

### Prerequisites
- Node.js 18+ (for JavaScript exercises and wallet minting)
- Python 3.9+ (for Python exercises)
- Java 17+ and Maven 3.8+ (for Java exercises)

### Steps

```bash
# Clone the repository
git clone https://github.com/xrpl-commons/xrpl-academy.git
cd xrpl-academy

# Install JavaScript dependencies
cd js && npm install && cd ..

# Install Python dependencies
pip install -r python/requirements.txt

# Build Java project
cd java && mvn compile && cd ..

# Generate your Testnet wallets
node scripts/mint-wallets.js
```

---

## Troubleshooting

**Wallet minting fails:**
The Testnet faucet occasionally has downtime. Wait a few minutes and try again with
`node scripts/mint-wallets.js`. You can also manually get wallets at
https://faucet.altnet.rippletest.net/accounts

**Transaction fails with "tecUNFUNDED_PAYMENT":**
Your wallet does not have enough XRP. Re-mint wallets to get fresh funded accounts.

**Cannot connect to WebSocket:**
Check your internet connection. The Testnet WebSocket URL is
`wss://s.altnet.rippletest.net:51233`. Some corporate firewalls block WebSocket connections.

**Java build fails:**
Ensure Maven is installed (`mvn --version`). The first build downloads dependencies
and may take a few minutes.

---

## Further Reading

- XRPL Documentation: https://xrpl.org/docs
- XRPL Developer Portal: https://xrpl.org/resources/dev-tools
- Testnet Explorer: https://testnet.xrpl.org
- XRP Ledger Standards (XLS): https://github.com/XRPLF/XRPL-Standards
