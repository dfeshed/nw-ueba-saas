package fortscale.collection.jobs.scoring;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import fortscale.services.UserServiceFacade;

public class VpnScoringJob extends EventScoringJob{
	
	@Autowired
	private UserServiceFacade userServiceFacade;
			
	@Override
	protected boolean runUpdateUserWithEventScore(Date runtime){
		userServiceFacade.updateUserWithVpnScore(runtime);
		
		return true;
	}
}
