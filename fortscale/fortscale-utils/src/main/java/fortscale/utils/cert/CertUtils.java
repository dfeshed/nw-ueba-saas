package fortscale.utils.cert;

import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Value;

import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class CertUtils {

	private static final Logger logger = Logger.getLogger(CertUtils.class);

	private static final char[] HEXDIGITS = "0123456789abcdef".toCharArray();

	@Value("${java.cert.password}")
	private String password;
	@Value("${source.qradar.port}")
	private int port;

	/**
	 *
	 * This method installs the server certification
	 *
	 * @throws Exception
	 */
	public void installCert(String host) throws Exception {
		char[] passphrase = password.toCharArray();
		char SEP = File.separatorChar;
		File file = new File(System.getProperty("java.home") + SEP + "lib" + SEP + "security" + SEP + "cacerts");
		logger.info("Loading KeyStore {}...", file);
		InputStream in = new FileInputStream(file);
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		ks.load(in, passphrase);
		in.close();
		SSLContext context = SSLContext.getInstance("TLS");
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(ks);
		X509TrustManager defaultTrustManager = (X509TrustManager)tmf.getTrustManagers()[0];
		SavingTrustManager tm = new SavingTrustManager(defaultTrustManager);
		context.init(null, new TrustManager[] {tm}, null);
		SSLSocketFactory factory = context.getSocketFactory();
		logger.info("Opening connection to {}:{}...", host, port);
		SSLSocket socket = (SSLSocket)factory.createSocket(host, port);
		socket.setSoTimeout(10000);
		try {
			logger.info("Starting SSL handshake...");
			socket.startHandshake();
			socket.close();
			logger.info("No errors, certificate is already trusted");
		} catch (SSLException ex) {
			logger.error("Error - ", ex);
		}
		X509Certificate[] chain = tm.chain;
		if (chain == null) {
			logger.info("Could not obtain server certificate chain");
			return;
		}
		logger.info("Server sent {} certificate(s):", chain.length);
		MessageDigest sha1 = MessageDigest.getInstance("SHA1");
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		for (int i = 0; i < chain.length; i++) {
			X509Certificate cert = chain[i];
			logger.info(" " + (i + 1) + " Subject " + cert.getSubjectDN());
			logger.info("   Issuer  " + cert.getIssuerDN());
			sha1.update(cert.getEncoded());
			logger.info("   sha1    " + toHexString(sha1.digest()));
			md5.update(cert.getEncoded());
			logger.info("   md5     " + toHexString(md5.digest()));
		}
		X509Certificate cert = chain[0];
		ks.setCertificateEntry(host, cert);
		OutputStream out = new FileOutputStream(System.getProperty("java.home") + SEP + "lib" + SEP + "security" + SEP +
				"cacerts");
		ks.store(out, passphrase);
		out.close();
		logger.info("{}", cert);
		logger.info("Added certificate to keystore 'cacerts' using alias '{}'", host);
	}

	/**
	 *
	 * This method converts bytes to Hex string
	 *
	 * @param bytes
	 * @return
	 */
	private static String toHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 3);
		for (int b : bytes) {
			b &= 0xff;
			sb.append(HEXDIGITS[b >> 4]);
			sb.append(HEXDIGITS[b & 15]);
			sb.append(' ');
		}
		return sb.toString();
	}

	private static class SavingTrustManager implements X509TrustManager {

		private final X509TrustManager tm;
		private X509Certificate[] chain;

		SavingTrustManager(X509TrustManager tm) {
			this.tm = tm;
		}

		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[0];
		}

		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
			throw new UnsupportedOperationException();
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
			this.chain = chain;
			tm.checkServerTrusted(chain, authType);
		}

	}

}