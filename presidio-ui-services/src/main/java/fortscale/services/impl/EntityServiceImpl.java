package fortscale.services.impl;



import fortscale.domain.core.*;


import fortscale.domain.core.Entity;

import fortscale.domain.core.dao.FavoriteEntityFilterRepository;
import fortscale.domain.core.dao.rest.Entities;
import fortscale.domain.rest.EntityFilter;
import fortscale.domain.rest.EntityRestFilter;

import fortscale.presidio.output.client.api.EntitiesPresidioOutputClient;

import fortscale.services.EntityService;

import fortscale.services.presidio.core.converters.AggregationConverterHelper;
import fortscale.services.presidio.core.converters.EntityConverterHelper;
import fortscale.utils.JksonSerilaizablePair;

import fortscale.utils.logging.Logger;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.SetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import presidio.output.client.client.ApiException;
import presidio.output.client.model.*;
import presidio.output.client.model.JsonPatch;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service("userService")
public class EntityServiceImpl implements EntityService {

	private static Logger logger = Logger.getLogger(EntityServiceImpl.class);
	private static final String SEARCH_FIELD_PREFIX = "##";


	@Autowired
	private MongoOperations mongoTemplate;

	@Autowired
	private FavoriteEntityFilterRepository favoriteUserFilterRepository;


	@Value("${ad.info.update.read.page.size:1000}")
	private int readPageSize;

	@Value("${users.ou.filter:}")
	private String usersOUfilter;

	@Value("${user.service.impl.page.size:1000}")
	private int userServiceImplPageSize;


	@Value("${list.of.builtin.ad.users:Administrator,Guest,krbtgt}")
	private String listOfBuiltInADUsers;


	private EntityConverterHelper entityConverterHelper;
	private AggregationConverterHelper aggregationConverterHelper;
	private List<String> setOfBuiltInADUsers;
	private EntitiesPresidioOutputClient remoteEntityClientService;

	public EntityServiceImpl(EntityConverterHelper entityConverterHelper, AggregationConverterHelper aggregationConverterHelper,
							 EntitiesPresidioOutputClient remoteEntityClientService) {
		this.entityConverterHelper = entityConverterHelper;
		this.aggregationConverterHelper = aggregationConverterHelper;
		this.remoteEntityClientService = remoteEntityClientService;
	}

	// For unit tests only
	protected int getPageSize() {
		return userServiceImplPageSize;
	}

	// For unit tests only
	protected void setPageSize(int pageSize) {
		userServiceImplPageSize = pageSize;
	}

	private Map<String, String> groupDnToNameMap = new HashMap<>();


	public void setListOfBuiltInADUsers(String listOfBuiltInADUsers) {
		this.listOfBuiltInADUsers = listOfBuiltInADUsers;
	}


	public List<Entity> findByIds(List<String> ids){

		List<Entity> entities = new ArrayList<>();
		//TODO: set IDS
		if (ids!=null) {
			for (String id:ids){
				try {
					presidio.output.client.model.Entity entityFromResponse = remoteEntityClientService.getConterollerApi().getEntity(id,true);

					if (entityFromResponse!=null){
						Entity entity = entityConverterHelper.convertFromResponseToUi(entityFromResponse);
						entities.add(entity);
					}
				} catch (ApiException e) {
					logger.error("Failed to get user with id"+id);
				}
			}
		}
		return entities;
	}

	public Map<String,Integer> getAlertsTypes(){
		EntityQuery entityQuery = new EntityQuery();
		entityQuery.addAggregateByItem(EntityQuery.AggregateByEnum.ALERT_CLASSIFICATIONS);
		try {
			Map<String,Map<String,Long>> aggregationData = remoteEntityClientService.getConterollerApi().getEntities(entityQuery).getAggregationData();
			Map<String,Integer> classificiations = aggregationConverterHelper.convertAggregation(aggregationData,EntityQuery.AggregateByEnum.ALERT_CLASSIFICATIONS.name());
			return classificiations;

		} catch (ApiException e) {
			logger.error("Cannot get alert aggregation by classifications");
			return null;
		}

	}


