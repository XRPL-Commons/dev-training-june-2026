package academy.xrpl;

/**
 * XRPL Academy - Day 1: Your First Transaction (Java)
 * =====================================================
 *
 * WHAT YOU WILL LEARN:
 * - How to connect to the XRPL network using xrpl4j (JSON-RPC client)
 * - How to reconstruct a wallet from a secret seed
 * - How to check an account's XRP balance
 * - How to construct, sign, and submit a Payment transaction
 * - How to verify the result
 *
 * BACKGROUND:
 * xrpl4j uses JSON-RPC (HTTP) to communicate with XRPL nodes, unlike the
 * JavaScript and Python libraries which use WebSocket. The functionality is
 * identical -- both connect to the same XRPL nodes and submit the same transactions.
 *
 * xrpl4j uses immutable objects (via the Immutables library) for type safety.
 * Transaction objects are built using the builder pattern: Payment.builder()...build()
 *
 * PREREQUISITES:
 * - Run "node scripts/mint-wallets.js" first to create wallets.json
 * - Build with: mvn compile
 *
 * RUN: mvn compile exec:java -Dexec.mainClass="academy.xrpl.Day1FirstTransaction"
 */

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import org.xrpl.xrpl4j.client.XrplClient;
import org.xrpl.xrpl4j.client.faucet.FaucetClient;
import org.xrpl.xrpl4j.crypto.keys.Seed;
import org.xrpl.xrpl4j.crypto.keys.KeyPair;
import org.xrpl.xrpl4j.crypto.signing.SingleSignedTransaction;
import org.xrpl.xrpl4j.crypto.signing.bc.BcSignatureService;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoRequestParams;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoResult;
import org.xrpl.xrpl4j.model.client.common.LedgerSpecifier;
import org.xrpl.xrpl4j.model.client.transactions.SubmitResult;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.Payment;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;
import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;

import java.io.File;
import java.math.BigDecimal;

public class Day1FirstTransaction {

    // XRPL Testnet JSON-RPC endpoint (HTTP, not WebSocket)
    // xrpl4j uses JSON-RPC which is equivalent to WebSocket for submitting transactions
    private static final String TESTNET_URL = "https://s.altnet.rippletest.net:51234/";

    public static void main(String[] args) throws Exception {
        System.out.println("XRPL Academy - Day 1: First Transaction (Java)");
        System.out.println("===============================================");
        System.out.println();

        // ---------------------------------------------------------------------
        // STEP 1: Load wallets from wallets.json
        // ---------------------------------------------------------------------
        // The wallets.json file was created by the mint-wallets.js script.
        // We read the seed (secret key) for each wallet and reconstruct the
        // key pair from it. In xrpl4j, Seed.fromBase58EncodedSecret() decodes
        // the seed string, and deriveKeyPair() generates the full key pair.

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File("wallets.json"));
        JsonNode wallets = root.get("wallets");

        String senderSeed = wallets.get(0).get("seed").asText();
        String receiverAddress = wallets.get(1).get("address").asText();

        // Reconstruct the sender's key pair from the seed.
        // The seed encodes both the key type (ed25519 or secp256k1) and the entropy.
        KeyPair senderKeyPair = Seed.fromBase58EncodedSecret(
            org.xrpl.xrpl4j.crypto.keys.Base58EncodedSecret.of(senderSeed)
        ).deriveKeyPair();

        Address senderAddress = senderKeyPair.publicKey().deriveAddress();
        Address destination = Address.of(receiverAddress);

        System.out.println("Sender address  : " + senderAddress);
        System.out.println("Receiver address: " + destination);

        // ---------------------------------------------------------------------
        // STEP 2: Connect to the XRPL Testnet
        // ---------------------------------------------------------------------
        // XrplClient communicates via JSON-RPC over HTTP.
        // Unlike WebSocket, each request is independent (no persistent connection).
        // The client handles serialization/deserialization of XRPL protocol messages.

        HttpUrl rippledUrl = HttpUrl.get(TESTNET_URL);
        XrplClient client = new XrplClient(rippledUrl);

        System.out.println();
        System.out.println("Connected to XRPL Testnet (" + TESTNET_URL + ")");

        // ---------------------------------------------------------------------
        // STEP 3: Look up the sender's account info (includes balance and sequence)
        // ---------------------------------------------------------------------
        // AccountInfo gives us:
        //   - balance: current XRP in drops (1 XRP = 1,000,000 drops)
        //   - sequence: the next valid transaction sequence number
        //
        // The sequence number is critical -- every transaction must include the
        // correct sequence number or it will be rejected. It increments by 1
        // after each successful transaction.

