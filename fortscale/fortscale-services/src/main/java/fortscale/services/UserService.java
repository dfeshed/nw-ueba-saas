package fortscale.services;

import fortscale.domain.ad.AdGroup;
import fortscale.domain.ad.AdUser;
import fortscale.domain.ad.UserMachine;
import fortscale.domain.core.ApplicationUserDetails;
import fortscale.domain.core.FavoriteUserFilter;
import fortscale.domain.core.User;
import fortscale.domain.rest.UserFilter;
import fortscale.domain.rest.UserRestFilter;
import fortscale.services.types.PropertiesDistribution;
import fortscale.utils.JksonSerilaizablePair;
import org.joda.time.DateTime;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserService extends CachingService{
	void updateUserWithCurrentADInfo();
	
	void updateUserWithADInfo(Long timestampepoch);
	
	List<User> findBySearchFieldContaining(String prefix, int page, int size);
	

	List<UserMachine> getUserMachines(String uid);

	List<User> getUsersActiveSinceIncludingUsernameAndLogLastActivity(DateTime date);

		
	ApplicationUserDetails createApplicationUserDetails(UserApplication userApplication, String username);
	
	List<User> findByApplicationUserName(UserApplication userApplication, List<String> usernames);

	String getUserThumbnail(User user);

	void updateUserWithADInfo(AdUser adUser);

	void updateUser(User user, Update update);

	boolean findIfUserExists(String username);

	String getUserId(String username);

	User createUser(String userApplication, String username, String appUsername);

	boolean createNewApplicationUserDetails(User user, String userApplication, String username, boolean isSave);

	void updateOrCreateUserWithClassifierUsername(String classifierId, String normalizedUsername, String logUsername, boolean onlyUpdate, boolean updateAppUsername);

	User saveUser(User user);

	/**
	 * Update user's info - the last activities of specific user: both the general last-activity and per-type , the logusernmae or create the user if needed
	 * @param username	The username to update
	 * @param dataSourceUpdateOnlyFlagMap	Map: dupdateOnlyFlag - data source
	 * @param userInfo Map: datasource - <lastActivity,logusername>
	 */
	void updateUsersInfo(String username, Map<String, JksonSerilaizablePair<Long, String>> userInfo, Map<String, Boolean> dataSourceUpdateOnlyFlagMap);
	
	void updateTags(String username, Map<String, Boolean> tagSettings);
	
	boolean isUserTagged(String username, String tag);
	
	PropertiesDistribution getDestinationComputerPropertyDistribution(String uid, String propertyName, Long latestDate, Long earliestDate, int maxValues, int minScore);
	
	String findByNormalizedUserName(String normalizedUsername);

	Set<String> findNamesInGroup(List<String> groupsToTag, Pageable pageable);

	Set<String> findNamesInOU(List<String> ousToTag, Pageable pageable);

	Set<String> findByUsernameRegex(String usernameRegex);

	Set<String> findNamesByTag(String tag);

	Map<String, Set<String>> findAllTaggedUsers();

	String findAdMembers(String adName);

	List<AdGroup> getActiveDirectoryGroups(int maxNumberOfReturnElements);

	void updateUserTag(String userTagEnumId, String username, boolean value);

	User getUserById(String id);

	Boolean isPasswordExpired(User user);

	Boolean isNoPasswordRequiresValue(User user);

	Boolean isNormalUserAccountValue(User user);

	Boolean isPasswordNeverExpiresValue(User user);

	String getOu(User user);

	void fillUserRelatedDns(User user, Set<String> userRelatedDnsSet);

	void fillDnToUsersMap(Set<String> userRelatedDnsSet, Map<String, User> dnToUserMap);

	User getUserManager(User user, Map<String, User> dnToUserMap);

	List<User> getUserDirectReports(User user, Map<String, User> dnToUserMap);

	User findByUsername(String username);

	void updateUserTagList(List<String> tagsToAdd, List<String> tagsToRemove, String username);

	List<Map<String, String>> getUsersByPrefix(String prefix, Pageable pageable);

	List<Map<String, String>> getUsersByIds(String ids, Pageable pageable);

	Set<String> findIdsByTags(String[] tags, String entityIds);

	Set<String> findUsernamesByTags(String[] tags);

	Map<String, Long> groupByTags();

	/**
	 * Count how many users exists in the USERS table with the same display name.
	 * @param displayNames - Set of the display names to query
	 * @return map of display names to users
	 */
	Map<String, Integer> countUsersByDisplayName(Set<String> displayNames);

	List<User> findUsersByFilter(UserRestFilter userRestFilter, PageRequest pageRequest, Set<String> relevantUserIds, List<String> fieldsRequired);

	int countUsersByFilter(UserRestFilter userRestFilter, Set<String> relevantUsers);

	void saveFavoriteFilter(UserFilter userFilter, String filterName);

	List<FavoriteUserFilter> getAllFavoriteFilters();

	long deleteFavoriteFilter(String filterName);

    List<String> getDistinctValuesByFieldName(String fieldName);
}
