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
import org.springframework.beans.factory.InitializingBean;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fortscale.web.rest.ApiActiveDirectoryController.AdTaskType.ETL;
import static fortscale.web.rest.ApiActiveDirectoryController.AdTaskType.FETCH;

@Controller
@RequestMapping(value = "/api/active_directory")
public class ApiActiveDirectoryController implements InitializingBean {

	private static Logger logger = Logger.getLogger(ApiActiveDirectoryController.class);

	private static final String AD_USERS = "User";
	private static final String AD_GROUPS = "Group";
	private static final String AD_OU = "OU";
	private static final String AD_Devices = "Computer";
	//private static final List<String> dataSources = new ArrayList<>(Arrays.asList(AD_USERS, AD_GROUPS, AD_OU, AD_Devices));

	//private AtomicBoolean adTaskInProgress = new AtomicBoolean(false);
	private static Map<String,Boolean> adTaskInProgress = new HashMap<>();
    private static final List<AdObject.AdObjectType> dataSources = new ArrayList<>(Arrays.asList(AdObject.AdObjectType.values()));
    private static final String RESPONSE_DESTINATION = "/wizard/ad_fetch_etl_response";

	//private AtomicInteger numAdTasksInProgress = new AtomicInteger(0);
	public void afterPropertiesSet() throws Exception {
		dataSources.forEach(dataSource -> adTaskInProgress.put(dataSource.toString(), Boolean.FALSE));
	}

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
	
	@RequestMapping("/ad_fetch_etl" )
	public ResponseEntity executeAdFetchAndEtl() {

		//if (numAdTasksInProgress.compareAndSet(0, 1)) {


		if (!this.isAnyTaskRunning()){

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

			//numAdTasksInProgress.decrementAndGet();
			return new ResponseEntity(HttpStatus.OK);
		}
		else {
			logger.warn("Active Directory fetch and ETL already in progress. Can't execute again until the previous execution is finished. Request to execute ignored.");
			return new ResponseEntity(HttpStatus.LOCKED);
		}


	}

	@RequestMapping(method = RequestMethod.GET,value = "/ad_etl_fetch_status")
	@LogException
	public List<JobStatus> getJobStatus() {
		List<JobStatus> statuses = new ArrayList<>();
		dataSources.forEach(dataSource -> {
			statuses.add(new JobStatus(dataSource.toString(), new Date().getTime(),ApiActiveDirectoryController.adTaskInProgress.get(dataSource.toString())));
		});
		return statuses;

	}

