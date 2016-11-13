package fortscale.collection.jobs.cleanup;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.utils.cleanup.CleanupDeletionUtil;
import fortscale.utils.cleanup.CleanupUtil;
import fortscale.services.cloudera.ClouderaService;
import fortscale.services.hdfs.HDFSService;
import fortscale.services.impala.ImpalaService;
import fortscale.services.kafka.KafkaService;
import fortscale.utils.logging.Logger;
import fortscale.services.mongo.MongoService;
import fortscale.services.store.StoreService;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Amir Keren on 18/09/2015.
 *
 * This task is in charge of cleaning various information from the system
 *
 */
public class CleanJob extends FortscaleJob {

	private static Logger logger = Logger.getLogger(CleanJob.class);

	public enum Technology { MONGO, HDFS, KAFKA, STORE, IMPALA, ALL }
	public enum Strategy { DELETE, FASTDELETE, RESTORE }

	@Autowired
	private CleanupManagement cleanupManagement;

	@Autowired
	private MongoService mongoService;
	@Autowired
	private KafkaService kafkaService;
	@Autowired
	private ImpalaService impalaService;
	@Autowired
	private HDFSService hdfsService;
	@Autowired
	private StoreService storeService;
	@Autowired
	private ClouderaService clouderaService;

	@Value("${seconds.to.sleep}")
	private int secondsToSleep;
	@Value("${no.prompt.param}")
	private String noPromptParam;
	@Value("${start.time.param}")
	private String startTimeParam;
	@Value("${end.time.param}")
	private String endTimeParam;
	@Value("${technology.param}")
	private String technologyParam;
	@Value("${strategy.param}")
	private String strategyParam;
	@Value("${data.sources.param}")
	private String dataSourcesParam;
	@Value("${data.sources.delimiter}")
	private String dataSourcesDelimiter;
	@Value("${data.sources.field.delimiter}")
	private String dataSourcesFieldDelimiter;
	@Value("${data.sources.optional.delimiter}")
	private String optionalDelimiter;
	@Value("${dates.format}")
	private String datesFormat;
	@Value("${kafka.service.name}")
	private String kafkaServiceName;
	@Value("${streaming.service.name}")
	private String streamingServiceName;
	@Value("${collection.service.name}")
	private String collectionServiceName;
	@Value("${step.param}")
	private String stepParam;
	@Value("${brutal.delete}")
	private String boolBrutalDelete;

