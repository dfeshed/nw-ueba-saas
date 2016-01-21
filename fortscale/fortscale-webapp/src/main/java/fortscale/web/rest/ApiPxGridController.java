package fortscale.web.rest;

import fortscale.collection.jobs.PxGridFetchJob;
import fortscale.domain.core.ApplicationConfiguration;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepositoryImpl;
import fortscale.services.ApplicationConfigurationService;
import fortscale.services.UserService;
import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.LogException;
import fortscale.utils.pxGrid.PxGridHandler;
import fortscale.utils.pxGrid.pxGridConnectionStatus;
import fortscale.web.DataQueryController;
import fortscale.web.beans.DataBean;
import fortscale.web.rest.Utils.ApiUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@RequestMapping("/api/pxgrid")
public class ApiPxGridController extends DataQueryController{

	private static Logger logger = Logger.getLogger(ApiPxGridController.class);

	@Autowired ApplicationConfigurationService applicationConfigurationService;

	@RequestMapping(method = RequestMethod.GET)
	@LogException
	public @ResponseBody ResponseEntity connectToPxGrid() {
		PxGridHandler pxGridHandler = createPxGridHandler();
		pxGridConnectionStatus status = pxGridHandler.connectToGrid();
		switch (status){
		case CONNECTED:
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		case DISCONNECTED:
		case CONNECTION_ERROR:
		case INVALID_KEYS:
		case MISSING_CONFIGURATION:
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
		return null;
	}

	private PxGridHandler createPxGridHandler() {
		String hosts = readFromConfigurationService(PxGridFetchJob.HOSTS_KEY);
		String userName = readFromConfigurationService(PxGridFetchJob.USERNAME_KEY);
		String group = readFromConfigurationService(PxGridFetchJob.GROUP_KEY);
		String keystorePath = readFromConfigurationService(PxGridFetchJob.KEYSTOREPATH_KEY);
		String keystorePassphrase = readFromConfigurationService(PxGridFetchJob.KEYSTORE_PASSPHARSE_KEY);
		String truststorePath = readFromConfigurationService(PxGridFetchJob.TRUSTSTORE_PATH_KEY);
		String truststorePassphrase = readFromConfigurationService(PxGridFetchJob.TRUSTSTORE_PASSPHARSE_KEY);
		int connectionRetryMillisecond = Integer.parseInt(readFromConfigurationService(PxGridFetchJob.CONNECTION_RETRY_MILLISECOND_KEY));

		return new PxGridHandler(hosts, userName, group, keystorePath, keystorePassphrase, truststorePath, truststorePassphrase, connectionRetryMillisecond);
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
