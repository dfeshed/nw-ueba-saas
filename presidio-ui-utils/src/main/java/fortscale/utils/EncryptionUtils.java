package fortscale.utils;

import org.apache.commons.net.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Basic encryption utility, generally used to obfuscate configuration values.
 * This is not cryptographic encryption.
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

	public static String decrypt(String encrypted) throws Exception {
		Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
		byte[] plainBytes = cipher.doFinal(Base64.decodeBase64(encrypted));

		return new String(plainBytes);
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
