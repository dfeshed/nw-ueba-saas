package fortscale.collection.jobs.scoring;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import fortscale.collection.hadoop.ImpalaClient;
import fortscale.collection.io.BufferedLineReader;
import fortscale.collection.jobs.FortscaleJob;
import fortscale.collection.jobs.ad.AdProcessJob;
import fortscale.services.UserServiceFacade;
import fortscale.utils.logging.Logger;

public class AdGroupMembershipScoringJob extends FortscaleJob {
	
	private static Logger logger = Logger.getLogger(AdProcessJob.class);

	@Autowired
	protected ImpalaClient impalaClient;
	
	@Autowired
	private UserServiceFacade userServiceFacade;
	
	@Autowired
	private ImpalaWriterFactoryImpl impalaWriterFactory;
	
	
	@Value("${collection.shell.scripts.dir.path}/runProfRankRuby.sh")
	private String runProfRankRubyScript;


	// job parameters:
	protected String hadoopFilename;
	protected String hadoopDirPath;
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
		
		boolean isSucceeded = runGroupMembershipScoring();
		if(!isSucceeded){
			return;
		}

		isSucceeded = updateGroupMembershipScore();
		if(!isSucceeded){
			return;
		}
		
		runAddPartitionQuery();
		
		refreshImpala();
	}
	
	private void runAddPartitionQuery() throws JobExecutionException{	
		startNewStep(String.format("%s add partitions ", impalaTableName));
				
		// declare new partitions for impala
		for (String partition : impalaWriterFactory.getGroupsScoreNewPartitions()) {
			try {
				impalaClient.addPartitionToTable(impalaTableName, partition); 
			} catch (JobExecutionException e) {
				logger.error(String.format("got exception while trying to add group score partition %s.", partition), e);
				monitor.warn(getMonitorId(), getStepName(), String.format("got exception while trying to add group score partition %s. exception: %s", partition, e.toString()));
			}
		}
		
		finishStep();		
	}
	
	private boolean updateGroupMembershipScore() throws JobExecutionException{
		startNewStep("update");
		try {
			impalaWriterFactory.createGroupsScoreAppender(hadoopDirPath, hadoopFilename);
		} catch (IOException e) {
			logger.error("error opening hdfs file for append at " + hadoopDirPath, e);
			monitor.error(getMonitorId(), getStepName(), String.format("error opening hdfs file %s: \n %s", hadoopDirPath, e.toString()));
			throw new JobExecutionException("error opening hdfs file for append at " + hadoopDirPath, e);
		}
		
		try {
			userServiceFacade.updateUserWithGroupMembershipScore();
		} catch(Exception e){
			logger.error("got an exception during the process of updating group membership.", e);
			monitor.error(getMonitorId(), getStepName(),String.format("got an exception during the process of updating group membership: %s", e.toString()));
			return false;
		} finally{
		
			try {
				impalaWriterFactory.closeGroupsScoreAppender();
			} catch (IOException e) {
				logger.error("error closing hdfs file " + hadoopDirPath, e);
				monitor.error(getMonitorId(), getStepName(), String.format("error closing hdfs file %s: \n %s", hadoopDirPath, e.toString()));
				throw new JobExecutionException("error closing hdfs file " + hadoopDirPath, e);
			}
		}
		finishStep();
		
		return true;
	}
	
	private boolean runGroupMembershipScoring() throws JobExecutionException, IOException{
		startNewStep("scoring");
		BufferedLineReader reader = null;
		try {
			Process pr =  runCmd(null, runProfRankRubyScript);
			reader = new BufferedLineReader( new BufferedReader(new InputStreamReader(pr.getInputStream())));
			String line = null;
			while ((line = reader.readLine()) != null) {
				logger.info(line);
			}
			
			if (reader.HasErrors()) {
				monitor.error(getMonitorId(), getStepName(), reader.getException().toString());
			} else {
				if (reader.hasWarnings()) {
					monitor.warn(getMonitorId(), getStepName(), reader.getException().toString());
				}
			}
		} finally{
			if(reader != null){
				reader.close();
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
