package fortscale.collection.jobs;

import java.io.File;
import java.util.Arrays;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import fortscale.collection.JobDataMapExtension;
import fortscale.monitor.JobProgressReporter;


/**
 * Folder cleanup jobs to remove files from a folder once the available disk space is bellow a certain threshold
 */
@DisallowConcurrentExecution
public class FolderCleanupJob implements Job {

	private static final Logger logger = LoggerFactory.getLogger(FolderCleanupJob.class);
	
	@Autowired
	protected JobDataMapExtension jobDataMapExtension;
	
	@Autowired
	protected JobProgressReporter monitor;

	private String monitorId;
	
	@Override public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.info("folder cleanup job has started");
		try {
			// report monitor start
			String sourceName = context.getJobDetail().getKey().getGroup();
			String jobName = context.getJobDetail().getKey().getName();
			monitorId = monitor.startJob(sourceName, jobName, 1);
			monitor.startStep(monitorId, "Cleanup", 1);
			
			// get job parameters
			JobDataMap map = context.getMergedJobDataMap();
			String folder = jobDataMapExtension.getJobDataMapStringValue(map, "folder");
			int threshold = jobDataMapExtension.getJobDataMapIntValue(map, "threshold");
		
			// execute cleanup logic
			cleanupFolder(new File(folder), threshold);
		
			monitor.finishStep(monitorId, "Cleanup");
		} catch (Exception e) {
			monitor.error(monitorId, "Cleanup", e.toString());
			throw e;
		} finally {
			monitor.finishJob(monitorId);
		}
		logger.info("folder cleanup job has finished");
	}

	
	public void cleanupFolder(File folderFile, int threshold) throws JobExecutionException {
		if (!folderFile.exists() || !folderFile.isDirectory())
			throw new JobExecutionException("folder " + folderFile.getPath() + " does not exists or not a folder");
		
		// get the folder free space
		int freespaceMB = (int)(folderFile.getUsableSpace() / (1024 * 1024));
		if (freespaceMB <= threshold) {
			logger.info("reached avaialbe size threshold, starting to delete files from " + folderFile.getPath());
			
			// get the list of files sorted according to name so that old files will be deleted first
			File[] files = folderFile.listFiles();
			Arrays.sort(files);

			// delete files until we reach the threshold goal
			for (int i=0;i<files.length && freespaceMB<=threshold;i++) {
				File fileToDelete = files[i];
				try {
					fileToDelete.delete();
					freespaceMB = (int)(folderFile.getUsableSpace() / (1024 * 1024));
					logger.info("file {} was deleted", fileToDelete.getName());
				} catch (Exception e) {
					logger.error("cannot delete file " + fileToDelete.getName(), e);
					monitor.error(monitorId, "Cleanup", "cannot delete file " + fileToDelete.getName());
				}
			}
		}
	}
	
}
