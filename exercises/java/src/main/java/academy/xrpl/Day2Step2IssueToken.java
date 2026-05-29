// Note: Verify xrpl4j API compatibility before running. See https://github.com/XRPLF/xrpl4j
package academy.xrpl;

import org.xrpl.xrpl4j.client.XrplClient;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoRequestParams;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoResult;
import org.xrpl.xrpl4j.model.client.fees.FeeResult;
import org.xrpl.xrpl4j.model.transactions.Payment;
import org.xrpl.xrpl4j.model.transactions.IssuedCurrencyAmount;
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
 * Day 2 Step 2: Issue a custom token by sending it from issuer to holder.
 * (Must run Day2Step1SetupAccount first to create the trust line)
 *
 * Reference materials:
 * - https://learn.xrpl-commons.org/course/code-with-the-xrpl/lesson/create-trustline-and-send-currency/
 * - https://xrpl.org/docs/references/protocol/transactions/types/trustset
 * - https://xrpl.org/docs/references/protocol/transactions/types/payment
 * - https://github.com/XRPLF/xrpl4j
 *
 * Run: mvn compile exec:java -Dexec.mainClass="academy.xrpl.Day2Step2IssueToken"
 */
public class Day2Step2IssueToken {

    public static void main(String[] args) throws Exception {
        // Connect to XRPL Testnet
        XrplClient client = new XrplClient(HttpUrl.parse("https://s.altnet.rippletest.net:51234"));
        System.out.println("Connected to XRPL Testnet");

        // Load wallets
        ObjectMapper mapper = new ObjectMapper();
        JsonNode wallets = mapper.readTree(new File("../../wallets.json"));

        // wallet1 = issuer, wallet2 = holder
        String issuerSeed = wallets.get("wallets").get(0).get("seed").asText();
        String issuerAddress = wallets.get("wallets").get(0).get("address").asText();
        String holderAddress = wallets.get("wallets").get(1).get("address").asText();

        // Derive issuer's key pair
        KeyPair keyPair = Seed.fromBase58EncodedSecret(Base58EncodedSecret.of(issuerSeed)).deriveKeyPair();
        BcSignatureService signatureService = new BcSignatureService();

        // Get account info for issuer
        AccountInfoResult accountInfo = client.accountInfo(
                AccountInfoRequestParams.of(Address.of(issuerAddress)));
        UnsignedInteger sequence = accountInfo.accountData().sequence();

        // Get fee
        FeeResult fee = client.fee();
        XrpCurrencyAmount openLedgerFee = fee.drops().openLedgerFee();

        String currencyCode = "USD";
        String amount = "100";
        System.out.println("Issuing " + amount + " " + currencyCode + " from " + issuerAddress + " to " + holderAddress);

        // ╔══════════════════════════════════════════════════════════════════╗
        // ║ TODO 1: Build a Payment transaction with an issued currency      ║
        // ║                                                                  ║
        // ║ HINT: Payment.builder()                                          ║
        // ║         .account(Address.of(issuerAddress))                      ║
        // ║         .destination(Address.of(???))                            ║
        // ║         .amount(IssuedCurrencyAmount.builder()                   ║
        // ║             .issuer(Address.of(???))                             ║
        // ║             .currency(???)                                       ║
        // ║             .value(???)                                          ║
        // ║             .build())                                            ║
        // ║         .fee(openLedgerFee)                                      ║
        // ║         .sequence(sequence)                                      ║
        // ║         .signingPublicKey(keyPair.publicKey())                   ║
        // ║         .build();                                                ║
        // ║                                                                  ║
        // ║ The issuer sends their OWN token to the holder                   ║
        // ╚══════════════════════════════════════════════════════════════════╝
        Payment payment = null; // Replace this line

        // Sign and submit
        SingleSignedTransaction<Payment> signed = signatureService.sign(keyPair.privateKey(), payment);
        var result = client.submit(signed);
        System.out.println("Result: " + result.engineResult());
        System.out.println("Token issued successfully!");
    }
}
