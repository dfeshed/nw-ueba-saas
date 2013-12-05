package fortscale.services;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Sort.Direction;

import fortscale.domain.ad.UserMachine;
import fortscale.domain.core.ApplicationUserDetails;
import fortscale.domain.core.User;
import fortscale.domain.fe.IFeature;

public interface UserService {
	
	public void updateUserWithCurrentADInfo();
	
	public void updateUserWithADInfo(String timestamp);
	
	public List<User> findBySearchFieldContaining(String prefix);
	
	public List<IUserScore> getUserScores(String uid); 
	
	public List<IUserScore> getUserScoresByDay(String uid, Long dayTimestamp); 
	
	public List<IFeature> getUserAttributesScores(String uid, String classifierId, Long timestamp, String orderBy, Direction direction); 
	
	public List<IUserScoreHistoryElement> getUserScoresHistory(String uid, String classifierId, int offset, int limit); 
	
	public List<UserMachine> getUserMachines(String uid);
	
	public void updateUserWithAuthScore();

	public User updateUserScore(User user, Date timestamp, String classifierId, double value, double avgScore, boolean isToSave, boolean isSaveMaxScore);
	
	public void updateUserWithVpnScore();
	
	public void updateUserWithGroupMembershipScore();
	
	public ApplicationUserDetails createApplicationUserDetails(UserApplication userApplication, String username);
	
	public ApplicationUserDetails getApplicationUserDetails(User user, UserApplication userApplication);
	
	public List<User> findByApplicationUserName(UserApplication userApplication, List<String> usernames);
	
	public void recalculateUsersScores();

	public void recalculateTotalScore();
	
	public User findByAuthUsername(String username);

	public User findByVpnUsername(String username);
	
	public void removeClassifierFromAllUsers(String classifierId);
}
