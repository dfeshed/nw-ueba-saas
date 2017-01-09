package fortscale.web.rest;

import fortscale.domain.Exceptions.PasswordDecryptionException;
import fortscale.domain.ad.AdConnection;
import fortscale.domain.ad.AdObject.AdObjectType;
import fortscale.services.ActiveDirectoryService;
import fortscale.services.ApplicationConfigurationService;
import fortscale.utils.EncryptionUtils;
import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.HideSensitiveArgumentsFromLog;
import fortscale.utils.logging.annotation.LogException;
import fortscale.utils.logging.annotation.LogSensitiveFunctionsAsEnum;
import fortscale.utils.spring.SpringPropertiesUtil;
import fortscale.web.beans.AuthenticationTestResult;
import fortscale.web.beans.ResponseEntityMessage;
import fortscale.web.beans.request.ActiveDirectoryRequest;
import fortscale.web.tasks.CompoundControllerInvokedAdTask;
import fortscale.web.tasks.ControllerInvokedAdTask;
import fortscale.web.tasks.ControllerInvokedAdTask.AdTaskResponse;
import fortscale.web.tasks.ControllerInvokedAdTask.AdTaskType;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static fortscale.web.tasks.ControllerInvokedAdTask.AdTaskStatus;

@Controller
@RequestMapping(value = "/api/active_directory")
public class ApiActiveDirectoryController {

	public static final String SERVER_URL_ERROR = "Server URL is not available. Update the server URL and try again.";
	public static final String WRONG_CREDENTIALS_ERROR = "Wrong Credentials, please update and try again.";
	private static Logger logger = Logger.getLogger(ApiActiveDirectoryController.class);

	public String COLLECTION_TARGET_DIR;

	public String COLLECTION_USER;

	public String USER_HOME_DIR;

	private final long FETCH_AND_ETL_TIMEOUT_IN_SECONDS = 60;

	private final String SYSTEM_SETUP_AD_LAST_EXECUTION_TIME_PREFIX ="system_setup_ad.last_execution_time";

	private final String SYSTEM_SETUP_AD_EXECUTION_START_TIME_PREFIX ="system_setup_ad.execution_start_time";

	private final Set<AdObjectType> dataSources = new HashSet<>(Arrays.asList(AdObjectType.values()));

	private final AtomicBoolean isFetchEtlExecutionRequestInProgress = new AtomicBoolean(false);

	private final AtomicBoolean isFetchEtlExecutionRequestStopped = new AtomicBoolean(false);

	private Set<ControllerInvokedAdTask> activeTasks = ConcurrentHashMap.newKeySet(dataSources.size());

	private Long lastAdFetchEtlExecutionStartTime;

	private ExecutorService executorService;

	@Autowired
	private ActiveDirectoryService activeDirectoryService;

