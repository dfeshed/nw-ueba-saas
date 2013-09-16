package fortscale.services;

import java.util.Date;
import java.util.List;

import fortscale.domain.ad.UserMachine;
import fortscale.domain.core.User;
import fortscale.domain.fe.IFeature;

public interface UserService {

	public User getUserById(String uid);
	
	public void updateUserWithCurrentADInfo();
	
	public void updateUserWithADInfo(String timestamp);
	
	public List<User> findBySearchFieldContaining(String prefix);
	
	public List<IUserScore> getUserScores(String uid); 
	
	public List<IFeature> getUserAttributesScores(String uid, String classifierId, Date timestamp); 
	
	public List<IUserScoreHistoryElement> getUserScoresHistory(String uid, String classifierId); 
	
	public List<UserMachine> getUserMachines(String uid);
	
	public void updateUserWithAuthScore();

	public void updateUserScore(User user, Date timestamp, String classifierId, double value, double avgScore);
}
