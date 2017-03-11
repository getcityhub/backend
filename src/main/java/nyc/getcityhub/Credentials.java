package nyc.getcityhub;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by v-jacco on 3/11/17.
 */
public class Credentials {

    // The password protecting the SSL keystore
    public static String SSL_KEYPASS;

    public static void loadCredentials() {
        try {
            String[] keys = new String(Files.readAllBytes(Paths.get("keys.txt"))).split("\n");

            for (String secretStuff : keys) {
                String key = secretStuff.split("=")[0];
                String value = secretStuff.split("=")[1];

                switch (key) {
                    case "AWS_ACCESS_KEY_ID":
                        System.setProperty("aws.accessKeyId", value);
                        break;
                    case "AWS_SECRET_ACCESS_KEY":
                        System.setProperty("aws.secretKey", value);
                        break;
                    case "SSL_KEYPASS":
                        SSL_KEYPASS = value;
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading credentials, exiting now...");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
