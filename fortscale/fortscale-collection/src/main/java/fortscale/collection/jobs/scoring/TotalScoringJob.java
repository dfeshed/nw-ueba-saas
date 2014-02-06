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
import fortscale.services.UserServiceFacade;
import fortscale.utils.logging.Logger;

public class TotalScoringJob extends FortscaleJob {
	
	private static Logger logger = Logger.getLogger(AdProcessJob.class);

	@Autowired
	protected ImpalaClient impalaClient;
	
	@Autowired
	private UserServiceFacade userServiceFacade;
	
	@Autowired
	private ImpalaWriterFactoryImpl impalaWriterFactory;
	

	// job parameters:
	protected String hadoopDirPath;
	protected String hadoopFilename;
	protected String impalaTableName;
	
	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();

		// get parameters values from the job data map
		hadoopDirPath = jobDataMapExtension.getJobDataMapStringValue(map, "hadoopDirPath");
		impalaTableName = jobDataMapExtension.getJobDataMapStringValue(map, "impalaTableName");
				
		// generate filename according to the job name and time
		String filenameFormat = jobDataMapExtension.getJobDataMapStringValue(map, "filenameFormat");
		hadoopFilename = String.format(filenameFormat, (new Date()).getTime()/1000);
	}

	@Override
	protected int getTotalNumOfSteps() {
		return 3;
	}

	@Override
	protected void runSteps() throws Exception {
		
		boolean isSucceeded = updateTotalScore();
		if(!isSucceeded){
			return;
		}
		
		runAddPartitionQuery();
		
		refreshImpala();
	}
	
	private void runAddPartitionQuery() throws JobExecutionException{	
		startNewStep(String.format("%s add partitions ", impalaTableName));
				
		// declare new partitions for impala
		for (String partition : impalaWriterFactory.getTotalScoreNewPartitions()) {
			try {
				impalaClient.addPartitionToTable(impalaTableName, partition); 
			} catch (JobExecutionException e) {
				logger.error(String.format("got exception while trying to add total score partition %s.", partition), e);
				monitor.warn(getMonitorId(), getStepName(), String.format("got exception while trying to add total score partition %s. exception: %s", partition, e.toString()));
			}
		}
		
		finishStep();		
	}
	
	private boolean updateTotalScore() throws JobExecutionException{
		startNewStep("update");
		try {
			impalaWriterFactory.createTotalScoreAppender(hadoopDirPath, hadoopFilename);
		} catch (IOException e) {
			logger.error("error opening hdfs file for append at " + hadoopDirPath, e);
			monitor.error(getMonitorId(), getStepName(), String.format("error opening hdfs file %s: \n %s", hadoopDirPath, e.toString()));
			throw new JobExecutionException("error opening hdfs file for append at " + hadoopDirPath, e);
		}
		
		try{
			userServiceFacade.updateUserTotalScore();
		} catch(Exception e){
			logger.error("got an exception during the process of updating total score.", e);
			monitor.error(getMonitorId(), getStepName(),String.format("got an exception during the process of updating total score: %s", e.toString()));
			return false;
		} finally{
		
			try {
				impalaWriterFactory.closeTotalScoreAppender();
			} catch (IOException e) {
				logger.error("error closing hdfs file " + hadoopDirPath, e);
				monitor.error(getMonitorId(), getStepName(), String.format("error closing hdfs file %s: \n %s", hadoopDirPath, e.toString()));
				throw new JobExecutionException("error closing hdfs file " + hadoopDirPath, e);
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
