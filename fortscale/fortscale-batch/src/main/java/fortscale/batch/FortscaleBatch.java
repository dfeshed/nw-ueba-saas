package fortscale.batch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fortscale.services.AdService;
import fortscale.services.UserServiceFacade;
import fortscale.services.fe.Classifier;
import fortscale.services.fe.impl.FeServiceImpl;
import fortscale.services.impl.ImpalaWriterFactory;




@Component
public class FortscaleBatch {

	@Autowired
	private FeServiceImpl feService;
	@Autowired
	private UserServiceFacade userServiceFacade;
	@Autowired
	private AdService adService;
	@Autowired
	private ImpalaWriterFactory impalaGroupsScoreWriterFactory;
	
	
//	public void runfe(String userAdScoreCsvFileFullPathString) {
//		ADManager adManager = new ADManager();
//		if(userAdScoreCsvFileFullPathString != null) {
//			impalaGroupsScoreWriterFactory.setUserAdScoreCsvFileFullPathString(userAdScoreCsvFileFullPathString);
//		}
//		adManager.run(feService, null);
//	}
	
	public void updateGroupMembershipScore(String userAdScoreCsvFileFullPathString, String userTotalScoreCsvFileFullPathString) {
		if(userAdScoreCsvFileFullPathString != null) {
			impalaGroupsScoreWriterFactory.setUserAdScoreCsvFileFullPathString(userAdScoreCsvFileFullPathString);
		}
		if(userTotalScoreCsvFileFullPathString != null) {
			impalaGroupsScoreWriterFactory.setUserTotalScoreCsvFileFullPathString(userTotalScoreCsvFileFullPathString);
		}
		userServiceFacade.updateUserWithGroupMembershipScore();
	}
	
	public void updateAdInfo() {
		userServiceFacade.updateUserWithCurrentADInfo();
		adService.addLastModifiedFieldToAllCollections();
		adService.removeThumbnails();
	}
	
	public void updateAuthScore(String userTotalScoreCsvFileFullPathString) {
		if(userTotalScoreCsvFileFullPathString != null) {
			impalaGroupsScoreWriterFactory.setUserTotalScoreCsvFileFullPathString(userTotalScoreCsvFileFullPathString);
		}
		userServiceFacade.updateUserWithAuthScore(Classifier.auth);
	}
	
	public void updateSshScore(String userTotalScoreCsvFileFullPathString) {
		if(userTotalScoreCsvFileFullPathString != null) {
			impalaGroupsScoreWriterFactory.setUserTotalScoreCsvFileFullPathString(userTotalScoreCsvFileFullPathString);
		}
		userServiceFacade.updateUserWithAuthScore(Classifier.ssh);
	}
	
	public void updateVpnScore(String userTotalScoreCsvFileFullPathString) {
		if(userTotalScoreCsvFileFullPathString != null) {
			impalaGroupsScoreWriterFactory.setUserTotalScoreCsvFileFullPathString(userTotalScoreCsvFileFullPathString);
		}
		userServiceFacade.updateUserWithVpnScore();
	}
}
