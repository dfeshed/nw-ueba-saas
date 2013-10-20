package fortscale.batch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import fortscale.activedirectory.main.ADManager;
import fortscale.services.UserService;
import fortscale.services.fe.FeService;




@Configuration
public class FortscaleBatch {

	@Autowired
	private FeService feService;
	@Autowired
	private UserService userService;
	
	
	public void runfe() {
		ADManager adManager = new ADManager();
		adManager.run(feService, null);
	}
	
	public void updateAdInfo() {
		userService.updateUserWithCurrentADInfo();
	}
	
	public void updateAuthScore() {
		userService.updateUserWithAuthScore();
	}
	
	public void updateVpnScore() {
		userService.updateUserWithVpnScore();
	}
}
