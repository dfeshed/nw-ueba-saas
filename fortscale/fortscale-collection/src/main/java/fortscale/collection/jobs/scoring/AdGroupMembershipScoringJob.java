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
import org.springframework.core.io.Resource;

import fortscale.collection.hadoop.ImpalaClient;
import fortscale.collection.io.BufferedLineReader;
import fortscale.collection.jobs.FortscaleJob;
import fortscale.collection.jobs.ad.AdProcessJob;
import fortscale.services.UserService;
import fortscale.utils.logging.Logger;

public class AdGroupMembershipScoringJob extends FortscaleJob {
	
	private static Logger logger = Logger.getLogger(AdProcessJob.class);

	@Autowired
	protected ImpalaClient impalaClient;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ImpalaWriterFactoryImpl impalaWriterFactory;
	
	
	@Value("${collection.shell.scripts.dir.path}/runProfRankRuby.sh")
	private String runProfRankRubyScript;


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
		
		refreshImpala();
	}
	
	private boolean updateGroupMembershipScore() throws JobExecutionException{
		startNewStep("update");
		try {
			impalaWriterFactory.createGroupsScoreAppender(hadoopFilePath);
		} catch (IOException e) {
			logger.error("error opening hdfs file for append at " + hadoopFilePath, e);
			monitor.error(getMonitorId(), getStepName(), String.format("error opening hdfs file %s: \n %s", hadoopFilePath, e.toString()));
			throw new JobExecutionException("error opening hdfs file for append at " + hadoopFilePath, e);
		}
		userService.updateUserWithGroupMembershipScore();
		
		try {
			impalaWriterFactory.closeGroupsScoreAppender();
		} catch (IOException e) {
			logger.error("error closing hdfs file " + hadoopFilePath, e);
			monitor.error(getMonitorId(), getStepName(), String.format("error closing hdfs file %s: \n %s", hadoopFilePath, e.toString()));
			throw new JobExecutionException("error closing hdfs file " + hadoopFilePath, e);
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
