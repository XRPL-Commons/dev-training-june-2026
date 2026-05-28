#!/bin/bash
set -e

echo ""
echo "============================================"
echo "  XRPL Academy - Installing Dependencies"
echo "============================================"
echo ""

# -- JavaScript ---------------------------------------------------
echo "[1/3] Installing JavaScript dependencies..."
cd /workspaces/*/exercises/js 2>/dev/null || cd exercises/js
npm install --silent
cd ../..

# -- Python -------------------------------------------------------
echo "[2/3] Installing Python dependencies..."
pip install xrpl-py --quiet

# -- Java (Maven) -------------------------------------------------
echo "[3/3] Building Java project..."
if [ -d "exercises/java" ] && [ -f "exercises/java/pom.xml" ]; then
  cd exercises/java
  mvn compile -q -DskipTests 2>/dev/null || echo "  Note: Java build will complete on first run."
  cd ../..
else
  echo "  Skipping Java build."
fi

echo ""
echo "  ✅ Dependencies installed."
echo "  Wallets will be minted on first start..."
echo ""
