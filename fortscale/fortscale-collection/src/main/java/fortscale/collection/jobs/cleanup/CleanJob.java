package fortscale.collection.jobs.cleanup;

import com.mongodb.DBCollection;
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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.springframework.data.mongodb.core.query.Criteria.where;

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
	private MongoTemplate mongoTemplate;
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
		if (strategy == Strategy.DELETE && technology == Technology.ALL) {
			dropMongoCollections(mongoTemplate.getCollectionNames());
			//for each data source (evidence, alerts etc.) clear all technologies (Mongo, HDFS etc.)
			for (Map.Entry<String, DAO> entry : dataSourceToDAO.entrySet()) {
				DAO dao = entry.getValue();
				logger.info("deleting all {}", dao.daoObject.getSimpleName());
				success = clearAllOfEntity(dao);
				if (success) {
					logger.info("Clean operation successful");
				} else {
					logError(String.format("Clean operation failed,delete %s manually", dao.daoObject.getSimpleName()));
				}
			}
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
				success = deleteMongoEntityBetween(toDelete, startDate, endDate);
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
				success = clearMongoOfEntity(toDelete);
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
				deleteHDFSPartition("/user/cloudera/rawdata");
				topics.add("fortscale-amt-sessionized");
				topics.add("ssh-user-score-changelog");
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
				success = restoreMongoForEntity(toRestore, backupCollectionName);
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

	private boolean restoreMongoForEntity(DAO toRestore, String backupCollectionName) {
		boolean success = false;
		logger.debug("check if backup collection exists");
		if (mongoTemplate.collectionExists(backupCollectionName)) {
			DBCollection backupCollection = mongoTemplate.getCollection(backupCollectionName);
			logger.debug("drop collection");
			mongoTemplate.dropCollection(toRestore.queryField);
			//verify drop
			if (mongoTemplate.collectionExists(toRestore.queryField)) {
				logger.debug("dropping failed, abort");
				return success;
			}
			logger.debug("renaming backup collection");
			backupCollection.rename(toRestore.queryField);
			if (mongoTemplate.collectionExists(toRestore.queryField)) {
				//verify restore
				logger.info("snapshot restored");
				success = true;
				return success;
			} else {
				logError("snapshot failed to restore - could not rename collection");
			}
		} else {
			logError(String.format("snapshot failed to restore - no backup collection %s found", restoreName));
			return success;
		}
		logError("snapshot failed to restore - manually rename backup collection");
		return success;
	}

	private boolean deleteKafkaTopics(List<String> topics) {
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

	private boolean clearMongoOfEntity(DAO toDelete) {
		boolean success;
		logger.info("attempting to delete {} from mongo", toDelete.daoObject.getSimpleName());
		mongoTemplate.remove(new Query(), toDelete.daoObject);
		long recordsFound = mongoTemplate.count(new Query(), toDelete.daoObject);
		if (recordsFound > 0) {
            success = false;
			logError("failed to remove documents");
        } else {
            success = true;
            logger.info("successfully removed all documents");
        }
		return success;
	}

	private boolean deleteHDFSPartition(String hdfsPath) {
		boolean success = false;
		logger.debug("attempting to remove {}", hdfsPath);
		try {
			Process process = Runtime.getRuntime().exec("hdfs dfs -rm -r -skipTrash " + hdfsPath);
			if (process.waitFor() != 0) {
				logError("failed to remove " + hdfsPath);
			} else {
				process = Runtime.getRuntime().exec("hdfs dfs -ls " + hdfsPath);
				/*OutputStream stdin = process.getOutputStream();
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));
				while ((line = reader.readLine ()) != null) {
					System.out.println ("Stdout: " + line);
				}*/
				if (process.waitFor() == 0) {
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
		boolean success = false;
		String files = buildFileList(hdfsPath, startDate, endDate);
		logger.debug("attempting to remove {}", files);
		try {
			Process process = Runtime.getRuntime().exec("hdfs dfs -rm -r -skipTrash " + files);
			if (process.waitFor() == 0) {
				success = true;
				logger.info("deleted successfully");
			}
		} catch (Exception ex) {
			logError("failed to remove partition files - " + ex.getMessage());
		}
		return success;
	}

	private boolean deleteMongoEntityBetween(DAO toDelete, Date startDate, Date endDate) {
		logger.info("attempting to delete {} from mongo", toDelete.daoObject.getSimpleName());
		Query query;
		if (startDate != null && endDate == null) {
			query = new Query(where(toDelete.queryField).gte(startDate.getTime()));
		} else if (startDate == null && endDate != null) {
			query = new Query(where(toDelete.queryField).lte(endDate.getTime()));
		} else {
			query = new Query(where(toDelete.queryField).gte(startDate.getTime()).lte(endDate.getTime()));
		}
		logger.debug("query is {}", query.toString());
		long recordsFound = mongoTemplate.count(query, toDelete.daoObject);
		logger.info("found {} records", recordsFound);
		if (recordsFound > 0) {
            mongoTemplate.remove(query, toDelete.daoObject);
        }
		return true;
	}

	private boolean dropImpalaTables(Set<String> tableNames) {
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

	private boolean dropMongoCollections(Set<String> collectionNames) {
		int numberOfCollectionsDropped = 0;
		logger.debug("attempting to drop {} collections from mongo", collectionNames.size());
		for (String collectionName: collectionNames) {
			mongoTemplate.dropCollection(collectionName);
			//verify drop
			if (mongoTemplate.collectionExists(collectionName)) {
				String message = "failed to drop collection " + collectionName;
				logger.warn(message);
				monitor.warn(getMonitorId(), getStepName(), message);
			} else {
				logger.info("dropped collection {}", collectionName);
				numberOfCollectionsDropped++;
			}
		}
		if (numberOfCollectionsDropped == collectionNames.size()) {
			logger.info("dropped all {} collections", collectionNames.size());
			return true;
		}
		logError(String.format("failed to drop all %s collections, dropped only %s", collectionNames.size(),
				numberOfCollectionsDropped));
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
	private Set<String> getAllImpalaTablesWithPrefix(String prefix) {
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

	//run with empty prefix to get all collections
	private Set<String> getAllMongoCollectionsWithPrefix(String prefix) {
		logger.debug("getting all collections");
		Set<String> collectionNames = mongoTemplate.getCollectionNames();
		logger.debug("found {} collections", collectionNames.size());
		if (prefix.isEmpty()) {
			return collectionNames;
		}
		Iterator<String> it = collectionNames.iterator();
		logger.debug("filtering out collections not starting with {}", prefix);
		while (it.hasNext()) {
			String collectionName = it.next();
			if (!collectionName.startsWith(prefix)) {
				it.remove();
			}
		}
		logger.info("found {} collections with prefix {}", collectionNames.size(), prefix);
		return collectionNames;
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

	private class DAO {

		public Class daoObject;
		public String queryField;

		public DAO(Class daoObject, String queryField) {
			this.daoObject = daoObject;
			this.queryField = queryField;
		}

	}

}