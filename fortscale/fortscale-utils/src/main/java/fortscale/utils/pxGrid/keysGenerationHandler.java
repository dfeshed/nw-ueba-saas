package fortscale.utils.pxGrid;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by tomerd on 31/01/2016.
 */
public class KeysGenerationHandler {

	private final static int KEY_SIZE = 4096;
	private final static String PRIVATE_KEY_NAME = "self1.key";
	private final static String SELF_SIGNED_CSR_NAME = "self1.csr";

	public void generateCSRrequest() throws IOException, InterruptedException {
		String command = "openssl req -new -batch -key pxGridClient.key  -out pxGridClient.csr";
		executeCommand(command);
	}

	public String generateSelfSignedCert() throws IOException, InterruptedException {
		String command = "openssl req -x509 -days 365 -key pxGridClient.key -in pxGridClient.csr -out pxGridClient.cer";
		executeCommand(command);
	}

	public void generatePKCS12() throws IOException, InterruptedException {
		String command = "openssl pkcs12 -export -password pass:P@ssw0rd -out pxGridClient.p12 -inkey pxGridClient.key -in pxGridClient.cer";
		executeCommand(command);
	}

	public void importIntoIdentityKeystore() throws IOException, InterruptedException {
		String command = "keytool -importkeystore -noprompt -srckeystore pxGridClient.p12 -destkeystore pxGridClient.jks -srcstoretype PKCS12 -storepass P@ssw0rd -srckeypass P@ssw0rd -srcstorepass P@ssw0rd -alias 1";
		executeCommand(command);
	}

	public void convertPemToDer() throws IOException, InterruptedException {
		String command = "openssl x509 -outform der -in isemnt.pem -out isemnt.der";
		executeCommand(command);
	}

	public void addISEIdentityCertToIdentityKeystore() throws IOException, InterruptedException {
		String command = "keytool -import -noprompt -alias mnt1 -keystore pxGridClient.jks -file isemnt.der -storepass P@ssw0rd";
		executeCommand(command);
	}

	public void importPxGridClientCertToIdentityKeystore() throws IOException, InterruptedException {
		String command = "keytool -import -noprompt -alias pxGridclient1 -keystore pxGridClient.jks -file pxGridClient.cer -storepass P@ssw0rd";
		executeCommand(command);
	}

	public void importIseIdentityCertToTrustKeystore() throws IOException, InterruptedException {
		String command = "keytool -import -noprompt -alias root1 -keystore root1.jks -file isemnt.der -storepass P@ssw0rd";
		executeCommand(command);
	}


	private void executeCommand(String command) throws InterruptedException, IOException {
		Runtime r = Runtime.getRuntime();
		Process p = r.exec(command);
		p.waitFor();
	}
}