	@Autowired
	private ApplicationConfigurationService applicationConfigurationService;

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
		isFetchEtlExecutionRequestStopped.set(false);
		final String inProgressMsg = "Active Directory fetch and ETL already in progress. Can't execute again until the previous execution is finished. Request to execute ignored.";
		if (isFetchEtlExecutionRequestInProgress.compareAndSet(false, true)) {
			if (!activeTasks.isEmpty()) {
				logger.warn(inProgressMsg);
				isFetchEtlExecutionRequestInProgress.set(false);
				return new ResponseEntity<>(new ResponseEntityMessage(inProgressMsg), HttpStatus.FORBIDDEN);
			}

			try {
				lastAdFetchEtlExecutionStartTime = System.currentTimeMillis();
				logger.info("Starting Active Directory fetch and ETL");
				initExecutorService();
				final List<ControllerInvokedAdTask> adTasks = createAdTasks();
				if (isFetchEtlExecutionRequestStopped.get()) { //check that there weren't any requests to stop between execution and now (actual execution)
					final String stopMessage = "Active Directory fetch and ETL already was signaled to stop. Request to execute ignored.";
					logger.warn(stopMessage);
					isFetchEtlExecutionRequestInProgress.set(false);
					return new ResponseEntity<>(new ResponseEntityMessage(stopMessage), HttpStatus.LOCKED);
				}
				executeTasks(adTasks);
			} finally {
				isFetchEtlExecutionRequestInProgress.set(false);
			}

			return new ResponseEntity<>(new ResponseEntityMessage("Fetch and ETL is running."), HttpStatus.OK);
		}
		else {
			logger.warn(inProgressMsg);
			return new ResponseEntity<>(new ResponseEntityMessage(inProgressMsg), HttpStatus.LOCKED);
		}
	}


	@RequestMapping("/stop_ad_fetch_etl" )
	public ResponseEntity<ResponseEntityMessage> stopAdFetchAndEtlExecution() {
		if (!activeTasks.isEmpty()) {
			isFetchEtlExecutionRequestStopped.set(true);
			logger.info("Attempting to kill all running threads {}", activeTasks);
			executorService.shutdownNow();
			try {
				executorService.awaitTermination(FETCH_AND_ETL_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
				activeTasks = ConcurrentHashMap.newKeySet(dataSources.size());
			} catch (InterruptedException e) {
				final String msg = "Failed to await termination of running threads.";
				logger.error(msg);
				return new ResponseEntity<>(new ResponseEntityMessage(msg), HttpStatus.FORBIDDEN);
			}

			lastAdFetchEtlExecutionStartTime = null;
			return new ResponseEntity<>(new ResponseEntityMessage("AD fetch and ETL execution has stopped successfully"), HttpStatus.OK);
		} else {
			final String msg = "Attempted to stop threads was made but there are no running tasks.";
			logger.warn(msg);
			return new ResponseEntity<>(new ResponseEntityMessage(msg), HttpStatus.NOT_ACCEPTABLE);
		}
	}



	@RequestMapping(method = RequestMethod.GET,value = "/ad_etl_fetch_status")
	@LogException
	public FetchEtlExecutionStatus getJobStatus() {
		Set<AdTaskStatus> statuses = new HashSet<>();
		dataSources.forEach(datasource -> {
			final AdTaskType runningMode = getRunningMode(datasource);
			final Long currExecutionStartTime = getLastExecutionTime(runningMode, datasource);
			if (runningMode != null) { //running
				statuses.add(new AdTaskStatus(runningMode, datasource, -1L, -1L, currExecutionStartTime));
			}
			else { //not running
				Long currLastExecutionFinishTime;
				if (datasource == AdObjectType.USER_THUMBNAIL) {
					currLastExecutionFinishTime = getLastExecutionTime(AdTaskType.FETCH_ETL, datasource);
				}
				else {
					currLastExecutionFinishTime = getLastExecutionTime(AdTaskType.ETL, datasource);
				}
				final Long currObjectsCount = activeDirectoryService.getCount(datasource);
				statuses.add(new AdTaskStatus(null, datasource, currLastExecutionFinishTime, currObjectsCount, currExecutionStartTime));
			}
		});

		return new FetchEtlExecutionStatus(lastAdFetchEtlExecutionStartTime, statuses);
	}

	private List<ControllerInvokedAdTask> createAdTasks() {
		final List<ControllerInvokedAdTask> tasks = new ArrayList<>();
		for (AdObjectType dataSource : dataSources) {
			if (dataSource != AdObjectType.USER_THUMBNAIL) { //user thumbnail job shouldn't run initially
				final ControllerInvokedAdTask currTask = new ControllerInvokedAdTask(this, activeDirectoryService, applicationConfigurationService, dataSource);
				if (currTask.getDataSource() == AdObjectType.USER) { //user thumbnail job should run after user job
					currTask.addFollowingTask(new CompoundControllerInvokedAdTask(this, activeDirectoryService, applicationConfigurationService, AdObjectType.USER_THUMBNAIL));
				}
				tasks.add(currTask);
			}
		}

		return tasks;
	}

	/**
	 * this method returns the running mode (Fetch, ETL or null for not running) of the given {@code dataSource}
	 * @param datasource the datasource whose running mode we want
	 * @return Fetch, ETL or null for not running
	 */
	private AdTaskType getRunningMode(AdObjectType datasource) {
		for (ControllerInvokedAdTask activeThread : activeTasks) {
			if (datasource.equals(activeThread.getDataSource())) {
				return activeThread.getCurrentAdTaskType();
			}
		}

		return null;
	}

	public Long getLastExecutionTime(AdTaskType adTaskType, AdObjectType dataSource) {
		return applicationConfigurationService.getApplicationConfigurationAsObject(SYSTEM_SETUP_AD_LAST_EXECUTION_TIME_PREFIX + "_" + adTaskType + "_" + dataSource.toString(), Long.class);
	}

	public void setLastExecutionTime(AdTaskType adTaskType, AdObjectType dataSource, Long lastExecutionTime) {
		applicationConfigurationService.updateConfigItemAsObject(SYSTEM_SETUP_AD_LAST_EXECUTION_TIME_PREFIX + "_" + adTaskType + "_" + dataSource.toString(), lastExecutionTime);
	}

	public Long getExecutionStartTime(AdTaskType adTaskType, AdObjectType dataSource) {
		return applicationConfigurationService.getApplicationConfigurationAsObject(SYSTEM_SETUP_AD_EXECUTION_START_TIME_PREFIX + "_" + adTaskType + "_" + dataSource.toString(), Long.class);
	}

	public void setExecutionStartTime(AdTaskType adTaskType, AdObjectType dataSource, Long executionStartTime) {
		applicationConfigurationService.updateConfigItemAsObject(SYSTEM_SETUP_AD_EXECUTION_START_TIME_PREFIX + "_" + adTaskType + "_" + dataSource.toString(), executionStartTime);
	}

	public void sendTemplateMessage(String responseDestination, AdTaskResponse fetchResponse) {
		simpMessagingTemplate.convertAndSend(responseDestination, fetchResponse);
	}

	private void initExecutorService() {
		if (executorService != null && !executorService.isShutdown()) {
			return; // use the already working executor service
		}
		else {
			executorService = Executors.newFixedThreadPool(dataSources.size(), runnable -> {
				Thread thread = new Thread(runnable);
				thread.setUncaughtExceptionHandler((exceptionThrowingThread, e) -> logger.error("Thread {} threw an uncaught exception", exceptionThrowingThread.getName(), e));
				return thread;
			});
		}
	}

	public void executeTasks(List<ControllerInvokedAdTask> tasksToExecute) {
		for (ControllerInvokedAdTask controllerInvokedAdTask : tasksToExecute) {
			executeTask(controllerInvokedAdTask);
		}
	}

	private void executeTask(ControllerInvokedAdTask taskToExecute) {
		logger.debug("Executing task for data source {}.", taskToExecute.getDataSource());
		executorService.execute(taskToExecute);
	}


	public boolean addActiveTask(ControllerInvokedAdTask controllerInvokedAdTask) {
		return activeTasks.add(controllerInvokedAdTask);
	}

	public boolean removeActiveTask(ControllerInvokedAdTask controllerInvokedAdTask) {
		return activeTasks.remove(controllerInvokedAdTask);
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