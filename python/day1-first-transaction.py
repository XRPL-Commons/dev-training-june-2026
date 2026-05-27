"""
XRPL Academy - Day 1: Your First Transaction (Python)
======================================================

WHAT YOU WILL LEARN:
- How to connect to the XRPL network using the xrpl-py library
- How to reconstruct a wallet from a secret seed
- How to check an account's XRP balance
- How to build and submit a Payment transaction
- How to verify the result

BACKGROUND:
The xrpl-py library provides a Pythonic interface to the XRP Ledger.
It uses WebSocket connections (like the JavaScript version) and provides
typed transaction models for safety and autocompletion.

Key differences from the JavaScript version:
- Transactions are constructed as Python objects (e.g., Payment(...))
- The WebSocket client is used as a context manager (with ... as client)
- Amounts are always in "drops" (1 XRP = 1,000,000 drops)

PREREQUISITES:
- Install dependencies: pip install xrpl-py
- Run "node scripts/mint-wallets.js" first to create wallets.json

RUN: python day1-first-transaction.py
"""

import json
from xrpl.clients import WebsocketClient
from xrpl.wallet import Wallet
from xrpl.models.transactions import Payment
from xrpl.utils import xrp_to_drops
from xrpl.transaction import submit_and_wait
from xrpl.account import get_balance

# The Testnet WebSocket endpoint -- same network as the JavaScript exercises
TESTNET_URL = "wss://s.altnet.rippletest.net:51233"

# ---------------------------------------------------------------------------
# STEP 1: Load wallets from the JSON file
# ---------------------------------------------------------------------------
# The wallets.json file was created by mint-wallets.js.
# Each wallet has a "seed" (secret key) that we use to reconstruct the
# full wallet object including the address and key pair.

with open("../wallets.json") as f:
    data = json.load(f)

sender = Wallet.from_seed(data["wallets"][0]["seed"])
receiver = Wallet.from_seed(data["wallets"][1]["seed"])

print("XRPL Academy - Day 1: First Transaction (Python)")
print("=================================================")
print("")
print(f"Sender address  : {sender.address}")
print(f"Receiver address: {receiver.address}")

# ---------------------------------------------------------------------------
# STEP 2: Connect to the XRPL Testnet and execute the transaction
# ---------------------------------------------------------------------------
# WebsocketClient is used as a context manager. When the "with" block ends,
# the connection is automatically closed. This is the Pythonic equivalent
# of client.connect() / client.disconnect() in JavaScript.

with WebsocketClient(TESTNET_URL) as client:
    print("")
    print(f"Connected to XRPL Testnet ({TESTNET_URL})")

    # -----------------------------------------------------------------------
    # STEP 3: Check balance before the transaction
    # -----------------------------------------------------------------------
    # get_balance() returns the balance in "drops" (1 XRP = 1,000,000 drops).
    # We convert to XRP for readability.

    balance_before_drops = get_balance(sender.address, client)
    balance_before_xrp = int(balance_before_drops) / 1_000_000
    print(f"Sender balance before: {balance_before_xrp} XRP")

    # -----------------------------------------------------------------------
    # STEP 4: Construct the Payment transaction
    # -----------------------------------------------------------------------
    # In xrpl-py, transactions are typed Python objects.
    # Payment() requires:
    #   - account: the sender's address
    #   - amount: how much to send in drops (use xrp_to_drops() to convert)
    #   - destination: the recipient's address
    #
    # xrp_to_drops(10) converts 10 XRP to "10000000" (a string of drops).

    payment_tx = Payment(
        account=sender.address,
        amount=xrp_to_drops(10),  # 10 XRP = 10,000,000 drops
        destination=receiver.address,
    )

    print("")
    print("Submitting payment of 10 XRP...")
    print(f"  From: {sender.address}")
    print(f"  To  : {receiver.address}")

    # -----------------------------------------------------------------------
    # STEP 5: Sign and submit the transaction
    # -----------------------------------------------------------------------
    # submit_and_wait() handles:
    #   1. Auto-filling missing fields (Sequence, Fee, LastLedgerSequence)
    #   2. Signing with the sender's private key
    #   3. Submitting to the network
    #   4. Waiting for the transaction to be validated (3-5 seconds)

    result = submit_and_wait(payment_tx, client, sender)

    # -----------------------------------------------------------------------
    # STEP 6: Check the result
    # -----------------------------------------------------------------------
    # The result code "tesSUCCESS" means the payment was successful.
    # The hash is a unique identifier for looking up the transaction.

    tx_result = result.result["meta"]["TransactionResult"]
    tx_hash = result.result["hash"]

    print("")
    print(f"Transaction result: {tx_result}")
    print(f"Transaction hash  : {tx_hash}")
    print(f"Explorer link     : https://testnet.xrpl.org/transactions/{tx_hash}")

    # -----------------------------------------------------------------------
    # STEP 7: Verify the balance changed
    # -----------------------------------------------------------------------
    balance_after_drops = get_balance(sender.address, client)
    balance_after_xrp = int(balance_after_drops) / 1_000_000

    print("")
    print(f"Sender balance after: {balance_after_xrp} XRP")
    print(f"Difference: {balance_before_xrp - balance_after_xrp:.6f} XRP (payment + fee)")

print("")
print("Disconnected. Day 1 complete.")
