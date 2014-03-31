package fortscale.collection.jobs.scoring;

import java.util.Date;

import org.apache.pig.backend.executionengine.ExecJob;
import org.springframework.beans.factory.annotation.Autowired;

import fortscale.collection.hadoop.pig.SecurityEventsScoringPigRunner;
import fortscale.services.UserServiceFacade;
import fortscale.services.fe.Classifier;

public class LDAPAuthenticationScoringJob extends EventScoringJob{
	
	@Autowired
	private UserServiceFacade userServiceFacade;
	
	@Autowired
	private SecurityEventsScoringPigRunner secevtScoringPigRunner;
		
	@Override
	protected boolean runUpdateUserWithEventScore(Date runtime){
		userServiceFacade.updateUserWithAuthScore(Classifier.auth, runtime);
		
		return true;
	}
	
	@Override
	protected ExecJob runPig() throws Exception {
		return secevtScoringPigRunner.run(runtime, earliestEventTime, getPigScriptResource(), getPigInputData(), getPigOutputDataPrefix());
	}
}
