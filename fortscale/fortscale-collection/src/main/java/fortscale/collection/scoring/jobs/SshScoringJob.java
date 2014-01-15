package fortscale.collection.scoring.jobs;

import java.util.Date;

import org.apache.pig.backend.executionengine.ExecJob;
import org.springframework.beans.factory.annotation.Autowired;

import fortscale.collection.hadoop.pig.SshScoringPigRunner;
import fortscale.services.LogEventsEnum;
import fortscale.services.UserService;
import fortscale.services.fe.Classifier;



public class SshScoringJob extends EventScoringJob{
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private SshScoringPigRunner sshScoringPigRunner;
		
	@Override
	protected void runSteps() throws Exception{
		runScoringSteps(LogEventsEnum.ssh);		
	}
	
	@Override
	protected boolean runUpdateUserWithEventScore(Date runtime){
		userService.updateUserWithAuthScore(Classifier.ssh, runtime);
		
		return true;
	}


	@Override
	protected ExecJob runPig(Long runtime, Long deltaTime) throws Exception {
		return sshScoringPigRunner.run(runtime, deltaTime);
	}
	
	@Override
	protected int getTotalNumOfSteps() {
		return 4;
	}
}
