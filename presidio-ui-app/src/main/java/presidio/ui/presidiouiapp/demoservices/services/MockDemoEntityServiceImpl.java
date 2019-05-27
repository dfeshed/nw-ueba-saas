package presidio.ui.presidiouiapp.demoservices.services;


import fortscale.domain.core.*;
import fortscale.domain.core.dao.FavoriteEntityFilterRepository;
import fortscale.domain.core.dao.rest.Entities;
import fortscale.domain.rest.EntityFilter;
import fortscale.domain.rest.EntityRestFilter;
import fortscale.services.EntityService;
import fortscale.utils.logging.Logger;
import presidio.ui.presidiouiapp.demoservices.DemoBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MockDemoEntityServiceImpl implements EntityService {

	private static Logger logger = Logger.getLogger(MockDemoEntityServiceImpl.class);
	private static final String SEARCH_FIELD_PREFIX = "##";



	private FavoriteEntityFilterRepository favoriteUserFilterRepository;


	private DemoBuilder demoBuilder;


	@Value("${ad.info.update.read.page.size:1000}")
	private int readPageSize;

	@Value("${users.ou.filter:}")
	private String usersOUfilter;

	@Value("${user.service.impl.page.size:1000}")
	private int userServiceImplPageSize;


	@Value("${list.of.builtin.ad.users:Administrator,Guest,krbtgt}")
	private String listOfBuiltInADUsers;

	private List<String> setOfBuiltInADUsers;

	@Autowired
	public MockDemoEntityServiceImpl(FavoriteEntityFilterRepository favoriteUserFilterRepository, DemoBuilder demoBuilder){
		this.favoriteUserFilterRepository = favoriteUserFilterRepository;
		this.demoBuilder = demoBuilder;
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


	public List<Entity> findByIds(List<String> ids){
		List<Entity> entities = this.demoBuilder.getEntities().stream().filter(user->ids.contains(user.getId())).collect(Collectors.toList());
		return entities;
	}

	@Override
	public Map<String, Integer> getAlertsTypes() {
		return new HashMap<>();
	}


	@Override
	public String getEntityThumbnail(Entity entity) {
		return "";
	}




	@Override
	public List<Entity> findBySearchFieldContaining(String prefix, int page, int size) {
		if(page<1 || size<1){
			return Collections.emptyList();
		}
		List<Entity> filteredEntities = demoBuilder.getEntities().stream().filter(user -> {
			return org.apache.commons.lang.StringUtils.isNotBlank(user.getSearchField())? user.getSearchField().toLowerCase().startsWith(prefix.toLowerCase()):false;
		}).collect(Collectors.toList());

		if (filteredEntities.size()>size){
			return  filteredEntities.subList((page-1)*5,page*5);
		} else {
			return filteredEntities;
		}


	}

		public Set<Entity> findByFollowed(){
			return demoBuilder.getEntities().stream().filter(user -> user.getFollowed()).collect(Collectors.toSet());
	}


	@Override
	public Set<String> findIdsByTags(String[] tags, String entityIds) {
		return getUserStreamFilterByTag(tags).map(Entity::getId).collect(Collectors.toSet());


	}

	@Override
	public Set<String> findEntityNamesByTags(String[] tags) {
		return getUserStreamFilterByTag(tags).map(Entity::getUsername).collect(Collectors.toSet());
	}

	public Stream<Entity> getUserStreamFilterByTag(String[] tags) {
		return demoBuilder.getEntities().stream().filter(user ->{
			boolean exist=false;
			for (String tag:tags){
				if (user.getTags().contains(tag)){
					exist=true;
				}
			}
			return exist;
		});
	}

	@Override
	public Map<String, Long> groupByTags(boolean forceCacheUpdate) {
		final String TAGS = "tags";


		Map<String, Long> counted = new HashMap<>();
		for (Entity entity :demoBuilder.getEntities()){
			for (String tag: entity.getTags()){

				Long value = counted.getOrDefault( tag, 0L);
				value=value+1;
				counted.put(tag,value);
			}
		}

		return counted;
	}



	@Override
	public void updateUserTagList(List<String> tagsToAdd, List<String> tagsToRemove , String username) {

	}


	@Override
	public Entity getEntityById(String id) {
		return demoBuilder.getEntities().stream().filter(entity -> entity.getId().equals(id)).findAny().get();
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
		return this.demoBuilder.getEntities().stream().filter(user -> user.getUsername().equals(username)).findFirst().orElse(null);
	}

	@Override public Entities findEntitiesByFilter(EntityRestFilter entityRestFilter, PageRequest pageRequest,
												   Set<String> relevantUserIds, List<String> fieldsRequired, boolean exapnd) {

		List<Entity> entities = demoBuilder.getEntities();
		entities = entities.stream().filter(user -> getUserByCondition(user, entityRestFilter,relevantUserIds)).collect(Collectors.toList());
		int totalCount = entities.size();
		entities = MockServiceUtils.getPage(entities,pageRequest,Entity.class);
		return  new Entities(entities,totalCount);
	}

	private boolean getUserByCondition(Entity entity, EntityRestFilter filter, Set<String> relevantUserIds) {
		if(filter.getIsDisabled()!=null && filter.getIsDisabled()!=isUserDisabled(entity)){
			return false;
		}
		if(filter.getIsDisabledWithActivity()!=null && filter.getIsDisabledWithActivity()!=isUserDisabled(entity)){
			return false;
		}
		if(filter.getIsWatched()!=null && filter.getIsWatched()!= entity.getFollowed()){
			return false;
		}

		if(filter.getSearchValue()!=null && (entity.getSearchField() == null    || !entity.getSearchField().contains(filter.getSearchValue()))){
			return false;
		}

		Set<String> idsSet = relevantUserIds!=null?relevantUserIds:new HashSet<>();
		if (filter.getUserIds()!=null){
			idsSet.addAll(filter.getUserIds());
		}

		if(idsSet.size()>0 && !idsSet.contains(entity.getId())){
			return false;
		}
		if (filter.getAlertTypes()!=null && !isUserHasAlertWithAnyOfThoseTypes(entity,filter.getAlertTypes())){
			return false;
		}
		if (filter.getDataEntities()!=null && !isUserHasAlertWithAnyOfThoseDataEntities(entity,Arrays.asList(filter.getDataEntities().split(",")))){
			return false;
		}
		if (filter.getEntityMinScore()!=null && entity.getScore()<filter.getEntityMinScore()){
			return false;
		}

		if (filter.getMinScore()!=null && entity.getScore()<=filter.getMinScore()){
			return false;
		}
		if (filter.getMaxScore()!=null && entity.getScore()>=filter.getMaxScore()){
			return false;
		}


		if (filter.getIndicatorTypes()!=null && !userHasIndicatorTypes(entity, filter.getIndicatorTypes())){
			return false;
		}

		if (filter.getEntityTags()!=null && filter.getEntityTags().size()>0){
			if ("any".equals(filter.getEntityTags().get(0) )) {
				if (CollectionUtils.isEmpty(entity.getTags())) { //Filter by any tag, but entity tags is empty
					return false;
				}
			}else {
				//No common tags between entity and search
				if(CollectionUtils.intersection(entity.getTags(),filter.getEntityTags()).size()==0){
					return false;
				}

			}
		}
		return  true;

	}

	private boolean isUserDisabled(Entity entity){
		return Math.abs(Days.daysBetween(entity.getLastActivity(), new DateTime()).getDays())>90;
	}
	private boolean isUserHasAlertWithAnyOfThoseTypes(Entity u, Collection<String> alertNames){
		List<Alert> alerts = demoBuilder.getAlertsByEntityName(u.getUsername());
		if (alerts == null || alerts.size()==0){
			return false;
		}
		return alerts.stream().anyMatch(alert->alertNames.contains(alert.getName()));
	}
	private boolean isUserHasAlertWithAnyOfThoseDataEntities(Entity u, Collection<String> dataEntities){
		return demoBuilder.getAlertsByEntityName(u.getUsername()).stream().anyMatch(alert->{
			Set<String> alertDataSources = alert.getDataSourceAnomalyTypePair().stream().map(DataSourceAnomalyTypePair::getDataSource).collect(Collectors.toSet());
			return CollectionUtils.intersection(alertDataSources,dataEntities).size()>0;
		});
	}

	private boolean userHasIndicatorTypes(Entity u, Set<String> anomalyTypePairs){
		return demoBuilder.getAlertsByEntityName(u.getUsername()).stream().anyMatch(alert->{

			return CollectionUtils.intersection(alert.getDataSourceAnomalyTypePair(),anomalyTypePairs).size()>0;
		});
	}



	@Override public int countEntitiesByFilter(EntityRestFilter userRestFilter, Set<String> relevantUsers) {

		List<Entity> entities = demoBuilder.getEntities();
		entities = entities.stream().filter(user -> getUserByCondition(user,userRestFilter,relevantUsers)).collect(Collectors.toList());
		return entities.size();
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
		throw new UnsupportedOperationException("getDistinctValuesByFieldName is not supported in presidio for quest CA 3.1");
	}


	@Override
	public int updateWatched(EntityRestFilter entityRestFilter, Set<String> relevantEntities, Boolean watch) {

		final AtomicInteger atomicInteger = new AtomicInteger(0);
		List<Entity> entities = demoBuilder.getEntities();
		entities = entities.stream().filter(user -> getUserByCondition(user, entityRestFilter, relevantEntities)).collect(Collectors.toList());
		entities.forEach(user-> {
			user.setFollowed(watch);
			atomicInteger.addAndGet(1);
		});


		return atomicInteger.get();
	}

	@Override
	public int updateSingleEntityWatched(String entityId, Boolean watch){
		return 0;
	}


	public Entity findOne(String id){
		List<Entity> entities = this.findByIds(Arrays.asList(id));
		if (entities !=null && entities.size()>0){
			return entities.get(0);
		} else {
			return null;
		}
	}

	@Override
	public Map<String, Map<String, Integer>> getSeverityScoreMap(EntityRestFilter entityRestFilter) {
		Map<String, Map<String, Integer>> severitiesMap= new HashMap<>();
		for (Severity severity : Severity.values()){
			long count = findEntitiesByFilter(entityRestFilter,null,null,null,true).getEntities()
					.stream()

					.filter(user->severity.equals(user.getScoreSeverity()))
					.count();
			Map<String,Integer> internalMap = new HashMap<>();
			internalMap.put("userCount",new Long(count).intValue());
			severitiesMap.put(severity.name(),internalMap);
		}

		return severitiesMap;
	}

	public List<Entity> getEntitiesByPrefix(String entityName, PageRequest pageRequest){
		List<Entity> allEntities = demoBuilder.getEntities().stream().filter(user->user.getUsername().startsWith(entityName)).collect(Collectors.toList());
		return MockServiceUtils.getPage(allEntities,pageRequest, Entity.class);
	}

	@Override
	public Map<String,Integer> getDistinctAnomalyType(){

		Map<String,Integer> anomalyTypes=new HashMap<>();
		for (Alert a: demoBuilder.getAlerts()){
			for (Evidence indicator: a.getEvidences()){
				anomalyTypes.put(indicator.getAnomalyType(),1);
			}
		}

		return anomalyTypes;
	}

}