	/**
	 * This method will determine if fir that user we need to only update the mongo or also create the user if needed
	 * @param userInfo - Map: <DataSource,Pair <lastActivity,logUserName>>
	 * @param dataSourceUpdateOnlyFlagMap - Map: <DataSource,update only flag>
	 * @return - boolean need to only update or not
	 */
	private boolean  udpateOnly(Map<String, JksonSerilaizablePair<Long,String>> userInfo,Map<String,Boolean> dataSourceUpdateOnlyFlagMap){
		boolean result = true;

		for (Entry<String, JksonSerilaizablePair<Long,String>> entry : userInfo.entrySet() )
		{
			if (!dataSourceUpdateOnlyFlagMap.get(entry.getKey()))
			{
				return false;
			}
		}
		return result;


	}

	/**
	 * This method will return the earliest event classifier that trigger new user creation
	 * @param userInfo - Map: <DataSource,Pair <lastActivity,logUserName>>
	 * @param dataSourceUpdateOnlyFlagMap - Map: <DataSource,update only flag>
	 * @return - the Classifier of the win event
	 */
	private String getFirstClassifierId(Map<String, JksonSerilaizablePair<Long, String>> userInfo, Map<String, Boolean> dataSourceUpdateOnlyFlagMap)
	{
		Entry<String, JksonSerilaizablePair<Long,String>> earlierEntry = null;

		for (Entry<String, JksonSerilaizablePair<Long,String>> entry : userInfo.entrySet() )
		{
			if (!dataSourceUpdateOnlyFlagMap.get(entry.getKey()))
			{
				if (earlierEntry == null  || earlierEntry.getValue().getKey() > entry.getValue().getKey())
					earlierEntry = entry;
			}
		}

		return earlierEntry != null ? earlierEntry.getKey() : null;
	}

	@Override
	public String getEntityThumbnail(Entity entity) {
		return "";
	}



	public boolean needToBeDeleted(Entity oldEntityRecord)
	{
		//laze upload
		if (setOfBuiltInADUsers == null || setOfBuiltInADUsers.size()==0)
		{
			setOfBuiltInADUsers = Arrays.asList(listOfBuiltInADUsers.split(","));
			for (ListIterator idx = setOfBuiltInADUsers.listIterator();  idx.hasNext();)
				idx.set(((String)idx.next()).toLowerCase());
		}




		return !setOfBuiltInADUsers.contains(oldEntityRecord.getUsername());


	}




	private Entity findEntityByObjectGUID(String objectGUID){
		return null;
	}



	private void updateEntityInMongo(String userId, Update update){
		mongoTemplate.updateFirst(query(where(Entity.ID_FIELD).is(userId)), update, Entity.class);
	}

	@Override
	public List<Entity> findBySearchFieldContaining(String prefix, int page, int size) {

		return null;
	}

	private String getUserNameFromID(String uid) {

		Entity entity = new Entity();
		entity.setUsername("mock");
		if(entity == null){
			throw new RuntimeException(String.format("entity with id [%s] does not exist", uid));
		}
		return entity.getUsername();
	}



//	@Override
//	public String findByNormalizedUserName(String normalizedUsername) {
//		return "mock user name";
//	}

	public Set<Entity> findByFollowed(){
		return SetUtils.EMPTY_SET;
	}

	public void updateTags(String username, Map<String, Boolean> tagSettings) {
		// construct lists of tags to remove and tags to add from the map
		List<String> tagsToAdd = new LinkedList<String>();
		List<String> tagsToRemove = new LinkedList<String>();
		for (String tag : tagSettings.keySet()) {
			if (tagSettings.get(tag))
				tagsToAdd.add(tag);
			else
				tagsToRemove.add(tag);
		}
//		userRepository.syncTags(username, tagsToAdd, tagsToRemove);
	}



	@Override
	public Set<String> findIdsByTags(String[] tags, String entityIds) {
		Set<String> idsByTag = new HashSet();
		Query query = new Query();
		query.fields().include(Entity.ID_FIELD);
		List<Criteria> criterias = new ArrayList<>();

		criterias.add(where(Entity.tagsField).in(tags));

		if (entityIds != null) {
			String[] entityIdsList = entityIds.split(",");
			criterias.add(where(Entity.ID_FIELD).in(entityIdsList));
		}

		Criteria[] criteriasArr;
		if (entityIds != null) {
			criteriasArr = new Criteria[]{criterias.get(0), criterias.get(1)};
		} else {
			criteriasArr = new Criteria[]{criterias.get(0)};
		}
		query.addCriteria(new Criteria().andOperator(criteriasArr));

		List<Entity> entities = mongoTemplate.find(query, Entity.class);
		for (Entity entity : entities) {
			idsByTag.add(entity.getId());
		}
		return idsByTag;
	}

