#!/bin/bash
set -e

REPO_ROOT="$(pwd)"

echo ""
echo "============================================"
echo "  XRPL Academy - Installing Dependencies"
echo "============================================"
echo ""

# -- JavaScript ---------------------------------------------------
echo "[1/3] Installing JavaScript dependencies..."
cd /workspaces/*/exercises/js 2>/dev/null || cd "$REPO_ROOT/exercises/js"
npm install --silent
cd "$REPO_ROOT"

# -- Python -------------------------------------------------------
echo "[2/3] Installing Python dependencies..."
pip install xrpl-py --quiet

# -- Java (Maven) -------------------------------------------------
echo "[3/3] Building Java project..."
if [ -d "exercises/java" ] && [ -f "exercises/java/pom.xml" ]; then
  cd exercises/java
  mvn compile -q -DskipTests 2>/dev/null || echo "  Note: Java build will complete on first run."
  cd "$REPO_ROOT"
else
  echo "  Skipping Java build."
fi

echo "[setup] Installing internal progress watcher..."
mkdir -p "$HOME/.xrpl-academy" "$HOME/.local/bin"
cat > "$HOME/.xrpl-academy/progress-watcher.js" <<'EOF'
const fs = require("fs");
const path = require("path");

const repoRoot = process.argv[2];
const stateFile = path.join(process.env.HOME, ".xrpl-academy", "progress-state.json");
const webhookUrl = process.env.GOOGLE_CHAT_WEBHOOK_URL || "";
const explorerBase = "https://testnet.xrpl.org/accounts/";
const pollIntervalMs = 30000;
const maxRuntimeMs = 6 * 60 * 60 * 1000;

if (!repoRoot || !webhookUrl.startsWith("https://chat.googleapis.com/")) {
  process.exit(0);
}

const xrpl = require(path.join(repoRoot, "exercises", "js", "node_modules", "xrpl"));

function sleep(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

function loadJson(filePath, fallback) {
  try {
    return JSON.parse(fs.readFileSync(filePath, "utf8"));
  } catch {
    return fallback;
  }
}

function saveJson(filePath, value) {
  fs.writeFileSync(filePath, `${JSON.stringify(value, null, 2)}\n`);
}

function hasIssuedAmount(amount) {
  return amount && typeof amount === "object" && amount.currency && amount.issuer && amount.value;
}

async function postToChat(text) {
  const response = await fetch(webhookUrl, {
    method: "POST",
    headers: { "Content-Type": "application/json; charset=UTF-8" },
    body: JSON.stringify({ text }),
  });

  if (!response.ok) {
    throw new Error(`Webhook returned ${response.status}`);
  }
}

async function waitForWallets() {
  const walletsFile = path.join(repoRoot, "wallets.json");

  for (let attempt = 0; attempt < 30; attempt += 1) {
    const data = loadJson(walletsFile, null);
    if (Array.isArray(data?.wallets) && data.wallets.length >= 2) {
      return data.wallets;
    }
    await sleep(2000);
  }

  return null;
}

function walletLines(wallets) {
  return [
    `- Issuer / Sender: ${wallets[0].address} (${explorerBase}${wallets[0].address})`,
    `- Holder / Receiver: ${wallets[1].address} (${explorerBase}${wallets[1].address})`,
  ].join("\n");
}

function buildMessage(username, status, wallets) {
  return [
    "XRPL Academy progress update",
    `Participant: ${username}`,
    `Status: ${status}`,
    `Wallets:\n${walletLines(wallets)}`,
  ].join("\n\n");
}

function loadState(walletKey) {
  const state = loadJson(stateFile, { sent: {}, walletKey: null });
  if (state.walletKey !== walletKey) {
    return { sent: {}, walletKey };
  }
  return state;
}

function detectMilestones(issuerTxs, holderTxs, issuerAddress, holderAddress) {
  const combined = [...issuerTxs, ...holderTxs];
  const detected = {
    "day1-send-xrp": false,
    "homework-nft": { minted: false, burned: false },
    "day2-step1-setup-account": false,
    "day2-step2-issue-token": false,
    "day2-step3-create-amm": false,
  };

  for (const entry of combined) {
    const tx = entry.tx_json || {};
    if (entry.meta?.TransactionResult !== "tesSUCCESS") {
      continue;
    }

    if (
      tx.TransactionType === "Payment" &&
      tx.Account === issuerAddress &&
      tx.Destination === holderAddress &&
      tx.Amount === xrpl.xrpToDrops("10")
    ) {
      detected["day1-send-xrp"] = true;
    }

    if (tx.TransactionType === "NFTokenMint" && tx.Account === issuerAddress) {
      detected["homework-nft"].minted = true;
    }

    if (tx.TransactionType === "NFTokenBurn" && tx.Account === issuerAddress) {
      detected["homework-nft"].burned = true;
    }

    if (
      (tx.TransactionType === "AccountSet" &&
        tx.Account === issuerAddress &&
        String(tx.SetFlag) === String(xrpl.AccountSetAsfFlags.asfDefaultRipple)) ||
      (tx.TransactionType === "TrustSet" &&
        tx.Account === holderAddress &&
        tx.LimitAmount?.issuer === issuerAddress)
    ) {
      detected["day2-step1-setup-account"] = true;
    }

    if (
      tx.TransactionType === "Payment" &&
      tx.Account === issuerAddress &&
      tx.Destination === holderAddress &&
      hasIssuedAmount(tx.Amount) &&
      tx.Amount.issuer === issuerAddress
    ) {
      detected["day2-step2-issue-token"] = true;
    }

    if (tx.TransactionType === "AMMCreate" && [issuerAddress, holderAddress].includes(tx.Account)) {
      detected["day2-step3-create-amm"] = true;
    }
  }

  return detected;
}

async function fetchTransactions(client, account) {
  const response = await client.request({
    command: "account_tx",
    account,
    ledger_index_min: -1,
    ledger_index_max: -1,
    limit: 200,
    forward: false,
  });

  return response.result.transactions || [];
}

async function sendIfNew(state, key, username, status, wallets) {
  if (state.sent[key]) {
    return false;
  }

  await postToChat(buildMessage(username, status, wallets));
  state.sent[key] = new Date().toISOString();
  saveJson(stateFile, state);
  return true;
}

async function main() {
  const wallets = await waitForWallets();
  if (!wallets) {
    process.exit(0);
  }

  const username = process.env.GITHUB_USER || "unknown-codespaces-user";
  const issuerAddress = wallets[0].address;
  const holderAddress = wallets[1].address;
  const walletKey = `${issuerAddress}:${holderAddress}`;
  const state = loadState(walletKey);

  await sendIfNew(state, "started", username, "started the Codespace and minted wallets", wallets);

  const client = new xrpl.Client("wss://s.altnet.rippletest.net:51233");
  await client.connect();

  const startedAt = Date.now();

  try {
    while (Date.now() - startedAt < maxRuntimeMs) {
      const [issuerTxs, holderTxs] = await Promise.all([
        fetchTransactions(client, issuerAddress),
        fetchTransactions(client, holderAddress),
      ]);

      const detected = detectMilestones(issuerTxs, holderTxs, issuerAddress, holderAddress);

      if (detected["day1-send-xrp"]) {
        await sendIfNew(state, "day1-send-xrp", username, "completed Day 1: Send XRP", wallets);
      }

      if (detected["homework-nft"].minted && detected["homework-nft"].burned) {
        await sendIfNew(state, "homework-nft", username, "completed Homework: NFT lifecycle", wallets);
      }

      if (detected["day2-step1-setup-account"]) {
        await sendIfNew(state, "day2-step1-setup-account", username, "completed Day 2 Step 1", wallets);
      }

      if (detected["day2-step2-issue-token"]) {
        await sendIfNew(state, "day2-step2-issue-token", username, "completed Day 2 Step 2", wallets);
      }

      if (detected["day2-step3-create-amm"]) {
        await sendIfNew(state, "day2-step3-create-amm", username, "completed Day 2 Step 3 and created an AMM", wallets);
        await sendIfNew(state, "workshop-complete", username, "submitted the final workshop deliverable", wallets);
        break;
      }

      await sleep(pollIntervalMs);
    }
  } finally {
    await client.disconnect();
  }
}

main().catch(() => {
  process.exit(0);
});
EOF
cat > "$HOME/.local/bin/xrpl-start-progress-watcher" <<'EOF'
#!/bin/bash
pkill -f "$HOME/.xrpl-academy/progress-watcher.js" >/dev/null 2>&1 || true
nohup node "$HOME/.xrpl-academy/progress-watcher.js" "$@" >/dev/null 2>&1 &
EOF
chmod +x "$HOME/.local/bin/xrpl-start-progress-watcher"

echo ""
echo "  ✅ Dependencies installed."
echo "  Wallets will be minted on first start..."
echo ""
