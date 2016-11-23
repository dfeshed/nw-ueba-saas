package fortscale.web.rest;

import fortscale.domain.ad.AdConnection;
import fortscale.domain.ad.AdObject;
import fortscale.services.ActiveDirectoryService;
import fortscale.utils.EncryptionUtils;
import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.HideSensitiveArgumentsFromLog;
import fortscale.utils.logging.annotation.LogException;
import fortscale.utils.logging.annotation.LogSensitiveFunctionsAsEnum;
import fortscale.web.beans.request.ActiveDirectoryRequest;
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fortscale.web.rest.ApiActiveDirectoryController.AdTaskType.ETL;
import static fortscale.web.rest.ApiActiveDirectoryController.AdTaskType.FETCH;

@Controller
@RequestMapping(value = "/api/active_directory")
public class ApiActiveDirectoryController {

	private static Logger logger = Logger.getLogger(ApiActiveDirectoryController.class);
	private static final String RESPONSE_DESTINATION = "/wizard/ad_fetch_etl_response";

	private final List<AdObject.AdObjectType> dataSources = new ArrayList<>(Arrays.asList(AdObject.AdObjectType.values()));

	private final AtomicBoolean isExecutionRequestInProgress = new AtomicBoolean(false);

	private final Set<AdTask> activeThreads = ConcurrentHashMap.newKeySet(dataSources.size());