	@Override
	public Set<String> findEntityNamesByTags(String[] tags) {
		Set<String> usernamesByTags = new HashSet();
		Query query = new Query();
		query.fields().include(Entity.usernameField);
		List<Criteria> criterias = new ArrayList<>();
		criterias.add(where(Entity.tagsField).in(tags));
		Criteria[] criteriasArr = new Criteria[]{criterias.get(0)};
		query.addCriteria(new Criteria().andOperator(criteriasArr));
		List<Entity> entities = mongoTemplate.find(query, Entity.class);
		usernamesByTags.addAll(entities.stream().map(Entity::getUsername).collect(Collectors.toList()));
		return usernamesByTags;
	}

	@Override
	public Map<String, Long> groupByTags(boolean forceCacheUpdate) {
		final String TAGS = "tags";


		return MapUtils.EMPTY_MAP;
	}

	@Override
	public void updateUserTagList(List<String> tagsToAdd, List<String> tagsToRemove , String username) {

	}

//	@Override public List<Map<String, String>> getEntitiesByPrefix(String prefix, Pageable pageable) {
//		return null;
//	}
//
//	@Override
//	public List<Map<String, String>> getUsersByIds(String ids, Pageable pageable) {
//		return null;
//	}

	@Override
	public Entity getEntityById(String id) {
		return new Entity();
	}

	@Override
	public Boolean isPasswordExpired(Entity entity) {
		return false;
	}
	@Override
	public Boolean isNoPasswordRequiresValue(Entity entity) {
		return false;
	}
	@Override
	public Boolean isNormalUserAccountValue(Entity entity) {
		return false;
	}


	@Override
	public Boolean isPasswordNeverExpiresValue(Entity entity) {
		return false;
	}
	@Override
	public String getOu(Entity entity){
		return "";
	}



	@Override
	public Entity getUserManager(Entity entity, Map<String, Entity> dnToUserMap){
		return null;
	}
	@Override
	public List<Entity> getUserDirectReports(Entity entity, Map<String, Entity> dnToUserMap){

		return null;
	}

	@Override
	public Entity findByUsername(String username){
		return null;
	}

	@Override public Entities findEntitiesByFilter(EntityRestFilter entityRestFilter, PageRequest pageRequest,
												   Set<String> relevantUserIds, List<String> fieldsRequired, boolean fetchAlertsOnUsers) {


		EntityQuery entityQuery = entityConverterHelper.convertUiFilterToQueryDto(entityRestFilter,pageRequest,relevantUserIds,fetchAlertsOnUsers);
		Entities entities =null;
		try {
			EntitiesWrapper entitiesWrapper = remoteEntityClientService.getConterollerApi().getEntities(entityQuery);
			if (entitiesWrapper!=null && entitiesWrapper.getTotal()>0){
				entities = new Entities(entityConverterHelper.convertResponseToUiDto(entitiesWrapper.getEntities()),entitiesWrapper.getTotal());

			}
		} catch (ApiException e) {
			logger.error("Can't fetch entities. Reson {} "+e.getMessage());

		}

		if (entities == null){
			entities = new Entities(Collections.emptyList(),0);
		}
		return entities;
	}


	@Override public int countEntitiesByFilter(EntityRestFilter userRestFilter, Set<String> relevantUsers) {

		Entities entities = findEntitiesByFilter(userRestFilter,null,relevantUsers,null,false);
		if (entities !=null){
			return new Long(entities.getTotalCount()).intValue();
		} else {
			return 0;
		}

	}

	@Override public void saveFavoriteFilter(EntityFilter entityFilter, String filterName) {
		favoriteUserFilterRepository.save(entityFilter, filterName);
	}

	@Override public List<FavoriteEntityFilter> getAllFavoriteFilters() {
		return favoriteUserFilterRepository.findAll();
	}

	@Override public long deleteFavoriteFilter(String filterName) {
		return favoriteUserFilterRepository.deleteById(filterName);
	}

	@Override
	public List getDistinctValuesByFieldName(String fieldName) {
		return null;
	}




