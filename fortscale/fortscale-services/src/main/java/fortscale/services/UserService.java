package fortscale.services;

import fortscale.domain.ad.AdGroup;
import fortscale.domain.ad.AdUser;
import fortscale.domain.ad.UserMachine;
import fortscale.domain.core.ApplicationUserDetails;
import fortscale.domain.core.User;
import fortscale.domain.rest.UserRestFilter;
import fortscale.services.types.PropertiesDistribution;
import fortscale.utils.JksonSerilaizablePair;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
	
	public List<User> findByApplicationUserName(UserApplication userApplication, List<String> usernames);
			
	public void removeClassifierFromAllUsers(String classifierId);
	
	public String getUserThumbnail(User user);

	public void updateUserWithADInfo(AdUser adUser);

	public void updateUser(User user, Update update);

	public boolean findIfUserExists(String username);

	public String getUserId(String username);

	public User createUser(String userApplication, String username, String appUsername);

	public boolean createNewApplicationUserDetails(User user, String userApplication, String username, boolean isSave);

	public void updateOrCreateUserWithClassifierUsername(String classifierId, String normalizedUsername, String logUsername, boolean onlyUpdate, boolean updateAppUsername);

	/**
	 * Update user's info - the last activities of specific user: both the general last-activity and per-type , the logusernmae or create the user if needed
	 * @param username	The username to update
	 * @param dataSourceUpdateOnlyFlagMap	Map: dupdateOnlyFlag - data source
	 * @param userInfo Map: datasource - <lastActivity,logusername>
	 */
	public void updateUsersInfo(String username, Map<String, JksonSerilaizablePair<Long,String>> userInfo,Map<String,Boolean> dataSourceUpdateOnlyFlagMap);
	
	public void updateTags(String username, Map<String, Boolean> tagSettings);
	
	public boolean isUserTagged(String username, String tag);
	
	public PropertiesDistribution getDestinationComputerPropertyDistribution(String uid, String propertyName, Long latestDate, Long earliestDate, int maxValues, int minScore);
	
	public String findByNormalizedUserName(String normalizedUsername);

	public Set<String> findNamesInGroup(List<String> groupsToTag, Pageable pageable);

	public Set<String> findNamesInOU(List<String> ousToTag, Pageable pageable);

	public Set<String> findNamesByTag(String tagFieldName, Boolean value);

	public String findAdMembers(String adName);

	public List<AdGroup> getActiveDirectoryGroups(int maxNumberOfReturnElements);

	public Set<String> findNamesByTag(String tagFieldName, String value);

	public void updateUserTag(String tagField, String userTagEnumId, String username, boolean value);

	User getUserById(String id);

	public Boolean isPasswordExpired(User user);

	public Boolean isNoPasswordRequiresValue(User user);

	public Boolean isNormalUserAccountValue(User user);

	public Boolean isPasswordNeverExpiresValue(User user);

	public String getOu(User user);

	public void fillUserRelatedDns(User user, Set<String> userRelatedDnsSet);

	public void fillDnToUsersMap(Set<String> userRelatedDnsSet, Map<String, User> dnToUserMap);

	public User getUserManager(User user, Map<String, User> dnToUserMap);

	public List<User> getUserDirectReports(User user, Map<String, User> dnToUserMap);

	public User findByUsername(String username);

	public void updateUserTagList(List<String> tagsToAdd, List<String> tagsToRemove , String username);

	public List<Map<String, String>> getUsersByPrefix(String prefix, Pageable pageable);

	public List<Map<String, String>> getUsersByIds(String ids, Pageable pageable);

	public Set<String> findIdsByTags(String[] tags, String entityIds);

	public Map<String, Long> groupByTags();

	/**
	 * Count how many users exists in the USERS table with the same display name.
	 * @param displayNames - Set of the display names to query
	 * @return map of display names to users
	 */
	public Map<String, Integer> countUsersByDisplayName(Set<String> displayNames);

	public void recalculateNumberOfUserAlerts(String userName);

	public List<User> findUsersByFilter(UserRestFilter userRestFilter, PageRequest pageRequest);

	public int countUsersByFilter(UserRestFilter userRestFilter);
}
