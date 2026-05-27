"""
XRPL Academy - Day 2: Tokens (IOUs) and NFTs (Python)
======================================================

WHAT YOU WILL LEARN:
- How to configure an account as a token issuer (Default Ripple)
- How to create a trust line (permission to hold a token)
- How to issue custom tokens
- How to mint an NFT with royalties

BACKGROUND - TOKENS ON XRPL:
Custom tokens on XRPL work differently from XRP (the native currency):
  1. An "issuer" account creates tokens by sending them via a Payment
  2. A "holder" must first create a "trust line" to the issuer
  3. The trust line declares: "I trust this issuer for up to X of this token"
  4. Without a trust line, the payment will fail

This trust-based model prevents token spam -- nobody can force tokens
into your account without your explicit permission.

BACKGROUND - NFTs ON XRPL:
XRPL has built-in NFT support (no smart contracts needed):
  - NFTokenMint creates a new NFT on the ledger
  - NFTs can have royalties (TransferFee) paid on every resale
  - NFTs can be transferable or non-transferable (controlled by Flags)
  - Metadata is stored off-chain, referenced by a URI

PREREQUISITES:
- Install dependencies: pip install xrpl-py
- Run "node scripts/mint-wallets.js" first to create wallets.json
- Complete Day 1 to understand basic transaction flow

RUN: python day2-tokens-nft.py
"""

import json
from xrpl.clients import WebsocketClient
from xrpl.wallet import Wallet
from xrpl.models.transactions import AccountSet, TrustSet, Payment, NFTokenMint
from xrpl.models.amounts import IssuedCurrencyAmount
from xrpl.models.requests import AccountNFTs
from xrpl.transaction import submit_and_wait
from xrpl.utils import str_to_hex

TESTNET_URL = "wss://s.altnet.rippletest.net:51233"

# ---------------------------------------------------------------------------
# STEP 1: Load wallets
# ---------------------------------------------------------------------------
# Wallet 1 = issuer (creates the token and mints the NFT)
# Wallet 2 = holder (receives the token)

with open("../wallets.json") as f:
    data = json.load(f)

issuer = Wallet.from_seed(data["wallets"][0]["seed"])
holder = Wallet.from_seed(data["wallets"][1]["seed"])

print("XRPL Academy - Day 2: Tokens and NFTs (Python)")
print("===============================================")
print("")
print(f"Issuer address: {issuer.address}")
print(f"Holder address: {holder.address}")

with WebsocketClient(TESTNET_URL) as client:
    print(f"Connected to: {TESTNET_URL}")

    # =======================================================================
    # PART 1: CREATE A CUSTOM TOKEN (IOU)
    # =======================================================================
    print("")
    print("--- PART 1: Creating a Custom Token ---")
    print("")

    # -----------------------------------------------------------------------
    # STEP 2: Enable Default Ripple on the issuer account
    # -----------------------------------------------------------------------
    # "Rippling" allows tokens to flow through accounts.
    # An issuer MUST enable Default Ripple so holders can transfer the token
    # to each other (not just back to the issuer).
    #
    # AccountSet with set_flag=8 enables asfDefaultRipple.
    # Flag value 8 = asfDefaultRipple in the XRPL protocol.

    print("Step 1: Enabling Default Ripple on issuer account...")
    print("  (Allows the token to be transferred between holders)")

    account_set_tx = AccountSet(
        account=issuer.address,
        set_flag=8,  # asfDefaultRipple
    )
    result = submit_and_wait(account_set_tx, client, issuer)
    print(f"  Result: {result.result['meta']['TransactionResult']}")

    # -----------------------------------------------------------------------
    # STEP 3: Create a Trust Line from holder to issuer
    # -----------------------------------------------------------------------
    # The holder declares: "I trust the issuer for up to 1,000,000 ACD tokens."
    # This is required before the issuer can send tokens to the holder.
    #
    # TrustSet requires a LimitAmount which specifies:
    #   - currency: 3-character code (e.g., "ACD")
    #   - issuer: the token issuer's address
    #   - value: maximum amount the holder will accept
    #
    # Creating a trust line locks 2 XRP as an "owner reserve" (returned if deleted).

    currency_code = "ACD"  # Change this to your own 3-letter token name

    print("")
    print(f"Step 2: Creating trust line (holder trusts issuer for {currency_code})...")
    print(f"  Limit: 1,000,000 {currency_code}")

    trust_set_tx = TrustSet(
        account=holder.address,
        limit_amount=IssuedCurrencyAmount(
            currency=currency_code,
            issuer=issuer.address,
            value="1000000",
        ),
    )
    result = submit_and_wait(trust_set_tx, client, holder)
    print(f"  Result: {result.result['meta']['TransactionResult']}")

    # -----------------------------------------------------------------------
    # STEP 4: Issue tokens (issuer sends tokens to holder)
    # -----------------------------------------------------------------------
    # Token issuance is simply a Payment from issuer to holder.
    # The issuer "creates" tokens by sending them. There is no separate mint step.
    #
    # For token payments, the amount is an IssuedCurrencyAmount object
    # (not a string of drops like XRP payments).

    print("")
    print(f"Step 3: Issuing 500 {currency_code} tokens to holder...")

    issue_tx = Payment(
        account=issuer.address,
        destination=holder.address,
        amount=IssuedCurrencyAmount(
            currency=currency_code,
            issuer=issuer.address,
            value="500",
        ),
    )
    result = submit_and_wait(issue_tx, client, issuer)
    print(f"  Result: {result.result['meta']['TransactionResult']}")
    print(f"  Holder now has 500 {currency_code}")
    print(f"  View: https://testnet.xrpl.org/accounts/{holder.address}")

    # =======================================================================
    # PART 2: MINT AN NFT
    # =======================================================================
    print("")
    print("--- PART 2: Minting an NFT ---")
    print("")

    # -----------------------------------------------------------------------
    # STEP 5: Mint an NFT using NFTokenMint
    # -----------------------------------------------------------------------
    # NFTokenMint fields:
    #   - nftoken_taxon: numeric category ID (0 for default/testing)
    #   - flags: controls behavior
    #       8 = tfTransferable (NFT can be sold/transferred)
    #   - transfer_fee: royalty in basis points (5000 = 5%)
    #       Creator earns this on every secondary sale
    #   - uri: hex-encoded link to metadata (image, description, etc.)
    #
    # str_to_hex() converts a regular string to hexadecimal encoding,
    # which is how XRPL stores URI data.

    metadata_uri = "https://xrpl.org/img/logo.svg"

    print("Step 4: Minting NFT...")
    print(f"  Minter     : {issuer.address}")
    print(f"  Taxon      : 0 (default collection)")
    print(f"  Transferable: yes")
    print(f"  Royalty    : 5% on every resale")
    print(f"  URI        : {metadata_uri}")

    nft_mint_tx = NFTokenMint(
        account=issuer.address,
        nftoken_taxon=0,
        flags=8,  # tfTransferable
        transfer_fee=5000,  # 5% royalty (basis points)
        uri=str_to_hex(metadata_uri),
    )
    result = submit_and_wait(nft_mint_tx, client, issuer)

    print("")
    print(f"  Result: {result.result['meta']['TransactionResult']}")
    print(f"  View NFTs: https://testnet.xrpl.org/accounts/{issuer.address}/nfts")

print("")
print("Day 2 complete.")
print("You have created a custom token and minted an NFT on the XRPL Testnet.")