        AccountInfoRequestParams requestParams = AccountInfoRequestParams.builder()
            .account(senderAddress)
            .ledgerSpecifier(LedgerSpecifier.VALIDATED)
            .build();

        AccountInfoResult accountInfo = client.accountInfo(requestParams);
        UnsignedInteger sequence = accountInfo.accountData().sequence();
        XrpCurrencyAmount balanceBefore = accountInfo.accountData().balance();

        System.out.println("Sender balance before: " + dropsToXrp(balanceBefore) + " XRP");
        System.out.println("Account sequence     : " + sequence);

        // ---------------------------------------------------------------------
        // STEP 4: Construct the Payment transaction
        // ---------------------------------------------------------------------
        // Payment.builder() creates an immutable Payment object.
        //
        // Required fields:
        //   - account: sender's address
        //   - destination: recipient's address
        //   - amount: XRP amount in drops (XrpCurrencyAmount.ofDrops)
        //   - fee: transaction cost in drops (typically 12 drops = 0.000012 XRP)
        //   - sequence: must match the account's current sequence number
        //   - signingPublicKey: the sender's public key (for verification)
        //
        // 10 XRP = 10,000,000 drops

        XrpCurrencyAmount tenXrp = XrpCurrencyAmount.ofDrops(10_000_000L);
        XrpCurrencyAmount fee = XrpCurrencyAmount.ofDrops(12L);

        Payment payment = Payment.builder()
            .account(senderAddress)
            .destination(destination)
            .amount(tenXrp)
            .fee(fee)
            .sequence(sequence)
            .signingPublicKey(senderKeyPair.publicKey())
            .build();

        System.out.println();
        System.out.println("Submitting payment of 10 XRP...");
        System.out.println("  From: " + senderAddress);
        System.out.println("  To  : " + destination);
        System.out.println("  Fee : 0.000012 XRP (12 drops)");

        // ---------------------------------------------------------------------
        // STEP 5: Sign the transaction
        // ---------------------------------------------------------------------
        // Signing proves that the account owner authorized this transaction.
        // BcSignatureService uses Bouncy Castle (a Java cryptography library)
        // to perform the cryptographic signing.
        //
        // The signed transaction includes the original transaction plus a
        // cryptographic signature that anyone can verify using the public key.

        BcSignatureService signatureService = new BcSignatureService();
        SingleSignedTransaction<Payment> signedPayment = signatureService.sign(
            senderKeyPair.privateKey(), payment
        );

        // ---------------------------------------------------------------------
        // STEP 6: Submit the signed transaction to the network
        // ---------------------------------------------------------------------
        // submit() sends the signed transaction to the connected XRPL node.
        // The node validates the signature and format, then proposes it to
        // the network for consensus.
        //
        // The result contains an "engineResult" indicating whether the
        // transaction was accepted into the queue (preliminary result).

        SubmitResult<Payment> result = client.submit(signedPayment);

        String engineResult = result.engineResult();
        System.out.println();
        System.out.println("Transaction result: " + engineResult);
        System.out.println("  (tesSUCCESS = payment accepted and will be validated)");

        // Note: In production, you would poll for the validated transaction
        // to confirm it was included in a closed ledger. For this exercise,
        // tesSUCCESS from submit is sufficient confirmation on Testnet.

        // ---------------------------------------------------------------------
        // STEP 7: Verify the balance changed
        // ---------------------------------------------------------------------
        // Wait briefly for the ledger to close, then check the new balance.

        Thread.sleep(5000); // Wait for ledger close (3-5 seconds)

        AccountInfoResult accountInfoAfter = client.accountInfo(
            AccountInfoRequestParams.builder()
                .account(senderAddress)
                .ledgerSpecifier(LedgerSpecifier.VALIDATED)
                .build()
        );
        XrpCurrencyAmount balanceAfter = accountInfoAfter.accountData().balance();

        System.out.println();
        System.out.println("Sender balance after: " + dropsToXrp(balanceAfter) + " XRP");
        System.out.println();
        System.out.println("Day 1 complete.");
    }

    /**
     * Converts an XrpCurrencyAmount (in drops) to a human-readable XRP string.
     * 1 XRP = 1,000,000 drops.
     */
    private static String dropsToXrp(XrpCurrencyAmount drops) {
        BigDecimal xrp = new BigDecimal(drops.value().toString())
            .divide(BigDecimal.valueOf(1_000_000));
        return xrp.toPlainString();
    }
}
