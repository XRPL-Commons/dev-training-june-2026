// Note: Verify xrpl4j API compatibility before running. See https://github.com/XRPLF/xrpl4j
package academy.xrpl;

import org.xrpl.xrpl4j.client.XrplClient;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoRequestParams;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoResult;
import org.xrpl.xrpl4j.model.client.fees.FeeResult;
import org.xrpl.xrpl4j.model.transactions.Payment;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.crypto.keys.Seed;
import org.xrpl.xrpl4j.crypto.keys.KeyPair;
import org.xrpl.xrpl4j.crypto.keys.Base58EncodedSecret;
import org.xrpl.xrpl4j.crypto.signing.SingleSignedTransaction;
import org.xrpl.xrpl4j.crypto.signing.bc.BcSignatureService;
import com.google.common.primitives.UnsignedInteger;
import okhttp3.HttpUrl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;

/**
 * Day 1 Solution: Send XRP from one account to another.
 *
 * Run: mvn compile exec:java -Dexec.mainClass="academy.xrpl.Day1SendXrp"
 */
public class Day1SendXrp {

    public static void main(String[] args) throws Exception {
        // Connect to XRPL Testnet
        XrplClient client = new XrplClient(HttpUrl.parse("https://s.altnet.rippletest.net:51234"));
        System.out.println("Connected to XRPL Testnet");

        // Load wallets from wallets.json
        ObjectMapper mapper = new ObjectMapper();
        JsonNode wallets = mapper.readTree(new File("../../wallets.json"));
        String senderSeed = wallets.get("wallets").get(0).get("seed").asText();
        String senderAddress = wallets.get("wallets").get(0).get("address").asText();
        String receiverAddress = wallets.get("wallets").get(1).get("address").asText();

        // Derive key pair from seed
        KeyPair keyPair = Seed.fromBase58EncodedSecret(Base58EncodedSecret.of(senderSeed)).deriveKeyPair();
        BcSignatureService signatureService = new BcSignatureService();

        // Get account info (for sequence number)
        AccountInfoResult accountInfo = client.accountInfo(
                AccountInfoRequestParams.of(Address.of(senderAddress)));
        UnsignedInteger sequence = accountInfo.accountData().sequence();

        // Get current network fee
        FeeResult fee = client.fee();
        XrpCurrencyAmount openLedgerFee = fee.drops().openLedgerFee();

        System.out.println("Sender: " + senderAddress);
        System.out.println("Receiver: " + receiverAddress);
        System.out.println("Sequence: " + sequence);

        // Build a Payment transaction to send 10 XRP
        Payment payment = Payment.builder()
                .account(Address.of(senderAddress))
                .destination(Address.of(receiverAddress))
                .amount(XrpCurrencyAmount.ofDrops(10000000)) // 10 XRP
                .fee(openLedgerFee)
                .sequence(sequence)
                .signingPublicKey(keyPair.publicKey())
                .build();

        // Sign the transaction
        SingleSignedTransaction<Payment> signed = signatureService.sign(keyPair.privateKey(), payment);
        System.out.println("Transaction signed");

        // Submit to the network
        var result = client.submit(signed);
        System.out.println("Result: " + result.engineResult());
        System.out.println("TX Hash: " + signed.hash());
    }
}
