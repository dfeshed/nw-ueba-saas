package fortscale.collection.jobs;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import fortscale.collection.hadoop.ImpalaClient;
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
	private String directoryPrefix;
	
	// step computed data
	private List<String> partitions;
	
	
	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();
		
		hdfsPath = jobDataMapExtension.getJobDataMapStringValue(map, "hdfsPath");
		tableName = jobDataMapExtension.getJobDataMapStringValue(map, "tableName");
		directoryPrefix = jobDataMapExtension.getJobDataMapStringValue(map, "directoryPrefix", "");
	}

	@Override
	protected int getTotalNumOfSteps() {
		return 2;
	}

	@Override
	protected void runSteps() throws Exception {
		boolean isSucceeded = listHDFSDirectoriesStep(); 
		if(!isSucceeded){
			return;
		}
		
		isSucceeded = addPartitionsToImpalaStep();
		if(!isSucceeded){
			return;
		}
	}

	
	private boolean listHDFSDirectoriesStep() {
		startNewStep("List HDFS Paths");
		partitions = new LinkedList<String>();
		
		try {
			// check that input hdfs path exists
			if (!hadoopFs.exists(new Path(hdfsPath))) {
				String message = String.format("hdfs path '%s' does not exists", hdfsPath);
				logger.error(message);
				monitor.error(getMonitorId(), getStepName(), message);
				return false;
			}
			
			// create a filter for folders
			PathFilter filter = new PathFilter() {
				 @Override
				  public boolean accept(Path path) {
					try {
						return hadoopFs.isDirectory(path) && path.getName().startsWith(directoryPrefix);
					} catch (IOException e) {
						logger.debug(String.format("error quering if a '%s' is a directory", path), e);
						return false;
					}
				  }
			};
			
			// get all matching folders
			FileStatus[] files = hadoopFs.listStatus(new Path(hdfsPath), filter);
			for (FileStatus file : files) {
				partitions.add(file.getPath().getName());
			}
			
		} catch (IOException e) {
			logger.error("error listing hdfs partition directories", e);
			monitor.error(getMonitorId(), getStepName(), "error listing hdfs partition directories\n" + e.toString());
			return false;
		}
		
		finishStep();
		return true;
	}
	
	private boolean addPartitionsToImpalaStep() {
		startNewStep("Sync Partitions");
		boolean result = true;
		
		for (String partition : partitions) {
			try {
				impalaClient.addPartitionToTable(tableName, partition);
			} catch (JobExecutionException e) {
				String message = String.format("error addition partition '%s' to table '%s'", partition, tableName);
				logger.error(message, e);
				monitor.error(getMonitorId(), getStepName(), message + "\n" + e.toString());
				result = false;
			}
		}
		
		try {
			impalaClient.refreshTable(tableName);
		} catch (JobExecutionException e) {
			String message = String.format("error refreshing table '%s'", tableName);
			logger.error(message, e);
			monitor.error(getMonitorId(), getStepName(), message + "\n" + e.toString());
			result = false;
		}
		
		finishStep();
		return result;
	}
	
}
