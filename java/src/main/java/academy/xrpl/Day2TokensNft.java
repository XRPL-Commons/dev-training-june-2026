package academy.xrpl;

/**
 * XRPL Academy - Day 2: Tokens (IOUs) and NFTs (Java)
 * =====================================================
 *
 * WHAT YOU WILL LEARN:
 * - How to configure an account as a token issuer (AccountSet with Default Ripple)
 * - How to create a trust line (TrustSet transaction)
 * - How to issue custom tokens via Payment
 * - How to mint an NFT (NFTokenMint transaction)
 *
 * BACKGROUND - TOKENS:
 * On XRPL, custom tokens require a trust relationship:
 *   1. The issuer enables "Default Ripple" (allows tokens to flow between holders)
 *   2. The holder creates a "trust line" to the issuer (permission to hold the token)
 *   3. The issuer sends tokens via a Payment (this creates/issues the tokens)
 *
 * BACKGROUND - NFTs:
 * XRPL has native NFT support (no smart contracts). NFTokenMint creates an NFT with:
 *   - A taxon (collection/category ID)
 *   - Flags (e.g., transferable)
 *   - Transfer fee (royalty on resales)
 *   - URI (link to off-chain metadata)
 *
 * PREREQUISITES:
 * - Run "node scripts/mint-wallets.js" first
 * - Complete Day 1 to understand basic transaction flow
 *
 * RUN: mvn compile exec:java -Dexec.mainClass="academy.xrpl.Day2TokensNft"
 */

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.primitives.UnsignedInteger;
import okhttp3.HttpUrl;
import org.xrpl.xrpl4j.client.XrplClient;
import org.xrpl.xrpl4j.crypto.keys.Base58EncodedSecret;
import org.xrpl.xrpl4j.crypto.keys.KeyPair;
import org.xrpl.xrpl4j.crypto.keys.Seed;
import org.xrpl.xrpl4j.crypto.signing.SingleSignedTransaction;
import org.xrpl.xrpl4j.crypto.signing.bc.BcSignatureService;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoRequestParams;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoResult;
import org.xrpl.xrpl4j.model.client.common.LedgerSpecifier;
import org.xrpl.xrpl4j.model.client.transactions.SubmitResult;
import org.xrpl.xrpl4j.model.flags.AccountSetFlag;
import org.xrpl.xrpl4j.model.flags.NfTokenMintFlags;
import org.xrpl.xrpl4j.model.flags.TransactionFlags;
import org.xrpl.xrpl4j.model.transactions.*;

import java.io.File;

public class Day2TokensNft {

    private static final String TESTNET_URL = "https://s.altnet.rippletest.net:51234/";

    public static void main(String[] args) throws Exception {
        System.out.println("XRPL Academy - Day 2: Tokens and NFTs (Java)");
        System.out.println("=============================================");
        System.out.println();

        // ---------------------------------------------------------------------
        // STEP 1: Load wallets and connect
        // ---------------------------------------------------------------------
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File("wallets.json"));
        JsonNode wallets = root.get("wallets");

        // Wallet 1 = issuer (creates tokens and mints NFTs)
        // Wallet 2 = holder (receives tokens)
        KeyPair issuerKeyPair = Seed.fromBase58EncodedSecret(
            Base58EncodedSecret.of(wallets.get(0).get("seed").asText())
        ).deriveKeyPair();

        KeyPair holderKeyPair = Seed.fromBase58EncodedSecret(
            Base58EncodedSecret.of(wallets.get(1).get("seed").asText())
        ).deriveKeyPair();

        Address issuerAddress = issuerKeyPair.publicKey().deriveAddress();
        Address holderAddress = holderKeyPair.publicKey().deriveAddress();

        System.out.println("Issuer address: " + issuerAddress);
        System.out.println("Holder address: " + holderAddress);

        HttpUrl rippledUrl = HttpUrl.get(TESTNET_URL);
        XrplClient client = new XrplClient(rippledUrl);
        BcSignatureService signatureService = new BcSignatureService();

