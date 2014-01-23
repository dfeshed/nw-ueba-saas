package fortscale.collection.jobs.scoring;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import fortscale.services.UserService;

public class VpnScoringJob extends EventScoringJob{
	
	@Autowired
	private UserService userService;
			
	@Override
	protected boolean runUpdateUserWithEventScore(Date runtime){
		userService.updateUserWithVpnScore(runtime);
		
		return true;
	}
}
