package fortscale.collection.jobs.cleanup;

import com.mongodb.DBCollection;
import fortscale.collection.jobs.FortscaleJob;
import fortscale.domain.core.Evidence;
import fortscale.ml.service.dao.Model;
import fortscale.utils.logging.Logger;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.io.IOException;
import java.text.DateFormat;
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

	private enum Technology { MONGO, HDFS, KAFKA, SAMZA }
	private enum Strategy { DELETE, RESTORE }

	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private FileSystem hadoopFs;

	private Date startTime;
	private Date endTime;
	private String dataSource;
	private String restoreName;
	private Strategy strategy;
	private Technology technology;
	private Map<String, DAO> dataSourceToDAO;

	private PathFilter filter;

	public CleanJob() {
		createDataSourceToDAOMap();
		filter = new PathFilter() {
			@Override
			public boolean accept(Path path) {
			try {
				return hadoopFs.isDirectory(path);
			} catch (Exception ex) {
				return false;
			}
			}
		};
	}

	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();
		// get parameters values from the job data map
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		if (strategy == Strategy.DELETE) {
			try {
				startTime = sdf.parse(jobDataMapExtension.getJobDataMapStringValue(map, "startTime"));
				endTime = sdf.parse(jobDataMapExtension.getJobDataMapStringValue(map, "endTime"));
			} catch (Exception ex) {
				logger.error("Bad date format - {}", ex);
				throw new JobExecutionException(ex);
			}
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
		DAO dao = dataSourceToDAO.get(dataSource);
		switch (strategy) {
			case DELETE: {
				logger.info("deleting {} from {} to {}", dao.daoObject.getSimpleName(), startTime, endTime);
				success = deleteBetween(dataSourceToDAO.get(dataSource), startTime, endTime);
				break;
			} case RESTORE: {
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
				logger.debug("check if backup collection exists");
				if (mongoTemplate.collectionExists(backupCollectionName)) {
					DBCollection backupCollection = mongoTemplate.getCollection(backupCollectionName);
					logger.debug("drop collection");
					mongoTemplate.dropCollection(toRestore.queryField);
					//verify drop
					if (mongoTemplate.collectionExists(toRestore.queryField)) {
						logger.debug("dropping failed, abort");
						break;
					}
					logger.debug("renaming backup collection");
					backupCollection.rename(toRestore.queryField);
					if (mongoTemplate.collectionExists(toRestore.queryField)) {
						//verify restore
						logger.info("snapshot restored");
						success = true;
						break;
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
					break;
				}
				String message = "snapshot failed to restore - manually rename backup collection";
				logger.error(message);
				monitor.error(getMonitorId(), getStepName(), message);
				break;
			}
			case HDFS: {
				//TODO - implement
				success = false;
				break;
			}
		}
		return success;
	}

	private boolean deleteBetween(DAO toDelete, Date startDate, Date endDate) {
		boolean success = false;
		switch (technology) {
			case MONGO: {
				logger.info("attempting to delete {} from mongo", toDelete.daoObject.getSimpleName());
				Query query = new Query(where(toDelete.queryField).gte(startDate.getTime()).lt(endDate.getTime()));
				logger.debug("query is {}", query.toString());
				long recordsFound = mongoTemplate.count(query, toDelete.daoObject);
				logger.info("found {} records", recordsFound);
				if (recordsFound > 0) {
					mongoTemplate.remove(query, toDelete.daoObject);
				}
				success = true;
				break;
			} case HDFS: {
				//TODO - get hdfs path
				String hdfsPath = "";
				try {
					if (!hadoopFs.exists(new Path(hdfsPath))) {
						String message = String.format("hdfs path '%s' does not exists", hdfsPath);
						logger.error(message);
						monitor.error(getMonitorId(), getStepName(), message);
						return false;
					}
					// get all matching folders
					FileStatus[] files = hadoopFs.listStatus(new Path(hdfsPath), filter);
					for (FileStatus file : files) {
						Path path = file.getPath();
						logger.info("deleting hdfs path {}", path);
						success = hadoopFs.delete(path, true);
						if (!success) {
							String message = "cannot delete hdfs path " + path;
							logger.error(message);
							monitor.error(getMonitorId(), getStepName(), message);
						}
					}
				} catch (IOException ex) {
					String message = "cannot delete hdfs path " + ex.getMessage();
					logger.error(message);
					monitor.error(getMonitorId(), getStepName(), message);
				}
				break;
			} case KAFKA: {
				//TODO - implement
				success = false;
				break;
			} case SAMZA: {
				//TODO - implement
				success = false;
				break;
			}
		}
		return success;
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