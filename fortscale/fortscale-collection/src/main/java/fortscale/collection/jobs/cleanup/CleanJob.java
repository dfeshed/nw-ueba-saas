package fortscale.collection.jobs.cleanup;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.utils.cleanup.CleanupDeletionUtil;
import fortscale.utils.cleanup.CleanupUtil;
import fortscale.utils.cloudera.ClouderaUtils;
import fortscale.utils.hdfs.HDFSUtil;
import fortscale.utils.impala.ImpalaUtils;
import fortscale.utils.kafka.KafkaUtils;
import fortscale.utils.logging.Logger;
import fortscale.utils.mongodb.MongoUtil;
import fortscale.utils.store.StoreUtils;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

	private enum Technology { MONGO, HDFS, KAFKA, STORE, IMPALA, ALL }
	private enum Strategy { DELETE, FASTDELETE, RESTORE }

	@Autowired
	private MongoUtil mongoUtils;
	@Autowired
	private KafkaUtils kafkaUtils;
	@Autowired
	private ImpalaUtils impalaUtils;
	@Autowired
	private HDFSUtil hdfsUtils;
	@Autowired
	private StoreUtils storeUtils;
	@Autowired
	private ClouderaUtils clouderaUtils;

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
	@Value("${dates.format}")
	private String datesFormat;
	@Value("${kafka.service.name}")
	private String kafkaServiceName;
	@Value("${streaming.service.name}")
	private String streamingServiceName;
	@Value("${collection.service.name}")
	private String collectionServiceName;

	private Date startTime;
	private Date endTime;
	private Map<String, String> dataSources;
	private Strategy strategy;
	private Technology technology;

	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();
		Set<String> keys = map.keySet();
		DateFormat sdf = new SimpleDateFormat(datesFormat);
		// get parameters values from the job data map
		try {
			if (keys.contains(startTimeParam)) {
				startTime = sdf.parse(jobDataMapExtension.getJobDataMapStringValue(map, startTimeParam));
			}
			if (keys.contains(endTimeParam)) {
				endTime = sdf.parse(jobDataMapExtension.getJobDataMapStringValue(map, endTimeParam));
			}
		} catch (ParseException ex) {
			logger.error("Bad date format - {}", ex.getMessage());
			throw new JobExecutionException(ex);
		}
		technology = Technology.valueOf(jobDataMapExtension.getJobDataMapStringValue(map, technologyParam));
		strategy = Strategy.valueOf(jobDataMapExtension.getJobDataMapStringValue(map, strategyParam));
		if (keys.contains(dataSourcesParam)) {
			createDataSourcesMap(jobDataMapExtension.getJobDataMapStringValue(map, dataSourcesParam));
		}
	}

	@Override
	protected void runSteps() {
		startNewStep("Clean Job");
		boolean success = false;
		//if command is to delete everything
		if ((strategy == Strategy.DELETE || strategy == Strategy.FASTDELETE) && technology == Technology.ALL) {
			//if fast delete - no validation is performed
			success = clearAllData(strategy == Strategy.DELETE);
		} else {
			switch (strategy) {
				//for both delete and fastdelete, do
				case DELETE:
				case FASTDELETE: {
					//if fast delete - no validation is performed
					logger.info("deleting {} entities", dataSources.size());
					success = deleteEntities(dataSources, startTime, endTime, strategy == Strategy.DELETE);
					break;
				}
				case RESTORE: {
					logger.info("restoring {} entities", dataSources.size());
					success = restoreEntities(dataSources);
					break;
				}
			}
		}
		if (success) {
			logger.info("Clean job successful");
		} else {
			logger.error("Clean job failed");
		}
		finishStep();
	}

	/***
	 *
	 * This method handles the delete command, determines which technology to use and executes it
	 *
	 * @param toDelete    collection of key,value (keys are collection/tables/topic/hdfs paths etc)
	 * @param startDate   start date to filter deletion by
	 * @param endDate	  end date to filter deletion by
	 * @param doValidate  flag to determine should we perform validations
	 * @return
	 */
	private boolean deleteEntities(Map<String, String> toDelete, Date startDate, Date endDate, boolean doValidate) {
		boolean success = false;
		switch (technology) {
			case MONGO: {
				success = handleMongoDeletion(toDelete, startDate, endDate, doValidate);
				break;
			} case HDFS: {
				success = handleHDFSDeletion(toDelete, startDate, endDate, doValidate);
				break;
			} case IMPALA: {
				success = handleDeletion(toDelete, doValidate, impalaUtils);
				break;
			} case STORE: {
				checkAndStopService(kafkaServiceName);
				checkAndStopService(streamingServiceName);
				success = handleDeletion(toDelete, doValidate, storeUtils);
				break;
			} case KAFKA: {
				checkAndStopService(kafkaServiceName);
				success = handleDeletion(toDelete, doValidate, kafkaUtils);
				break;
			}
		}
		return success;
	}

	/***
	 *
	 * This method handles the restore command, determines which technology to use and executes it
	 *
	 * @param sources   collection of key,value (keys are collection/tables/topic/hdfs paths etc)
	 * @return
	 */
	private boolean restoreEntities(Map<String, String> sources) {
		boolean success = false;
		switch (technology) {
			case MONGO: {
				success = restoreSnapshot(sources, mongoUtils);
				break;
			} case HDFS: {
				success = restoreSnapshot(sources, hdfsUtils);
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
	 * @return
	 */
	private boolean handleDeletion(Map<String, String> toDelete, boolean doValidate, CleanupDeletionUtil customUtil) {
		Set<String> entities;
		//if deleting specific entities
		if (toDelete != null) {
			Collection<String> temp = toDelete.keySet();
			entities = new HashSet(temp);
			for (Map.Entry<String, String> entry : toDelete.entrySet()) {
				String value = entry.getValue();
				if (!value.isEmpty()) {
					entities.addAll(customUtil.getEntitiesMatchingPredicate(entry.getKey(), value));
					entities.remove(entry.getKey());
				}
			}
		} else {
			//deleting all
			entities = new HashSet(customUtil.getAllEntities());
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
		if (startTime == null && endTime == null) {
			if (toDelete != null) {
				logger.info("deleting {} entities", toDelete.size());
				success = hdfsUtils.deleteEntities(toDelete.keySet(), doValidate);
			} else {
				logger.info("deleting all entities");
				success = hdfsUtils.deleteAll(doValidate);
			}
		} else {
			logger.info("deleting {} entities from {} to {}", toDelete.size(), startDate, endDate);
			success = deleteEntityBetween(toDelete, startDate, endDate, hdfsUtils);
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
	private boolean handleMongoDeletion(Map<String, String> toDelete, Date startDate, Date endDate, boolean doValidate) {
		boolean success;
		if (startTime == null && endTime == null) {
			return handleDeletion(toDelete, doValidate, mongoUtils);
		} else {
			logger.info("deleting {} entities from {} to {}", toDelete.size(), startDate, endDate);
			success = deleteEntityBetween(toDelete, startDate, endDate, mongoUtils);
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
										CleanupUtil cleanupUtil) {
		int deleted = 0;
		logger.debug("trying to delete {} entities", sources.size());
		for (Map.Entry<String, String> dataSource: sources.entrySet()) {
			if (cleanupUtil.deleteEntityBetween(dataSource.getKey(), dataSource.getValue(), startDate, endDate)) {
				deleted++;
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
	 * it with the intended backup
	 *
	 * @param sources     collection of key,value (keys are collection/tables/topic/hdfs paths etc)
	 * @param cleanupUtil
	 * @return
	 */
	private boolean restoreSnapshot(Map<String, String> sources, CleanupUtil cleanupUtil) {
		int restored = 0;
		logger.debug("trying to restore {} snapshots", sources.size());
		for (Map.Entry<String, String> dataSource: sources.entrySet()) {
            String toRestore = dataSource.getKey();
            String backupName = dataSource.getValue();
			logger.debug("origin - {}, backup - {}", toRestore, backupName);
            if (cleanupUtil.restoreSnapshot(toRestore, backupName)) {
                restored++;
            }
        }
		if (restored != sources.size()) {
			logger.error("failed to restore all {} entities, restored only {}", sources.size(),
					restored);
			return false;
		}
		logger.info("restored all {} entities", sources.size());
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
		return mongoUtils.dropAllCollections(doValidate);
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
		return impalaUtils.dropAllTables(doValidate);
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
		return hdfsUtils.deleteAll(doValidate);
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
		return kafkaUtils.deleteAllTopics(doValidate);
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
		return storeUtils.deleteAllStates(doValidate);
	}

	/***
	 *
	 * This method clears the system entirely
	 *
	 * @param doValidate  flag to determine should we perform validations
	 * @return
	 */
	private boolean clearAllData(boolean doValidate) {
		logger.info("attempting to clear system");
		checkAndStopService(collectionServiceName);
		checkAndStopService(streamingServiceName);
		checkAndStopService(kafkaServiceName);
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
	private boolean checkAndStopService(String serviceName) {
		boolean stopped = true;
		//if service is not stopped
		if (!clouderaUtils.validateServiceStartedOrStopped(serviceName, stopped)) {
			logger.info("{} service is not stopped, attempting to stop...", serviceName);
			//try to stop the service
			clouderaUtils.startOrStopService(serviceName, stopped);
			//validate if stopping succeeded
			boolean success = clouderaUtils.validateServiceStartedOrStopped(serviceName, stopped);
			if (success) {
				logger.info("{} is down, good!", serviceName);
			} else {
				logger.warn("{} service is not stopped, cleaning might not be performed fully", serviceName);
			}
			return success;
		}
		logger.info("{} is down, good!", serviceName);
		return stopped;
	}

	/***
	 *
	 * This method builds the list of sources to delete according to the following format -
	 * dataSource (collection name, kafka topic, etc.) optional DELIMITER queryField (prefix flag, partition type etc.)
	 *
	 * @param dataSourcesString
	 */
	private void createDataSourcesMap(String dataSourcesString) {
		dataSources = new HashMap();
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
	}

	@Override
	protected int getTotalNumOfSteps() { return 1; }

	@Override
	protected boolean shouldReportDataReceived() { return false; }

}