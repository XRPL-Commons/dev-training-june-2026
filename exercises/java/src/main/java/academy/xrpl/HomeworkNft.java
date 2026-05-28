// Note: Verify xrpl4j API compatibility before running. See https://github.com/XRPLF/xrpl4j
package academy.xrpl;

import org.xrpl.xrpl4j.client.XrplClient;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoRequestParams;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoResult;
import org.xrpl.xrpl4j.model.client.fees.FeeResult;
import org.xrpl.xrpl4j.model.transactions.NfTokenMint;
import org.xrpl.xrpl4j.model.transactions.NfTokenUri;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.flags.NfTokenMintFlags;
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
 * Homework: Mint an NFT on the XRPL Testnet.
 *
 * Run: mvn compile exec:java -Dexec.mainClass="academy.xrpl.HomeworkNft"
 */
public class HomeworkNft {

    public static void main(String[] args) throws Exception {
        // Connect to XRPL Testnet
        XrplClient client = new XrplClient(HttpUrl.parse("https://s.altnet.rippletest.net:51234"));
        System.out.println("Connected to XRPL Testnet");

        // Load wallet
        ObjectMapper mapper = new ObjectMapper();
        JsonNode wallets = mapper.readTree(new File("../../wallets.json"));
        String seed = wallets.get("wallets").get(0).get("seed").asText();
        String address = wallets.get("wallets").get(0).get("address").asText();

        // Derive key pair
        KeyPair keyPair = Seed.fromBase58EncodedSecret(Base58EncodedSecret.of(seed)).deriveKeyPair();
        BcSignatureService signatureService = new BcSignatureService();

        // Get account info
        AccountInfoResult accountInfo = client.accountInfo(
                AccountInfoRequestParams.of(Address.of(address)));
        UnsignedInteger sequence = accountInfo.accountData().sequence();

        // Get fee
        FeeResult fee = client.fee();
        XrpCurrencyAmount openLedgerFee = fee.drops().openLedgerFee();

        System.out.println("Minting NFT from account: " + address);

        // ╔══════════════════════════════════════════════════════════════════╗
        // ║ TODO 1: Build an NfTokenMint transaction                        ║
        // ║                                                                  ║
        // ║ HINT: NfTokenMint.builder()                                      ║
        // ║         .account(Address.of(address))                            ║
        // ║         .uri(NfTokenUri.ofPlainText(???))                        ║
        // ║         .nfTokenTaxon(UnsignedInteger.ZERO)                      ║
        // ║         .flags(NfTokenMintFlags.builder()                        ║
        // ║             .tfTransferable(true).build())                       ║
        // ║         .fee(openLedgerFee)                                      ║
        // ║         .sequence(sequence)                                      ║
        // ║         .signingPublicKey(keyPair.publicKey())                   ║
        // ║         .build();                                                ║
        // ║                                                                  ║
        // ║ Use any URI string for the NFT metadata                          ║
        // ╚══════════════════════════════════════════════════════════════════╝
        NfTokenMint mint = null; // Replace this line

        // Sign and submit
        SingleSignedTransaction<NfTokenMint> signed = signatureService.sign(keyPair.privateKey(), mint);
        var result = client.submit(signed);
        System.out.println("Result: " + result.engineResult());
        System.out.println("TX Hash: " + signed.hash());
        System.out.println("NFT minted successfully!");
    }
}
