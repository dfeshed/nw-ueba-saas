package fortscale.collection.jobs.scoring;

import java.util.Date;

import org.apache.pig.backend.executionengine.ExecJob;
import org.springframework.beans.factory.annotation.Autowired;

import fortscale.collection.hadoop.pig.VpnScoringPigRunner;
import fortscale.services.LogEventsEnum;
import fortscale.services.UserService;

public class VpnScoringJob extends EventScoringJob{
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private VpnScoringPigRunner vpnScoringPigRunner;
	
	
	@Override
	protected void runSteps() throws Exception{
		runScoringSteps(LogEventsEnum.vpn);		
	}
	
	@Override
	protected boolean runUpdateUserWithEventScore(Date runtime){
		userService.updateUserWithVpnScore(runtime);
		
		return true;
	}



	@Override
	protected ExecJob runPig(Long runtime, Long deltaTime) throws Exception {
		return vpnScoringPigRunner.run(runtime, deltaTime);
	}
}