	private Date startTime;
	private Date endTime;
	private Map<String, String> dataSources;
	private Strategy strategy;
	private Technology technology;
	private CleanupStep cleanupStep;
	private String cleanupStepId;
	//delete files physically
	private boolean isBrutalDelete;
	private boolean displayPrompt;

	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();
		DateFormat sdf = new SimpleDateFormat(datesFormat);
		if (map.containsKey(noPromptParam)) {
			displayPrompt = !jobDataMapExtension.getJobDataMapBooleanValue(map, noPromptParam, false);
		} else {
			displayPrompt = true;
		}
		// get parameters values from the job data map
		try {
			if (map.containsKey(startTimeParam)) {
				startTime = sdf.parse(jobDataMapExtension.getJobDataMapStringValue(map, startTimeParam));
			}
			if (map.containsKey(endTimeParam)) {
				endTime = sdf.parse(jobDataMapExtension.getJobDataMapStringValue(map, endTimeParam));
			}
		} catch (ParseException ex) {
			logger.error("Bad date format - {}", ex.getMessage());
			throw new JobExecutionException(ex);
		}
		if (map.containsKey(stepParam)) {
			cleanupStepId = map.getString(stepParam);
			cleanupStep = cleanupManagement.getCleanupStep(cleanupStepId);
			if (cleanupStep == null) {
				logger.error("No step {} found", map.getString(stepParam));
				throw new JobExecutionException();
			}
			//if step param passed - ignore all other parameters
			return;
		}
		technology = Technology.valueOf(jobDataMapExtension.getJobDataMapStringValue(map, technologyParam));
		isBrutalDelete = jobDataMapExtension.getJobDataMapBooleanValue(map, boolBrutalDelete, true);
		kafkaService.setIsBrutalDelete(isBrutalDelete);
		strategy = Strategy.valueOf(jobDataMapExtension.getJobDataMapStringValue(map, strategyParam));
		if (map.containsKey(dataSourcesParam)) {
			dataSources = createDataSourcesMap(jobDataMapExtension.getJobDataMapStringValue(map, dataSourcesParam));
		}
	}

	@Override
	protected void runSteps() {
		startNewStep("Clean Job");
		if (displayPrompt) {
			if (cleanupStep != null) {
				System.out.println(cleanupStep.getDescription());
			} else if (strategy == Strategy.DELETE || strategy == Strategy.FASTDELETE) {
				if (technology == technology.ALL) {
					System.out.println("This will delete EVERYTHING!!!!exclamation mark!! it's the equivalent of " +
							"dropping an atom bomb on the machine!!");
				} else {
					System.out.println("This will delete " + technology);
				}
			}
			System.out.println("Are you sure? [Yes/literally anything else]");
			Scanner scanner = new Scanner(System.in);
			String input;
			try {
				input = scanner.nextLine();
			} catch (NoSuchElementException ex) {
				logger.error("Do not run Clean Job with nohup and/or in the background! aborting cleanup");
				return;
			}
			InputStreamReader fileInputStream = new InputStreamReader(System.in);
			BufferedReader bufferedReader = new BufferedReader(fileInputStream);
			if (input.equals("Yes")) {
				for (int i = secondsToSleep; i > 0; i--) {
					System.out.println("Cleanup starting in " + i + " seconds... Press Enter to interrupt...");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException ex) {
						logger.error("cleanup sleep interrupted - {}", ex);
						return;
					}
					try {
						if (bufferedReader.ready()) {
							System.out.println("Countdown stopped, aborting cleanup");
							return;
						}
					} catch (IOException ex) {
						logger.error("cleanup countdown interrupted - {}", ex);
						return;
					}
				}
			} else {
				System.out.println("Did not enter 'Yes', aborting cleanup");
				return;
			}
		}
		System.out.println("Cleanup starting");
		boolean success;
		//bdp run
		if (cleanupStep != null) {
			success = bdpClean(cleanupStep, startTime, endTime);
			//normal run
		} else {
			success = normalClean(strategy, technology, dataSources, startTime, endTime);
		}
		if (success) {
			logger.info("Clean job successful");
		} else {
			logger.error("Clean job failed");
			System.exit(1);
		}
		finishStep();
	}

	/***
	 *
	 * This method runs the cleaning steps for BDP
	 *
	 * @param cleanupStep  the specific step to run
	 * @param startTime    the starting time of entities to address
	 * @param endTime      the ending time of entities to address
	 * @return
	 */
	private boolean bdpClean(CleanupStep cleanupStep, Date startTime, Date endTime) {
		boolean success = false;
		int successfulSteps = 0, totalSteps = 0;
		Map<String, String> dataSources;
		List<MiniStep> miniSteps = cleanupStep.getTimeBasedSteps();
		if (miniSteps != null) {
			totalSteps += miniSteps.size();
			//running all time based mini steps
			for (int i = 0; i < miniSteps.size(); i++) {
				MiniStep miniStep = miniSteps.get(i);
				dataSources = createDataSourcesMap(miniStep.getDataSources());
				success = normalClean(miniStep.getStrategy(), miniStep.getTechnology(), dataSources, startTime, endTime);
				if (!success) {
					logger.error("Time based step {}: {} - failed", i + 1, miniStep.toString());
				} else {
					logger.info("Time based step {}: {} - succeeded", i + 1, miniStep.toString());
					successfulSteps++;
				}
			}
		}
		miniSteps = cleanupStep.getOtherSteps();
		if (miniSteps != null) {
			totalSteps += miniSteps.size();
			//running all other mini steps
			for (int i = 0; i < miniSteps.size(); i++) {
				MiniStep miniStep = miniSteps.get(i);
				dataSources = createDataSourcesMap(miniStep.getDataSources());
				success = normalClean(miniStep.getStrategy(), miniStep.getTechnology(), dataSources, null, null);
				if (!success) {
					logger.error("Normal step {}: {} - failed", i + 1, miniStep.toString());
				} else {
					logger.info("Normal step {}: {} - succeeded", i + 1, miniStep.toString());
					successfulSteps++;
				}
			}
		}
		logger.info("Finished cleaning {} out of {} mini steps", successfulSteps, totalSteps);
		if (successfulSteps == totalSteps) {
			logger.info("Cleanup step {} completed successfully", cleanupStepId);
			success = true;
		} else {
			logger.error("Cleanup step {} failed to complete", cleanupStepId);
		}
		return success;
	}

	/***
	 *
	 * This method runs the normal cleaning procedure
	 *
	 * @param strategy     the strategy to run (delete, restore etc.)
	 * @param technology   the technology to run (mongo, hdfs etc.)
	 * @param dataSources  the entities and parameters to run
	 * @param startTime    the starting time of entities to address
	 * @param endTime      the ending time of entities to address
	 * @return
	 */
	private boolean normalClean(Strategy strategy, Technology technology, Map<String, String> dataSources,
								Date startTime, Date endTime) {
		boolean success = false;
		//if command is to delete everything
		if ((strategy == Strategy.DELETE || strategy == Strategy.FASTDELETE) && technology == Technology.ALL) {
            //if fast delete - no validation is performed
            success = clearAllData(strategy == Strategy.DELETE);
        } else {
            switch (strategy) {
                case DELETE:
					checkAndStopAllRelevantServices();
                case FASTDELETE: {
                    //if fast delete - no validation is performed
                    success = deleteEntities(technology, dataSources, startTime, endTime, strategy == Strategy.DELETE);
                    break;
                }
                case RESTORE: {
                    logger.info("restoring {} entities", dataSources.size());
                    success = restoreEntities(technology, dataSources);
                    break;
                }
            }
        }
		return success;
	}

	/***
	 *
	 * This method handles the delete command, determines which technology to use and executes it
	 *
	 * @param technology  technology to use (mongo, hdfs, etc.)
	 * @param toDelete    collection of key,value (keys are collection/tables/topic/hdfs paths etc)
	 * @param startDate   start date to filter deletion by
	 * @param endDate	  end date to filter deletion by
	 * @param doValidate  flag to determine should we perform validations
	 * @return
	 */
	private boolean deleteEntities(Technology technology, Map<String, String> toDelete, Date startDate, Date endDate,
								   boolean doValidate) {
		boolean success = false;
		switch (technology) {
			case MONGO: {
				success = handleMongoDeletion(toDelete, startDate, endDate, doValidate);
				break;
			} case HDFS: {
				success = handleHDFSDeletion(toDelete, startDate, endDate, doValidate);
				break;
			} case IMPALA: {
				success = handleDeletion(toDelete, doValidate, impalaService);
				break;
			} case STORE: {
				success = handleDeletion(toDelete, doValidate, storeService);
				break;
			} case KAFKA: {
				success = handleDeletion(toDelete, doValidate, kafkaService);
				break;
			}
		}
		return success;
	}

	/***
	 *
	 * This method attempts to stop all of the services below
	 *
	 */
	private boolean checkAndStopAllRelevantServices() {
		boolean collectionServiceStoppedSuccess = checkAndStopService(collectionServiceName, true);
		boolean streamingServiceStoppedSuccess = checkAndStopService(streamingServiceName, true);
		boolean kafkaServiceStartedSuccess = checkAndStopService(kafkaServiceName, true);
		return collectionServiceStoppedSuccess && streamingServiceStoppedSuccess && kafkaServiceStartedSuccess;
	}

	/***
	 *
	 * This method handles the restore command, determines which technology to use and executes it
	 *
	 * @param technology  technology to use (mongo, hdfs, etc.)
	 * @param sources     collection of key,value (keys are collection/tables/topic/hdfs paths etc)
	 * @return
	 */
	private boolean restoreEntities(Technology technology, Map<String, String> sources) {
		boolean success = false;
		switch (technology) {
			case MONGO: {
				success = restoreMongo(sources);
				break;
			} case HDFS: {
				success = restoreHDFS(sources);
				break;
			}
		}
		return success;
	}

	/***
	 *
	 * This method handles generic deletion
	 *
	 * @param toDelete    collection of key,value (keys are collection/tables/topic/hdfs paths etc)
	 * @param doValidate  flag to determine should we perform validations
	 * @param customUtil
	 * @return
	 */
	private boolean handleDeletion(Map<String, String> toDelete, boolean doValidate, CleanupDeletionUtil customUtil) {
		if (toDelete == null || toDelete.keySet().contains("ALL")) {
			//deleting all
			logger.info("deleting all entities");
			return customUtil.deleteAllEntities(doValidate);
		}
		Set<String> entities;
		//deleting specific entities
		Collection<String> temp = toDelete.keySet();
		entities = new HashSet(temp);
		for (Map.Entry<String, String> entry : toDelete.entrySet()) {
			String filter = entry.getValue();
			String name = entry.getKey();
			if (!filter.isEmpty()) {
				entities.addAll(customUtil.getEntitiesMatchingPredicate(name, filter));
				entities.remove(name);
			}
		}
		logger.info("deleting {} entities", entities.size());
		return customUtil.deleteEntities(entities, doValidate);
	}

	/***
	 *
	 * This method handles HDFS deletion operations
	 *
	 * @param toDelete    collection of key,value (keys are collection/tables/topic/hdfs paths etc)
	 * @param startDate   start date to filter deletion by
	 * @param endDate	  end date to filter deletion by
	 * @param doValidate  flag to determine should we perform validations
	 * @return
	 */
	private boolean handleHDFSDeletion(Map<String, String> toDelete, Date startDate, Date endDate, boolean doValidate) {
		boolean success;
		if (startDate == null && endDate == null && toDelete == null) {
			logger.info("deleting all entities");
			success = hdfsService.deleteAllEntities(doValidate);
		} else if (startDate == null && endDate == null) {
			logger.info("deleting {} entities", toDelete.size());
			success = hdfsService.deleteEntities(toDelete.keySet(), doValidate);
		} else {
			logger.info("deleting {} entities from {} to {}", toDelete.size(), startDate, endDate);
			success = deleteEntityBetween(toDelete, startDate, endDate, hdfsService, null);
		}
		return success;
	}

	/***
	 *
	 * This method handles the different cases of deleting objects from mongo (with or without dates)
	 *
	 * @param toDelete    collection of key,value (keys are collection/tables/topic/hdfs paths etc)
	 * @param startDate   start date to filter deletion by
	 * @param endDate	  end date to filter deletion by
	 * @param doValidate  flag to determine should we perform validations
	 * @return
	 */
	private boolean handleMongoDeletion(Map<String, String> toDelete, Date startDate, Date endDate, boolean doValidate){
		boolean success;
		if (startDate == null && endDate == null && toDelete == null) {
			logger.info("deleting all entities");
			success = mongoService.deleteAllEntities(doValidate);
		} else if (startDate == null && endDate == null) {
			logger.info("deleting {} entities", toDelete.size());
			success = handleDeletion(toDelete, doValidate, mongoService);
		} else {
			logger.info("deleting {} entities from {} to {}", toDelete.size(), startDate, endDate);
			success = deleteEntityBetween(toDelete, startDate, endDate, mongoService, mongoService);
		}
		return success;
	}

	/***
	 *
	 * This method deletes whatever object it was given in the sources param between the two given dates
	 *
	 * @param sources	  collection of key,value (keys are collection/tables/topic/hdfs paths etc)
	 * @param startDate   start date to filter deletion by
	 * @param endDate	  end date to filter deletion by
	 * @param cleanupUtil
	 * @return
	 */
	private boolean deleteEntityBetween(Map<String, String> sources, Date startDate, Date endDate,
										CleanupUtil cleanupUtil, CleanupDeletionUtil customUtil) {
		int deleted = 0;
		logger.debug("trying to delete {} entities", sources.size());
		for (Map.Entry<String, String> dataSource: sources.entrySet()) {
			if (dataSource.getKey().contains(optionalDelimiter)) {
				if (customUtil == null) {
					throw new UnsupportedOperationException("Not supported for this type of technology");
				}
				Collection<String> collections = customUtil.getEntitiesMatchingPredicate(dataSource.getKey().
								split(optionalDelimiter)[0], dataSource.getKey().split(optionalDelimiter)[1]);
				for (String collection: collections) {
					cleanupUtil.deleteEntityBetween(collection, dataSource.getValue(), startDate, endDate);
				}
				deleted++;
			} else {
				if (cleanupUtil.deleteEntityBetween(dataSource.getKey(), dataSource.getValue(), startDate, endDate)) {
					deleted++;
				}
			}
		}
		if (deleted != sources.size()) {
			logger.error("failed to delete all {} entities, deleted only {}", sources.size(), deleted);
			return true;
		}
		logger.info("deleted all {} entities", sources.size());
		return true;
	}

	/****
	 *
	 * This method attempts to restore a previously created snapshot by removing the active data and replacing
	 * it with the intended backup if one exists or dropping the collection if backup not found
	 *
	 * @param sources  collection of key and empty values - keys are the prefixes of the collections
	 * @return
	 */
	private boolean restoreMongo(Map<String, String> sources) {
		boolean success;
		logger.debug("trying to restore from {} prefixes", sources.size());
		for (Map.Entry<String, String> dataSource: sources.entrySet()) {
			String prefix = dataSource.getKey();
			success = mongoService.restoreSnapshot(prefix);
			if (!success) {
				logger.error("failed to restore from prefix {}", prefix);
				return success;
			} else {
				logger.info("restored successfully from prefix {}", prefix);
			}
		}
		return true;
	}

	/****
	 *
	 * This method attempts to restore a previously created snapshot by removing the active data and replacing
	 * it with the intended backup
	 *
	 * @param sources  collection of key and empty values - keys are the backup paths
	 * @return
	 */
	private boolean restoreHDFS(Map<String, String> sources) {
		boolean success;
		logger.debug("trying to restore from {} paths", sources.size());
		for (Map.Entry<String, String> dataSource: sources.entrySet()) {
			String backupPath = dataSource.getKey();
			success = hdfsService.restoreSnapshot(backupPath);
			if (!success) {
				logger.error("failed to restore from path {}", backupPath);
				return success;
			} else {
				logger.info("restored successfully from path {}", backupPath);
			}
		}
		return true;
	}

	/***
	 *
	 * This method clears Mongo entirely
	 *
	 * @param doValidate  flag to determine should we perform validations
	 * @return
	 */
	private boolean clearMongo(boolean doValidate) {
		logger.info("attempting to clear all mongo collections");
		return mongoService.deleteAllEntities(doValidate);
	}

	/***
	 *
	 * This method clears Impala entirely
	 *
	 * @param doValidate  flag to determine should we perform validations
	 * @return
	 */
	private boolean clearImpala(boolean doValidate) {
		logger.info("attempting to clear all impala tables");
		return impalaService.deleteAllEntities(doValidate);
	}

	/***
	 *
	 * This method clears HDFS entirely
	 *
	 * @param doValidate  flag to determine should we perform validations
	 * @return
	 */
	private boolean clearHDFS(boolean doValidate) {
		logger.info("attempting to clear all hdfs partitions");
		return hdfsService.deleteAllEntities(doValidate);
	}

	/***
	 *
	 * This method clears Kafka entirely
	 *
	 * @param doValidate  flag to determine should we perform validations
	 * @return
	 */
	private boolean clearKafka(boolean doValidate) {
		logger.info("attempting to clear all kafka topics");
		return kafkaService.deleteAllEntitiesIncludingDataFolder(doValidate);
	}

	/***
	 *
	 * This method clears the store states entirely
	 *
	 * @param doValidate  flag to determine should we perform validations
	 * @return
	 */
	private boolean clearStore(boolean doValidate) {
		logger.info("attempting to clear all states");
		return storeService.deleteAllEntities(doValidate);
	}

	/***
	 *
	 * This method clears the system entirely
	 *
	 * @param doValidate  flag to determine should we perform validations and whether or not to stop services
	 * @return
	 */
	private boolean clearAllData(boolean doValidate) {
		logger.info("attempting to clear system");
		if (doValidate) checkAndStopAllRelevantServices();
		boolean mongoSuccess = clearMongo(doValidate);
		boolean impalaSuccess = clearImpala(doValidate);
		boolean hdfsSuccess = clearHDFS(doValidate);
		boolean kafkaSuccess = clearKafka(doValidate);
		boolean storeSuccess = clearStore(doValidate);
		return mongoSuccess && impalaSuccess && hdfsSuccess && kafkaSuccess && storeSuccess;
	}

	/***
	 *
	 * This method checks if the service name is running, and if so - tries to stop it
	 *
	 * @param serviceName
	 */
	private boolean checkAndStopService(String serviceName, boolean stopped) {
		//if service is not started/stopped
		if (!clouderaService.validateServiceStartedOrStopped(serviceName, stopped)) {
			if (stopped) {
				logger.info("{} service is not stopped, attempting to stop...", serviceName);
			} else {
				logger.info("{} service is not started, attempting to start...", serviceName);
			}
			//try to start/stop the service
			clouderaService.startOrStopService(serviceName, stopped);
			//validate if starting/stopping succeeded
			boolean success = clouderaService.validateServiceStartedOrStopped(serviceName, stopped);
			if (success) {
				if (stopped) {
					logger.info("{} is down, good!", serviceName);
				} else {
					logger.info("{} is up, good!", serviceName);
				}
			} else {
				if (stopped) {
					logger.warn("{} service is not stopped, cleaning might not be performed fully", serviceName);
				} else {
					logger.warn("{} service is not started, cleaning might not be performed fully", serviceName);
				}
			}
			return success;
		}
		if (stopped) {
			logger.info("{} is down, good!", serviceName);
		} else {
			logger.info("{} is up, good!", serviceName);
		}
		return true;
	}

	/***
	 *
	 * This method builds the list of sources to delete according to the following format -
	 * dataSource (collection name, kafka topic, etc.) optional DELIMITER queryField (prefix flag, partition type etc.)
	 *
	 * @param dataSourcesString
	 */
	private Map<String, String> createDataSourcesMap(String dataSourcesString) {
		Map<String, String> dataSources = new HashMap();
		for (String entry: dataSourcesString.split(dataSourcesDelimiter)) {
			String dataSource;
			String queryField;
			if (entry.contains(dataSourcesFieldDelimiter)) {
				dataSource = entry.split(dataSourcesFieldDelimiter)[0];
				try {
					queryField = entry.split(dataSourcesFieldDelimiter)[1];
				} catch (Exception ex) {
					queryField = "";
				}
			} else {
				dataSource = entry;
				queryField = "";
			}
			dataSources.put(dataSource, queryField);
		}
		return dataSources;
	}

	@Override
	protected int getTotalNumOfSteps() { return 1; }

	@Override
	protected boolean shouldReportDataReceived() { return false; }

}
