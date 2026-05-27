#!/bin/bash
set -e

echo ""
echo "============================================"
echo "  XRPL Academy - Environment Setup"
echo "============================================"
echo ""

# -- JavaScript / TypeScript ---------------------------------------------------
echo "[1/4] Installing JavaScript dependencies..."
cd /workspaces/*/js 2>/dev/null || cd js
npm install --silent
cd ..

# -- Python --------------------------------------------------------------------
echo "[2/4] Installing Python dependencies..."
pip install xrpl-py --quiet

# -- Java (Maven) --------------------------------------------------------------
echo "[3/4] Building Java project (downloading xrpl4j dependencies)..."
if [ -d "java" ] && [ -f "java/pom.xml" ]; then
  cd java
  mvn compile -q -DskipTests 2>/dev/null || echo "  Note: Java build will complete on first run if Maven is still downloading."
  cd ..
else
  echo "  Skipping Java build (java/pom.xml not found)."
fi

# -- Mint Testnet Wallets ------------------------------------------------------
echo "[4/4] Minting 2 Testnet wallets (free test XRP from the faucet)..."
echo ""
node scripts/mint-wallets.js

echo ""
echo "============================================"
echo "  Setup complete."
echo "  Your wallets are saved in: wallets.json"
echo "  Network: XRPL Testnet"
echo "  Start with Day 1 in your preferred language."
echo "============================================"
echo ""
