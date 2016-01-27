package fortscale.web.rest;

import fortscale.domain.core.ApplicationConfiguration;
import fortscale.services.ApplicationConfigurationService;
import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.LogException;
import fortscale.utils.pxGrid.PxGridHandler;
import fortscale.utils.pxGrid.pxGridConnectionStatus;
import fortscale.web.DataQueryController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/pxgrid")
public class ApiPxGridController extends DataQueryController{

	private static Logger logger = Logger.getLogger(ApiPxGridController.class);

	@Value("${pxgrid.hosts.key}")
	private String hostsKey;
	@Value("${pxgrid.username.key}")
	private String usernameKey;
	@Value("${pxgrid.group.key}")
	private String groupKey;
	@Value("${pxgrid.keystorepath.key}")
	private String keystorePathKey;
	@Value("${pxgrid.keystorepassphrase.key}")
	private String keystorePassphraseKey;
	@Value("${pxgrid.truststorepath.key}")
	private String truststorePathKey;
	@Value("${pxgrid.truststorepassphrase.key}")
	private String truststorePassphraseKey;
	@Value("${pxgrid.connectionretry.key}")
	private String connectionRetryKey;

	@Autowired
	ApplicationConfigurationService applicationConfigurationService;

	@RequestMapping(method = RequestMethod.GET)
	@LogException
	public @ResponseBody ResponseEntity connectToPxGrid() {
		PxGridHandler pxGridHandler = createPxGridHandler();
		pxGridConnectionStatus status = pxGridHandler.connectToGrid();
		switch (status){
			case CONNECTED: return new ResponseEntity(pxGridHandler.getHost(), HttpStatus.NO_CONTENT);
			case DISCONNECTED:
			case CONNECTION_ERROR:
			case INVALID_KEYS:
			case MISSING_CONFIGURATION: return new ResponseEntity(status.message(), HttpStatus.BAD_REQUEST);
			default: return null;
		}
	}

	private PxGridHandler createPxGridHandler() {
		String hosts = readFromConfigurationService(hostsKey);
		String userName = readFromConfigurationService(usernameKey);
		String group = readFromConfigurationService(groupKey);
		String keystorePath = readFromConfigurationService(keystorePathKey);
		String keystorePassphrase = readFromConfigurationService(keystorePassphraseKey);
		String truststorePath = readFromConfigurationService(truststorePathKey);
		String truststorePassphrase = readFromConfigurationService(truststorePassphraseKey);
		int connectionRetryMillisecond = Integer.
				parseInt(readFromConfigurationService(connectionRetryKey));
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