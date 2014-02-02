package fortscale.collection.jobs.scoring;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import fortscale.services.UserServiceFacade;
import fortscale.services.fe.Classifier;



public class SshScoringJob extends EventScoringJob{
	
	@Autowired
	private UserServiceFacade userServiceFacade;
	
	@Override
	protected boolean runUpdateUserWithEventScore(Date runtime){
		userServiceFacade.updateUserWithAuthScore(Classifier.ssh, runtime);
		
		return true;
	}	
}
