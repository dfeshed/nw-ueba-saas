package fortscale.web.rest;

import fortscale.domain.ad.AdConnection;
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

	private static final String AD_USERS = "AD Users";
	private static final String AD_GROUPS = "AD Groups";
	private static final String AD_OU = "AD OU";
	private static final String AD_OTHER = "AD Other";
	private static final List<String> dataSources = new ArrayList<>(Arrays.asList(AD_USERS, AD_GROUPS, AD_OU, AD_OTHER));
	private static final String RESPONSE_DESTINATION = "/wizard/ad-fetch-response";

	private AtomicBoolean adTaskInProgress = new AtomicBoolean(false);


	@Autowired
	private ActiveDirectoryService activeDirectoryService;

	@Autowired
	private SimpMessagingTemplate template;

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
	
	@RequestMapping("/ad_fetch" )
	public ResponseEntity executeAdFetchAndEtl() {

		if (adTaskInProgress.compareAndSet(false, true)) {
			logger.debug("Starting Active Directory fetch and ETL");

			final ExecutorService executorService = Executors.newFixedThreadPool(4, runnable -> {
				Thread thread = new Thread(runnable);
				thread.setUncaughtExceptionHandler((exceptionThrowingThread, e) -> logger.error("Thread {} threw an uncaught exception", exceptionThrowingThread.getName(), e));
				return thread;
			});

			try {
				dataSources.forEach(dataSource -> executorService.execute(new FetchEtlTask(dataSource)));
			} finally {
				executorService.shutdown();
			}

			logger.debug("Finished Active Directory fetch and ETL");

			adTaskInProgress.set(false);
			return new ResponseEntity(HttpStatus.OK);
		}
		else {
			logger.warn("Active Directory fetch and ETL already in progress. Can't execute again until the previous execution is finished. Request to execute ignored.");
			return new ResponseEntity(HttpStatus.LOCKED);
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
		private int objectsCount;
		private String dataSource;

		public AdTaskResponse(AdTaskType taskType, boolean success, int objectsCount, String dataSource) {
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

		public int getObjectsCount() {
			return objectsCount;
		}

		public String getDataSource() {
			return dataSource;
		}

	}

	private class FetchEtlTask implements Runnable {

		public static final String TASK_RESULTS_PATH = "/tmp";
		public static final String DELIMITER = "=";
		public static final String KEY_SUCCESS = "success"; //Todo: should be shared between this and collection
		public static final String KEY_OBJECT_COUNT = "objectCount"; //Todo: should be shared between this and collection
		public static final String COLLECTION_JAR_NAME = "COLLECTION JAR NAME";
		public static final String THREAD_NAME = "deployment_wizard_fetch_and_etl";

		private final String dataSource;

		public FetchEtlTask(String dataSource) {
			this.dataSource = dataSource;
		}

		@Override
		public void run() {
			Thread.currentThread().setName(THREAD_NAME + "_" + dataSource);

			final AdTaskResponse fetchResponse = executeAdTask(FETCH, dataSource);
			template.convertAndSend(RESPONSE_DESTINATION, fetchResponse);

			final AdTaskResponse etlResponse = executeAdTask(ETL, dataSource);
			template.convertAndSend(RESPONSE_DESTINATION, etlResponse);
		}


		/**
		 * Runs a new process with the given arguments. This method is BLOCKING.
		 * @param adTaskType the type of task to run
		 * @param dataSource the data source
		 * @return an AdTaskResponse representing the results of the task
		 */
		private AdTaskResponse executeAdTask(AdTaskType adTaskType, String dataSource) {
			logger.debug("Executing task {} for data source {}", adTaskType, dataSource);
			Process process;
			try {
				final ArrayList<String> arguments = new ArrayList<>(Arrays.asList("java", "-jar", COLLECTION_JAR_NAME, adTaskType.toString(), dataSource));
				process = new ProcessBuilder(arguments).start();
			} catch (IOException e) {
				logger.error("Execution of task {} for data source {} has failed.", adTaskType, dataSource, e);
				return new AdTaskResponse(adTaskType, false, -1, dataSource);
			}
			int status;
			try {
				status = process.waitFor();
			} catch (InterruptedException e) {
				if (process.isAlive()) {
					logger.error("Killing the process forcibly");
					process.destroyForcibly();
				}
				logger.error("Execution of task {} for data source {} has failed. Task has been interrupted", adTaskType, dataSource, e);
				return new AdTaskResponse(adTaskType, false, -1, dataSource);
			}

			logger.debug("Execution of task {} for step {} has finished with status {}", adTaskType, dataSource, status);

			Map<String, String> taskResults = new HashMap<>();
			try (Stream<String> stream = Files.lines(Paths.get(TASK_RESULTS_PATH + "/" + adTaskType.toString() + "_" + dataSource))) {
				final List<String> lines = stream.collect(Collectors.toList());
				for (String line : lines) {
					final String[] split = line.split(DELIMITER);
					if (split.length != 2) {
						logger.error("Invalid output for task {} for data source {}. Task Failed", adTaskType, dataSource);
						return new AdTaskResponse(adTaskType, false, -1, dataSource);
					}

					taskResults.put(split[0], split[1]);
				}
			} catch (IOException e) {
				logger.error("Execution of task {} for data source {} has failed.", adTaskType, dataSource, e);
				return new AdTaskResponse(adTaskType, false, -1, dataSource);
			}

			final String success = taskResults.get(KEY_SUCCESS);
			if (success == null) {
				logger.error("Invalid output for task {} for data source {}. success status is missing. Task Failed", adTaskType, dataSource);
				return new AdTaskResponse(adTaskType, false, -1, dataSource);
			}

			final String objectsCount = taskResults.get(KEY_OBJECT_COUNT);
			if (objectsCount == null) {
				logger.error("Invalid output for task {} for dta source {}. Objects count is missing. Task Failed", adTaskType, dataSource);
				return new AdTaskResponse(adTaskType, false, -1, dataSource);
			}

			return new AdTaskResponse(adTaskType, Boolean.valueOf(success), Integer.parseInt(objectsCount), dataSource);
		}
	}




}