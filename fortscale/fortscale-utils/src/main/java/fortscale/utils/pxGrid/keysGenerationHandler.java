package fortscale.utils.pxGrid;

import sun.security.pkcs10.PKCS10;
import sun.security.x509.X500Name;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tomerd on 31/01/2016.
 */
public class keysGenerationHandler {

	private final static int KEY_SIZE = 4096;
	private final static String PRIVATE_KEY_NAME = "self1.key";
	private final static String SELF_SIGNED_CSR_NAME = "self1.csr";

	public void generatePrivateKey() throws NoSuchAlgorithmException, IOException, InterruptedException {
		String command = "openssl genrsa -out pxGridClient.key 4096";

		Runtime r  =Runtime.getRuntime();
		Process p = r.exec(command);
		p.waitFor();

		//ProcessBuilder pb = new ProcessBuilder(commandList);
		//Process p = pb.start();

		//p.waitFor();

		/*KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(KEY_SIZE);
		KeyPair keyPair = keyGen.genKeyPair();
		saveFile(keyPair.getPrivate().getEncoded(), PRIVATE_KEY_NAME);
		return keyPair;*/
	}

	public void generateCSRrequest(KeyPair keyPair, String commonName, String organizationalUnit,
			String organizationalName, String location, String state, String country)
			throws InvalidKeyException, NoSuchAlgorithmException, IOException, CertificateException, SignatureException,
			InterruptedException {

		/*PKCS10 pkcs10 = new PKCS10(keyPair.getPublic());
		Signature signature = Signature.getInstance("MD5WithRSA");
		signature.initSign(keyPair.getPrivate());
		X500Name x500Name = new X500Name(commonName, organizationalUnit, organizationalName, location, state, country);
		pkcs10.encodeAndSign(x500Name, signature);
		saveFile(pkcs10.getEncoded(), SELF_SIGNED_CSR_NAME);*/
	}

	public void generateCSRrequest() throws IOException, InterruptedException {
		String command = "openssl req -new -key pxGridClient.key -out pxGridClient.csr";

		Runtime r  =Runtime.getRuntime();
		Process p = r.exec(command);
		p.waitFor();
	}

	private void generateSelfSignedCert(PrivateKey privateKey) {
		//Certificate certificate = Certificate
	}

	private void generatePKCS12() {

	}

	private void importIntoIdentityKeystore() {

	}

	private void convertPemToDer() {

	}

	private void addISEIdentityCertToIdentityKeystore() {

	}

	private void importPxGridClientCertToIdentityKeystore() {

	}

	private void importIseIdentityCertToTrustKeystore() {

	}

	private String saveFile(byte[] file, String fileName) throws IOException {
		try (OutputStream stream = new FileOutputStream(fileName)) {
			stream.write(file);
		}

		return fileName;
	}
}
