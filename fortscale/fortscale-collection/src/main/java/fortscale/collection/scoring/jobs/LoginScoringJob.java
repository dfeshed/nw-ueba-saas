package fortscale.collection.scoring.jobs;

import java.util.Date;

import org.apache.pig.backend.executionengine.ExecJob;
import org.springframework.beans.factory.annotation.Autowired;

import fortscale.collection.hadoop.pig.LoginScoringPigRunner;
import fortscale.services.LogEventsEnum;
import fortscale.services.UserService;
import fortscale.services.fe.Classifier;

public class LoginScoringJob extends EventScoringJob{
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private LoginScoringPigRunner loginScoringPigRunner;
	
	@Override
	protected void runSteps() throws Exception{
		
		runScoringSteps(LogEventsEnum.login);		
	}
	
//	private boolean runPrepareRegex(){
//		String cmd = "/home/cloudera/fortscale/fortscale-scripts/scripts/uploadWMIDataToHDFS_part4_prepareregex.sh";
//		String stepName = "prepareregex";
//		
//		return runCmd(cmd, stepName);
//	}
	
	@Override
	protected boolean runUpdateUserWithEventScore(Date runtime){
		userService.updateUserWithAuthScore(Classifier.auth, runtime);
		
		return true;
	}
	
	@Override
	protected ExecJob runPig(Long runtime, Long deltaTime) throws Exception {
		return loginScoringPigRunner.run(runtime, deltaTime);
	}

	@Override
	protected int getTotalNumOfSteps() {
		return 4;
	}	
}
