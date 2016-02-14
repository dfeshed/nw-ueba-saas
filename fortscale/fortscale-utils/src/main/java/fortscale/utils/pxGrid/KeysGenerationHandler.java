package fortscale.utils.pxGrid;

import org.apache.commons.codec.binary.Base64;
import sun.misc.BASE64Decoder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.Map;

/**
 * Created by tomerd on 31/01/2016.
 */
public class KeysGenerationHandler {

	private final static String SELF_SIGNED_CERT_NAME = "pxGridClient.cer";
	private final static String ISE_IDENTITY_NAME = "isemnt.pem";
	private final static String PRIVATE_KEY_NAME = "pxGridClient.jks";
	private final static String TRUST_KEYSTORE_NAME = "root.jks";

	public String generateKeySelfSignedCert() throws IOException, InterruptedException {
		generatePrivateKey();
		generateCSRrequest();
		return generateSelfSignedCert();
	}

	public Map.Entry<String, String> generateKeys(String password, String base64PemFile) throws IOException, InterruptedException {
		generatePKCS12(password);
		importIntoIdentityKeystore(password);
		saveKey(base64PemFile, ISE_IDENTITY_NAME);
		convertPemToDer();
		addISEIdentityCertToIdentityKeystore(password);
		importPxGridClientCertToIdentityKeystore(password);
		importIseIdentityCertToTrustKeystore(password);

		String privateKey  = readFileToBase64(PRIVATE_KEY_NAME);
		String trustKeystore = readFileToBase64(TRUST_KEYSTORE_NAME);

		return new AbstractMap.SimpleEntry<String, String>(privateKey, trustKeystore);
	}

	protected void generatePrivateKey() throws IOException, InterruptedException {
		String command = "openssl genrsa -out pxGridClient.key 4096";
		executeCommand(command);
	}

	protected void generateCSRrequest() throws IOException, InterruptedException {
		String command = "openssl req -new -batch -key pxGridClient.key  -out pxGridClient.csr";
		executeCommand(command);
	}

	protected String generateSelfSignedCert() throws IOException, InterruptedException {
		String command = String.format("openssl req -x509 -days 365 -key pxGridClient.key -in pxGridClient.csr -out %s", SELF_SIGNED_CERT_NAME);
		executeCommand(command);
		return readFileToBase64(SELF_SIGNED_CERT_NAME);
	}

	protected void generatePKCS12(String password) throws IOException, InterruptedException {
		password = "pass:" + password;
		String command = String.format("openssl pkcs12 -export -password %s -out pxGridClient.p12 -inkey pxGridClient.key -in pxGridClient.cer",
				password);
		executeCommand(command);
	}

	protected void importIntoIdentityKeystore(String password) throws IOException, InterruptedException {
		String command = String.format("keytool -importkeystore -noprompt -srckeystore pxGridClient.p12 -destkeystore pxGridClient.jks -srcstoretype PKCS12 -storepass %s -srckeypass %s -srcstorepass %s -alias pxGridclient",
				password, password, password);
		executeCommand(command);
	}

	protected void convertPemToDer() throws IOException, InterruptedException {
		String command = "openssl x509 -outform der -in isemnt.pem -out isemnt.der";
		executeCommand(command);
	}

	protected void addISEIdentityCertToIdentityKeystore(String password) throws IOException, InterruptedException {
		String command = String.format("keytool -import -noprompt -alias pxGridclient -keystore pxGridClient.jks -file isemnt.der -storepass %s",
				password);
		executeCommand(command);
	}

	protected void importPxGridClientCertToIdentityKeystore(String password) throws IOException, InterruptedException {
		String command = String.format("keytool -import -noprompt -alias pxGridclient -keystore pxGridClient.jks -file pxGridClient.cer -storepass %s",
				password);
		executeCommand(command);
	}

	protected void importIseIdentityCertToTrustKeystore(String password) throws IOException, InterruptedException {
		String command = String.format("keytool -import -noprompt -alias root1 -keystore root.jks -file isemnt.der -storepass %s",
				password);
		executeCommand(command);
	}


	private void executeCommand(String command) throws InterruptedException, IOException {
		Runtime r = Runtime.getRuntime();
		Process p = r.exec(command);
		p.waitFor();
	}

	private String readFileToBase64(String path) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		encoded = Base64.encodeBase64(encoded);
		return new String(encoded, StandardCharsets.UTF_8);
	}

	private void saveKey(String base64Key, String fileName) throws IOException {
		byte[] keyBytes;

		BASE64Decoder decoder = new BASE64Decoder();
		keyBytes = decoder.decodeBuffer(base64Key);
		try (OutputStream stream = new FileOutputStream(fileName)) {
			stream.write(keyBytes);
		}
	}
}
