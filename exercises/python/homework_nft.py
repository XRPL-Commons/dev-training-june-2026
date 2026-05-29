"""
Homework Exercise: NFT Mint, Query, and Burn
=============================================
Mint an NFT, query it, then burn it on XRPL Testnet.

Reference materials:
- https://learn.xrpl-commons.org/course/code-with-the-xrpl/lesson/mint-and-burn-nfts/
- https://xrpl.org/docs/references/protocol/transactions/types/nftokenmint
- https://xrpl.org/docs/references/protocol/transactions/types/nftokenburn
- https://xrpl.org/docs/references/http-websocket-apis/public-api-methods/account-methods/account_nfts
- https://xrpl-py.readthedocs.io/en/stable/
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

    # ╔══════════════════════════════════════════════════════════════════╗
    # ║ TODO 1: Mint an NFT                                             ║
    # ║                                                                  ║
    # ║ HINT: NFTokenMint(account=wallet.address, nftoken_taxon=0,       ║
    # ║       flags=8, transfer_fee=5000,                                ║
    # ║       uri=str_to_hex('ipfs://YOUR_NAME_HERE'))                   ║
    # ╚══════════════════════════════════════════════════════════════════╝
    mint_tx = None  # Replace with NFTokenMint(...)
    # mint_result = submit_and_wait(mint_tx, client, wallet)

    # ╔══════════════════════════════════════════════════════════════════╗
    # ║ TODO 2: Query NFTs to get the nftoken_id                        ║
    # ║                                                                  ║
    # ║ HINT: nfts_response = client.request(                            ║
    # ║           AccountNFTs(account=wallet.address))                    ║
    # ║       nftoken_id = nfts_response.result['account_nfts'][-1]      ║
    # ║                    ['NFTokenID']                                  ║
    # ╚══════════════════════════════════════════════════════════════════╝
    nftoken_id = None  # Replace with query logic

    # ╔══════════════════════════════════════════════════════════════════╗
    # ║ TODO 3: Burn the NFT                                            ║
    # ║                                                                  ║
    # ║ HINT: NFTokenBurn(account=wallet.address,                        ║
    # ║                   nftoken_id=nftoken_id)                         ║
    # ╚══════════════════════════════════════════════════════════════════╝
    burn_tx = None  # Replace with NFTokenBurn(...)
    # burn_result = submit_and_wait(burn_tx, client, wallet)

    # ╔══════════════════════════════════════════════════════════════════╗
    # ║ TODO 4: Query NFTs again to verify the NFT is gone              ║
    # ║                                                                  ║
    # ║ HINT: Same as TODO 2 - check the count                          ║
    # ╚══════════════════════════════════════════════════════════════════╝
    pass

# Explorer: https://testnet.xrpl.org/accounts/{address}/nfts
print(f"\nExplorer: https://testnet.xrpl.org/accounts/{wallet.address}/nfts")
