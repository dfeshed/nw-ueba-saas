package fortscale.batch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import fortscale.activedirectory.main.ADManager;
import fortscale.services.UserService;
import fortscale.services.fe.impl.FeServiceImpl;
import fortscale.services.impl.ImpalaScoreWriterFactory;




@Configuration
public class FortscaleBatch {

	@Autowired
	private FeServiceImpl feService;
	@Autowired
	private UserService userService;
	@Autowired
	private ImpalaScoreWriterFactory impalaGroupsScoreWriterFactory;
	
	
	public void runfe(String userAdScoreCsvFileFullPathString) {
		ADManager adManager = new ADManager();
		if(userAdScoreCsvFileFullPathString != null) {
			impalaGroupsScoreWriterFactory.setUserAdScoreCsvFileFullPathString(userAdScoreCsvFileFullPathString);
		}
		adManager.run(feService, null);
	}
	
	public void updateGroupMembershipScore(String userAdScoreCsvFileFullPathString) {
		if(userAdScoreCsvFileFullPathString != null) {
			impalaGroupsScoreWriterFactory.setUserAdScoreCsvFileFullPathString(userAdScoreCsvFileFullPathString);
		}
		userService.updateUserWithGroupMembershipScore();
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
