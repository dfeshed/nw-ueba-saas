package fortscale.services;


import fortscale.domain.core.Entity;
import fortscale.domain.core.FavoriteUserFilter;
import fortscale.domain.core.dao.rest.Entities;
import fortscale.domain.rest.EntityFilter;
import fortscale.domain.rest.EntityRestFilter;

import org.springframework.data.domain.PageRequest;


import java.util.List;
import java.util.Map;
import java.util.Set;

public interface EntityService {

	List<Entity> findByIds(List<String> ids);

	public Map<String,Integer> getAlertsTypes();
	
	List<Entity> findBySearchFieldContaining(String prefix, int page, int size);


	String getEntityThumbnail(Entity entity);


	Set<Entity> findByFollowed();


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

	Entity getEntityById(String id);

	Boolean isPasswordExpired(Entity entity);

	Boolean isNoPasswordRequiresValue(Entity entity);

	Boolean isNormalUserAccountValue(Entity entity);

	Boolean isPasswordNeverExpiresValue(Entity entity);

	String getOu(Entity entity);



	Entity getUserManager(Entity entity, Map<String, Entity> dnToUserMap);

	List<Entity> getUserDirectReports(Entity entity, Map<String, Entity> dnToUserMap);

	Entity findByUsername(String username);


	void updateUserTagList(List<String> tagsToAdd, List<String> tagsToRemove, String username);

//	List<Map<String, String>> getEntitiesByPrefix(String prefix, Pageable pageable);
//
//	List<Map<String, String>> getUsersByIds(String ids, Pageable pageable);
//
	Set<String> findIdsByTags(String[] tags, String entityIds);

	Set<String> findEntityNamesByTags(String[] tags);

	Map<String, Long> groupByTags(boolean forceCacheUpdate);

//	/**
//	 * Count how many users exists in the USERS table with the same display name.
//	 * @param displayNames - Set of the display names to query
//	 * @return map of display names to users
//	 */
////	Map<String, Integer> countUsersByDisplayName(Set<String> displayNames);

	Entities findEntitiesByFilter(EntityRestFilter userRestFilter, PageRequest pageRequest, Set<String> relevantUserIds, List<String> fieldsRequired, boolean fetchAlertsOnUsers);

	int countEntitiesByFilter(EntityRestFilter userRestFilter, Set<String> relevantUsers);

	void saveFavoriteFilter(EntityFilter entityFilter, String filterName);

	List<FavoriteUserFilter> getAllFavoriteFilters();

	long deleteFavoriteFilter(String filterName);

    List getDistinctValuesByFieldName(String fieldName);

//	void updateSourceMachineCount(String userId, int sourceMachineCount);

//	int updateTags(EntityRestFilter userRestFilter, Boolean addTag, List<String> tagNames, Set<String> relevantUsers);

	int updateWatched(EntityRestFilter userRestFilter, Set<String> relevantUsers, Boolean watch);
	int updateSingleEntityWatched(String userId, Boolean watch);

//	Map<String,Integer> getDistinctAnomalyType();

//	int removeTagFromAllEntities(String tagName);
//
//    int updateUserScoreForUsersNotInIdList(Set<String> userIds, double score);

	Entity findOne(String id);

	Map<String,Map<String,Integer>> getSeverityScoreMap(EntityRestFilter userRestFilter);
	List<Entity> getEntitiesByPrefix(String entityName, PageRequest pageRequest);
	Map<String,Integer> getDistinctAnomalyType();

}
