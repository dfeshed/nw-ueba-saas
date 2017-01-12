package fortscale.web.rest;

import fortscale.domain.Exceptions.PasswordDecryptionException;
import fortscale.domain.ad.AdConnection;
import fortscale.domain.ad.AdObject;
import fortscale.domain.ad.AdObject.AdObjectType;
import fortscale.domain.ad.AdTaskType;
import fortscale.services.ActiveDirectoryService;
import fortscale.services.ad.AdTaskPersistencyService;
import fortscale.utils.EncryptionUtils;
import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.HideSensitiveArgumentsFromLog;
import fortscale.utils.logging.annotation.LogException;
import fortscale.utils.logging.annotation.LogSensitiveFunctionsAsEnum;
import fortscale.utils.spring.SpringPropertiesUtil;
import fortscale.web.beans.AuthenticationTestResult;
import fortscale.web.beans.ResponseEntityMessage;
import fortscale.web.beans.request.ActiveDirectoryRequest;
import fortscale.web.services.AdTaskServiceImpl;
import fortscale.web.tasks.ControllerInvokedAdTask;
import fortscale.web.tasks.ControllerInvokedAdTask.AdTaskStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import javax.naming.AuthenticationException;
import javax.naming.CommunicationException;
import javax.naming.NamingException;
import javax.validation.Valid;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;



@Controller
@RequestMapping(value = "/api/active_directory")
public class ApiActiveDirectoryController {

	public static final String SERVER_URL_ERROR = "Server URL is not available. Update the server URL and try again.";
	public static final String WRONG_CREDENTIALS_ERROR = "Wrong Credentials, please update and try again.";
	private static Logger logger = Logger.getLogger(ApiActiveDirectoryController.class);

	public static String COLLECTION_TARGET_DIR;

	public String COLLECTION_USER;

	public String USER_HOME_DIR;

	private Long lastAdFetchEtlExecutionStartTime;

	private final long FETCH_AND_ETL_TIMEOUT_IN_SECONDS = 60;

	private final AtomicBoolean isFetchEtlExecutionRequestStopped = new AtomicBoolean(false);

	private final Set<AdObject.AdObjectType> dataSources = new HashSet<>(Arrays.asList(AdObject.AdObjectType.values()));


	@Autowired
	private AdTaskServiceImpl adTaskService;

	@Autowired
	private ActiveDirectoryService activeDirectoryService;

	@Autowired
	private AdTaskPersistencyService adTaskPersistencyService;

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	@PostConstruct
	private void getProperties() {
		final String homeDirProperty = SpringPropertiesUtil.getProperty("user.home.dir");
		USER_HOME_DIR = homeDirProperty != null ? homeDirProperty : "/home/cloudera";

		COLLECTION_TARGET_DIR =  USER_HOME_DIR + "/fortscale/fortscale-core/fortscale/fortscale-collection/target";

		final String userName = SpringPropertiesUtil.getProperty("user.name");
		COLLECTION_USER = userName!=null? userName : "cloudera";
	}


