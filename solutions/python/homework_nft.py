"""
Homework Solution: NFT Mint, Query, and Burn
=============================================
Mint an NFT, query it, then burn it on XRPL Testnet.
"""

import json
from xrpl.clients import WebsocketClient
from xrpl.wallet import Wallet
from xrpl.models.transactions import NFTokenMint, NFTokenBurn
from xrpl.models.requests import AccountNFTs
from xrpl.transaction import submit_and_wait
from xrpl.utils import str_to_hex

TESTNET_URL = 'wss://s.altnet.rippletest.net:51233'

# Load wallets
with open('../../wallets.json') as f:
    data = json.load(f)
    wallets = data['wallets']

wallet = Wallet.from_seed(wallets[0]['seed'])
print(f"Wallet: {wallet.address}")

with WebsocketClient(TESTNET_URL) as client:

    # Mint NFT
    mint_tx = NFTokenMint(
        account=wallet.address,
        nftoken_taxon=0,
        flags=8,
        transfer_fee=5000,
        uri=str_to_hex('ipfs://YOUR_NAME_HERE')
    )
    mint_result = submit_and_wait(mint_tx, client, wallet)
    print(f"Mint result: {mint_result.result['meta']['TransactionResult']}")

    # Query NFTs
    nfts_response = client.request(AccountNFTs(account=wallet.address))
    nfts = nfts_response.result['account_nfts']
    print(f"NFTs owned: {len(nfts)}")
    nftoken_id = nfts[-1]['NFTokenID']
    print(f"NFToken ID: {nftoken_id}")

    # Burn NFT
    burn_tx = NFTokenBurn(
        account=wallet.address,
        nftoken_id=nftoken_id
    )
    burn_result = submit_and_wait(burn_tx, client, wallet)
    print(f"Burn result: {burn_result.result['meta']['TransactionResult']}")

    # Verify burned
    nfts_response = client.request(AccountNFTs(account=wallet.address))
    nfts = nfts_response.result['account_nfts']
    print(f"NFTs owned after burn: {len(nfts)}")

# Explorer: https://testnet.xrpl.org/accounts/{address}/nfts
print(f"\nExplorer: https://testnet.xrpl.org/accounts/{wallet.address}/nfts")
