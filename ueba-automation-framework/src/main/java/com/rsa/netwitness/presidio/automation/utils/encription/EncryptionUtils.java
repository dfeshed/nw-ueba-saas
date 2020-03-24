package com.rsa.netwitness.presidio.automation.utils.encription;

import com.rsa.netwitness.presidio.automation.ssh.client.SshResponse;
import com.rsa.netwitness.presidio.automation.ssh.helper.SshHelper;
import org.apache.commons.net.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.concurrent.TimeUnit;

/**
 * Created by lirana on 17/05/2017.
 */
public class EncryptionUtils {

    private static String encryptionKey = "FortScale4Ever!!";

    // prevents initialization
    private EncryptionUtils() {}

    public static String encrypt(String plainText) throws Exception {
        Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());

        return Base64.encodeBase64String(encryptedBytes);
    }

    public static String decrypt(String encrypted, boolean useJar) throws Exception {
        if (useJar) {
            return byEncryptionUtilsJar("decrypt", encrypted);
        } else {
            return decrypt(encrypted);
        }
    }

    public static String decrypt(String encrypted) throws Exception {
        Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
        byte[] plainBytes = cipher.doFinal(Base64.decodeBase64(encrypted));

        return new String(plainBytes);
    }

    private static String byEncryptionUtilsJar(String jarParams, String encrypted) {
        String cmdPrefix = "java -jar /var/lib/netwitness/presidio/install/configserver/EncryptionUtils.jar ".concat(jarParams).concat(" ");
        String command = cmdPrefix.concat(encrypted);
        SshResponse p = new SshHelper().uebaHostExec().withTimeout(20, TimeUnit.SECONDS).run(command);
        p.output.forEach(System.out::println);
        String out = p.output.get(p.output.size()-1);
        System.out.println("Decrypted: " + out);
        return out;
    }

    private static Cipher getCipher(int cipherMode) throws Exception
    {
        String encryptionAlgorithm = "AES";
        SecretKeySpec keySpecification = new SecretKeySpec(
                encryptionKey.getBytes("UTF-8"), encryptionAlgorithm);
        Cipher cipher = Cipher.getInstance(encryptionAlgorithm);
        cipher.init(cipherMode, keySpecification);

        return cipher;
    }

    /**
     * Encrypt utility
     */
    public static void main(String[] args) throws Exception {
        if (args.length!=2) {
            System.out.println("Usage: <encrypt/decrypt> <value>");
            return;
        }

        String mode = args[0];
        String value = args[1];

        switch (mode) {
            case "encrypt":
                System.out.print(encrypt(value));
                break;
            case "decrypt":
                System.out.print(decrypt(value));
                break;
            default:
                System.out.println("Usage: <encrypt/decrypt> <value>");
                return;
        }
    }
}