        System.out.println("Connected to: " + TESTNET_URL);

        // =====================================================================
        // PART 1: CREATE A CUSTOM TOKEN (IOU)
        // =====================================================================
        System.out.println();
        System.out.println("--- PART 1: Creating a Custom Token ---");
        System.out.println();

        // ---------------------------------------------------------------------
        // STEP 2: Enable Default Ripple on the issuer account
        // ---------------------------------------------------------------------
        // AccountSet modifies account-level settings.
        // asfDefaultRipple (flag 8) allows issued tokens to "ripple" through
        // the issuer's account, enabling holder-to-holder transfers.
        // Without this, holders could only send tokens back to the issuer.

        System.out.println("Step 1: Enabling Default Ripple on issuer account...");
        System.out.println("  (Allows tokens to be transferred between holders)");

        UnsignedInteger issuerSequence = getSequence(client, issuerAddress);

        AccountSet accountSet = AccountSet.builder()
            .account(issuerAddress)
            .fee(XrpCurrencyAmount.ofDrops(12L))
            .sequence(issuerSequence)
            .setFlag(AccountSetFlag.DEFAULT_RIPPLE)
            .signingPublicKey(issuerKeyPair.publicKey())
            .build();

        SingleSignedTransaction<AccountSet> signedAccountSet =
            signatureService.sign(issuerKeyPair.privateKey(), accountSet);
        SubmitResult<AccountSet> accountSetResult = client.submit(signedAccountSet);

        System.out.println("  Result: " + accountSetResult.engineResult());

        Thread.sleep(5000); // Wait for ledger validation

        // ---------------------------------------------------------------------
        // STEP 3: Create a Trust Line from holder to issuer
        // ---------------------------------------------------------------------
        // TrustSet creates a trust line -- the holder's declaration that they
        // are willing to hold up to a certain amount of the issuer's token.
        //
        // The LimitAmount specifies:
        //   - currency: 3-character code (e.g., "ACD")
        //   - issuer: who issues this token
        //   - value: maximum amount the holder will accept
        //
        // A trust line costs 2 XRP in owner reserve (locked, not spent).

        String currencyCode = "ACD"; // Change to your own 3-letter token name

        System.out.println();
        System.out.println("Step 2: Creating trust line (holder trusts issuer for " + currencyCode + ")...");
        System.out.println("  Limit: 1,000,000 " + currencyCode);

        UnsignedInteger holderSequence = getSequence(client, holderAddress);

        TrustSet trustSet = TrustSet.builder()
            .account(holderAddress)
            .fee(XrpCurrencyAmount.ofDrops(12L))
            .sequence(holderSequence)
            .limitAmount(IssuedCurrencyAmount.builder()
                .currency(currencyCode)
                .issuer(issuerAddress)
                .value("1000000")
                .build())
            .signingPublicKey(holderKeyPair.publicKey())
            .build();

        SingleSignedTransaction<TrustSet> signedTrustSet =
            signatureService.sign(holderKeyPair.privateKey(), trustSet);
        SubmitResult<TrustSet> trustSetResult = client.submit(signedTrustSet);

        System.out.println("  Result: " + trustSetResult.engineResult());

        Thread.sleep(5000);

        // ---------------------------------------------------------------------
        // STEP 4: Issue tokens (issuer sends tokens to holder)
        // ---------------------------------------------------------------------
        // Token issuance is a Payment from issuer to holder.
        // The Amount is an IssuedCurrencyAmount (not XRP drops).
        // The issuer "creates" tokens by sending them -- there is no separate mint.

        System.out.println();
        System.out.println("Step 3: Issuing 500 " + currencyCode + " tokens to holder...");

        UnsignedInteger issuerSequence2 = getSequence(client, issuerAddress);

        Payment tokenPayment = Payment.builder()
            .account(issuerAddress)
            .destination(holderAddress)
            .amount(IssuedCurrencyAmount.builder()
                .currency(currencyCode)
                .issuer(issuerAddress)
                .value("500")
                .build())
            .fee(XrpCurrencyAmount.ofDrops(12L))
            .sequence(issuerSequence2)
            .signingPublicKey(issuerKeyPair.publicKey())
            .build();

