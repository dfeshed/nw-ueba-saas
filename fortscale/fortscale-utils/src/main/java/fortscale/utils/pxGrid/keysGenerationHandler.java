package fortscale.utils.pxGrid;

import org.apache.commons.codec.binary.Base64;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by tomerd on 31/01/2016.
 */
public class KeysGenerationHandler {

	private final static int KEY_SIZE = 4096;
	private final static String PRIVATE_KEY_NAME = "self1.key";
	private final static String SELF_SIGNED_CSR_NAME = "self1.csr";
	private final static String SELF_SIGNED_CERT_NAME = "pxGridClient.cer";

	public String generateKeySelfSignedCert() throws IOException, InterruptedException {
		generatePrivateKey();
		generateCSRrequest();
		return generateSelfSignedCert();
	}

	protected void generatePrivateKey() throws IOException, InterruptedException {
		String command = "openssl genrsa -out self1.key 4096";
		executeCommand(command);
	}

	protected void generateCSRrequest() throws IOException, InterruptedException {
		String command = "openssl req -new -batch -key pxGridClient.key  -out pxGridClient.csr";
		executeCommand(command);
	}

	protected String generateSelfSignedCert() throws IOException, InterruptedException {
		String command = String.format("openssl req -x509 -days 365 -key pxGridClient.key -in pxGridClient.csr -out {}", SELF_SIGNED_CERT_NAME);
		executeCommand(command);
		return readFileToBase64(SELF_SIGNED_CERT_NAME);
	}

	protected void generatePKCS12() throws IOException, InterruptedException {
		String command = "openssl pkcs12 -export -password pass:P@ssw0rd -out pxGridClient.p12 -inkey pxGridClient.key -in pxGridClient.cer";
		executeCommand(command);
	}

	protected void importIntoIdentityKeystore() throws IOException, InterruptedException {
		String command = "keytool -importkeystore -noprompt -srckeystore pxGridClient.p12 -destkeystore pxGridClient.jks -srcstoretype PKCS12 -storepass P@ssw0rd -srckeypass P@ssw0rd -srcstorepass P@ssw0rd -alias 1";
		executeCommand(command);
	}

	protected void convertPemToDer() throws IOException, InterruptedException {
		String command = "openssl x509 -outform der -in isemnt.pem -out isemnt.der";
		executeCommand(command);
	}

	protected void addISEIdentityCertToIdentityKeystore() throws IOException, InterruptedException {
		String command = "keytool -import -noprompt -alias mnt1 -keystore pxGridClient.jks -file isemnt.der -storepass P@ssw0rd";
		executeCommand(command);
	}

	protected void importPxGridClientCertToIdentityKeystore() throws IOException, InterruptedException {
		String command = "keytool -import -noprompt -alias pxGridclient1 -keystore pxGridClient.jks -file pxGridClient.cer -storepass P@ssw0rd";
		executeCommand(command);
	}

	protected void importIseIdentityCertToTrustKeystore() throws IOException, InterruptedException {
		String command = "keytool -import -noprompt -alias root1 -keystore root1.jks -file isemnt.der -storepass P@ssw0rd";
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
}
