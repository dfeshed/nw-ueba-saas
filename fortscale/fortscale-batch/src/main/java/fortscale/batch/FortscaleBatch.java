package fortscale.batch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import fortscale.activedirectory.main.ADManager;
import fortscale.services.AdService;
import fortscale.services.UserService;
import fortscale.services.fe.impl.FeServiceImpl;
import fortscale.services.impl.ImpalaWriterFactory;




@Configuration
public class FortscaleBatch {

	@Autowired
	private FeServiceImpl feService;
	@Autowired
	private UserService userService;
	@Autowired
	private AdService adService;
	@Autowired
	private ImpalaWriterFactory impalaGroupsScoreWriterFactory;
	
	
	public void runfe(String userAdScoreCsvFileFullPathString) {
		ADManager adManager = new ADManager();
		if(userAdScoreCsvFileFullPathString != null) {
			impalaGroupsScoreWriterFactory.setUserAdScoreCsvFileFullPathString(userAdScoreCsvFileFullPathString);
		}
		adManager.run(feService, null);
	}
	
	public void updateGroupMembershipScore(String userAdScoreCsvFileFullPathString, String userTotalScoreCsvFileFullPathString) {
		if(userAdScoreCsvFileFullPathString != null) {
			impalaGroupsScoreWriterFactory.setUserAdScoreCsvFileFullPathString(userAdScoreCsvFileFullPathString);
		}
		if(userTotalScoreCsvFileFullPathString != null) {
			impalaGroupsScoreWriterFactory.setUserTotalScoreCsvFileFullPathString(userTotalScoreCsvFileFullPathString);
		}
		userService.updateUserWithGroupMembershipScore();
	}
	
	public void updateAdInfo() {
		userService.updateUserWithCurrentADInfo();
		adService.addLastModifiedFieldToAllCollections();
	}
	
	public void updateAuthScore(String userTotalScoreCsvFileFullPathString) {
		if(userTotalScoreCsvFileFullPathString != null) {
			impalaGroupsScoreWriterFactory.setUserTotalScoreCsvFileFullPathString(userTotalScoreCsvFileFullPathString);
		}
		userService.updateUserWithAuthScore();
	}
	
	public void updateVpnScore(String userTotalScoreCsvFileFullPathString) {
		if(userTotalScoreCsvFileFullPathString != null) {
			impalaGroupsScoreWriterFactory.setUserTotalScoreCsvFileFullPathString(userTotalScoreCsvFileFullPathString);
		}
		userService.updateUserWithVpnScore();
	}
}
