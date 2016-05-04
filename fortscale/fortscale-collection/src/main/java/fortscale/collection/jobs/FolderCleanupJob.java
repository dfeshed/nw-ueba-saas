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
import org.apache.commons.io.FileUtils;

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
			monitorId = monitor.startJob(sourceName, jobName, 1, false);
			monitor.startStep(monitorId, "Cleanup", 1);
			
			// get job parameters
			JobDataMap map = context.getMergedJobDataMap();
			String folder = jobDataMapExtension.getJobDataMapStringValue(map, "folder");
			int threshold = jobDataMapExtension.getJobDataMapIntValue(map, "threshold");
			int maxFolderSize = jobDataMapExtension.getJobDataMapIntValue(map, "maxFolderSize");
			boolean recursive = jobDataMapExtension.getJobDataMapBooleanValue(map, "recursive", true);
		
			// execute cleanup logic
			cleanupFolder(new File(folder), threshold, maxFolderSize, recursive);
		
			monitor.finishStep(monitorId, "Cleanup");
		} catch (Exception e) {
			monitor.error(monitorId, "Cleanup", e.toString());
			if (e instanceof JobExecutionException)
				throw e;
			else {
				logger.error("unexpected error during folder clean up job: " + e.toString());
				throw new JobExecutionException(e);
			}
		} finally {
			monitor.finishJob(monitorId);
		}
		logger.info("folder cleanup job has finished");
	}

	
	public void cleanupFolder(File folderFile, int threshold, int maxFolderSize, boolean recursive) throws JobExecutionException {
		if (!folderFile.exists()) {
			// log warning and exit as there is nothing to do (some folders may not be created all the time, e.g. error)
			logger.info("folder {} does not exist", folderFile.getName());
			monitor.warn(monitorId, "Cleanup", String.format("folder '%s' does not exist", folderFile.getName()));
			return;
		}
		
		if (!folderFile.isDirectory())
			throw new JobExecutionException("folder '" + folderFile.getPath() + "' does not exist or not a folder", false);
		
		// get the folder free space
		long totalMB = (folderFile.getTotalSpace() / (1024 *1024));
		long freespaceMB = (folderFile.getUsableSpace() / (1024 * 1024));
		int precentFree = (int) (freespaceMB * 100 / totalMB);
		
		long folderSizeMB = getFolderSize(folderFile, recursive);
		
		if (precentFree <= threshold || folderSizeMB > maxFolderSize) {
			logger.info("reached avaialbe size threshold, starting to delete files from " + folderFile.getPath());
			
			// get the list of files sorted according to name so that old files will be deleted first
			File[] files = folderFile.listFiles();
			Arrays.sort(files);

			// delete files until we reach the threshold goal
			for (int i=0;i<files.length && (precentFree<=threshold || folderSizeMB > maxFolderSize);i++) {
				File fileToDelete = files[i];
				try {
					if (fileToDelete.isDirectory()) {
						if (recursive)
							cleanupFolder(fileToDelete, threshold, maxFolderSize, recursive);
					} else {
						fileToDelete.delete();
						logger.info("file {} was deleted", fileToDelete.getName());
					}
					freespaceMB = (folderFile.getUsableSpace() / (1024 * 1024));
					precentFree = (int) (freespaceMB * 100 / totalMB);
					folderSizeMB = getFolderSize(folderFile, recursive);
				} catch (Exception e) {
					logger.error("cannot delete file " + fileToDelete.getName(), e);
					monitor.error(monitorId, "Cleanup", "cannot delete file " + fileToDelete.getName());
				}
			}
			
			// if we still have not enough free space report as warning
			if (precentFree <= threshold || folderSizeMB > maxFolderSize) {
				String message = String.format("delete all that can be from %s but still not enough free space", folderFile.getName());
				logger.warn(message);
				monitor.warn(monitorId, "Cleanup", message);
			}
		}
	}	
	
	private long getFolderSize(File folder, boolean recursive) {
		if (recursive) {
			return (FileUtils.sizeOfDirectory(folder) / (1024 * 1024));
		} else {
			File[] files = folder.listFiles();
			long size = 0L;
			for (File file : files) {
				if (!file.isDirectory()) {
					size += FileUtils.sizeOf(file);
				}
			}
			return size / (1024 * 1024);
		}
	}
}
