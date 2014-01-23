package fortscale.collection.jobs.scoring;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import fortscale.services.UserService;
import fortscale.services.fe.Classifier;



public class SshScoringJob extends EventScoringJob{
	
	@Autowired
	private UserService userService;
	
	@Override
	protected boolean runUpdateUserWithEventScore(Date runtime){
		userService.updateUserWithAuthScore(Classifier.ssh, runtime);
		
		return true;
	}	
}
