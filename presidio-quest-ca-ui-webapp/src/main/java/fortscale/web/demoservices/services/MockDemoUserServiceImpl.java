package fortscale.web.demoservices.services;


import fortscale.domain.core.*;
import fortscale.domain.core.dao.FavoriteUserFilterRepository;
import fortscale.domain.core.dao.rest.Users;
import fortscale.domain.rest.UserFilter;
import fortscale.domain.rest.UserRestFilter;
import fortscale.services.UserService;
import fortscale.temp.HardCodedMocks;
import fortscale.utils.logging.Logger;
import fortscale.web.demoservices.DemoBuilder;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.SetUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MockDemoUserServiceImpl implements UserService {

	private static Logger logger = Logger.getLogger(MockDemoUserServiceImpl.class);
	private static final String SEARCH_FIELD_PREFIX = "##";



	private FavoriteUserFilterRepository favoriteUserFilterRepository;


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
	public MockDemoUserServiceImpl(FavoriteUserFilterRepository favoriteUserFilterRepository, DemoBuilder demoBuilder){
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


	public List<User> findByIds(List<String> ids){
		List<User> users = this.demoBuilder.getUsers().stream().filter(user->ids.contains(user.getId())).collect(Collectors.toList());
		return  users;
	}

	@Override
	public Map<String, Integer> getAlertsTypes() {
		return new HashMap<>();
	}


	@Override
	public String getUserThumbnail(User user) {
		return "";
	}




	@Override
	public List<User> findBySearchFieldContaining(String prefix, int page, int size) {
		if(page<1 || size<1){
			return Collections.emptyList();
		}
		List<User> filteredUsers = demoBuilder.getUsers().stream().filter(user -> {
			return org.apache.commons.lang.StringUtils.isNotBlank(user.getSearchField())? user.getSearchField().toLowerCase().startsWith(prefix.toLowerCase()):false;
		}).collect(Collectors.toList());

		if (filteredUsers.size()>size){
			return  filteredUsers.subList((page-1)*5,page*5);
		} else {
			return  filteredUsers;
		}


	}

		public Set<User> findByFollowed(){
			return demoBuilder.getUsers().stream().filter(user -> user.getFollowed()).collect(Collectors.toSet());
	}


	@Override
	public Set<String> findIdsByTags(String[] tags, String entityIds) {
		return getUserStreamFilterByTag(tags).map(User::getId).collect(Collectors.toSet());


	}

	@Override
	public Set<String> findUsernamesByTags(String[] tags) {
		return getUserStreamFilterByTag(tags).map(User::getUsername).collect(Collectors.toSet());
	}

	public Stream<User> getUserStreamFilterByTag(String[] tags) {
		return demoBuilder.getUsers().stream().filter(user ->{
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
		for (User user:demoBuilder.getUsers()){
			for (String tag:user.getTags()){

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
	public User getUserById(String id) {
		return demoBuilder.getUsers().stream().filter(user -> user.getId().equals(id)).findAny().get();
	}

	@Override
	public Boolean isPasswordExpired(User user) {
		return false;
	}
	@Override
	public Boolean isNoPasswordRequiresValue(User user) {
		return false;
	}
	@Override
	public Boolean isNormalUserAccountValue(User user) {
		return false;
	}


	@Override
	public Boolean isPasswordNeverExpiresValue(User user) {
		return false;
	}
	@Override
	public String getOu(User user){
		return "";
	}



	@Override
	public User getUserManager(User user, Map<String, User> dnToUserMap){
		return null;
	}
	@Override
	public List<User> getUserDirectReports(User user, Map<String, User> dnToUserMap){

		return null;
	}

	@Override
	public User findByUsername(String username){
		return this.demoBuilder.getUsers().stream().filter(user -> user.getUsername().equals(username)).findFirst().orElse(null);
	}

	@Override public Users findUsersByFilter(UserRestFilter userRestFilter, PageRequest pageRequest,
											 Set<String> relevantUserIds, List<String> fieldsRequired,boolean exapnd) {

		List<User> users = demoBuilder.getUsers();
		users = users.stream().filter(user -> getUserByCondition(user,userRestFilter,relevantUserIds)).collect(Collectors.toList());
		int totalCount = users.size();
		users= MockServiceUtils.getPage(users,pageRequest,User.class);
		return  new Users(users,totalCount);
	}

	private boolean getUserByCondition(User user, UserRestFilter filter, Set<String> relevantUserIds) {
		if(filter.getIsDisabled()!=null && filter.getIsDisabled()!=isUserDisabled(user)){
			return false;
		}
		if(filter.getIsDisabledWithActivity()!=null && filter.getIsDisabledWithActivity()!=isUserDisabled(user)){
			return false;
		}
		if(filter.getIsWatched()!=null && filter.getIsWatched()!=user.getFollowed()){
			return false;
		}

		if(filter.getSearchValue()!=null && (user.getSearchField() == null    || !user.getSearchField().contains(filter.getSearchValue()))){
			return false;
		}

		Set<String> idsSet = relevantUserIds!=null?relevantUserIds:new HashSet<>();
		if (filter.getUserIds()!=null){
			idsSet.addAll(filter.getUserIds());
		}

		if(idsSet.size()>0 && !idsSet.contains(user.getId())){
			return false;
		}
		if (filter.getAlertTypes()!=null && !isUserHasAlertWithAnyOfThoseTypes(user,filter.getAlertTypes())){
			return false;
		}
		if (filter.getDataEntities()!=null && !isUserHasAlertWithAnyOfThoseDataEntities(user,Arrays.asList(filter.getDataEntities().split(",")))){
			return false;
		}
		if (filter.getEntityMinScore()!=null && user.getScore()<filter.getEntityMinScore()){
			return false;
		}

		if (filter.getMinScore()!=null && user.getScore()<=filter.getMinScore()){
			return false;
		}
		if (filter.getMaxScore()!=null && user.getScore()>=filter.getMaxScore()){
			return false;
		}


		if (filter.getIndicatorTypes()!=null && !userHasIndicatorTypes(user, filter.getIndicatorTypes())){
			return false;
		}

		if (filter.getUserTags()!=null && filter.getUserTags().size()>0){
			if ("any".equals(filter.getUserTags().get(0) )) {
				if (CollectionUtils.isEmpty(user.getTags())) { //Filter by any tag, but user tags is empty
					return false;
				}
			}else {
				//No common tags between user and search
				if(CollectionUtils.intersection(user.getTags(),filter.getUserTags()).size()==0){
					return false;
				}

			}
		}
		return  true;

	}

	private boolean isUserDisabled(User user){
		return Math.abs(Days.daysBetween(user.getLastActivity(), new DateTime()).getDays())>90;
	}
	private boolean isUserHasAlertWithAnyOfThoseTypes(User u,Collection<String> alertNames){
		List<Alert> alerts = demoBuilder.getAlertsByUserName(u.getUsername());
		if (alerts == null || alerts.size()==0){
			return false;
		}
		return alerts.stream().anyMatch(alert->alertNames.contains(alert.getName()));
	}
	private boolean isUserHasAlertWithAnyOfThoseDataEntities(User u,Collection<String> dataEntities){
		return demoBuilder.getAlertsByUserName(u.getUsername()).stream().anyMatch(alert->{
			Set<String> alertDataSources = alert.getDataSourceAnomalyTypePair().stream().map(DataSourceAnomalyTypePair::getDataSource).collect(Collectors.toSet());
			return CollectionUtils.intersection(alertDataSources,dataEntities).size()>0;
		});
	}

	private boolean userHasIndicatorTypes(User u,Set<String> anomalyTypePairs){
		return demoBuilder.getAlertsByUserName(u.getUsername()).stream().anyMatch(alert->{

			return CollectionUtils.intersection(alert.getDataSourceAnomalyTypePair(),anomalyTypePairs).size()>0;
		});
	}


//	private List<Criteria> getCriteriaListByFilterAndUserIds(UserRestFilter userRestFilter,
//															 Set<String> relevantUserNames) {
////		List<Criteria> criteriaList = userRepository.getUsersCriteriaByFilters(userRestFilter);
////
////		// If there was filter for alert type or anomaly type or locations
////		// we want to add criteria for getting data of specific users
////		if (CollectionUtils.isNotEmpty(userRestFilter.getAnomalyTypesAsSet())
////				|| CollectionUtils.isNotEmpty(userRestFilter.getAlertTypes())
////				|| CollectionUtils.isNotEmpty(userRestFilter.getLocations())
////				|| CollectionUtils.isNotEmpty(userRestFilter.getUserIds())) {
////			criteriaList.add(userRepository.getUserCriteriaByUserIds(relevantUserNames));
////		}
////
////		return criteriaList;
//		List<User> users = demoBuilder.getUsers();
//		return null;
//	}

	@Override public int countUsersByFilter(UserRestFilter userRestFilter, Set<String> relevantUsers) {

		List<User> users = demoBuilder.getUsers();
		users = users.stream().filter(user -> getUserByCondition(user,userRestFilter,relevantUsers)).collect(Collectors.toList());
		return users.size();
	}

	@Override public void saveFavoriteFilter(UserFilter userFilter, String filterName) {
		favoriteUserFilterRepository.save(userFilter, filterName);
	}

	@Override public List<FavoriteUserFilter> getAllFavoriteFilters() {
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
	public int updateWatched(UserRestFilter userRestFilter, Set<String> relevantUsers, Boolean watch) {

		final AtomicInteger atomicInteger = new AtomicInteger(0);
		List<User> users = demoBuilder.getUsers();
		users = users.stream().filter(user -> getUserByCondition(user,userRestFilter,relevantUsers)).collect(Collectors.toList());
		users.forEach(user-> {
			user.setFollowed(watch);
			atomicInteger.addAndGet(1);
		});


		return atomicInteger.get();
	}

	@Override
	public int updateSingleUserWatched(String userId, Boolean watch){
		return 0;
	}


	public User findOne(String id){
		List<User> users = this.findByIds(Arrays.asList(id));
		if (users!=null && users.size()>0){
			return users.get(0);
		} else {
			return null;
		}
	}

	@Override
	public Map<String, Map<String, Integer>> getSeverityScoreMap(UserRestFilter userRestFilter) {
		Map<String, Map<String, Integer>> severitiesMap= new HashMap<>();
		for (Severity severity : Severity.values()){
			long count = findUsersByFilter(userRestFilter,null,null,null,true).getUsers()
					.stream()
//					.filter(user->user.getScore()>0)
					.filter(user->severity.equals(user.getScoreSeverity()))
					.count();
			Map<String,Integer> internalMap = new HashMap<>();
			internalMap.put("userCount",new Long(count).intValue());
			severitiesMap.put(severity.name(),internalMap);
		}

		return severitiesMap;
	}

	public List<User> getUsersByPrefix(String entityName, PageRequest pageRequest){
		List<User> allUsers= demoBuilder.getUsers().stream().filter(user->user.getUsername().startsWith(entityName)).collect(Collectors.toList());
		return MockServiceUtils.getPage(allUsers,pageRequest, User.class);
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
