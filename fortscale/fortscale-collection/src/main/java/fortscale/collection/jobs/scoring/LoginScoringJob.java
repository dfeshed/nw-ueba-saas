package fortscale.collection.jobs.scoring;

import java.util.Date;

import org.apache.pig.backend.executionengine.ExecJob;
import org.springframework.beans.factory.annotation.Autowired;

import fortscale.collection.hadoop.pig.SecurityEventsScoringPigRunner;;

public class LoginScoringJob extends EventScoringJob {

	@Autowired
	private SecurityEventsScoringPigRunner secevtScoringPigRunner;
	
	@Override
	protected ExecJob runPig() throws Exception {
		return secevtScoringPigRunner.run(runtime, earliestEventTime, getPigScriptResource(), getPigInputData(), getPigOutputDataPrefix());
	}

	@Override
	protected boolean runUpdateUserWithEventScore(Date runtime) {
		// no need to update user score as we currently don't have a ui page 
		// for the login scoring of a user 
		return true;
	}
	
}
