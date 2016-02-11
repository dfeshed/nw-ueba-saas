package fortscale.utils.pxGrid;

import com.cisco.pxgrid.GridConnection;
import com.cisco.pxgrid.ReconnectionManager;
import com.cisco.pxgrid.TLSConfiguration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import sun.misc.BASE64Decoder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;

/**
 * Created by tomerd on 19/01/2016.
 */
public class PxGridHandler {
	private static Logger logger = LoggerFactory.getLogger(PxGridHandler.class);

	static final String KEYSTORE_FILENAME = "keystore.jks";
	static final String TRUSTSTORE_FILENAME = "truststore.jks";

	//<editor-fold desc="pxGrid connection variables">
	private String hosts;
	private String userName;
	private String group;
	private String keystorePath;
	private String keystorePassphrase;
	private String truststorePath;
	private String truststorePassphrase;
	private int connectionRetryMillisecond;

	GridConnection con;
	ReconnectionManager recon;
	//</editor-fold>

	private PxGridConnectionStatus status;

	/**
	 * Create new instance of pxGrid handler
	 *
	 * @param hosts
	 * @param userName
	 * @param group
	 * @param base64KeystorePath
	 * @param keystorePassphrase
	 * @param base64Truststore
	 * @param truststorePassphrase
	 * @param connectionRetryMillisecond
	 */
	public PxGridHandler(String hosts, String userName, String group, String base64KeystorePath,
			String keystorePassphrase, String base64Truststore, String truststorePassphrase,
			int connectionRetryMillisecond) {

		try {
			Assert.isTrue(StringUtils.isNotBlank(hosts));
			Assert.isTrue(StringUtils.isNotBlank(userName));
			Assert.isTrue(StringUtils.isNotBlank(group));
			Assert.isTrue(StringUtils.isNotBlank(base64KeystorePath));
			Assert.isTrue(StringUtils.isNotBlank(keystorePassphrase));
			Assert.isTrue(StringUtils.isNotBlank(base64Truststore));
			Assert.isTrue(StringUtils.isNotBlank(truststorePassphrase));
			Assert.isTrue(connectionRetryMillisecond > 0);
		} catch (Exception e) {
			status = PxGridConnectionStatus.MISSING_CONFIGURATION;
			return;
		}

		try {
			this.hosts = hosts;
			this.userName = userName;
			this.group = group;
			this.keystorePath = saveKey(base64KeystorePath, KEYSTORE_FILENAME);
			this.keystorePassphrase = keystorePassphrase;
			this.truststorePath = saveKey(base64Truststore, TRUSTSTORE_FILENAME);
			this.truststorePassphrase = truststorePassphrase;
			this.connectionRetryMillisecond = connectionRetryMillisecond;
			status = PxGridConnectionStatus.DISCONNECTED;
		} catch (IOException e) {
			status = PxGridConnectionStatus.INVALID_KEYS_SETTINGS;
		}
	}

	/**
	 * Connect to pxGrid
	 *
	 * @return The connection status
	 */
	public PxGridConnectionStatus connectToGrid() {
		logger.debug("establishing a connection with the pxGrid controller");

		if (status == PxGridConnectionStatus.INVALID_KEYS_SETTINGS || status == PxGridConnectionStatus.MISSING_CONFIGURATION) {
			return getStatus();
		}

		if (!validateKeys()) {
			status = PxGridConnectionStatus.INVALID_KEYS;
			return getStatus();
		}

		if (!initPxGridConnection()) {
			status = PxGridConnectionStatus.CONNECTION_ERROR;
			return getStatus();
		}

		status = PxGridConnectionStatus.CONNECTED;
		return getStatus();
	}

	/**
	 * Close the connection to the grid
	 *
	 * @return
	 */
	public PxGridConnectionStatus close() {
		if (recon != null && con.isConnected()) {
			// disconnect from pxGrid
			recon.stop();
		}
		try {
			Files.delete(Paths.get(KEYSTORE_FILENAME));
			Files.delete(Paths.get(TRUSTSTORE_FILENAME));
		} catch (Exception e) {
			// do nothing
		}

		status = PxGridConnectionStatus.DISCONNECTED;
		return getStatus();
	}

	/**
	 * Get the connection status
	 *
	 * @return
	 */
	public PxGridConnectionStatus getStatus() {
		return status;
	}

	public GridConnection getGridConnection() {
		return this.con;
	}

	public String getHost() {
		// TODO: After implementing fail - over, return the active host
		return hosts;
	}

	/**
	 * Validate the keys
	 *
	 * @return
	 */
	private boolean validateKeys() {
		logger.debug("Validating connection keys");
		return validateKey(keystorePath, keystorePassphrase) && validateKey(truststorePath, truststorePassphrase);
	}

	/**
	 * Validate single key
	 *
	 * @param filename
	 * @param password
	 * @return
	 */
	private boolean validateKey(String filename, String password) {
		try {
			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(new FileInputStream(filename), password.toCharArray());
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	/**
	 * Connect to pxGrid
	 *
	 * @return
	 */
	private boolean initPxGridConnection() {
		// configure the connection properties
		TLSConfiguration config = createConfigObject();

		try {
			con = new GridConnection(config);
			//con.addListener(new MyListener());

			recon = new ReconnectionManager(con);
			recon.setRetryMillisecond(connectionRetryMillisecond);

			logger.debug("Trying to connect to pxGrid");
			recon.start();

			// Wait for the connection to establish
			while (!con.isConnected()) {
				Thread.sleep(100);
			}
		} catch (Exception e) {
			logger.warn("Error while connecting to pxGrid. error: {}", e.getMessage());
			return false;
		}

		logger.debug("Connected to pxGrid successfully");
		return true;
	}

	/**
	 * Create pxGrid configuration object
	 *
	 * @return
	 */
	private TLSConfiguration createConfigObject() {
		TLSConfiguration config = new TLSConfiguration();
		config.setHosts(new String[] { hosts });
		config.setUserName(userName);
		config.setGroup(group);
		config.setKeystorePath(keystorePath);
		config.setKeystorePassphrase(keystorePassphrase);
		config.setTruststorePath(truststorePath);
		config.setTruststorePassphrase(truststorePassphrase);

		return config;
	}

	private String saveKey(String base64Key, String fileName) throws IOException {
		byte[] keyBytes;

		BASE64Decoder decoder = new BASE64Decoder();
		keyBytes = decoder.decodeBuffer(base64Key);
		try (OutputStream stream = new FileOutputStream(fileName)) {
			stream.write(keyBytes);
		}

		return fileName;
	}
}