	@Autowired
	private ActiveDirectoryService activeDirectoryService;

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
	public ResponseEntity testActiveDirectoryConnection(@Valid @RequestBody AdConnection activeDirectoryDomain,
														@RequestParam(required = true, value = "encrypted_password") Boolean encryptedPassword) {
		if (!encryptedPassword) {
			try {
				activeDirectoryDomain.setDomainPassword(EncryptionUtils.encrypt(activeDirectoryDomain.
						getDomainPassword()));
			} catch (Exception ex) {
				logger.error("failed to encrypt password");
				return new ResponseEntity(ex.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
			}
		}
		String result = activeDirectoryService.canConnect(activeDirectoryDomain);
		if (result.isEmpty()) {
			return new ResponseEntity(HttpStatus.OK);
		} else {
			return new ResponseEntity(result, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(method = RequestMethod.GET)
	@LogException
	public List<AdConnection> getActiveDirectory() {
		return activeDirectoryService.getAdConnectionsFromDatabase();
	}

	@RequestMapping("/ad_fetch_etl" )
	public ResponseEntity executeAdFetchAndEtl() {
		if (isExecutionRequestInProgress.compareAndSet(false, true)) {
			if (!activeThreads.isEmpty()) {
				logger.warn("Active Directory fetch and ETL already in progress. Can't execute again until the previous execution is finished. Request to execute ignored.");
				isExecutionRequestInProgress.set(false);
				return new ResponseEntity(HttpStatus.LOCKED);
			}
			logger.debug("Starting Active Directory fetch and ETL");

			final ExecutorService executorService = Executors.newFixedThreadPool(dataSources.size(), runnable -> {
				Thread thread = new Thread(runnable);
				thread.setUncaughtExceptionHandler((exceptionThrowingThread, e) -> logger.error("Thread {} threw an uncaught exception", exceptionThrowingThread.getName(), e));
				return thread;
			});


			try {
				dataSources.forEach(dataSource -> executorService.execute(new AdTask(dataSource)));
			} finally {
				executorService.shutdown();
			}

			logger.debug("Finished Active Directory fetch and ETL");

			isExecutionRequestInProgress.set(false);
			return new ResponseEntity(HttpStatus.OK);
		}
		else {
			logger.warn("Active Directory fetch and ETL already in progress. Can't execute again until the previous execution is finished. Request to execute ignored.");
			return new ResponseEntity(HttpStatus.LOCKED);
		}
	}

	


	private class AdTask implements Runnable {

		public static final String TASK_RESULTS_PATH = "/tmp";
		public static final String DELIMITER = "=";
		public static final String KEY_SUCCESS = "success";
		public static final String COLLECTION_JAR_NAME = "${user.home.dir}/fortscale/fortscale-core/fortscale/fortscale-collection/target/fortscale-collection-1.1.0-SNAPSHOT.jar";
		public static final String THREAD_NAME = "deployment_wizard_fetch_and_etl";
		public static final String AD_JOB_GROUP = "AD";

		private final AdObject.AdObjectType dataSource;

		public AdTask(AdObject.AdObjectType dataSource) {
			this.dataSource = dataSource;
		}

		@Override
		public void run() {
			notifyTaskStart();
			Thread.currentThread().setName(THREAD_NAME + "_" + dataSource);

			final AdTaskResponse fetchResponse = executeAdTask(FETCH, dataSource);
			simpMessagingTemplate.convertAndSend(RESPONSE_DESTINATION, fetchResponse);

			final AdTaskResponse etlResponse = executeAdTask(ETL, dataSource);
			simpMessagingTemplate.convertAndSend(RESPONSE_DESTINATION, etlResponse);
		}

		private void notifyTaskStart() {
			if (!activeThreads.add(this)) {
				logger.warn("Tried to add task but the task already exists. This may occur due to concurrency issues.");
			}
		}

		private void notifyTaskDone() {
			if (!activeThreads.remove(this)) {
				logger.warn("Tried to remove task but task doesn't exist. This may occur due to concurrency issues.");
			}
		}


		/**
		 * Runs a new process with the given arguments. This method is BLOCKING.
		 * @param adTaskType the type of task to run (fetch/etl)
		 * @param dataSource the data source (user/groups/etc..)
		 * @return an AdTaskResponse representing the results of the task
		 */
		private AdTaskResponse executeAdTask(AdTaskType adTaskType, AdObject.AdObjectType dataSource) {
			final String dataSourceName = dataSource.toString();
			logger.debug("Executing task {} for data source {}", adTaskType, dataSourceName);

			UUID resultsFileId = UUID.randomUUID();
			final String filePath = TASK_RESULTS_PATH + "/" + dataSourceName.toLowerCase() + "_" + adTaskType.toString().toLowerCase() + "_" + resultsFileId;

            /* run task */
			if (!runTask(dataSourceName, adTaskType, resultsFileId)) {
				notifyTaskDone();
				return new AdTaskResponse(adTaskType, false, -1, dataSourceName);
			}

            /* get task results from file */
			final Map<String, String> taskResults = getTaskResults(dataSourceName, adTaskType, filePath);
			if (taskResults == null) {
				notifyTaskDone();
				return new AdTaskResponse(adTaskType, false, -1, dataSourceName);
			}

            /* process results and understand if task finished successfully */
			final String success = taskResults.get(KEY_SUCCESS);
			if (success == null) {
				logger.error("Invalid output for task {} for data source {}. success status is missing. Task Failed", adTaskType, dataSourceName);
				notifyTaskDone();
				return new AdTaskResponse(adTaskType, false, -1, dataSourceName);
			}

			/* get objects count for this data source from mongo */
			final long objectsCount = activeDirectoryService.getRepository(dataSource).count();

			notifyTaskDone();
			return new AdTaskResponse(adTaskType, Boolean.valueOf(success), objectsCount, dataSourceName);
		}




		private Map<String, String> getTaskResults(Object dataSourceName, Object adTaskType, String filePath) {
			Map<String, String> taskResults = new HashMap<>();
			try {
				try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
					final List<String> lines = stream.collect(Collectors.toList());
					for (String line : lines) {
						final String[] split = line.split(DELIMITER);
						if (split.length != 2) {
							logger.error("Invalid output for task {} for data source {}. Task Failed", adTaskType, dataSourceName);
							return null;
						}

						taskResults.put(split[0], split[1]);
					}
				} catch (IOException e) {
					logger.error("Execution of task {} for data source {} has failed.", adTaskType, dataSourceName, e);
					return null;
				}
			} finally {
				try {
					Files.delete(Paths.get(filePath));
				} catch (IOException e) {
					logger.warn("Failed to delete results file {}.", filePath);
				}
			}

			return taskResults;
		}

		private boolean runTask(String dataSourceName, AdTaskType adTaskType, UUID resultsFileId) {
			Process process;
			try {
				final String jobName = dataSourceName + "_" + adTaskType.toString();
				final ArrayList<String> arguments = new ArrayList<>(Arrays.asList("java", "-jar", COLLECTION_JAR_NAME, jobName, AD_JOB_GROUP, "resultsFileId="+resultsFileId));
				process = new ProcessBuilder(arguments).start();
			} catch (IOException e) {
				logger.error("Execution of task {} for data source {} has failed.", adTaskType, dataSourceName, e);
				return false;
			}
			int status;
			try {
				status = process.waitFor();
			} catch (InterruptedException e) {
				if (process.isAlive()) {
					logger.error("Killing the process forcibly");
					process.destroyForcibly();
				}
				logger.error("Execution of task {} for data source {} has failed. Task has been interrupted", adTaskType, dataSourceName, e);
				return false;
			}

			logger.debug("Execution of task {} for step {} has finished with status {}", adTaskType, dataSourceName, status);
			return true;
		}
	}



	public enum AdTaskType {
		FETCH("Fetch"), ETL("ETL");

		private final String type;

		AdTaskType(String type) {
			this.type = type;
		}

		@Override
		public String toString() {
			return type;
		}
	}

	public static class AdTaskResponse {
		private AdTaskType taskType;
		private boolean success;
		private long objectsCount;
		private String dataSource;

		public AdTaskResponse(AdTaskType taskType, boolean success, long objectsCount, String dataSource) {
			this.taskType = taskType;
			this.success = success;
			this.objectsCount = objectsCount;
			this.dataSource = dataSource;
		}

		public AdTaskType getTaskType() {
			return taskType;
		}

		public boolean isSuccess() {
			return success;
		}

		public long getObjectsCount() {
			return objectsCount;
		}

		public String getDataSource() {
			return dataSource;
		}

	}


}