	/**
	 * Updates or creates config items.
	 *
	 * @return ResponseEntity
	 */
	@RequestMapping(method = RequestMethod.POST)
	@HideSensitiveArgumentsFromLog(sensitivityCondition = LogSensitiveFunctionsAsEnum.APPLICATION_CONFIGURATION)
	@LogException
	public ResponseEntity updateActiveDirectory(@Valid @RequestBody List<ActiveDirectoryRequest> activeDirectoryDomains) {
		List<AdConnection> adConnectionList = new ArrayList<>();
		try {
			for (ActiveDirectoryRequest newAdConfiguration : activeDirectoryDomains) {
				//Password is not already encrypted
				if (!newAdConfiguration.isEncryptedPassword()) {
					String encryptedPassword = EncryptionUtils.encrypt(newAdConfiguration.getDomainPassword()).trim();
					newAdConfiguration.setDomainPassword(encryptedPassword);
				}
				adConnectionList.add(newAdConfiguration.getAdConnection());
			}
			activeDirectoryService.saveAdConnectionsInDatabase(adConnectionList);
			return new ResponseEntity(HttpStatus.OK);
		} catch (Exception ex) {
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Tests connection to Active Directory
	 *
	 * @return ResponseEntity
	 */
	@RequestMapping(method = RequestMethod.POST,value = "/test")
	@HideSensitiveArgumentsFromLog(sensitivityCondition = LogSensitiveFunctionsAsEnum.APPLICATION_CONFIGURATION)
	@LogException
	public AuthenticationTestResult testActiveDirectoryConnection(@Valid @RequestBody AdConnection activeDirectoryDomain,
																  @RequestParam(value = "encrypted_password") Boolean encryptedPassword) {
		if (!encryptedPassword) {
			try {
				activeDirectoryDomain.setDomainPassword(EncryptionUtils.encrypt(activeDirectoryDomain.
						getDomainPassword()));
			} catch (Exception ex) {
				logger.error("failed to encrypt password");
				return new AuthenticationTestResult(false, "Wrong Credentials, please update and try again.");
			}
		}
		boolean connectSuccess;
		try {
			connectSuccess = activeDirectoryService.canConnect(activeDirectoryDomain);
		} catch (CommunicationException e) {
			logger.warn("Server is not available");
			return new AuthenticationTestResult(false, SERVER_URL_ERROR);

		} catch (AuthenticationException e) {
			logger.warn("Wrong user or password");
			return new AuthenticationTestResult(false, WRONG_CREDENTIALS_ERROR);
		} catch (NamingException e) {
			logger.warn("Unknown error while trying to connect to server");

			return new AuthenticationTestResult(false, e.getExplanation());
		} catch (PasswordDecryptionException e) {
			logger.warn("failed to encrypt password");
			return new AuthenticationTestResult(false, WRONG_CREDENTIALS_ERROR);

		}

		return  new AuthenticationTestResult(true,"");

	}

	@RequestMapping(method = RequestMethod.GET)
	@LogException
	public List<AdConnection> getActiveDirectory() {
		return activeDirectoryService.getAdConnectionsFromDatabase();
	}

	@RequestMapping("/ad_fetch_etl" )
	public ResponseEntity<ResponseEntityMessage> executeAdFetchAndEtl() {
		logger.debug("Executing AD Fetch and ETL");
		final boolean executedSuccessfully = adTaskService.executeTasks(simpMessagingTemplate);
		if (executedSuccessfully) {
			lastAdFetchEtlExecutionStartTime = System.currentTimeMillis();
			return new ResponseEntity<>(new ResponseEntityMessage("Fetch and ETL is running."), HttpStatus.OK);
		}
		else {
			final String inProgressMsg = "Active Directory fetch and ETL already in progress. Can't execute again until the previous execution is finished. Request to execute ignored.";
			logger.warn(inProgressMsg);
			return new ResponseEntity<>(new ResponseEntityMessage(inProgressMsg), HttpStatus.LOCKED);
		}
	}


	@RequestMapping("/stop_ad_fetch_etl" )
	public ResponseEntity<ResponseEntityMessage> stopAdFetchAndEtlExecution() {
		logger.debug("Stopping AD Fetch and ETL execution");
		if (adTaskService.stopAllTasks(FETCH_AND_ETL_TIMEOUT_IN_SECONDS)) {
			lastAdFetchEtlExecutionStartTime = null;
			final String message = "AD fetch and ETL execution has stopped successfully";
			logger.debug(message);
			return new ResponseEntity<>(new ResponseEntityMessage(message), HttpStatus.OK);
		}
		else {
			final String msg = "Failed to stop AD Fetch and ETL execution";
			logger.error(msg);
			return new ResponseEntity<>(new ResponseEntityMessage(msg), HttpStatus.NOT_ACCEPTABLE);
		}
	}



	@RequestMapping(method = RequestMethod.GET,value = "/ad_etl_fetch_status")
	@LogException
	public FetchEtlExecutionStatus getJobStatus() {
		Set<AdTaskStatus> statuses = new HashSet<>();
		dataSources.forEach(datasource -> {
			final AdTaskType runningMode = getRunningMode(datasource);
			final Long currExecutionStartTime = adTaskPersistencyService.getLastExecutionTime(runningMode, datasource);
			if (runningMode != null) { //running
				statuses.add(new AdTaskStatus(runningMode, datasource, -1L, -1L, currExecutionStartTime));
			}
			else { //not running
				Long currLastExecutionFinishTime;
				if (datasource == AdObjectType.USER_THUMBNAIL) {
					currLastExecutionFinishTime = adTaskPersistencyService.getLastExecutionTime(AdTaskType.FETCH_ETL, datasource);
				}
				else {
					currLastExecutionFinishTime = adTaskPersistencyService.getLastExecutionTime(AdTaskType.ETL, datasource);
				}
				final Long currObjectsCount = activeDirectoryService.getLastRunCount(datasource);
				statuses.add(new AdTaskStatus(null, datasource, currLastExecutionFinishTime, currObjectsCount, currExecutionStartTime));
			}
		});

		return new FetchEtlExecutionStatus(lastAdFetchEtlExecutionStartTime, statuses);
	}


	/**
	 * this method returns the running mode (Fetch, ETL or null for not running) of the given {@code dataSource}
	 * @param datasource the datasource whose running mode we want
	 * @return Fetch, ETL or null for not running
	 */
	private AdTaskType getRunningMode(AdObjectType datasource) {
		for (ControllerInvokedAdTask activeThread : adTaskService.getActiveTasks()) {
			if (datasource.equals(activeThread.getDataSource())) {
				return activeThread.getCurrentAdTaskType();
			}
		}

		return null;
	}




	private static class FetchEtlExecutionStatus {

		public final Long lastAdFetchEtlExecutionTime;
		public final Set<AdTaskStatus> runningTasksStatuses;

		public FetchEtlExecutionStatus(Long lastAdFetchEtlExecutionTime, Set<AdTaskStatus> runningTasksStatuses) {
			this.lastAdFetchEtlExecutionTime = lastAdFetchEtlExecutionTime;
			this.runningTasksStatuses = runningTasksStatuses;
		}
	}
}