        SingleSignedTransaction<Payment> signedTokenPayment =
            signatureService.sign(issuerKeyPair.privateKey(), tokenPayment);
        SubmitResult<Payment> tokenResult = client.submit(signedTokenPayment);

        System.out.println("  Result: " + tokenResult.engineResult());
        System.out.println("  Holder now has 500 " + currencyCode);
        System.out.println("  View: https://testnet.xrpl.org/accounts/" + holderAddress);

        Thread.sleep(5000);

        // =====================================================================
        // PART 2: MINT AN NFT
        // =====================================================================
        System.out.println();
        System.out.println("--- PART 2: Minting an NFT ---");
        System.out.println();

        // ---------------------------------------------------------------------
        // STEP 5: Mint an NFT using NFTokenMint
        // ---------------------------------------------------------------------
        // NFTokenMint creates a non-fungible token on the ledger.
        //
        // Key fields:
        //   - nfTokenTaxon: category/collection ID (0 for testing)
        //   - flags: tfTransferable (allows the NFT to be sold/transferred)
        //   - transferFee: royalty in basis points (500 = 5%, range 0-50000)
        //   - uri: hex-encoded link to metadata
        //
        // The NFT belongs to the minting account until transferred.

        String metadataUri = "https://xrpl.org/img/logo.svg";
        String hexUri = stringToHex(metadataUri);

        System.out.println("Step 4: Minting NFT...");
        System.out.println("  Minter     : " + issuerAddress);
        System.out.println("  Taxon      : 0 (default collection)");
        System.out.println("  Transferable: yes");
        System.out.println("  Royalty    : 5% on every resale");
        System.out.println("  URI        : " + metadataUri);

        UnsignedInteger issuerSequence3 = getSequence(client, issuerAddress);

        NfTokenMint nftMint = NfTokenMint.builder()
            .account(issuerAddress)
            .fee(XrpCurrencyAmount.ofDrops(12L))
            .sequence(issuerSequence3)
            .nfTokenTaxon(NfTokenTaxon.of(UnsignedLong.ZERO))
            .flags(NfTokenMintFlags.TRANSFERABLE)
            .transferFee(TransferFee.of(UnsignedInteger.valueOf(5000))) // 5%
            .uri(NfTokenUri.of(hexUri))
            .signingPublicKey(issuerKeyPair.publicKey())
            .build();

        SingleSignedTransaction<NfTokenMint> signedNftMint =
            signatureService.sign(issuerKeyPair.privateKey(), nftMint);
        SubmitResult<NfTokenMint> nftResult = client.submit(signedNftMint);

        System.out.println();
        System.out.println("  Result: " + nftResult.engineResult());
        System.out.println("  View NFTs: https://testnet.xrpl.org/accounts/" + issuerAddress + "/nfts");

        System.out.println();
        System.out.println("Day 2 complete.");
        System.out.println("You have created a custom token and minted an NFT on the XRPL Testnet.");
    }

    /**
     * Helper: Get the current sequence number for an account.
     * The sequence number must be included in every transaction and increments
     * after each successful transaction.
     */
    private static UnsignedInteger getSequence(XrplClient client, Address address) throws Exception {
        AccountInfoResult info = client.accountInfo(
            AccountInfoRequestParams.builder()
                .account(address)
                .ledgerSpecifier(LedgerSpecifier.VALIDATED)
                .build()
        );
        return info.accountData().sequence();
    }

    /**
     * Helper: Convert a UTF-8 string to uppercase hexadecimal.
     * XRPL stores URI and memo data as hex-encoded strings.
     */
    private static String stringToHex(String input) {
        StringBuilder hex = new StringBuilder();
        for (byte b : input.getBytes(java.nio.charset.StandardCharsets.UTF_8)) {
            hex.append(String.format("%02X", b));
        }
        return hex.toString();
    }
}
