package fortscale.web.rest;

import fortscale.domain.core.ApplicationConfiguration;
import fortscale.services.ApplicationConfigurationService;
import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.LogException;
import fortscale.utils.pxGrid.*;
import fortscale.web.DataQueryController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("/api/pxgrid")
public class ApiPxGridController extends DataQueryController{

	private static Logger logger = Logger.getLogger(ApiPxGridController.class);

	private final static String CER_KEY = "system.pxgrid.cer";

	private final static String HOSTS_KEY = "system.pxgrid.hosts";
	private final static String USERNAME_KEY = "system.pxgrid.username";
	private final static String GROUP_KEY = "system.pxgrid.group";
	private final static String KEYSTOREPATH_KEY = "system.pxgrid.keystorepath";
	private final static String KEYSTORE_PASSPHARSE_KEY = "system.pxgrid.keystorepasspharse";
	private final static String TRUSTSTORE_PATH_KEY = "system.pxgrid.truststore";
	private final static String TRUSTSTORE_PASSPHARSE_KEY = "system.pxgrid.truststorepasspharse";
	private final static String CONNECTION_RETRY_MILLISECOND_KEY = "system.pxgrid.connectionretrymillisecond";

	@Autowired
	ApplicationConfigurationService applicationConfigurationService;

	@RequestMapping(method = RequestMethod.GET)
	@LogException
	public @ResponseBody ResponseEntity connectToPxGrid() {
		PxGridHandler pxGridHandler = createPxGridHandler();
		PxGridConnectionStatus status = pxGridHandler.connectToGrid();
		switch (status){
			case CONNECTED: return new ResponseEntity(pxGridHandler.getHost(), HttpStatus.NO_CONTENT);
			case DISCONNECTED:
			case CONNECTION_ERROR:
			case INVALID_KEYS:
			case MISSING_CONFIGURATION: return new ResponseEntity(status.message(), HttpStatus.BAD_REQUEST);
			default: return null;
		}
	}

	@RequestMapping(value="/generateCER", method=RequestMethod.GET)
	@LogException
	public @ResponseBody ResponseEntity generateCER(@RequestParam(required=true) String password) {
		KeysGenerationHandler keysHandler = new KeysGenerationHandler();
		try {
			String base64Cert = keysHandler.generateKeySelfSignedCert();
			applicationConfigurationService.insertConfigItem(CER_KEY, base64Cert);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value="/generateKeys", method=RequestMethod.POST)
	@LogException
	public @ResponseBody ResponseEntity generateKeys(@RequestParam(required=true) String base64PemFile,
													@RequestParam(required=true) String password) {
		KeysGenerationHandler keysHandler = new KeysGenerationHandler();
		try {
			Map.Entry<String, String> entry = keysHandler.generateKeys(password, base64PemFile);
			applicationConfigurationService.insertConfigItem(KEYSTORE_PASSPHARSE_KEY, password);
			applicationConfigurationService.insertConfigItem(KEYSTOREPATH_KEY, entry.getKey());
			applicationConfigurationService.insertConfigItem(TRUSTSTORE_PASSPHARSE_KEY, password);
			applicationConfigurationService.insertConfigItem(TRUSTSTORE_PATH_KEY, entry.getValue());
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}

	}

	private PxGridHandler createPxGridHandler() {
		String hosts = readFromConfigurationService(HOSTS_KEY);
		String userName = readFromConfigurationService(USERNAME_KEY);
		String group = readFromConfigurationService(GROUP_KEY);
		String keystorePath = readFromConfigurationService(KEYSTOREPATH_KEY);
		String keystorePassphrase = readFromConfigurationService(KEYSTORE_PASSPHARSE_KEY);
		String truststorePath = readFromConfigurationService(TRUSTSTORE_PATH_KEY);
		String truststorePassphrase = readFromConfigurationService(TRUSTSTORE_PASSPHARSE_KEY);
		int connectionRetryMillisecond = Integer.
				parseInt(readFromConfigurationService(CONNECTION_RETRY_MILLISECOND_KEY));
		return new PxGridHandler(hosts, userName, group, keystorePath, keystorePassphrase, truststorePath,
				truststorePassphrase, connectionRetryMillisecond);
	}

	private String readFromConfigurationService(String key) {
		ApplicationConfiguration applicationConfiguration = applicationConfigurationService.
				getApplicationConfigurationByKey(key);
		if (applicationConfiguration != null) {
			return applicationConfiguration.getValue();
		}
		return null;
	}

}