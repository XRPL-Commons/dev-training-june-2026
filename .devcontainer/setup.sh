#!/bin/bash
set -e

echo ""
echo "============================================"
echo "  XRPL Academy - Environment Setup"
echo "============================================"
echo ""

# -- JavaScript ---------------------------------------------------
echo "[1/4] Installing JavaScript dependencies..."
cd /workspaces/*/exercises/js 2>/dev/null || cd exercises/js
npm install --silent
cd ../..

# -- Python -------------------------------------------------------
echo "[2/4] Installing Python dependencies..."
pip install xrpl-py --quiet

# -- Java (Maven) -------------------------------------------------
echo "[3/4] Building Java project..."
if [ -d "exercises/java" ] && [ -f "exercises/java/pom.xml" ]; then
  cd exercises/java
  mvn compile -q -DskipTests 2>/dev/null || echo "  Note: Java build will complete on first run."
  cd ../..
else
  echo "  Skipping Java build."
fi

# -- Mint Testnet Wallets -----------------------------------------
echo "[4/4] Minting 2 Testnet wallets..."
echo ""
node scripts/mint-wallets.js

echo ""
echo "============================================"
echo "  ✅ Setup complete!"
echo ""
echo "  Your wallets: wallets.json"
echo "  Network: XRPL Testnet"
echo ""
echo "  Start here:"
echo "    JS:     cd exercises/js && node day1-send-xrp.js"
echo "    Python: cd exercises/python && python day1_send_xrp.py"
echo "    Java:   cd exercises/java && mvn compile exec:java -Dexec.mainClass=\"academy.xrpl.Day1SendXrp\""
echo "============================================"
echo ""
