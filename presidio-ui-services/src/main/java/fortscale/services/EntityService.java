package fortscale.services;


import fortscale.domain.core.Entity;
import fortscale.domain.core.FavoriteEntityFilter;
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

	Set<String> findIdsByTags(String[] tags, String entityIds);

	Set<String> findEntityNamesByTags(String[] tags);

	Map<String, Long> groupByTags(boolean forceCacheUpdate);

	Entities findEntitiesByFilter(EntityRestFilter userRestFilter, PageRequest pageRequest, Set<String> relevantUserIds, List<String> fieldsRequired, boolean fetchAlertsOnUsers);

	int countEntitiesByFilter(EntityRestFilter userRestFilter, Set<String> relevantUsers);

	void saveFavoriteFilter(EntityFilter entityFilter, String filterName);

	List<FavoriteEntityFilter> getAllFavoriteFilters();

	long deleteFavoriteFilter(String filterName);

    List getDistinctValuesByFieldName(String fieldName);



	int updateWatched(EntityRestFilter userRestFilter, Set<String> relevantUsers, Boolean watch);
	int updateSingleEntityWatched(String userId, Boolean watch);


	Entity findOne(String id);

	Map<String,Map<String,Integer>> getSeverityScoreMap(EntityRestFilter userRestFilter);
	List<Entity> getEntitiesByPrefix(String entityName, PageRequest pageRequest);
	Map<String,Integer> getDistinctAnomalyType();

}
