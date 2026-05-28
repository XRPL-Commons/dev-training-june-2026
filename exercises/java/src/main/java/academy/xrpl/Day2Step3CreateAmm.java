// Note: Verify xrpl4j API compatibility before running. See https://github.com/XRPLF/xrpl4j
package academy.xrpl;

import org.xrpl.xrpl4j.client.XrplClient;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoRequestParams;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoResult;
import org.xrpl.xrpl4j.model.client.fees.FeeResult;
import org.xrpl.xrpl4j.model.transactions.AmmCreate;
import org.xrpl.xrpl4j.model.transactions.IssuedCurrencyAmount;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.TradingFee;
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
 * Day 2 Step 3: Create an AMM pool with XRP and an issued token.
 * (Must run Step1 and Step2 first)
 *
 * Run: mvn compile exec:java -Dexec.mainClass="academy.xrpl.Day2Step3CreateAmm"
 */
public class Day2Step3CreateAmm {

    public static void main(String[] args) throws Exception {
        // Connect to XRPL Testnet
        XrplClient client = new XrplClient(HttpUrl.parse("https://s.altnet.rippletest.net:51234"));
        System.out.println("Connected to XRPL Testnet");

        // Load wallets
        ObjectMapper mapper = new ObjectMapper();
        JsonNode wallets = mapper.readTree(new File("../../wallets.json"));

        // wallet2 holds the issued token and will create the AMM
        String holderSeed = wallets.get("wallets").get(1).get("seed").asText();
        String holderAddress = wallets.get("wallets").get(1).get("address").asText();
        String issuerAddress = wallets.get("wallets").get(0).get("address").asText();

        // Derive key pair
        KeyPair keyPair = Seed.fromBase58EncodedSecret(Base58EncodedSecret.of(holderSeed)).deriveKeyPair();
        BcSignatureService signatureService = new BcSignatureService();

        // Get account info
        AccountInfoResult accountInfo = client.accountInfo(
                AccountInfoRequestParams.of(Address.of(holderAddress)));
        UnsignedInteger sequence = accountInfo.accountData().sequence();

        // Get fee
        FeeResult fee = client.fee();
        XrpCurrencyAmount openLedgerFee = fee.drops().openLedgerFee();

        String currencyCode = "USD";
        System.out.println("Creating AMM pool: XRP / " + currencyCode);
        System.out.println("Creator: " + holderAddress);

        // ╔══════════════════════════════════════════════════════════════════╗
        // ║ TODO 1: Build an AmmCreate transaction                          ║
        // ║                                                                  ║
        // ║ HINT: AmmCreate.builder()                                        ║
        // ║         .account(Address.of(holderAddress))                      ║
        // ║         .amount(XrpCurrencyAmount.ofDrops(???))                  ║
        // ║         .amount2(IssuedCurrencyAmount.builder()                  ║
        // ║             .issuer(Address.of(issuerAddress))                   ║
        // ║             .currency(???)                                       ║
        // ║             .value(???)                                          ║
        // ║             .build())                                            ║
        // ║         .tradingFee(TradingFee.of(UnsignedInteger.valueOf(500))) ║
        // ║         .fee(openLedgerFee)                                      ║
        // ║         .sequence(sequence)                                      ║
        // ║         .signingPublicKey(keyPair.publicKey())                   ║
        // ║         .build();                                                ║
        // ║                                                                  ║
        // ║ Use 10 XRP (10000000 drops) and 10 USD for the pool             ║
        // ║ Trading fee 500 = 0.5%                                           ║
        // ╚══════════════════════════════════════════════════════════════════╝
        AmmCreate ammCreate = null; // Replace this line

        // Sign and submit
        SingleSignedTransaction<AmmCreate> signed = signatureService.sign(keyPair.privateKey(), ammCreate);
        var result = client.submit(signed);
        System.out.println("Result: " + result.engineResult());
        System.out.println("AMM pool created successfully!");
    }
}
