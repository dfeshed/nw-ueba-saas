package fortscale.collection.jobs.scoring;

import java.io.IOException;
import java.util.Date;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import fortscale.collection.hadoop.ImpalaClient;
import fortscale.collection.jobs.FortscaleJob;
import fortscale.collection.jobs.ad.AdProcessJob;
import fortscale.services.UserService;
import fortscale.utils.logging.Logger;

public class TotalScoringJob extends FortscaleJob {
	
	private static Logger logger = Logger.getLogger(AdProcessJob.class);

	@Autowired
	protected ImpalaClient impalaClient;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ImpalaWriterFactoryImpl impalaWriterFactory;
	
	String hadoopFilePath;

	// job parameters:
	protected String hadoopDirPath;
	private String filenameFormat;
	protected String impalaTableName;
	
	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();

		// get parameters values from the job data map
		hadoopDirPath = jobDataMapExtension.getJobDataMapStringValue(map, "hadoopDirPath");
		filenameFormat = jobDataMapExtension.getJobDataMapStringValue(map, "filenameFormat");
		impalaTableName = jobDataMapExtension.getJobDataMapStringValue(map, "impalaTableName");
				
		// generate filename according to the job name and time
		String filename = String.format(filenameFormat, (new Date()).getTime()/1000);
		hadoopFilePath = String.format("%s/%s", hadoopDirPath, filename);
	}

	@Override
	protected int getTotalNumOfSteps() {
		return 2;
	}

	@Override
	protected void runSteps() throws Exception {
		
		boolean isSucceeded = updateTotalScore();
		if(!isSucceeded){
			return;
		}
		
		refreshImpala();
	}
	
	private boolean updateTotalScore() throws JobExecutionException{
		startNewStep("update");
		try {
			impalaWriterFactory.createTotalScoreAppender(hadoopFilePath);
		} catch (IOException e) {
			logger.error("error opening hdfs file for append at " + hadoopFilePath, e);
			monitor.error(getMonitorId(), getStepName(), String.format("error opening hdfs file %s: \n %s", hadoopFilePath, e.toString()));
			throw new JobExecutionException("error opening hdfs file for append at " + hadoopFilePath, e);
		}
		
		try{
			userService.updateUserTotalScore();
		} catch(Exception e){
			logger.error("got an exception during the process of updating total score.", e);
			monitor.error(getMonitorId(), getStepName(),String.format("got an exception during the process of updating total score: %s", e.toString()));
			return false;
		} finally{
		
			try {
				impalaWriterFactory.closeTotalScoreAppender();
			} catch (IOException e) {
				logger.error("error closing hdfs file " + hadoopFilePath, e);
				monitor.error(getMonitorId(), getStepName(), String.format("error closing hdfs file %s: \n %s", hadoopFilePath, e.toString()));
				throw new JobExecutionException("error closing hdfs file " + hadoopFilePath, e);
			}
		}
		
		finishStep();
		
		return true;
	}
	
	
	protected void refreshImpala() throws JobExecutionException {
		startNewStep("impala refresh");
		impalaClient.refreshTable(impalaTableName);
		finishStep();
	}
}
