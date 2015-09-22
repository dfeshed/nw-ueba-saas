package fortscale.collection.jobs.cleanup;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.domain.core.Evidence;
import fortscale.domain.fe.dao.impl.VpnDAOImpl;
import fortscale.ml.service.dao.Model;
import fortscale.utils.impala.ImpalaClient;
import fortscale.utils.logging.Logger;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
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
	private ImpalaClient impalaClient;

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
	@Value("${zookeeper.connection}")
	private String zookeeperConnection;
	@Value("${zookeeper.timeout}")
	private int zookeeperTimeout;

	@Value("${hdfs.user.data.path}")
	private String dataPath;
	@Value("${hdfs.user.rawdata.path}")
	private String rawDataPath;
	@Value("${hdfs.user.enricheddata.path}")
	private String enrichedDataPath;
	@Value("${hdfs.user.processeddata.path}")
	private String processedDataPath;

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
			String message = String.format("Bad date format - %s", ex.getMessage());
			logError(message);
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
				logError("Clean operation failed");
			}
		}
		finishStep();
	}

	private boolean deleteEntityBetween(DAO toDelete, Date startDate, Date endDate) {
		boolean success = false;
		switch (technology) {
			case MONGO: {
				success = MongoUtils.deleteMongoEntityBetween(toDelete, startDate, endDate);
				break;
			} case HDFS: {
				//TODO - get hdfs path
				success = deleteHDFSFilesBetween(toDelete.queryField, startDate, endDate);
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
				success = MongoUtils.clearMongoOfEntity(toDelete);
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
				List<String> topics = new ArrayList();
				success = deleteKafkaTopics(topics);
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
				success = MongoUtils.restoreMongoForEntity(toRestore, backupCollectionName);
				break;
			}
			case HDFS: {
				//TODO - implement
				break;
			} case STORE: {
				//TODO - implement
				break;
			} case IMPALA: {
				//TODO - implement
				break;
			}
		}
		return success;
	}

	private boolean deleteKafkaTopics(Collection<String> topics) {
		boolean success = false;
		logger.debug("establishing connection to zookeeper");
		ZkClient zkClient = new ZkClient(zookeeperConnection, zookeeperTimeout);
		logger.debug("connection established, starting to delete topics");
		for (String topic: topics) {
			String topicPath = ZkUtils.getTopicPath(topic);
			if (zkClient.exists(topicPath)) {
				logger.debug("attempting to delete topic {}", topic);
				success = zkClient.deleteRecursive(topicPath);
				if (success) {
					logger.info("deleted topic [}", topic);
				} else {
					logError("failed to delete topic " + topic);
				}
			} else {
				String message = String.format("topic %s doesn't exist", topic);
				logger.warn(message);
				monitor.warn(getMonitorId(), getStepName(), message);
			}
		}
		return success;
	}

	private boolean deleteHDFSPath(String hdfsPath) {
		boolean success = false;
		logger.debug("attempting to remove {}", hdfsPath);
		try {
			Process process = Runtime.getRuntime().exec("hdfs dfs -rm -r -skipTrash " + hdfsPath);
			if (process.waitFor() != 0) {
				logError("failed to remove " + hdfsPath);
			} else {
				process = Runtime.getRuntime().exec("hdfs dfs -ls " + hdfsPath);
				if (process.waitFor() != 0) {
					success = true;
					logger.info("deleted successfully");
				} else {
					logError("failed to remove " + hdfsPath);
				}
			}
		} catch (Exception ex) {
			logError(String.format("failed to remove partition %s - %s", hdfsPath, ex.getMessage()));
		}
		return success;
	}

	private boolean deleteHDFSFilesBetween(String hdfsPath, Date startDate, Date endDate) {
		if (startDate != null && endDate != null) {
			hdfsPath = buildFileList(hdfsPath, startDate, endDate);
		}
		return deleteHDFSPath(hdfsPath);
	}

	private boolean dropImpalaTables(Collection<String> tableNames) {
		int numberOfTablesDropped = 0;
		logger.debug("attempting to drop {} tables from impala", tableNames.size());
		for (String tableName: tableNames) {
			impalaClient.dropTable(tableName);
			//verify drop
			if (impalaClient.isTableExists(tableName)) {
				String message = "failed to drop table " + tableName;
				logger.warn(message);
				monitor.warn(getMonitorId(), getStepName(), message);
			} else {
				logger.info("dropped table {}", tableName);
				numberOfTablesDropped++;
			}
		}
		if (numberOfTablesDropped == tableNames.size()) {
			logger.info("dropped all {} tables", tableNames.size());
			return true;
		}
		logError(String.format("failed to drop all %s tables, dropped only %s", tableNames.size(),
				numberOfTablesDropped));
		return false;
	}

	private String buildFileList(String hdfsPath, Date startDate, Date endDate) {
		StringBuilder sb = new StringBuilder();
		//TODO - generalize this to account for different strategies (monthly partitions for example)
		DateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		//creating list of files by advancing the date one day at a time from startDate to endDate
		while (calendar.getTimeInMillis() < endDate.getTime()) {
			sb.append(hdfsPath + sdf.format(calendar.getTime()) + " ");
			calendar.add(Calendar.DATE, 1);
		}
		return sb.toString();
	}

	//run with empty prefix to get all tables
	private Collection<String> getAllImpalaTablesWithPrefix(String prefix) {
		logger.debug("getting all tables");
		Set<String> tableNames = impalaClient.getAllTables();
		logger.debug("found {} tables", tableNames.size());
		if (prefix.isEmpty()) {
			return tableNames;
		}
		Iterator<String> it = tableNames.iterator();
		logger.debug("filtering out tables not starting with {}", prefix);
		while (it.hasNext()) {
			String collectionName = it.next();
			if (!collectionName.startsWith(prefix)) {
				it.remove();
			}
		}
		logger.info("found {} tables with prefix {}", tableNames.size(), prefix);
		return tableNames;
	}

	private Collection<String> getAllKafkaTopics() {
		boolean success = false;
		logger.debug("establishing connection to zookeeper");
		ZkClient zkClient = new ZkClient(zookeeperConnection, zookeeperTimeout);
		logger.debug("connection established, fetching topics");
		return scala.collection.JavaConversions.seqAsJavaList(ZkUtils.getAllTopics(zkClient));

	}

	private boolean clearMongo() {
		logger.info("attempting to clear all mongo collections");
		return MongoUtils.dropAllCollections();
	}

	private boolean clearImpala() {
		logger.info("attempting to clear all impala tables");
		Collection<String> tableNames = getAllImpalaTablesWithPrefix("");
		logger.debug("found {} tables to drop", tableNames.size());
		return dropImpalaTables(tableNames);
	}

	private boolean clearHDFS() {
		logger.info("attempting to clear all hdfs partitions");
		return deleteHDFSPath(dataPath) && deleteHDFSPath(rawDataPath) &&
				deleteHDFSPath(enrichedDataPath) && deleteHDFSPath(processedDataPath);
	}

	private boolean clearKafka() {
		logger.info("attempting to clear all kafka topics");
		Collection<String> kafkaTopics = getAllKafkaTopics();
		return deleteKafkaTopics(kafkaTopics);
	}

	private void clearAllData() {
		clearMongo();
		clearImpala();
		clearHDFS();
		clearKafka();
	}

	private void logError(String message) {
		logger.error(message);
		monitor.error(getMonitorId(), getStepName(), message);
	}

	@Override
	protected int getTotalNumOfSteps() { return 1; }

	@Override
	protected boolean shouldReportDataReceived() { return false; }

	private void createDataSourceToDAOMap() {
		dataSourceToDAO = new HashMap();
		dataSourceToDAO.put("evidence", new DAO(Evidence.class, Evidence.startDateField));
		dataSourceToDAO.put("model", new DAO(Model.class, Model.COLLECTION_NAME));
		dataSourceToDAO.put("vpn", new DAO(VpnDAOImpl.class, processedDataPath + "/vpn/yearmonthday="));
	}

}