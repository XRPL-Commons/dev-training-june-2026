"""
Day 2 Step 3 Solution: Create AMM
===================================
Create an AMM pool with a custom token and XRP on XRPL Testnet.
"""

import json
from xrpl.clients import WebsocketClient
from xrpl.wallet import Wallet
from xrpl.models.transactions import AMMCreate
from xrpl.models.amounts import IssuedCurrencyAmount
from xrpl.models.requests import AMMInfo
from xrpl.models.currencies import IssuedCurrency, XRP
from xrpl.transaction import submit_and_wait
from xrpl.utils import xrp_to_drops

TESTNET_URL = 'wss://s.altnet.rippletest.net:51233'

# Load wallets
with open('../../wallets.json') as f:
    data = json.load(f)
    wallets = data['wallets']

issuer = Wallet.from_seed(wallets[0]['seed'])
currency_code = 'TST'

print(f"Issuer: {issuer.address}")

with WebsocketClient(TESTNET_URL) as client:

    # Create AMM pool
    amm_tx = AMMCreate(
        account=issuer.address,
        amount=IssuedCurrencyAmount(
            currency=currency_code,
            issuer=issuer.address,
            value='100'
        ),
        amount2=xrp_to_drops(10),
        trading_fee=500
    )
    amm_result = submit_and_wait(amm_tx, client, issuer)
    print(f"AMMCreate: {amm_result.result['meta']['TransactionResult']}")

    # Query AMM info
    amm_info = client.request(AMMInfo(
        asset=IssuedCurrency(currency=currency_code, issuer=issuer.address),
        asset2=XRP()
    ))
    print(f"AMM Info: {amm_info.result}")

# Explorer: https://testnet.xrpl.org/accounts/{address}
print(f"\nExplorer: https://testnet.xrpl.org/accounts/{issuer.address}")
