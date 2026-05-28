"""
Day 2 Step 1 Solution: Setup Account
=====================================
Enable Default Ripple flag on the issuer account.
"""

import json
from xrpl.clients import WebsocketClient
from xrpl.wallet import Wallet
from xrpl.models.transactions import AccountSet
from xrpl.transaction import submit_and_wait

TESTNET_URL = 'wss://s.altnet.rippletest.net:51233'

# Load wallets
with open('../../wallets.json') as f:
    data = json.load(f)
    wallets = data['wallets']

issuer = Wallet.from_seed(wallets[0]['seed'])
print(f"Issuer: {issuer.address}")

with WebsocketClient(TESTNET_URL) as client:

    # Enable Default Ripple
    account_set_tx = AccountSet(
        account=issuer.address,
        set_flag=8
    )
    result = submit_and_wait(account_set_tx, client, issuer)
    print(f"Result: {result.result['meta']['TransactionResult']}")

# Explorer: https://testnet.xrpl.org/accounts/{address}
print(f"\nExplorer: https://testnet.xrpl.org/accounts/{issuer.address}")
