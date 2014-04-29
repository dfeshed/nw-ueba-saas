package fortscale.services;

import java.util.List;

import org.springframework.data.mongodb.core.query.Update;

import fortscale.domain.ad.AdUser;
import fortscale.domain.ad.UserMachine;
import fortscale.domain.core.ApplicationUserDetails;
import fortscale.domain.core.User;
import fortscale.domain.events.LogEventsEnum;
import fortscale.services.fe.Classifier;

public interface UserService {
	public void updateUserWithCurrentADInfo();
	
	public void updateUserWithADInfo(Long timestampepoch);
	
	public List<User> findBySearchFieldContaining(String prefix, int page, int size);
	
	public List<UserMachine> getUserMachines(String uid);
		
	public ApplicationUserDetails createApplicationUserDetails(UserApplication userApplication, String username);
	
	public ApplicationUserDetails getApplicationUserDetails(User user, UserApplication userApplication);
	
	public List<User> findByApplicationUserName(UserApplication userApplication, List<String> usernames);
			
	public void removeClassifierFromAllUsers(String classifierId);
	
	public String getUserThumbnail(User user);

	public void updateUserWithADInfo(AdUser adUser);

	public void updateUser(User user, Update update);
		
	public User findByUserId(String userId);

	public User createUser(UserApplication userApplication, String username, String appUsername);

	public boolean createNewApplicationUserDetails(User user, UserApplication userApplication, String username, boolean isSave);
	
	public void fillUpdateUserScore(Update update, User user, Classifier classifier);
	
	public String getTableName(LogEventsEnum eventId);

	public void updateOrCreateUserWithClassifierUsername(Classifier classifier, String normalizedUsername, String logUsername, boolean onlyUpdate, boolean updateAppUsername);
}
