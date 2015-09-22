package fortscale.collection.jobs.cleanup;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.utils.CustomUtil;
import fortscale.utils.hdfs.HDFSUtil;
import fortscale.utils.impala.ImpalaUtils;
import fortscale.utils.kafka.KafkaUtils;
import fortscale.utils.logging.Logger;
import fortscale.utils.mongodb.MongoUtil;
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
	private enum Strategy { DELETE, RESTORE }

	@Autowired
	private MongoUtil mongoUtils;
	@Autowired
	private KafkaUtils kafkaUtils;
	@Autowired
	private ImpalaUtils impalaUtils;
	@Autowired
	private HDFSUtil hdfsUtils;

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
	protected void runSteps() throws Exception {
		startNewStep("Clean Job");
		boolean success = false;
		//if command is to delete everything
		if (strategy == Strategy.DELETE && technology == Technology.ALL) {
			success = clearAllData();
		} else {
			switch (strategy) {
				case DELETE: {
					logger.info("deleting {} entities", dataSources.size());
					success = deleteEntities(dataSources, startTime, endTime);
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
			logger.info("Clean operation successful");
		} else {
			logger.error("Clean operation failed");
		}
		finishStep();
	}

	private boolean deleteEntities(Map<String, String> toDelete, Date startDate, Date endDate) {
		boolean success = false;
		switch (technology) {
			case MONGO: {
				if (startTime == null && endTime == null) {
					logger.info("deleting all {} entities", toDelete.size());
					success = mongoUtils.dropCollections(toDelete.keySet());
				} else {
					logger.info("deleting {} entities from {} to {}", toDelete.size(), startDate, endDate);
					success = deleteEntityBetween(toDelete, startDate, endDate, mongoUtils);
				}
				break;
			} case HDFS: {
				if (startTime == null && endTime == null) {
					logger.info("deleting all {} entities", toDelete.size());
					success = hdfsUtils.deleteFiles(toDelete.keySet());
				} else {
					logger.info("deleting {} entities from {} to {}", toDelete.size(), startDate, endDate);
					success = deleteEntityBetween(toDelete, startDate, endDate, hdfsUtils);
				}
				break;
			} case IMPALA: {
				success = impalaUtils.dropTables(toDelete.keySet());
				break;
			} case STORE: {
				//TODO - implement
				break;
			}
		}
		return success;
	}

	private boolean restoreEntities(Map<String, String> sources) {
		boolean success = false;
		switch (technology) {
			case MONGO: {
				success = restoreSnapshot(sources, mongoUtils);
				break;
			}
			case HDFS: {
				success = restoreSnapshot(sources, hdfsUtils);
				break;
			} case IMPALA: {
				//TODO - implement?
				break;
			} case STORE: {
				//TODO - implement?
				break;
			}
		}
		return success;
	}

	private boolean deleteEntityBetween(Map<String, String> sources, Date startDate, Date endDate,
										CustomUtil customUtil) {
		int deleted = 0;
		logger.debug("trying to delete {} entities", sources.size());
		for (Map.Entry<String, String> dataSource: sources.entrySet()) {
			if (customUtil.deleteEntityBetween(dataSource.getKey(), dataSource.getValue(), startDate, endDate)) {
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

	private boolean restoreSnapshot(Map<String, String> sources, CustomUtil customUtil) {
		int restored = 0;
		logger.debug("trying to restore {} snapshots", sources.size());
		for (Map.Entry<String, String> dataSource: sources.entrySet()) {
            String toRestore = dataSource.getKey();
            String backupCollectionName = dataSource.getValue();
			logger.debug("origin - {}, backup - {}", toRestore, backupCollectionName);
            if (customUtil.restoreSnapshot(toRestore, backupCollectionName)) {
                restored++;
            }
        }
		if (restored != sources.size()) {
			logger.error("failed to restore all {} collections, restored only {}", sources.size(),
					restored);
			return false;
		}
		logger.info("restored all {} collections", sources.size());
		return true;
	}

	private boolean clearMongo() {
		logger.info("attempting to clear all mongo collections");
		return mongoUtils.dropAllCollections();
	}

	private boolean clearImpala() {
		logger.info("attempting to clear all impala tables");
		return impalaUtils.dropAllTables();
	}

	private boolean clearHDFS() {
		logger.info("attempting to clear all hdfs partitions");
		return hdfsUtils.deleteAll();
	}

	private boolean clearKafka() {
		logger.info("attempting to clear all kafka topics");
		return kafkaUtils.deleteAllTopics();
	}

	private boolean clearAllData() {
		logger.info("attempting to clear system");
		return clearMongo() && clearImpala() && clearHDFS() && clearKafka();
	}

	private void createDataSourcesMap(String dataSourcesString) {
		dataSources = new HashMap();
		for (String entry: dataSourcesString.split(dataSourcesDelimiter)) {
			String dataSource = entry.split(dataSourcesFieldDelimiter)[0];
			String queryField = entry.split(dataSourcesFieldDelimiter)[1];
			dataSources.put(dataSource, queryField);
		}
	}

	@Override
	protected int getTotalNumOfSteps() { return 1; }

	@Override
	protected boolean shouldReportDataReceived() { return false; }

}