	@Override
	public int updateWatched(EntityRestFilter entityRestFilter, Set<String> relevantEntities, Boolean watch) {

		EntityQuery entityQuery = entityConverterHelper.convertUiFilterToQueryDto(entityRestFilter,null,null,true);


		JsonPatch jsonPatch = entityConverterHelper.createPatchOperation(watch);
		if (jsonPatch.getOperations().size()!=1 ){
			throw new RuntimeException("Must have watched value");
		}

		try {
			EntityPatchBody entityPatchBody = new EntityPatchBody();
			entityPatchBody.setJsonPatch(jsonPatch);
			entityPatchBody.setEntityQuery(entityQuery);
			remoteEntityClientService.getConterollerApi().updateEntities(entityPatchBody);
		} catch (ApiException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int updateSingleEntityWatched(String entityId, Boolean watch) {
//		List<Criteria> criteriaList = getCriteriaListByFilterAndUserIds(userRestFilter, relevantUsers);



		JsonPatch jsonPatch = entityConverterHelper.createPatchOperation(watch);
		if (jsonPatch.getOperations().size()!=1 ){
			throw new RuntimeException("Must have watched value");
		}

		try {
			remoteEntityClientService.getConterollerApi().updateEntity(entityId,jsonPatch);
		} catch (ApiException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public Map<String,Integer> getDistinctAnomalyType() {
		Set<String> anomalyTypes=new HashSet<>();


	EntityQuery entityQuery= entityConverterHelper.convertUiFilterToQueryDto(null,null,null,false );
		entityQuery.addAggregateByItem(EntityQuery.AggregateByEnum.INDICATORS);

		try {
			Map<String, Map<String, Long>> aggregationData = remoteEntityClientService.getConterollerApi().getEntities(entityQuery).getAggregationData();
			Map<String, Integer> aggregation = aggregationConverterHelper.convertAggregation(aggregationData, EntityQuery.AggregateByEnum.INDICATORS.name());

			return  aggregation;
		} catch (ApiException e) {
			logger.error("Cannot get indicators per alerts aggregation");
			return Collections.emptyMap();
		}


	}



	public Map<String,Map<String,Integer>> getSeverityScoreMap(EntityRestFilter entityRestFilter){
		EntityQuery entityQuery = entityConverterHelper.convertUiFilterToQueryDto(entityRestFilter,null,null,false);

		entityQuery.setAggregateBy(Arrays.asList(EntityQuery.AggregateByEnum.SEVERITY));
		Map<String,Map<String,Integer>> response=new HashMap<>();

		try {
			EntitiesWrapper entitiesWrapper = remoteEntityClientService.getConterollerApi().getEntities(entityQuery);
			Map<String,Long> counts = entitiesWrapper.getAggregationData().get(EntityQuery.AggregateByEnum.SEVERITY.name());


			for (Severity severity : Severity.values()){
				Long count = counts.get(severity.name().toUpperCase());
				if (count==null){
					count=new Long(0);
				}
				Map<String,Integer> internalMap = new HashMap<>();
				internalMap.put("userCount",new Long(count).intValue());

				response.put(severity.name(),internalMap);
			}

			return response;
		} catch (ApiException e) {
			logger.error("Cannot aggregate by SEVERITY");
			return MapUtils.EMPTY_MAP;
		}


	}

	@Override
	public List<Entity> getEntitiesByPrefix(String entityName, PageRequest pageRequest) {
		EntityRestFilter filter = new EntityRestFilter();
//		filter.setSearchFieldContains(entityName);
//		filter.setPrefix(true);
		EntityQuery entityQuery = entityConverterHelper.convertUiFilterToQueryDto(filter,pageRequest,null,false);
		entityQuery.setEntityName(entityName);
//		userQuery.isPrefix(true);

		Entities entities = null;
		try {
			EntitiesWrapper entitiesWrapper = remoteEntityClientService.getConterollerApi().getEntities(entityQuery);
			List<Entity> usersList = this.entityConverterHelper.convertResponseToUiDto(entitiesWrapper.getEntities());
			entities = new Entities(usersList,entitiesWrapper.getTotal());
		} catch (ApiException e) {
			e.printStackTrace();
		}


		if (entities ==null){
			return null;
		} else {
			return entities.getEntities();
		}
	}


	public Entity findOne(String id){
		return new Entity();
	}


}
