"""
Day 1 Solution: Send XRP
=========================
Send 10 XRP from wallet[0] to wallet[1] on XRPL Testnet.
"""

import json
from xrpl.clients import WebsocketClient
from xrpl.wallet import Wallet
from xrpl.models.transactions import Payment
from xrpl.transaction import submit_and_wait
from xrpl.utils import xrp_to_drops

TESTNET_URL = 'wss://s.altnet.rippletest.net:51233'

# Load wallets
with open('../../wallets.json') as f:
    data = json.load(f)
    wallets = data['wallets']

sender = Wallet.from_seed(wallets[0]['seed'])
receiver = Wallet.from_seed(wallets[1]['seed'])

print(f"Sender:   {sender.address}")
print(f"Receiver: {receiver.address}")

with WebsocketClient(TESTNET_URL) as client:

    # Create Payment transaction
    payment_tx = Payment(
        account=sender.address,
        amount=xrp_to_drops(10),
        destination=receiver.address
    )

    # Submit and print result
    result = submit_and_wait(payment_tx, client, sender)
    print(result)

# Explorer: https://testnet.xrpl.org/accounts/{address}
print(f"\nCheck sender:   https://testnet.xrpl.org/accounts/{sender.address}")
print(f"Check receiver: https://testnet.xrpl.org/accounts/{receiver.address}")
