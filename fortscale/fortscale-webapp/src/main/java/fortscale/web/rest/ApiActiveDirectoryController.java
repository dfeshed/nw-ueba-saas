package fortscale.web.rest;

import fortscale.domain.ad.AdConnection;
import fortscale.domain.ad.AdObject.AdObjectType;
import fortscale.services.ActiveDirectoryService;
import fortscale.services.ApplicationConfigurationService;
import fortscale.utils.EncryptionUtils;
import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.HideSensitiveArgumentsFromLog;
import fortscale.utils.logging.annotation.LogException;
import fortscale.utils.logging.annotation.LogSensitiveFunctionsAsEnum;
import fortscale.web.beans.request.ActiveDirectoryRequest;
import fortscale.web.tasks.ControllerInvokedAdTask;
import fortscale.web.tasks.ControllerInvokedAdTask.AdTaskResponse;
import fortscale.web.tasks.ControllerInvokedAdTask.AdTaskType;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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

	private static Logger logger = Logger.getLogger(ApiActiveDirectoryController.class);

	public final String COLLECTION_TARGET_DIR = System.getProperty("user.home") + "/fortscale/fortscale-core/fortscale/fortscale-collection/target";

	public final String COLLECTION_JAR_NAME = "fortscale-collection-1.1.0-SNAPSHOT.jar";

	private final long FETCH_AND_ETL_TIMEOUT_IN_SECONDS = 60;

	private final String DEPLOYMENT_WIZARD_AD_LAST_EXECUTION_TIME_PREFIX ="deployment_wizard_ad.last_execution_time";

	private final List<AdObjectType> dataSources = new ArrayList<>(Arrays.asList(AdObjectType.values()));

	private final AtomicBoolean isFetchEtlExecutionRequestInProgress = new AtomicBoolean(false);

	private final AtomicBoolean isFetchEtlExecutionRequestStopped = new AtomicBoolean(false);

	private Set<ControllerInvokedAdTask> activeThreads = ConcurrentHashMap.newKeySet(dataSources.size());

	private Long lastAdFetchEtlExecutionStartTime;

	private ExecutorService executorService;

	@Autowired
	private ActiveDirectoryService activeDirectoryService;

	@Autowired
	private ApplicationConfigurationService applicationConfigurationService;

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;


	/**
	 * Updates or creates config items.
	 *
	 * @return ResponseEntity
	 * @throws JSONException
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
	 * @throws JSONException
	 */
	@RequestMapping(method = RequestMethod.POST,value = "/test")
	@HideSensitiveArgumentsFromLog(sensitivityCondition = LogSensitiveFunctionsAsEnum.APPLICATION_CONFIGURATION)
	@LogException
	public ResponseEntity<String> testActiveDirectoryConnection(@Valid @RequestBody AdConnection activeDirectoryDomain,
																@RequestParam(value = "encrypted_password") Boolean encryptedPassword) {
		if (!encryptedPassword) {
			try {
				activeDirectoryDomain.setDomainPassword(EncryptionUtils.encrypt(activeDirectoryDomain.
						getDomainPassword()));
			} catch (Exception ex) {
				logger.error("failed to encrypt password");
				return new ResponseEntity<>(ex.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
			}
		}
		String result = activeDirectoryService.canConnect(activeDirectoryDomain);
		if (result.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(method = RequestMethod.GET)
	@LogException
	public List<AdConnection> getActiveDirectory() {
		return activeDirectoryService.getAdConnectionsFromDatabase();
	}

	@RequestMapping("/ad_fetch_etl" )
	public ResponseEntity executeAdFetchAndEtl() {
		isFetchEtlExecutionRequestStopped.set(false);
		if (isFetchEtlExecutionRequestInProgress.compareAndSet(false, true)) {
			if (!activeThreads.isEmpty()) {
				logger.warn("Active Directory fetch and ETL already in progress. Can't execute again until the previous execution is finished. Request to execute ignored.");
				isFetchEtlExecutionRequestInProgress.set(false);
				return new ResponseEntity(HttpStatus.FORBIDDEN);
			}
			lastAdFetchEtlExecutionStartTime = System.currentTimeMillis();

			logger.info("Starting Active Directory fetch and ETL");

			try {
				executorService = createExecutorService();
				if (isFetchEtlExecutionRequestStopped.get()) { //check that not asked to stop between execution and now (actual execution)
					logger.warn("Active Directory fetch and ETL already was signaled to stop. Request to execute ignored.");
					isFetchEtlExecutionRequestInProgress.set(false);
					return new ResponseEntity(HttpStatus.LOCKED);
				}
				dataSources.forEach(dataSource -> executorService.execute(new ControllerInvokedAdTask(this, activeDirectoryService, applicationConfigurationService, dataSource)));
			} finally {
				executorService.shutdown();
			}

			logger.info("Finished Active Directory fetch and ETL");

			isFetchEtlExecutionRequestInProgress.set(false);
			return new ResponseEntity(HttpStatus.OK);
		}
		else {
			logger.warn("Active Directory fetch and ETL already in progress. Can't execute again until the previous execution is finished. Request to execute ignored.");
			return new ResponseEntity(HttpStatus.LOCKED);
		}
	}

	@RequestMapping("/stop_ad_fetch_etl" )
	public ResponseEntity<String> stopAdFetchAndEtlExecution() {
		if (!activeThreads.isEmpty()) {
			isFetchEtlExecutionRequestStopped.set(true);
			logger.info("Attempting to kill all running threads {}", activeThreads);
			executorService.shutdownNow();
			try {
				executorService.awaitTermination(FETCH_AND_ETL_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
				activeThreads = ConcurrentHashMap.newKeySet(dataSources.size());
			} catch (InterruptedException e) {
				final String msg = "Failed to await termination of running threads.";
				logger.error(msg);
				return new ResponseEntity<>(msg, HttpStatus.FORBIDDEN);
			} finally {
				executorService = createExecutorService();
			}

			lastAdFetchEtlExecutionStartTime = null;
			return new ResponseEntity<>("AD fetch and ETL execution has stopped successfully", HttpStatus.OK);
		} else {
			final String msg = "Attempted to stop threads was made but there are no running tasks.";
			logger.warn(msg);
			return new ResponseEntity<>(msg, HttpStatus.NOT_ACCEPTABLE);
		}
	}



	@RequestMapping(method = RequestMethod.GET,value = "/ad_etl_fetch_status")
	@LogException
	public FetchEtlExecutionStatus getJobStatus() {
		Set<AdTaskStatus> statuses = new HashSet<>();
		dataSources.forEach(datasource -> {
			final AdTaskType runningMode = getRunningMode(datasource);
			if (runningMode != null) { //running
				statuses.add(new AdTaskStatus(runningMode, datasource, -1L, -1L));
			}
			else { //not running
				final Long currLastExecutionFinishTime = getLastExecutionTime(AdTaskType.ETL, datasource);
				final Long currObjectsCount = activeDirectoryService.getCount(datasource);
				statuses.add(new AdTaskStatus(null, datasource, currLastExecutionFinishTime, currObjectsCount));
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
		for (ControllerInvokedAdTask activeThread : activeThreads) {
			if (datasource.equals(activeThread.getDataSource())) {
				return activeThread.getCurrentAdTaskType();
			}
		}

		return null;
	}

	public Long getLastExecutionTime(AdTaskType adTaskType, AdObjectType dataSource) {
		return applicationConfigurationService.getApplicationConfigurationAsObject(DEPLOYMENT_WIZARD_AD_LAST_EXECUTION_TIME_PREFIX + "_" + adTaskType + "_" + dataSource.toString(), Long.class);
	}

	public void setLastExecutionTime(AdTaskType adTaskType, AdObjectType dataSource, Long lastExecutionTime) {
		applicationConfigurationService.updateConfigItemAsObject(DEPLOYMENT_WIZARD_AD_LAST_EXECUTION_TIME_PREFIX + "_" + adTaskType + "_" + dataSource.toString(), lastExecutionTime);
	}

	public void sendTemplateMessage(String responseDestination, AdTaskResponse fetchResponse) {
		simpMessagingTemplate.convertAndSend(responseDestination, fetchResponse);
	}

	private ExecutorService createExecutorService() {
		return Executors.newFixedThreadPool(dataSources.size(), runnable -> {
			Thread thread = new Thread(runnable);
			thread.setUncaughtExceptionHandler((exceptionThrowingThread, e) -> logger.error("Thread {} threw an uncaught exception", exceptionThrowingThread.getName(), e));
			return thread;
		});
	}

	public boolean addRunningTask(ControllerInvokedAdTask controllerInvokedAdTask) {
		logger.info("Adding running task {}", controllerInvokedAdTask);
		return activeThreads.add(controllerInvokedAdTask);
	}

	public boolean removeRunningTask(ControllerInvokedAdTask controllerInvokedAdTask) {
		logger.info("Removing running task {}", controllerInvokedAdTask);
		return activeThreads.remove(controllerInvokedAdTask);
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