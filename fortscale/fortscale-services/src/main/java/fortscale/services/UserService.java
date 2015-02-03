package fortscale.services;

import fortscale.domain.ad.AdUser;
import fortscale.domain.ad.UserMachine;
import fortscale.domain.core.ApplicationUserDetails;
import fortscale.domain.core.User;
import fortscale.domain.events.LogEventsEnum;
import fortscale.services.fe.Classifier;
import fortscale.services.types.PropertiesDistribution;
import org.apache.commons.lang3.tuple.MutablePair;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserService extends CachingService{
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

	public boolean findIfUserExists(String username);

	public User findByUserId(String userId);

	public User createUser(UserApplication userApplication, String username, String appUsername);

	public boolean createNewApplicationUserDetails(User user, UserApplication userApplication, String username, boolean isSave);
	
	public void fillUpdateUserScore(Update update, User user, Classifier classifier);
	
	public String getTableName(LogEventsEnum eventId);

	public void updateOrCreateUserWithClassifierUsername(Classifier classifier, String normalizedUsername, String logUsername, boolean onlyUpdate, boolean updateAppUsername);

	public void updateUserLastActivityOfType(LogEventsEnum eventId, String username,	DateTime dateTime);
	
	public void updateUsersLastActivityOfType(LogEventsEnum eventId, Map<String, Long> userLastActivityMap);
	
	public void updateUsersLastActivity(Map<String, Long> userLastActivityMap);

	@Deprecated
	public void updateUsersLastActivityGeneralAndPerType(LogEventsEnum eventId, Map<String, Long> userLastActivityMap);

	/**
	 * Update user's info - the last activities of specific user: both the general last-activity and per-type , the logusernmae or create the user if needed
	 * @param username	The username to update
	 * @param dataSourceUpdateOnlyFlagMap	Map: dupdateOnlyFlag - data source
	 * @param userInfo Map: datasource - <lastActivity,logusername>
	 */
	public void updateUsersInfo(String username, Map<String, MutablePair<Long,String>> userInfo,Map<String,Boolean> dataSourceUpdateOnlyFlagMap);

	public void updateUserLastActivity(String username, DateTime dateTime);

	public DateTime findLastActiveTime(LogEventsEnum eventId);
	
	public void updateTags(String username, Map<String, Boolean> tagSettings);
	
	public boolean isUserTagged(String username, String tag);
	
	public PropertiesDistribution getDestinationComputerPropertyDistribution(String uid, String propertyName, int daysToGet, int maxValues, int minScore);
	
	public String findByNormalizedUserName(String normalizedUsername);

	public Set<String> findNamesInGroup(List<String> groupsToTag);

	public Set<String> findNamesInOU(List<String> ousToTag);

	public Set<String> findNamesByTag(String tagFieldName, Boolean value);

	public void updateUserTag(String tagField, String userTagEnumId, String username, boolean value);
}
