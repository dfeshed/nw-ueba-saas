package fortscale.collection.jobs;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import fortscale.collection.hadoop.ImpalaClient;
import fortscale.utils.hdfs.partition.MonthlyPartitionStrategy;
import fortscale.utils.hdfs.partition.PartitionStrategy;
import fortscale.utils.hdfs.partition.RuntimePartitionStrategy;
import fortscale.utils.logging.Logger;

@DisallowConcurrentExecution
public class SyncImpalaPartitionsJob extends FortscaleJob {
	
	private static Logger logger = Logger.getLogger(SyncImpalaPartitionsJob.class);
	
	@Autowired
	private FileSystem hadoopFs;
	
	@Autowired
	protected ImpalaClient impalaClient;
	
	// job parameters
	private String hdfsPath;
	private String tableName;
	private int daysToRetain;
	
	// step computed data
	private List<Path> partitions;
	private List<Path> partitionsToAdd;
	private List<Path> partitionsToDelete;
	private List<Path> foldersToDelete;
	private PartitionStrategy partitionStrategy;
	
	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();
		
		hdfsPath = jobDataMapExtension.getJobDataMapStringValue(map, "hdfsPath");
		tableName = jobDataMapExtension.getJobDataMapStringValue(map, "tableName");
		daysToRetain = jobDataMapExtension.getJobDataMapIntValue(map, "daysToRetain");
		
		String strategy = jobDataMapExtension.getJobDataMapStringValue(map, "partitionStrategy");
		if (strategy.equals("runtime"))
			partitionStrategy = new RuntimePartitionStrategy();
		else if (strategy.equals("monthly"))
			partitionStrategy = new MonthlyPartitionStrategy();
		else 
			throw new JobExecutionException("invalid configuration for partition strategy", false);
			
	}

	@Override
	protected int getTotalNumOfSteps() {
		return 5;
	}

	@Override
	protected void runSteps() throws Exception {
		if (!listHDFSDirectoriesStep()) return;
		
		if (!decideOnDirectories()) return; 

		if (!deleteNonImpalaPartitionsStep()) return;
		
		if (!deleteImpalaPartitionsStep()) return;
		
		if (!syncImpalaPartitionsStep()) return;
	}

	
	private boolean listHDFSDirectoriesStep() {
		startNewStep("List HDFS Paths");
		partitions = new LinkedList<Path>();
		
		try {
			// check that input hdfs path exists
			if (!hadoopFs.exists(new Path(hdfsPath))) {
				String message = String.format("hdfs path '%s' does not exists", hdfsPath);
				logger.error(message);
				monitor.error(getMonitorId(), getStepName(), message);
				return false;
			}
			
			// create a filter for folders only
			PathFilter filter = new PathFilter() {
				 @Override
				  public boolean accept(Path path) {
					try {
						return hadoopFs.isDirectory(path);
					} catch (IOException e) {
						logger.debug(String.format("error quering if a '%s' is a directory", path), e);
						return false;
					}
				  }
			};
			
			// get all matching folders
			FileStatus[] files = hadoopFs.listStatus(new Path(hdfsPath), filter);
			for (FileStatus file : files) {
				partitions.add(file.getPath());
			}
			
		} catch (IOException e) {
			logger.error("error listing hdfs partition directories", e);
			monitor.error(getMonitorId(), getStepName(), "error listing hdfs partition directories\n" + e.toString());
			return false;
		}
		
		finishStep();
		return true;
	}
	
	private boolean decideOnDirectories() {
		startNewStep("Decide on Directories");
		
		partitionsToAdd = new LinkedList<Path>();
		partitionsToDelete = new LinkedList<Path>();
		foldersToDelete = new LinkedList<Path>();
		
		// get the timestamp to retain partitions after
		long retentionTS = DateTime.now(DateTimeZone.UTC).minusDays(daysToRetain).getMillis();
		
		// go over all directories in hdfs path and decide what to do which each one
		for (Path partition : partitions) {
			String partitionPath = partition.toString();
			if (partitionStrategy.isPartitionPath(partitionPath)) {
				// check if the partition should be retained
				if (partitionStrategy.comparePartitionTo(partitionPath, retentionTS)>0) {
					// retention time stamp is newer than the partition - delete it
					partitionsToDelete.add(partition);
				} else {
					// retention time stamp is older or within the partition - keep it
					partitionsToAdd.add(partition);
				}
			} else {
				// delete every non partition directory
				foldersToDelete.add(partition);
			}
		}
		
		finishStep();
		return true;
	}

	private boolean deleteNonImpalaPartitionsStep() {
		startNewStep("Delete Non Partitions Directories");
		boolean result = true;
		
		for (Path path : foldersToDelete) {
			try {
				logger.info("deleting hdfs path {}", path);
				boolean succeed = hadoopFs.delete(path, true);
				if (!succeed) {
					logger.error("error deleting hdfs path " + path);
					monitor.warn(getMonitorId(), getStepName(), "cannot delete hdfs path " + path);
				}
			} catch (Exception ioe) {
				String message = String.format("error deleting hdfs path %s", path);
				logger.error(message, ioe);
				monitor.error(getMonitorId(), getStepName(), message + "\n" + ioe.toString());
			}
		}
		
		finishStep();
		return result;
	}
	
	private boolean deleteImpalaPartitionsStep() {
		startNewStep("Delete Expired Partitions");
		boolean result = true;
	
		// go over the partitions to be removed from impala
		for (Path partition : partitionsToDelete) {
			try {
				// drop the partition from hdfs
				logger.info("droping partition {} from table {}", partition, tableName);

				String partitionName = partitionStrategy.getImpalaPartitionNameFromPath(partition.toString());
				if (partitionName!=null)
					impalaClient.dropPartitionFromTable(tableName, partitionName);
				
				logger.info("partition {} dropped from table {}", partitionName, tableName);
				
				// delete the partition folder from hdfs
				logger.info("deleting hdfs path {}", partition);
				boolean succeed = hadoopFs.delete(partition, true);
				if (!succeed) {
					logger.error("error deleting hdfs path " + partition);
					monitor.warn(getMonitorId(), getStepName(), "cannot delete hdfs path " + partition);
				}
				
			} catch (DataAccessException e) {
				String message = String.format("error droping partition %s from table %s", partition, tableName);
				logger.error(message, e);
				monitor.error(getMonitorId(), getStepName(), message + "\n" + e.toString());
			} catch (Exception ioe) {
				String message = String.format("error deleting hdfs path %s", partition);
				logger.error(message, ioe);
				monitor.error(getMonitorId(), getStepName(), message + "\n" + ioe.toString());
			}
		}
			
		
		finishStep();
		return result;
	}
	
	private boolean syncImpalaPartitionsStep() {
		startNewStep("Add Partitions");
		boolean result = true;
		
		for (Path partition : partitionsToAdd) {
			try {
				String partitionName = partitionStrategy.getImpalaPartitionNameFromPath(partition.toString());
				if (partitionName!=null)
					impalaClient.addPartitionToTable(tableName, partitionName);

			} catch (JobExecutionException e) {
				String message = String.format("error addition partition '%s' to table '%s'", partition, tableName);
				logger.error(message, e);
				monitor.error(getMonitorId(), getStepName(), message + "\n" + e.toString());
				result = false;
			}
		}	
	
		finishStep();
		return result;
	}
	
}
