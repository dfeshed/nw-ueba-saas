package fortscale.services;


import fortscale.domain.core.FavoriteUserFilter;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.rest.Users;
import fortscale.domain.rest.UserFilter;
import fortscale.domain.rest.UserRestFilter;

import fortscale.utils.JksonSerilaizablePair;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserService {

	List<User> findByIds(List<String> ids);

	public Map<String,Integer> getAlertsTypes();
	
	List<User> findBySearchFieldContaining(String prefix, int page, int size);


	String getUserThumbnail(User user);


	Set<User> findByFollowed();


//	String findByNormalizedUserName(String normalizedUsername);
//
//	Set<String> findNamesInGroup(List<String> groupsToTag, Pageable pageable);
//
//	Set<String> findNamesInOU(List<String> ousToTag, Pageable pageable);
//
//	Set<String> findByUsernameRegex(String usernameRegex);
//
//	Set<String> findNamesByTag(String tag);
//
//	Map<String, Set<String>> findAllTaggedUsers();





//	void updateUserTag(String userTagEnumId, String username, boolean value);

	User getUserById(String id);

	Boolean isPasswordExpired(User user);

	Boolean isNoPasswordRequiresValue(User user);

	Boolean isNormalUserAccountValue(User user);

	Boolean isPasswordNeverExpiresValue(User user);

	String getOu(User user);



	User getUserManager(User user, Map<String, User> dnToUserMap);

	List<User> getUserDirectReports(User user, Map<String, User> dnToUserMap);

	User findByUsername(String username);


	void updateUserTagList(List<String> tagsToAdd, List<String> tagsToRemove, String username);

//	List<Map<String, String>> getUsersByPrefix(String prefix, Pageable pageable);
//
//	List<Map<String, String>> getUsersByIds(String ids, Pageable pageable);
//
	Set<String> findIdsByTags(String[] tags, String entityIds);

	Set<String> findUsernamesByTags(String[] tags);

	Map<String, Long> groupByTags(boolean forceCacheUpdate);

//	/**
//	 * Count how many users exists in the USERS table with the same display name.
//	 * @param displayNames - Set of the display names to query
//	 * @return map of display names to users
//	 */
////	Map<String, Integer> countUsersByDisplayName(Set<String> displayNames);

	Users findUsersByFilter(UserRestFilter userRestFilter, PageRequest pageRequest, Set<String> relevantUserIds, List<String> fieldsRequired,boolean fetchAlertsOnUsers);

	int countUsersByFilter(UserRestFilter userRestFilter, Set<String> relevantUsers);

	void saveFavoriteFilter(UserFilter userFilter, String filterName);

	List<FavoriteUserFilter> getAllFavoriteFilters();

	long deleteFavoriteFilter(String filterName);

    List getDistinctValuesByFieldName(String fieldName);

//	void updateSourceMachineCount(String userId, int sourceMachineCount);

//	int updateTags(UserRestFilter userRestFilter, Boolean addTag, List<String> tagNames, Set<String> relevantUsers);

	int updateWatched(UserRestFilter userRestFilter, Set<String> relevantUsers, Boolean watch);
	int updateSingleUserWatched(String userId, Boolean watch);

//	Map<String,Integer> getDistinctAnomalyType();

//	int removeTagFromAllUsers(String tagName);
//
//    int updateUserScoreForUsersNotInIdList(Set<String> userIds, double score);

	User findOne(String id);

	Map<String,Map<String,Integer>> getSeverityScoreMap(UserRestFilter userRestFilter);
	List<User> getUsersByPrefix(String entityName, PageRequest pageRequest);
	Map<String,Integer> getDistinctAnomalyType();

}
