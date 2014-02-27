package fortscale.services;

import java.util.List;

import org.springframework.data.mongodb.core.query.Update;

import fortscale.domain.ad.AdUser;
import fortscale.domain.ad.UserMachine;
import fortscale.domain.core.ApplicationUserDetails;
import fortscale.domain.core.User;
import fortscale.services.fe.Classifier;

public interface UserService {
	public void updateUserWithCurrentADInfo();
	
	public void updateUserWithADInfo(Long timestampepoch);
	
	public List<User> findBySearchFieldContaining(String prefix, int page, int size);
	
	public List<UserMachine> getUserMachines(String uid);
		
	public ApplicationUserDetails createApplicationUserDetails(UserApplication userApplication, String username);
	
	public ApplicationUserDetails getApplicationUserDetails(User user, UserApplication userApplication);
	
	public List<User> findByApplicationUserName(UserApplication userApplication, List<String> usernames);
		
	public User findByAuthUsername(LogEventsEnum eventId, String username);
	
	public void removeClassifierFromAllUsers(String classifierId);

	public List<String> getFollowedUsersVpnLogUsername();

	public List<String> getFollowedUsersAuthLogUsername(LogEventsEnum eventId);
	
	public String getUserThumbnail(User user);

	public void updateUserWithADInfo(AdUser adUser);

	public void updateUser(User user, Update update);
	
	public String getAuthLogUsername(LogEventsEnum eventId, User user);
	
	public String getVpnLogUsername(User user);
	
	public User findByUserId(String userId);

	public void updateLogUsername(User user, String logname, String username, boolean isSave);

	public User createUser(UserApplication userApplication, String username);

	public boolean createNewApplicationUserDetails(User user, UserApplication userApplication, String username, boolean isSave);
	
	public void fillUpdateUserScore(Update update, User user, Classifier classifier);
	
	public void fillUpdateLogUsername(Update update, String username, String logname);
	
	public void fillUpdateAppUsername(Update update,  User user, Classifier classifier);

	public User findByLogUsername(LogEventsEnum eventId, String username);
}
