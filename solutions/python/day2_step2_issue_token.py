"""
Day 2 Step 2 Solution: Issue Token
====================================
Create a TrustLine and issue a custom token on XRPL Testnet.
"""

import json
from xrpl.clients import WebsocketClient
from xrpl.wallet import Wallet
from xrpl.models.transactions import TrustSet, Payment
from xrpl.models.amounts import IssuedCurrencyAmount
from xrpl.transaction import submit_and_wait

TESTNET_URL = 'wss://s.altnet.rippletest.net:51233'

# Load wallets
with open('../../wallets.json') as f:
    data = json.load(f)
    wallets = data['wallets']

issuer = Wallet.from_seed(wallets[0]['seed'])
holder = Wallet.from_seed(wallets[1]['seed'])
currency_code = 'TST'

print(f"Issuer: {issuer.address}")
print(f"Holder: {holder.address}")

with WebsocketClient(TESTNET_URL) as client:

    # TrustSet from holder to issuer
    trust_tx = TrustSet(
        account=holder.address,
        limit_amount=IssuedCurrencyAmount(
            currency=currency_code,
            issuer=issuer.address,
            value='1000000'
        )
    )
    trust_result = submit_and_wait(trust_tx, client, holder)
    print(f"TrustSet: {trust_result.result['meta']['TransactionResult']}")

    # Issue tokens from issuer to holder
    payment_tx = Payment(
        account=issuer.address,
        destination=holder.address,
        amount=IssuedCurrencyAmount(
            currency=currency_code,
            issuer=issuer.address,
            value='500'
        )
    )
    payment_result = submit_and_wait(payment_tx, client, issuer)
    print(f"Payment: {payment_result.result['meta']['TransactionResult']}")

# Explorer: https://testnet.xrpl.org/accounts/{address}
print(f"\nExplorer (issuer): https://testnet.xrpl.org/accounts/{issuer.address}")
print(f"Explorer (holder): https://testnet.xrpl.org/accounts/{holder.address}")