	private boolean isAnyTaskRunning(){

		for (Boolean isRunning : this.adTaskInProgress.values()){
			if(isRunning){
				return  true;
			}
		}
		return  false;// No task run
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

	public static class JobStatus{
		private String dataSource;
		private long lastSuccessfullExecution;
		private boolean isRunningNow;

		public JobStatus(String dataSource, long lastSuccessfullExecution, boolean isRunningNow) {
			this.dataSource = dataSource;
			this.lastSuccessfullExecution = lastSuccessfullExecution;
			this.isRunningNow = isRunningNow;
		}

		public String getDataSource() {
			return dataSource;
		}

		public long getLastSuccessfullExecution() {
			return lastSuccessfullExecution;
		}

		public boolean isRunningNow() {
			return isRunningNow;
		}

	}

	public static class AdTaskResponse {
		private AdTaskType taskType;
		private boolean success;
		private long objectsCount;
		private String dataSource;
		private long lastSuccessfullExecution;
		
		public AdTaskResponse(AdTaskType taskType, boolean success, long objectsCount, String dataSource, long lastSuccessfullExecution) {
			this.taskType = taskType;
			this.success = success;
			this.objectsCount = objectsCount;
			this.dataSource = dataSource;
			this.lastSuccessfullExecution = lastSuccessfullExecution;

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

		public long getLastSuccessfullExecution() {
			return lastSuccessfullExecution;
		}

	}

	private class FetchEtlTask implements Runnable {

		public static final String TASK_RESULTS_PATH = "/tmp";
		public static final String DELIMITER = "=";
		public static final String KEY_SUCCESS = "success";
		public static final String COLLECTION_JAR_NAME = "${user.home.dir}/fortscale/fortscale-core/fortscale/fortscale-collection/target/fortscale-collection-1.1.0-SNAPSHOT.jar";
		public static final String THREAD_NAME = "deployment_wizard_fetch_and_etl";
        public static final String AD_JOB_GROUP = "AD";

        private final AdObject.AdObjectType dataSource;

		public FetchEtlTask(AdObject.AdObjectType dataSource) {
			this.dataSource = dataSource;
		}

		@Override
		public void run() {
			//numAdTasksInProgress.incrementAndGet();
			Thread.currentThread().setName(THREAD_NAME + "_" + dataSource);
			ApiActiveDirectoryController.adTaskInProgress.put(dataSource.toString(),Boolean.TRUE);
			final AdTaskResponse fetchResponse = executeAdTask(FETCH, dataSource);
			template.convertAndSend(RESPONSE_DESTINATION, fetchResponse);

			final AdTaskResponse etlResponse = executeAdTask(ETL, dataSource));
			template.convertAndSend(RESPONSE_DESTINATION, etlResponse);
			//ApiActiveDirectoryController.adTaskInProgress.put(dataSource,Boolean.FALSE);
		}


		/**
		 * Runs a new process with the given arguments. This method is BLOCKING.
		 * @param adTaskType the type of task to run
		 * @param dataSource the data source
		 * @return an AdTaskResponse representing the results of the task
		 */
		private AdTaskResponse executeAdTask(AdTaskType adTaskType, AdObject.AdObjectType dataSource) {
            final String dataSourceName = dataSource.toString();
			logger.debug("Executing task {} for data source {}", adTaskType, dataSource);
			Process process;
			UUID resultsFileId = UUID.randomUUID();
			final String filePath = TASK_RESULTS_PATH + "/" + dataSourceName.toLowerCase() + "_" + adTaskType.toString().toLowerCase() + "_" + resultsFileId;
			try {
				final String jobName = dataSource + "_" + adTaskType.toString();
				final ArrayList<String> arguments = new ArrayList<>(Arrays.asList("java", "-jar", COLLECTION_JAR_NAME, jobName, "AD", "resultsFileId="+resultsFileId));
				process = new ProcessBuilder(arguments).start();
			} catch (IOException e) {
				logger.error("Execution of task {} for data source {} has failed.", adTaskType, dataSource, e);
				return new AdTaskResponse(adTaskType, false, -1, dataSourceName, new Date().getTime());
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
				return new AdTaskResponse(adTaskType, false, -1, dataSource.toString(),new Date().getTime());
			}




            /* run task */
            if (!runTask(dataSourceName, adTaskType, resultsFileId)) {
                //numAdTasksInProgress.decrementAndGet();
                return new AdTaskResponse(adTaskType, false, -1, dataSourceName, new Date().getTime());
            }

            /* get task results from file */
            final Map<String, String> taskResults = getTaskResults(dataSourceName, adTaskType, filePath);
            if (taskResults == null) {
               // numAdTasksInProgress.decrementAndGet();
                return new AdTaskResponse(adTaskType, false, -1, dataSourceName, new Date().getTime());
            }

            /* process results and understand if task finished successfully */
			final String success = taskResults.get(KEY_SUCCESS);
			if (success == null) {
				logger.error("Invalid output for task {} for data source {}. success status is missing. Task Failed", adTaskType, dataSourceName);
			//	numAdTasksInProgress.decrementAndGet();
				return new AdTaskResponse(adTaskType, false, -1, dataSourceName, new Date().getTime());
			}

			/* get objects count for this data source from mongo */
            final long objectsCount = activeDirectoryService.getRepository(dataSource).count();

			//numAdTasksInProgress.decrementAndGet();
			return new AdTaskResponse(adTaskType, Boolean.valueOf(success), objectsCount, dataSourceName, new Date().getTime());
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




}