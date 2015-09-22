package fortscale.collection.jobs.cleanup;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.domain.core.Evidence;
import fortscale.domain.fe.dao.impl.VpnDAOImpl;
import fortscale.utils.hdfs.HDFSUtils;
import fortscale.utils.impala.ImpalaUtils;
import fortscale.utils.kafka.KafkaUtils;
import fortscale.utils.logging.Logger;
import fortscale.utils.mongodb.MongoUtils;
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
	private MongoUtils mongoUtils;
	@Autowired
	private KafkaUtils kafkaUtils;
	@Autowired
	private ImpalaUtils impalaUtils;
	@Autowired
	private HDFSUtils hdfsUtils;

	@Value("${start.time.param}")
	private String startTimeParam;
	@Value("${end.time.param}")
	private String endTimeParam;
	@Value("${technology.param}")
	private String technologyParam;
	@Value("${strategy.param}")
	private String strategyParam;
	@Value("${data.source.param}")
	private String dataSourceParam;
	@Value("${restore.name.param}")
	private String restoreNameParam;
	@Value("${dates.format}")
	private String datesFormat;

	private Date startTime;
	private Date endTime;
	private String dataSource;
	private String restoreName;
	private Strategy strategy;
	private Technology technology;
	private Map<String, DAO> dataSourceToDAO;

	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();
		Set<String> keys = map.keySet();
		createDataSourceToDAOMap();
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
		if (strategy == Strategy.RESTORE) {
			restoreName = jobDataMapExtension.getJobDataMapStringValue(map, restoreNameParam);
		}
		dataSource = jobDataMapExtension.getJobDataMapStringValue(map, dataSourceParam);
	}

	@Override
	protected void runSteps() throws Exception {
		startNewStep("Clean Job");
		boolean success = false;
		//if command is to delete everything
		if (strategy == Strategy.DELETE && technology == Technology.ALL && dataSource == null) {
			clearAllData();
		} else {
			DAO dao = dataSourceToDAO.get(dataSource);
			switch (strategy) {
				case DELETE: {
					if (startTime == null && endTime == null) {
						logger.info("deleting all {}", dao.daoObject.getSimpleName());
						success = clearAllOfEntity(dataSourceToDAO.get(dataSource));
					} else {
						logger.info("deleting {} from {} to {}", dao.daoObject.getSimpleName(), startTime, endTime);
						success = deleteEntityBetween(dataSourceToDAO.get(dataSource), startTime, endTime);
					}
					break;
				}
				case RESTORE: {
					logger.info("restoring {} from {} to {}", dao.daoObject.getSimpleName(), dao.queryField,
							restoreName);
					success = restoreSnapshotOfEntity(dataSourceToDAO.get(dataSource), restoreName);
				}
			}
			if (success) {
				logger.info("Clean operation successful");
			} else {
				logger.error("Clean operation failed");
			}
		}
		finishStep();
	}

	private boolean deleteEntityBetween(DAO toDelete, Date startDate, Date endDate) {
		boolean success = false;
		switch (technology) {
			case MONGO: {
				success = mongoUtils.deleteMongoEntityBetween(null, startDate, endDate);
				break;
			} case HDFS: {
				//TODO - get hdfs path
				success = hdfsUtils.deleteHDFSFilesBetween(toDelete.queryField, startDate, endDate);
				break;
			} case STORE: {
				//TODO - implement
				break;
			}
		}
		return success;
	}

	private boolean clearAllOfEntity(DAO toDelete) {
		boolean success = false;
		switch (technology) {
			case ALL: //continue through all cases
			case MONGO: {
				success = mongoUtils.clearMongoOfEntity(null);
				if (technology != Technology.ALL) {
					break;
				}
			}
			case HDFS: {
				//TODO - implement
				if (technology != Technology.ALL) {
					break;
				}
			} case KAFKA: {
				//TODO - finish this
				List<String> topics = new ArrayList();
				success = kafkaUtils.deleteKafkaTopics(topics);
				if (technology != Technology.ALL) {
					break;
				}
			} case STORE: {
				//TODO - implement
				if (technology != Technology.ALL) {
					break;
				}
			} case IMPALA: {
				//TODO - implement
			}
		}
		return success;
	}

	private boolean restoreSnapshotOfEntity(DAO toRestore, String backupCollectionName) {
		boolean success = false;
		switch (technology) {
			case MONGO: {
				success = mongoUtils.restoreMongoForEntity(null, backupCollectionName);
				break;
			}
			case HDFS: {
				success = hdfsUtils.restoreSnapshot(null, backupCollectionName);
				break;
			} case STORE: {
				//TODO - implement?
				break;
			} case IMPALA: {
				//TODO - implement?
				break;
			}
		}
		return success;
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
		return hdfsUtils.deleteAllHDFS();
	}

	private boolean clearKafka() {
		logger.info("attempting to clear all kafka topics");
		return kafkaUtils.deleteAllKafkaTopics();
	}

	private void clearAllData() {
		clearMongo();
		clearImpala();
		clearHDFS();
		clearKafka();
	}

	@Override
	protected int getTotalNumOfSteps() { return 1; }

	@Override
	protected boolean shouldReportDataReceived() { return false; }

	private void createDataSourceToDAOMap() {
		dataSourceToDAO = new HashMap();
		dataSourceToDAO.put("evidence", new DAO(Evidence.class, Evidence.startDateField));
		dataSourceToDAO.put("vpn", new DAO(VpnDAOImpl.class, "/vpn/yearmonthday="));
	}

}