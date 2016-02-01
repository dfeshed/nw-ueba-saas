package fortscale.utils.pxGrid;

import com.cisco.pxgrid.GridConnection;
import com.cisco.pxgrid.ReconnectionManager;
import com.cisco.pxgrid.TLSConfiguration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.FileInputStream;
import java.security.KeyStore;

/**
 * Created by tomerd on 19/01/2016.
 */
public class PxGridHandler {
	private static Logger logger = LoggerFactory.getLogger(PxGridHandler.class);

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

	private pxGridConnectionStatus status;

	/**
	 * Create new instance of pxGrid handler
	 * @param hosts
	 * @param userName
	 * @param group
	 * @param keystorePath
	 * @param keystorePassphrase
	 * @param truststorePath
	 * @param truststorePassphrase
	 * @param connectionRetryMillisecond
	 */
	public PxGridHandler(String hosts, String userName, String group, String keystorePath, String keystorePassphrase,
			String truststorePath, String truststorePassphrase, int connectionRetryMillisecond) {

		Assert.isTrue(StringUtils.isNotBlank(hosts));
		Assert.isTrue(StringUtils.isNotBlank(userName));
		Assert.isTrue(StringUtils.isNotBlank(group));
		Assert.isTrue(StringUtils.isNotBlank(keystorePath));
		Assert.isTrue(StringUtils.isNotBlank(keystorePassphrase));
		Assert.isTrue(StringUtils.isNotBlank(truststorePath));
		Assert.isTrue(StringUtils.isNotBlank(truststorePassphrase));
		Assert.isTrue(connectionRetryMillisecond > 0);

		this.hosts = hosts;
		this.userName = userName;
		this.group = group;
		this.keystorePath = keystorePath;
		this.keystorePassphrase = keystorePassphrase;
		this.truststorePath = truststorePath;
		this.truststorePassphrase = truststorePassphrase;
		this.connectionRetryMillisecond = connectionRetryMillisecond;

		status = pxGridConnectionStatus.DISCONNECTED;
	}

	/**
	 * Connect to pxGrid
	 * @return The connection status
	 */
	public pxGridConnectionStatus connectToGrid() {
		logger.debug("establishing a connection with the pxGrid controller");

		if (!validateKeys()) {
			status = pxGridConnectionStatus.INVALID_KEYS;
			return getStatus();
		}

		if (!initPxGridConnection()) {
			status = pxGridConnectionStatus.CONNECTION_ERROR;
			return getStatus();
		}

		status = pxGridConnectionStatus.CONNECTED;
		return getStatus();
	}

	/**
	 * Close the connection to the grid
	 * @return
	 */
	public pxGridConnectionStatus close() {
		if (recon != null && con.isConnected()) {
			// disconnect from pxGrid
			recon.stop();
		}

		status = pxGridConnectionStatus.DISCONNECTED;
		return getStatus();
	}

	/**
	 * Get the connection status
	 * @return
	 */
	public pxGridConnectionStatus getStatus() {
		return status;
	}

	public GridConnection getGridConnection() {
		return this.con;
	}

	/**
	 * Validate the keys
	 * @return
	 */
	private boolean validateKeys() {
		logger.debug("Validating connection keys");
		return validateKey(keystorePath, keystorePassphrase) && validateKey(truststorePath, truststorePassphrase);
	}

	/**
	 * Validate single key
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
}
