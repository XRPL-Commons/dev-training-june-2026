# 🚀 XRPL Developer Training — June 8-9, 2026

[![Open in GitHub Codespaces](https://github.com/codespaces/badge.svg)](https://codespaces.new/XRPL-Commons/dev-training-june-2026)

Welcome to the XRPL Commons hands-on developer training! In two days, you'll go from zero to building a complete DeFi stack on the XRP Ledger.

---

## 🏁 Getting Started (5 minutes)

### Step 1: Open your coding environment

Click the **"Open in GitHub Codespaces"** button above. This gives you a fully configured coding environment in your browser — no installation needed.

> You need a free [GitHub account](https://github.com/signup). That's it.

### Step 2: Wait for setup to complete

The environment automatically:
- ✅ Installs all dependencies (JS, Python, Java)
- ✅ Creates two funded wallets on XRPL Testnet
- ✅ Saves your wallets to `wallets.json`

You'll see "Setup complete" in the terminal when it's ready (~60 seconds).

### Step 3: Choose your language

Navigate to your preferred language folder:

| Language | Folder | Run command |
|----------|--------|-------------|
| JavaScript | `exercises/js/` | `node filename.js` |
| Python | `exercises/python/` | `python filename.py` |
| Java | `exercises/java/` | `mvn compile exec:java -Dexec.mainClass="academy.xrpl.ClassName"` |

### Step 4: Start coding!

Each exercise file has **TODO blocks** — fill them in, run the file, and check the Explorer link to see your transaction on-chain.

---

## 📅 Workshop Schedule

### Day 1 — Sunday, June 8

| Time (CET) | Activity | Exercise |
|-------------|----------|----------|
| 3:00 - 3:30 | Welcome Keynote | — |
| 3:30 - 4:30 | Blockchain 101 + Quiz | — |
| 4:45 - 5:30 | XRPL 101 + Quiz | — |
| 5:30 - 6:00 | **Coding Session 1** | `day1-send-xrp` |
| 6:00 - 6:30 | Q&A + Homework assigned | `homework-nft` |

### Day 2 — Monday, June 9

| Time (CET) | Activity | Exercise |
|-------------|----------|----------|
| 3:00 - 3:30 | Homework Review | — |
| 3:30 - 4:00 | Keynote: Liquidity on XRPL | — |
| 4:05 - 5:35 | **Coding Session 2** | `day2-step1` → `step2` → `step3` |
| 5:50 - 6:10 | Alumni Testimonial | — |
| 6:10 - 6:30 | Q&A + Closing | — |

---

## 📝 Exercise Guide

### How exercises work

Every file has clearly marked TODO sections with hints:

```js
// ╔══════════════════════════════════════════════════════════════════╗
// ║ TODO 1: Construct the Payment transaction                       ║
// ║                                                                  ║
// ║ HINT: { TransactionType: 'Payment', Account: ..., Amount: ... } ║
// ╚══════════════════════════════════════════════════════════════════╝
const paymentTx = {
  // YOUR CODE HERE
};
```

Fill in the code, run the file, and verify on the [Testnet Explorer](https://testnet.xrpl.org).

---

### Exercise 1: Send XRP (Day 1 — Live)

**File:** `day1-send-xrp`  
**Time:** 10 minutes  
**What you'll do:** Send 10 XRP from one wallet to another  
**What you'll learn:** How transactions work on XRPL  

```bash
# JavaScript
cd exercises/js && node day1-send-xrp.js

# Python
cd exercises/python && python day1_send_xrp.py

# Java
cd exercises/java && mvn compile exec:java -Dexec.mainClass="academy.xrpl.Day1SendXrp"
```

✅ **Success:** You see `tesSUCCESS` and an Explorer link showing your transaction.

---

### Exercise 2: NFT Lifecycle (Homework)

**File:** `homework-nft`  
**Time:** 20-30 minutes (on your own)  
**What you'll do:** Mint an NFT → Query it → Burn it → Verify it's gone  
**What you'll learn:** Digital asset creation and destruction on-chain  

```bash
# JavaScript
cd exercises/js && node homework-nft.js

# Python
cd exercises/python && python homework_nft.py

# Java
cd exercises/java && mvn compile exec:java -Dexec.mainClass="academy.xrpl.HomeworkNft"
```

💡 **Personalize it:** Replace `YOUR_NAME_HERE` in the URI with your actual name!

✅ **Success:** You see your NFT appear on the Explorer, then disappear after burning.

---

### Exercise 3: Setup Account (Day 2 — Step 1)

**File:** `day2-step1-setup-account`  
**Time:** 5 minutes (warm-up)  
**What you'll do:** Enable "Default Ripple" on your issuer account  
**What you'll learn:** Account configuration for token issuance  

```bash
# JavaScript
cd exercises/js && node day2-step1-setup-account.js

# Python
cd exercises/python && python day2_step1_setup_account.py

# Java
cd exercises/java && mvn compile exec:java -Dexec.mainClass="academy.xrpl.Day2Step1SetupAccount"
```

---

### Exercise 4: Issue a Token (Day 2 — Step 2)

**File:** `day2-step2-issue-token`  
**Time:** 15 minutes  
**What you'll do:** Create a trust line + issue your own custom token  
**What you'll learn:** How tokens (IOUs) work on XRPL  

💡 **Personalize it:** Use your initials as the 3-letter currency code!

```bash
# JavaScript
cd exercises/js && node day2-step2-issue-token.js

# Python
cd exercises/python && python day2_step2_issue_token.py

# Java
cd exercises/java && mvn compile exec:java -Dexec.mainClass="academy.xrpl.Day2Step2IssueToken"
```

✅ **Success:** Your custom token appears on the Explorer under the holder's account.

---

### Exercise 5: Create an AMM Pool (Day 2 — Step 3)

**File:** `day2-step3-create-amm`  
**Time:** 20 minutes (capstone)  
**What you'll do:** Create a liquidity pool pairing your token with XRP  
**What you'll learn:** Automated Market Makers and DeFi on XRPL  

⚠️ **Prerequisite:** You must complete Step 1 and Step 2 first!

```bash
# JavaScript
cd exercises/js && node day2-step3-create-amm.js

# Python
cd exercises/python && python day2_step3_create_amm.py

# Java
cd exercises/java && mvn compile exec:java -Dexec.mainClass="academy.xrpl.Day2Step3CreateAmm"
```

✅ **Success:** Your AMM pool is live on the XRPL Testnet. You just created a DEX market!

---

## 🔑 Your Wallets

After setup, open `wallets.json` in the project root:

```json
{
  "wallets": [
    {
      "address": "rABC123...",   ← Your public address (safe to share)
      "seed": "sXYZ789...",      ← Your SECRET key (never share on Mainnet!)
      "balance": "100 XRP"
    },
    { ... }
  ]
}
```

- **Wallet 1** = Sender / Issuer / Minter
- **Wallet 2** = Receiver / Holder

To regenerate wallets: `node scripts/mint-wallets.js`

> ⚠️ These are **Testnet** wallets with fake XRP. Never reuse Testnet seeds on Mainnet.

---

## 🌐 Network Info

| | |
|---|---|
| **Network** | XRPL Testnet |
| **WebSocket** | `wss://s.altnet.rippletest.net:51233` |
| **JSON-RPC** | `https://s.altnet.rippletest.net:51234` |
| **Explorer** | https://testnet.xrpl.org |
| **Faucet** | https://faucet.altnet.rippletest.net/accounts |

---

## 🆘 Troubleshooting

| Problem | Solution |
|---------|----------|
| Wallet minting fails | Testnet faucet may be down. Wait 2 min, retry: `node scripts/mint-wallets.js` |
| `tecUNFUNDED_PAYMENT` | Not enough XRP. Re-mint wallets. |
| Cannot connect to WebSocket | Check internet. Some corporate firewalls block WebSocket. |
| Day 2 Step 3 fails | Run Step 1 and Step 2 first — AMM needs your token to exist. |
| Java build fails | Run `mvn compile` first. Initial build downloads dependencies (~2 min). |

**Still stuck?** Ask for help in the Zoom breakout room or post in Discord.

---

## 📚 Solutions

If you're stuck, complete solutions are available on the **`solutions`** branch:

```bash
git checkout solutions -- solutions/
```

Or browse them on GitHub: [solutions branch](../../tree/solutions)

---

## 📖 Further Reading

- [XRPL Documentation](https://xrpl.org/docs)
- [xrpl.js docs](https://js.xrpl.org) · [xrpl-py docs](https://xrpl-py.readthedocs.io) · [xrpl4j](https://github.com/XRPLF/xrpl4j)
- [Testnet Explorer](https://testnet.xrpl.org)
- [XRPL Dev Tools](https://xrpl.org/resources/dev-tools)

---

## 🎓 What You'll Have Built

By the end of this training:

```
✅ Sent XRP on a real blockchain
✅ Minted and burned an NFT
✅ Created your own custom token
✅ Launched a liquidity pool (AMM)
```

**You've built a complete mini DeFi stack on XRPL. Welcome to Web3.** 🎉
