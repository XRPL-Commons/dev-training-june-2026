// Note: Verify xrpl4j API compatibility before running. See https://github.com/XRPLF/xrpl4j
package academy.xrpl;

import org.xrpl.xrpl4j.client.XrplClient;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoRequestParams;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoResult;
import org.xrpl.xrpl4j.model.client.fees.FeeResult;
import org.xrpl.xrpl4j.model.transactions.TrustSet;
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
 * Day 2 Step 1: Set up a trust line so wallet2 can hold tokens issued by wallet1.
 *
 * Run: mvn compile exec:java -Dexec.mainClass="academy.xrpl.Day2Step1SetupAccount"
 */
public class Day2Step1SetupAccount {

    public static void main(String[] args) throws Exception {
        // Connect to XRPL Testnet
        XrplClient client = new XrplClient(HttpUrl.parse("https://s.altnet.rippletest.net:51234"));
        System.out.println("Connected to XRPL Testnet");

        // Load wallets
        ObjectMapper mapper = new ObjectMapper();
        JsonNode wallets = mapper.readTree(new File("../../wallets.json"));

        // wallet1 = issuer, wallet2 = holder (needs trust line)
        String issuerAddress = wallets.get("wallets").get(0).get("address").asText();
        String holderSeed = wallets.get("wallets").get(1).get("seed").asText();
        String holderAddress = wallets.get("wallets").get(1).get("address").asText();

        // Derive holder's key pair
        KeyPair keyPair = Seed.fromBase58EncodedSecret(Base58EncodedSecret.of(holderSeed)).deriveKeyPair();
        BcSignatureService signatureService = new BcSignatureService();

        // Get account info for holder
        AccountInfoResult accountInfo = client.accountInfo(
                AccountInfoRequestParams.of(Address.of(holderAddress)));
        UnsignedInteger sequence = accountInfo.accountData().sequence();

        // Get fee
        FeeResult fee = client.fee();
        XrpCurrencyAmount openLedgerFee = fee.drops().openLedgerFee();

        String currencyCode = "USD";
        System.out.println("Setting up trust line: " + holderAddress + " trusts " + issuerAddress + " for " + currencyCode);

        // ╔══════════════════════════════════════════════════════════════════╗
        // ║ TODO 1: Build a TrustSet transaction                            ║
        // ║                                                                  ║
        // ║ HINT: TrustSet.builder()                                         ║
        // ║         .account(Address.of(holderAddress))                      ║
        // ║         .limitAmount(IssuedCurrencyAmount.builder()              ║
        // ║             .issuer(Address.of(???))                             ║
        // ║             .currency(???)                                       ║
        // ║             .value(???)                                          ║
        // ║             .build())                                            ║
        // ║         .fee(openLedgerFee)                                      ║
        // ║         .sequence(sequence)                                      ║
        // ║         .signingPublicKey(keyPair.publicKey())                   ║
        // ║         .build();                                                ║
        // ║                                                                  ║
        // ║ Set a limit of "1000000" for the trust line                      ║
        // ╚══════════════════════════════════════════════════════════════════╝
        TrustSet trustSet = null; // Replace this line

        // Sign and submit
        SingleSignedTransaction<TrustSet> signed = signatureService.sign(keyPair.privateKey(), trustSet);
        var result = client.submit(signed);
        System.out.println("Result: " + result.engineResult());
        System.out.println("Trust line created successfully!");
    }
}
