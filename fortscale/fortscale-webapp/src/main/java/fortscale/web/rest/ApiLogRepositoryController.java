package fortscale.web.rest;

import fortscale.domain.fetch.LogRepository;
import fortscale.services.LogRepositoryService;
import fortscale.utils.EncryptionUtils;
import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.HideSensitiveArgumentsFromLog;
import fortscale.utils.logging.annotation.LogException;
import fortscale.utils.logging.annotation.LogSensitiveFunctionsAsEnum;
import fortscale.web.beans.request.LogRepositoryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Amir Keren on 8/15/16.
 */
@Controller
@RequestMapping(value = "/api/log_repository")
public class ApiLogRepositoryController {

	private static Logger logger = Logger.getLogger(ApiLogRepositoryController.class);

	@Autowired
	private LogRepositoryService logRepositoryService;

	/**
	 * Updates or creates config items.
	 *
	 * @return ResponseEntity
	 * @throws org.json.JSONException
	 */
	@RequestMapping(method = RequestMethod.POST)
	@HideSensitiveArgumentsFromLog(sensitivityCondition = LogSensitiveFunctionsAsEnum.APPLICATION_CONFIGURATION)
	@LogException
	public ResponseEntity updateLogRepository(@Valid @RequestBody List<LogRepositoryRequest> logRepositories) {
		List<LogRepository> logRepositoriesList = new ArrayList<>();
		try {
			for (LogRepositoryRequest logRepositoryRequest: logRepositories) {
				//Password is not already encrypted
				if (!logRepositoryRequest.isEncryptedPassword()) {
					String encryptedPassword = EncryptionUtils.encrypt(logRepositoryRequest.getPassword()).trim();
					logRepositoryRequest.setPassword(encryptedPassword);
				}
				logRepositoriesList.add(logRepositoryRequest.getLogRepository());
			}
			logRepositoryService.saveLogRepositoriesInDatabase(logRepositoriesList);
			return new ResponseEntity(HttpStatus.OK);
		} catch (Exception ex) {
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Tests connection to Log Repository
	 *
	 * @return ResponseEntity
	 * @throws org.json.JSONException
	 */
	@RequestMapping(method = RequestMethod.POST,value = "/test")
	@HideSensitiveArgumentsFromLog(sensitivityCondition = LogSensitiveFunctionsAsEnum.APPLICATION_CONFIGURATION)
	@LogException
	public ResponseEntity testActiveDirectoryConnection(@Valid @RequestBody LogRepository logRepository,
			@RequestParam(required = true, value = "encrypted_password") Boolean encryptedPassword) {
		if (!encryptedPassword) {
			try {
				logRepository.setPassword(EncryptionUtils.encrypt(logRepository.getPassword()).trim());
			} catch (Exception ex) {
				logger.error("failed to encrypt password");
				return new ResponseEntity(ex.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
			}
		}
		String result = logRepositoryService.canConnect(logRepository);
		if (result.isEmpty()) {
			return new ResponseEntity(HttpStatus.OK);
		} else {
			return new ResponseEntity(result, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(method = RequestMethod.GET)
	@LogException
	public List<LogRepository> getLogRepository() {
		return logRepositoryService.getLogRepositoriesFromDatabase();
	}

}