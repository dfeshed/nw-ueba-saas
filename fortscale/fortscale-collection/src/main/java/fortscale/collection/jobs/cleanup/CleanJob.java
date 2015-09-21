package fortscale.collection.jobs.cleanup;

import com.mongodb.DBCollection;
import fortscale.collection.jobs.FortscaleJob;
import fortscale.domain.core.Evidence;
import fortscale.ml.service.dao.Model;
import fortscale.utils.logging.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

	@Value("${impala.data.vpn.table.name}")
	private String impalaVpnDataTableName;
	@Value("${hdfs.user.data.vpn.path}")
	private String impalaVpnDataDirectory;
	@Value("${impala.data.vpn.table.partition.type}")
	private String impalaVpnDataTablePartitionType;

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
		createDataSourceToDAOMap();
		// get parameters values from the job data map
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		try {
			startTime = sdf.parse(jobDataMapExtension.getJobDataMapStringValue(map, "startTime"));
			endTime = sdf.parse(jobDataMapExtension.getJobDataMapStringValue(map, "endTime"));
		} catch (JobExecutionException ex) {
			//didn't pass startTime or endTime - ignore
		} catch (ParseException ex) {
			String message = String.format("Bad date format - %s", ex.getMessage());
			logger.error(message);
			monitor.error(getMonitorId(), getStepName(), message);
			throw new JobExecutionException(ex);
		}
		technology = Technology.valueOf(jobDataMapExtension.getJobDataMapStringValue(map, "technology"));
		strategy = Strategy.valueOf(jobDataMapExtension.getJobDataMapStringValue(map, "strategy"));
		if (strategy == Strategy.RESTORE) {
			restoreName = jobDataMapExtension.getJobDataMapStringValue(map, "restoreName");
		}
		dataSource = jobDataMapExtension.getJobDataMapStringValue(map, "dataSource");
	}

	@Override
	protected void runSteps() throws Exception {
		startNewStep("Running Clean Job");
		boolean success = false;
		//if command is to delete everything
		if (technology == Technology.ALL && strategy == Strategy.DELETE) {
			//for each data source (evidence, alerts etc.) clear all technologies (Mongo, HDFS etc.)
			for (Map.Entry<String, DAO> entry : dataSourceToDAO.entrySet()) {
				DAO dao = entry.getValue();
				logger.info("deleting all {}", dao.daoObject.getSimpleName());
				success = deleteAll(dao);
				if (success) {
					logger.info("Clean operation successful");
				} else {
					String message = "Clean operation failed";
					logger.error(message);
					monitor.error(getMonitorId(), getStepName(), message);
				}
			}
		} else {
			DAO dao = dataSourceToDAO.get(dataSource);
			switch (strategy) {
				case DELETE: {
					if (startTime == null && endTime == null) {
						logger.info("deleting all {}", dao.daoObject.getSimpleName());
						success = deleteAll(dataSourceToDAO.get(dataSource));
					} else {
						logger.info("deleting {} from {} to {}", dao.daoObject.getSimpleName(), startTime, endTime);
						success = deleteBetween(dataSourceToDAO.get(dataSource), startTime, endTime);
					}
					break;
				}
				case RESTORE: {
					logger.info("restoring {} from {} to {}", dao.daoObject.getSimpleName(), dao.queryField, restoreName);
					success = restoreSnapshot(dataSourceToDAO.get(dataSource), restoreName);
				}
			}
			if (success) {
				logger.info("Clean operation successful");
			} else {
				String message = "Clean operation failed";
				logger.error(message);
				monitor.error(getMonitorId(), getStepName(), message);
			}
		}
		finishStep();
	}

	@Override
	protected int getTotalNumOfSteps() { return 1; }

	@Override
	protected boolean shouldReportDataReceived() { return false; }

	private void createDataSourceToDAOMap() {
		dataSourceToDAO = new HashMap();
		dataSourceToDAO.put("evidence", new DAO(Evidence.class, Evidence.startDateField));
		dataSourceToDAO.put("model", new DAO(Model.class, Model.COLLECTION_NAME));
	}

	private boolean restoreSnapshot(DAO toRestore, String backupCollectionName) {
		boolean success = false;
		switch (technology) {
			case MONGO: {
				success = restoreMongo(toRestore, backupCollectionName);
				break;
			}
			case HDFS: {
				//TODO - implement
				break;
			}case STORE: {
				//TODO - implement
				break;
			}
		}
		return success;
	}

	private boolean restoreMongo(DAO toRestore, String backupCollectionName) {
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
                String message = "snapshot failed to restore - could not rename collection";
                logger.error(message);
                monitor.error(getMonitorId(), getStepName(), message);
            }
        } else {
            String message = String.format("snapshot failed to restore - no backup collection %s found",
                    restoreName);
            logger.error(message);
            monitor.error(getMonitorId(), getStepName(), message);
			return success;
        }
		String message = "snapshot failed to restore - manually rename backup collection";
		logger.error(message);
		monitor.error(getMonitorId(), getStepName(), message);
		return success;
	}

	private boolean deleteAll(DAO toDelete) {
		boolean success = false;
		switch (technology) {
			case ALL: {}
			case MONGO: {
				success = deleteAllMongo(toDelete);
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
				//TODO - implement
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

	private boolean deleteAllMongo(DAO toDelete) {
		boolean success;
		logger.info("attempting to delete {} from mongo", toDelete.daoObject.getSimpleName());
		mongoTemplate.remove(new Query(), toDelete.daoObject);
		long recordsFound = mongoTemplate.count(new Query(), toDelete.daoObject);
		if (recordsFound > 0) {
            success = false;
            String message = "failed to remove documents";
            logger.error(message);
            monitor.error(getMonitorId(), getStepName(), message);
        } else {
            success = true;
            logger.info("successfully removed all documents");
        }
		return success;
	}

	private boolean deleteBetween(DAO toDelete, Date startDate, Date endDate) {
		boolean success = false;
		switch (technology) {
			case MONGO: {
				success = deleteBetweenMongo(toDelete, startDate, endDate);
				break;
			} case HDFS: {
				//TODO - get hdfs path
				String hdfsPath = impalaVpnDataDirectory;
				success = deleteBetweenHDFS(hdfsPath, startDate, endDate);
				break;
			} case KAFKA: {
				//TODO - implement
				break;
			} case STORE: {
				//TODO - implement
				break;
			}
		}
		return success;
	}

	private boolean deleteBetweenHDFS(String hdfsPath, Date startDate, Date endDate) {
		boolean success = false;
		String file = hdfsPath + "/yearmonth=201507/vpnETL_20150729.csv";
		logger.debug("attempting to remove {}", file);
		ProcessBuilder pb = new ProcessBuilder("hdfs dfs -rm " + file);
		try {
			Process process = pb.start();
			int errCode = process.waitFor();
			if (errCode == 0) {
				success = true;
				logger.info("deleted {} successfully", file);
			}
		} catch (Exception ex) {
			String message = "failed to remove partition files - " + ex.getMessage();
			logger.error(message);
			monitor.error(getMonitorId(), getStepName(), message);
		}
		return success;
	}

	private boolean deleteBetweenMongo(DAO toDelete, Date startDate, Date endDate) {
		logger.info("attempting to delete {} from mongo", toDelete.daoObject.getSimpleName());
		Query query;
		if (startDate != null && endDate == null) {
			query = new Query(where(toDelete.queryField).gte(startDate.getTime()));
		} else if (startDate == null && endDate != null) {
			query = new Query(where(toDelete.queryField).lt(endDate.getTime()));
		} else {
			query = new Query(where(toDelete.queryField).gte(startDate.getTime()).lt(endDate.getTime()));
		}
		logger.debug("query is {}", query.toString());
		long recordsFound = mongoTemplate.count(query, toDelete.daoObject);
		logger.info("found {} records", recordsFound);
		if (recordsFound > 0) {
            mongoTemplate.remove(query, toDelete.daoObject);
        }
		return true;
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