package fortscale.utils;

import kms.KmsEncryptionConfiguration;
import kms.KmsTextEncryptor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

 /*
 * Basic encryption utility, generally used to obfuscate configuration values.
 * This is not cryptographic encryption.
 */
public class EncryptionUtils {

    private EncryptionUtils() {
    }

    public static String decrypt(String encrypted) throws Exception {
        ApplicationContext context = new AnnotationConfigApplicationContext(KmsEncryptionConfiguration.class);
        KmsTextEncryptor kmsTextEncryptor = context.getBean(KmsTextEncryptor.class);
        return  kmsTextEncryptor.decrypt(encrypted);
    }

    /**
     * Encrypt utility
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Usage: <decrypt> <value>");
            return;
        }

        String mode = args[0];
        String value = args[1];

        switch (mode) {
            case "decrypt":
                String decrypt = decrypt(value);
                System.out.print("password1: "+ decrypt);
                System.out.print("password: "+ StringUtils.chomp(decrypt));
                break;
            default:
                System.out.println("Usage: <decrypt> <value>");
                return;
        }

    }


}
