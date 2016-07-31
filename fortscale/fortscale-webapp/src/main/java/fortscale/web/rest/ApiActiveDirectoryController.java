package fortscale.web.rest;

import fortscale.domain.ad.AdConnection;
import fortscale.services.ActiveDirectoryService;
import fortscale.services.ApplicationConfigurationService;
import fortscale.utils.EncryptionUtils;
import fortscale.utils.logging.annotation.HideSensitiveArgumentsFromLog;
import fortscale.utils.logging.annotation.LogException;
import fortscale.utils.logging.annotation.LogSensitiveFunctionsAsEnum;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by Amir Keren on 7/31/16.
 */
public class ApiActiveDirectoryController {

	private static final String ACTIVE_DIRECTORY_KEY = "system.activeDirectory.settings";

	@Autowired
	private ApplicationConfigurationService applicationConfigurationService;
	@Autowired
	private ActiveDirectoryService activeDirectoryService;

	/**
	 * Updates or creates config items.
	 *
	 * @return ResponseEntity
	 * @throws JSONException
	 */
	@RequestMapping(method = RequestMethod.POST,value = "/active_directory")
	@HideSensitiveArgumentsFromLog(sensitivityCondition = LogSensitiveFunctionsAsEnum.APPLICATION_CONFIGURATION)
	@LogException
	public ResponseEntity updateActiveDirectory(@Valid @RequestBody List<AdConnection> activeDirectoryDomains) {
		try {
			for (AdConnection newAdConfiguration : activeDirectoryDomains) {
				//Password is not already encrypted
				if (shouldEncryptPassword(newAdConfiguration)) {
					String encryptedPassword = EncryptionUtils.encrypt(newAdConfiguration.getDomainPassword()).trim();
					newAdConfiguration.setDomainPassword(encryptedPassword);
				}
			}
			applicationConfigurationService.updateConfigItemAsObject(ACTIVE_DIRECTORY_KEY,activeDirectoryDomains);
			return new ResponseEntity(HttpStatus.OK);
		} catch (Exception ex) {
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Tests connection to Active Directory
	 *
	 * @return ResponseEntity
	 * @throws JSONException
	 */
	@RequestMapping(method = RequestMethod.POST,value = "/active_directory_test")
	@HideSensitiveArgumentsFromLog(sensitivityCondition = LogSensitiveFunctionsAsEnum.APPLICATION_CONFIGURATION)
	@LogException
	public ResponseEntity testActiveDirectoryConnection(@Valid @RequestBody AdConnection activeDirectoryDomain) {
		String result = activeDirectoryService.canConnect(activeDirectoryDomain);
		if (result.isEmpty()) {
			return new ResponseEntity(HttpStatus.OK);
		} else {
			return new ResponseEntity(result, HttpStatus.BAD_REQUEST);
		}
	}

	private boolean shouldEncryptPassword(AdConnection newAdConfiguration) {
		List<AdConnection> adConnectionsFromDB = applicationConfigurationService.
				getApplicationConfigurationAsObjects(ACTIVE_DIRECTORY_KEY, AdConnection.class);
		if (adConnectionsFromDB == null || adConnectionsFromDB.isEmpty()){
			return true;
		}
		for (AdConnection existsConnection:adConnectionsFromDB){
			String domainFromNewConfiguration = newAdConfiguration.getDomainUser().split("@")[1];
			String domainFromDBConfiguration = existsConnection.getDomainUser().split("@")[1];
			String newPasswordFromNewConfiguration = newAdConfiguration.getDomainPassword();
			String passwordFromOldConfiguration = existsConnection.getDomainPassword();
			//Iterate all connections until found connection with the same domain.
			//If password is the same - don't encrypt. If the password different- encrypt
			if (domainFromDBConfiguration.equals(domainFromNewConfiguration)){
				if (newPasswordFromNewConfiguration.equals(passwordFromOldConfiguration)) {
					return false;
				} else {
					return  true;
				}
			}
		}
		return true; //The domain wasn't found. Encrypt the password
	}

}