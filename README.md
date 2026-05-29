# XRPL Developer Training - June 8-9, 2026

[![Open in GitHub Codespaces](https://github.com/codespaces/badge.svg)](https://codespaces.new/XRPL-Commons/dev-training-june-2026)

This repository is the hands-on environment for the XRPL Commons developer training. Participants work from scaffolded exercises. Mentors can track progress through Google Chat and verify the final work on XRPL Testnet.

## What Is In This Repo

```text
.
├── .devcontainer/        Codespaces setup
├── scripts/              Wallet minting
├── exercises/
│   ├── js/
│   ├── python/
│   └── java/
├── wallets.json          Auto-generated on first Codespace start
└── README.md
```

Complete solutions live on the `solutions` branch.

## Getting Started

### 1. Open Codespaces

Click the button above and wait for the environment to finish starting.

The Codespace automatically:

- installs JavaScript, Python, and Java dependencies
- creates two funded XRPL Testnet wallets
- saves them to `wallets.json`
- silently notifies mentors in Google Chat that you started

Prebuild note:

- this repository is configured so dependency installation runs during Codespaces prebuild creation
- wallet minting still runs only when each participant's Codespace starts, so wallets stay unique per participant

### 2. Pick a Language

| Language | Folder | Run pattern |
|---|---|---|
| JavaScript | `exercises/js/` | `node file-name.js` |
| Python | `exercises/python/` | `python file_name.py` |
| Java | `exercises/java/` | `mvn compile exec:java -Dexec.mainClass="academy.xrpl.ClassName"` |

### 3. Complete the Exercises

Each exercise contains TODO blocks. Fill them in, run the file, and confirm success in the XRPL Testnet Explorer.

## Workshop Flow

### Day 1

| Time (CET) | Activity | Exercise |
|---|---|---|
| 3:00 - 3:30 | Welcome keynote | - |
| 3:30 - 4:30 | Blockchain 101 + quiz | - |
| 4:45 - 5:30 | XRPL 101 + quiz | - |
| 5:30 - 6:00 | Coding session 1 | `day1-send-xrp` |
| 6:00 - 6:30 | Q&A + homework | `homework-nft` |

### Day 2

| Time (CET) | Activity | Exercise |
|---|---|---|
| 3:00 - 3:30 | Homework review | - |
| 3:30 - 4:00 | Liquidity on XRPL | - |
| 4:05 - 4:10 | Warm-up | `day2-step1-setup-account` |
| 4:15 - 4:30 | Token issuance | `day2-step2-issue-token` |
| 4:35 - 4:55 | Capstone | `day2-step3-create-amm` |
| 5:50 - 6:10 | Alumni testimonial | - |
| 6:10 - 6:30 | Q&A + closing | - |

## Exercise Commands

### Day 1: Send XRP

```bash
cd exercises/js && node day1-send-xrp.js
cd exercises/python && python day1_send_xrp.py
cd exercises/java && mvn compile exec:java -Dexec.mainClass="academy.xrpl.Day1SendXrp"
```

Success signal: `tesSUCCESS` and an Explorer link.

### Homework: NFT Lifecycle

```bash
cd exercises/js && node homework-nft.js
cd exercises/python && python homework_nft.py
cd exercises/java && mvn compile exec:java -Dexec.mainClass="academy.xrpl.HomeworkNft"
```

Success signal: NFT mint succeeds, appears on-chain, then is burned.

### Day 2 Step 1: Setup Account

```bash
cd exercises/js && node day2-step1-setup-account.js
cd exercises/python && python day2_step1_setup_account.py
cd exercises/java && mvn compile exec:java -Dexec.mainClass="academy.xrpl.Day2Step1SetupAccount"
```

Success signal: issuer account enables `Default Ripple`.

### Day 2 Step 2: Issue Token

```bash
cd exercises/js && node day2-step2-issue-token.js
cd exercises/python && python day2_step2_issue_token.py
cd exercises/java && mvn compile exec:java -Dexec.mainClass="academy.xrpl.Day2Step2IssueToken"
```

Success signal: holder trust line exists and receives the issued token.

### Day 2 Step 3: Create AMM

Prerequisite: complete Step 1 and Step 2 first.

```bash
cd exercises/js && node day2-step3-create-amm.js
cd exercises/python && python day2_step3_create_amm.py
cd exercises/java && mvn compile exec:java -Dexec.mainClass="academy.xrpl.Day2Step3CreateAmm"
```

Success signal: `AMMCreate` succeeds and the pool exists on Testnet.

## Automatic Progress Tracking

Participants do not need to run any tracking commands.

Tracking is silent and automatic:

- when a Codespace starts and wallets are minted, mentors get a startup signal
- a hidden background watcher monitors XRPL Testnet activity for that participant's wallets
- when a milestone transaction appears on-chain, mentors get a Google Chat update
- when Day 2 Step 3 succeeds, the final deliverable is submitted automatically

Each notification includes:

- the participant GitHub username from Codespaces
- the stage they completed
- their XRPL Testnet wallet addresses
- Explorer links that mentors can review later

## How Prerequisites Are Verified

Verification is based on actual XRPL transaction history, not file edits or Codespace creation.

The hidden watcher looks for:

- Day 1 XRP payment
- NFT mint and burn
- `AccountSet` enabling `Default Ripple`
- `TrustSet` plus issued token payment
- `AMMCreate`

This is the defensible answer to "did they actually complete it?" because it checks ledger activity directly.

## Organizer Setup: Google Chat Hook

This repo supports Google Chat incoming webhooks.

Setup steps:

1. Create an incoming webhook in the mentor Google Chat space.
2. In GitHub, add a Codespaces repository secret named `GOOGLE_CHAT_WEBHOOK_URL`.
3. Use the webhook URL value from Google Chat.
4. New participant Codespaces will automatically post a startup event when wallets are minted.
5. Exercise success events and the final deliverable will also post automatically in the background.
6. In GitHub repository settings, open `Settings -> Codespaces -> Prebuild configuration` and create a prebuild for `main`.
7. Choose the nearest region for participants, for example `West Europe` for this workshop.

Participants do not need to know or paste the webhook URL.

Google documents incoming webhooks as one-way notifications into a Chat space, which matches this workshop use case:

- https://developers.google.com/workspace/chat/quickstart/webhooks

## Mentor Support

If participants are blocked, send them to a Zoom breakout room and route them to one of:

- Tushar Pardhe
- Florian
- Thomas
- Mathis

If you want photos in this README, add the image assets and they can be linked here. Right now only names are included.

## Wallets

After startup, `wallets.json` contains two funded Testnet wallets:

- Wallet 1: sender, issuer, minter
- Wallet 2: receiver, holder

Important:

- these are Testnet wallets only
- never reuse Testnet seeds on Mainnet
- you can re-mint them with `node scripts/mint-wallets.js`

## Network Reference

| Item | Value |
|---|---|
| Network | XRPL Testnet |
| WebSocket | `wss://s.altnet.rippletest.net:51233` |
| JSON-RPC | `https://s.altnet.rippletest.net:51234` |
| Explorer | `https://testnet.xrpl.org` |
| Faucet | `https://faucet.altnet.rippletest.net/accounts` |

## Reference Materials

### Start Here

- [XRPL Commons Learning Portal](https://learn.xrpl-commons.org/) for guided courses and workshop-friendly explanations
- [Intro to the XRPL](https://learn.xrpl-commons.org/course/intro-to-the-xrpl/) for the broad protocol overview
- [XRPL Docs](https://xrpl.org/docs) for official transaction, API, and network references
- [XRPLF GitHub Organization](https://github.com/XRPLF) for SDKs, examples, and core project source
- [XRPL Testnet Explorer](https://testnet.xrpl.org) to confirm results on-chain after each exercise

### Exercise-by-Exercise Links

| Exercise | Learning Portal | XRPL Docs | Helpful SDK / Source |
|---|---|---|---|
| `day1-send-xrp` | [Create Accounts and Send XRP](https://learn.xrpl-commons.org/course/blockchain-foundations-for-web2-developers/lesson/create-accounts-and-send-xrp/) | [Payment transaction](https://xrpl.org/docs/references/protocol/transactions/types/payment) | [xrpl.js](https://js.xrpl.org), [xrpl-py](https://xrpl-py.readthedocs.io/en/stable/) |
| `homework-nft` | [Mint and Burn NFTs](https://learn.xrpl-commons.org/course/code-with-the-xrpl/lesson/mint-and-burn-nfts/) | [NFTokenMint](https://xrpl.org/docs/references/protocol/transactions/types/nftokenmint), [NFTokenBurn](https://xrpl.org/docs/references/protocol/transactions/types/nftokenburn), [account_nfts](https://xrpl.org/docs/references/http-websocket-apis/public-api-methods/account-methods/account_nfts) | [xrpl.js](https://js.xrpl.org), [xrpl-py](https://xrpl-py.readthedocs.io/en/stable/) |
| `day2-step1-setup-account` | [Code with XRPL and JavaScript](https://learn.xrpl-commons.org/course/code-with-the-xrpl/) | [AccountSet transaction](https://xrpl.org/docs/references/protocol/transactions/types/accountset) | [xrpl.js](https://js.xrpl.org), [xrpl-py](https://xrpl-py.readthedocs.io/en/stable/), [xrpl4j](https://github.com/XRPLF/xrpl4j) |
| `day2-step2-issue-token` | [Create Trust Line and Send Currency](https://learn.xrpl-commons.org/course/code-with-the-xrpl/lesson/create-trustline-and-send-currency/) | [TrustSet transaction](https://xrpl.org/docs/references/protocol/transactions/types/trustset), [Payment transaction](https://xrpl.org/docs/references/protocol/transactions/types/payment) | [xrpl.js](https://js.xrpl.org), [xrpl-py](https://xrpl-py.readthedocs.io/en/stable/), [xrpl4j](https://github.com/XRPLF/xrpl4j) |
| `day2-step3-create-amm` | [What is an Automated Market Maker?](https://learn.xrpl-commons.org/course/deep-dive-into-xrpl-defi/lesson/what-is-an-automated-market-maker-amm/) | [AMMCreate transaction](https://xrpl.org/docs/references/protocol/transactions/types/ammcreate), [amm_info](https://xrpl.org/docs/references/http-websocket-apis/public-api-methods/path-and-order-book-methods/amm_info) | [xrpl.js](https://js.xrpl.org), [xrpl-py](https://xrpl-py.readthedocs.io/en/stable/), [xrpl4j](https://github.com/XRPLF/xrpl4j) |

### SDKs and Repositories

- [xrpl.js docs](https://js.xrpl.org) and [xrpl.js repo](https://github.com/XRPLF/xrpl.js)
- [xrpl-py docs](https://xrpl-py.readthedocs.io/en/stable/) and [xrpl-py repo](https://github.com/XRPLF/xrpl-py)
- [xrpl4j repo](https://github.com/XRPLF/xrpl4j) for Java examples and library source
- [XRPL Dev Portal repo](https://github.com/XRPLF/xrpl-dev-portal) for many of the code samples behind the official docs
- [rippled repo](https://github.com/XRPLF/rippled) for protocol/server implementation details if you want to go deeper

## Troubleshooting

| Problem | What to do |
|---|---|
| Wallet minting fails | Retry `node scripts/mint-wallets.js` after a short wait. |
| Java build is slow on first run | Run `mvn compile` once and wait for dependencies. |
| WebSocket connection fails | Retry on a different network if corporate firewall rules block WebSockets. |
| Day 2 Step 3 fails | Complete Step 1 and Step 2 first, then retry. |
| Progress message does not reach mentors | Confirm the `GOOGLE_CHAT_WEBHOOK_URL` Codespaces secret is set. |

## Solutions Branch

If a participant gets stuck, complete solutions are available on the `solutions` branch:

```bash
git checkout solutions -- solutions/
```

## Further Reading

- [XRPL Commons Learning Portal](https://learn.xrpl-commons.org/)
- [XRPL Docs](https://xrpl.org/docs)
- [xrpl.js](https://js.xrpl.org)
- [xrpl-py](https://xrpl-py.readthedocs.io/en/stable/)
- [xrpl4j](https://github.com/XRPLF/xrpl4j)
- [XRPLF GitHub Organization](https://github.com/XRPLF)
- [XRPL Dev Portal Repo](https://github.com/XRPLF/xrpl-dev-portal)
- [XRPL Testnet Explorer](https://testnet.xrpl.org)
