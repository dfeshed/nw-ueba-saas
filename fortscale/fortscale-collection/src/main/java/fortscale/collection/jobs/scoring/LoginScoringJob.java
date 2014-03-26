package fortscale.collection.jobs.scoring;

import java.util.Date;

import org.apache.pig.backend.executionengine.ExecJob;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import fortscale.collection.hadoop.pig.LoginScoringPigRunner;
import fortscale.services.UserServiceFacade;
import fortscale.services.fe.Classifier;

public class LoginScoringJob extends EventScoringJob{
	
	@Autowired
	private UserServiceFacade userServiceFacade;
	
	@Autowired
	private LoginScoringPigRunner loginScoringPigRunner;
		
	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		super.getJobParameters(jobExecutionContext);
		
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();
		String pigLoginInputData = jobDataMapExtension.getJobDataMapStringValue(map, "pigLoginInputData");
		loginScoringPigRunner.setPigLoginInputData(pigLoginInputData);
	}
	
	
//	private boolean runPrepareRegex(){
//		String cmd = "/home/cloudera/fortscale/fortscale-scripts/scripts/uploadWMIDataToHDFS_part4_prepareregex.sh";
//		String stepName = "prepareregex";
//		
//		return runCmd(cmd, stepName);
//	}
	
	@Override
	protected boolean runUpdateUserWithEventScore(Date runtime){
		userServiceFacade.updateUserWithAuthScore(Classifier.auth, runtime);
		
		return true;
	}
	
	@Override
	protected ExecJob runPig() throws Exception {
		return loginScoringPigRunner.run(runtime, earliestEventTime, getPigScriptResource(), getPigInputData(), getPigOutputDataPrefix());
	}
}
