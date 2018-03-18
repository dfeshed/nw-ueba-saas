package fortscale.services.impl;



import fortscale.domain.core.*;


import fortscale.domain.core.User;
import fortscale.domain.core.dao.FavoriteUserFilterRepository;

import fortscale.domain.core.dao.rest.Users;
import fortscale.domain.rest.UserFilter;
import fortscale.domain.rest.UserRestFilter;

import fortscale.services.UserService;

import fortscale.services.presidio.core.converters.AggregationConverterHelper;
import fortscale.services.presidio.core.converters.UserConverterHelper;
import fortscale.utils.JksonSerilaizablePair;

import fortscale.utils.logging.Logger;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.SetUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import presidio.output.client.api.UsersApi;
import presidio.output.client.client.ApiClient;
import presidio.output.client.client.ApiException;
import presidio.output.client.model.*;
import presidio.output.client.model.JsonPatch;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service("userService")
public class UserServiceImpl extends RemoteClientServiceAbs<UsersApi> implements UserService {

	private static Logger logger = Logger.getLogger(UserServiceImpl.class);
	private static final String SEARCH_FIELD_PREFIX = "##";


	@Autowired
	private MongoOperations mongoTemplate;

	@Autowired
	private FavoriteUserFilterRepository favoriteUserFilterRepository;


	@Value("${ad.info.update.read.page.size:1000}")
	private int readPageSize;

	@Value("${users.ou.filter:}")
	private String usersOUfilter;

	@Value("${user.service.impl.page.size:1000}")
	private int userServiceImplPageSize;


	@Value("${list.of.builtin.ad.users:Administrator,Guest,krbtgt}")
	private String listOfBuiltInADUsers;

	@Autowired
	private UserConverterHelper userConverterHelper;

	private AggregationConverterHelper aggregationConverterHelper = new AggregationConverterHelper();

	private List<String> setOfBuiltInADUsers;

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

//	@Override
//	public User createUser(String userApplication, String username, String appUsername){
//		User user = new User();
//		user.setUsername(username);
//		user.setSearchField("");
//		user.setWhenCreated(new Date());
//		createNewApplicationUserDetails(user, new ApplicationUserDetails(userApplication, appUsername), false);
//		return user;
//	}
//
//
//	//NOTICE: The user of this method should check the status of the event if he doesn't want to add new users with fail status he should call with onlyUpdate=true
//	//        The same goes for cases like security events where we don't want to create new User if there is no correlation with the active directory.
//	@Override
//	public void updateOrCreateUserWithClassifierUsername(String classifierId, String normalizedUsername, String logUsername, boolean onlyUpdate, boolean updateAppUsername) {
//		if(StringUtils.isEmpty(normalizedUsername)){
//			logger.warn("got a empty string {} username", classifierId);
//
//			return;
//		}
//
//
//		return;
//	}

	public List<User> findByIds(List<String> ids){

		List<User> users = new ArrayList<>();
		//TODO: set IDS
		if (ids!=null) {
			for (String id:ids){
				try {
					presidio.output.client.model.User userFromResponse = super.getConterollerApi().getUser(id,true);

					if (userFromResponse!=null){
						User user = userConverterHelper.convertFromResponseToUi(userFromResponse);
						users.add(user);
					}
				} catch (ApiException e) {
					logger.error("Failed to get user with id"+id);
				}
			}
		}
		return users;
	}

	public Map<String,Integer> getAlertsTypes(){
		UserQuery userQuery = new UserQuery();
		userQuery.addAggregateByItem(UserQuery.AggregateByEnum.ALERT_CLASSIFICATIONS);
		try {
			Map<String,Map<String,Long>> aggregationData = super.getConterollerApi().getUsers(userQuery).getAggregationData();
			Map<String,Integer> classificiations = aggregationConverterHelper.convertAggregation(aggregationData,UserQuery.AggregateByEnum.ALERT_CLASSIFICATIONS.name());
			return classificiations;

		} catch (ApiException e) {
			logger.error("Cannot get alert aggregation by classifications");
			return null;
		}

	}




//	@Override
//	public User saveUser(User user){
//		user = userRepository.save(user);
//		usernameService.updateUsernameInCache(user);
//		return user;
//	}
//
//	private void saveUsers(List<User> users) {
//		userRepository.save(users);
//		for (User user : users) {
//			usernameService.updateUsernameInCache(user);
//		}
//	}

//	@Override
//	public void updateUsersInfo(String username, Map<String, JksonSerilaizablePair<Long,String>> userInfo,Map<String,Boolean> dataSourceUpdateOnlyFlagMap) {
//		// get user by username
//		User user = userRepository.getLastActivityAndLogUserNameByUserName(username);
//
//		serviceMetrics.findByUsername++;
//
//		if (user == null) {
//
//			serviceMetrics.usernameNotFound++;
//
//			//in case that this user not need to be create in mongo (doesnt have data source info that related to OnlyUpdate flag = false)
//			if (udpateOnly(userInfo,dataSourceUpdateOnlyFlagMap)) {
//				logger.warn("Can't find user {} - Not going to update last activity and user info", username);
//				return;
//			}
//
//			String classifierId = getFirstClassifierId(userInfo, dataSourceUpdateOnlyFlagMap);
//			String logUsernameValue = userInfo.get(classifierId).getValue();
//
//			// need to create the user at mongo
//			serviceMetrics.attemptToCreateUser++;
//			user = createUser(ClassifierHelper.getUserApplicationId(classifierId), username, logUsernameValue);
//
//			saveUser(user);
//			if(user == null || user.getId() == null) {
//				serviceMetrics.failedToCreateUser++;
//				logger.info("Failed to save {} user with normalize username ({}) and log username ({})", classifierId, username, logUsernameValue);
//			}
//		}
//
//		DateTime userCurrLast = user.getLastActivity();
//
//		try {
//
//			Update update = null;
//
//
//
//			for (String classifierId : userInfo.keySet()) {
//
//				// get the time of the event
//				DateTime currTime = new DateTime(userInfo.get(classifierId).getKey(), DateTimeZone.UTC);
//
//				String logEventId = ClassifierHelper.getLogEventId(classifierId);
//				String logUsernameValue = userInfo.get(classifierId).getValue();
//
//
//				// last activity
//				if (userCurrLast == null || currTime.isAfter(userCurrLast)) {
//					if (update == null)
//						update = new Update();
//					update.set(User.lastActivityField, currTime);
//					userCurrLast = currTime;
//				}
//
//				// Last activity of data source
//				DateTime userCurrLastOfType = user.getLogLastActivity(logEventId);
//				if (userCurrLastOfType == null || currTime.isAfter(userCurrLastOfType)) {
//					if (update == null)
//						update = new Update();
//					update.set(User.getLogLastActivityField(logEventId), currTime);
//				}
//
//
//				//update the logusername if needed
//				boolean isLogUserNameExist = user.containsLogUsername(usernameService.getLogname(logEventId));
//
//				if (!isLogUserNameExist)
//				{
//
//					if (update == null)
//						update = new Update();
//					update.set(User.getLogUserNameField(usernameService.getLogname(logEventId)), logUsernameValue);
//				}
//
//			}
//
//
//			// update user
//			if (update != null) {
//				serviceMetrics.updatedUsers++;
//				mongoTemplate.updateFirst(query(where(User.usernameField).is(username)), update, User.class);
//			}
//
//		} catch (Exception e) {
//			serviceMetrics.failedToUpdate++;
//			logger.error("Failed to update last activity of user {} : {}", username, e.getMessage());
//		}
//
//	}

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
	public String getUserThumbnail(User user) {
		return "";
	}



	public boolean needToBeDeleted(User oldUserRecord)
	{
		//laze upload
		if (setOfBuiltInADUsers == null || setOfBuiltInADUsers.size()==0)
		{
			setOfBuiltInADUsers = Arrays.asList(listOfBuiltInADUsers.split(","));
			for (ListIterator idx = setOfBuiltInADUsers.listIterator();  idx.hasNext();)
				idx.set(((String)idx.next()).toLowerCase());
		}




		return !setOfBuiltInADUsers.contains(oldUserRecord.getUsername());


	}




	private User findUserByObjectGUID(String objectGUID){
		return null;
	}



	private void updateUserInMongo(String userId, Update update){
		mongoTemplate.updateFirst(query(where(User.ID_FIELD).is(userId)), update, User.class);
	}

	@Override
	public List<User> findBySearchFieldContaining(String prefix, int page, int size) {

		return null;
	}

	private String getUserNameFromID(String uid) {

		User user = new User();
		user.setUsername("mock");
		if(user == null){
			throw new RuntimeException(String.format("user with id [%s] does not exist", uid));
		}
		return user.getUsername();
	}



//	@Override
//	public String findByNormalizedUserName(String normalizedUsername) {
//		return "mock user name";
//	}

	public Set<User> findByFollowed(){
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

//	@Override
//	public Set<String> findNamesInGroup(List<String> groupsToTag, Pageable pageable) {
//		return null;
//	}
//
//	@Override
//	public Set<String> findNamesInOU(List<String> ousToTag, Pageable pageable) {
//		return null;
//	}
//
//	@Override
//	public Set<String> findByUsernameRegex(String usernameRegex) {
//		return null;
//	}



//	@Override
//	public Set<String> findNamesByTag(String tag) {
////		Set<String> namesByTag = new HashSet<String>();
////		int numOfPages = (int)(((userRepository.count() - 1) / userServiceImplPageSize) + 1);
////		for (int i = 0; i < numOfPages; i++) {
////			PageRequest pageRequest = new PageRequest(i, userServiceImplPageSize);
////			namesByTag.addAll(userRepository.findNameByTag(tag, pageRequest));
////		}
////		return namesByTag;
//		return null;
//	}
//
//	@Override
//	public Map<String, Set<String>> findAllTaggedUsers() {
//		Map<String, Set<String>> result = new HashMap();
//		Query query = new Query();
//		query.fields().include(User.usernameField);
//		query.fields().include(User.tagsField);
//		query.addCriteria(new Criteria().where(User.tagsField + ".0").exists(true));
//		List<User> users = mongoTemplate.find(query, User.class);
//		for (User user: users) {
//			result.put(user.getUsername(), user.getTags());
//		}
//		return result;
//	}

	@Override
	public Set<String> findIdsByTags(String[] tags, String entityIds) {
		Set<String> idsByTag = new HashSet();
		Query query = new Query();
		query.fields().include(User.ID_FIELD);
		List<Criteria> criterias = new ArrayList<>();

		criterias.add(where(User.tagsField).in(tags));

		if (entityIds != null) {
			String[] entityIdsList = entityIds.split(",");
			criterias.add(where(User.ID_FIELD).in(entityIdsList));
		}

		Criteria[] criteriasArr;
		if (entityIds != null) {
			criteriasArr = new Criteria[]{criterias.get(0), criterias.get(1)};
		} else {
			criteriasArr = new Criteria[]{criterias.get(0)};
		}
		query.addCriteria(new Criteria().andOperator(criteriasArr));

		List<User> users = mongoTemplate.find(query, User.class);
		for (User user: users) {
			idsByTag.add(user.getId());
		}
		return idsByTag;
	}

	@Override
	public Set<String> findUsernamesByTags(String[] tags) {
		Set<String> usernamesByTags = new HashSet();
		Query query = new Query();
		query.fields().include(User.usernameField);
		List<Criteria> criterias = new ArrayList<>();
		criterias.add(where(User.tagsField).in(tags));
		Criteria[] criteriasArr = new Criteria[]{criterias.get(0)};
		query.addCriteria(new Criteria().andOperator(criteriasArr));
		List<User> users = mongoTemplate.find(query, User.class);
		usernamesByTags.addAll(users.stream().map(User::getUsername).collect(Collectors.toList()));
		return usernamesByTags;
	}

	@Override
	public Map<String, Long> groupByTags(boolean forceCacheUpdate) {
		final String TAGS = "tags";


		return MapUtils.EMPTY_MAP;
	}

//	@Override
//	public void updateUserTag(String userTagEnumId, String username, boolean value) {
//		List<String> tagsToAdd = new ArrayList<>();
//		List<String> tagsToRemove = new ArrayList<>();
//		if (value) {
//			tagsToAdd.add(userTagEnumId);
//		}
//		else {
//			tagsToRemove.add(userTagEnumId);
//		}
//		updateUserTagList(tagsToAdd, tagsToRemove, username);
//	}

	@Override
	public void updateUserTagList(List<String> tagsToAdd, List<String> tagsToRemove , String username) {

	}

//	@Override public List<Map<String, String>> getUsersByPrefix(String prefix, Pageable pageable) {
//		return null;
//	}
//
//	@Override
//	public List<Map<String, String>> getUsersByIds(String ids, Pageable pageable) {
//		return null;
//	}

	@Override
	public User getUserById(String id) {
		return new User();
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
		return null;
	}

//	@Override
//	public Users findByUsernameAndPage(String username,PageRequest page){
//		UserQuery userQuery = this.userConverterHelper.convertQueryForUserNameFromUi(username,page);
//		try {
//			super.getConterollerApi().getUsers(userQuery);
//		} catch (ApiException e) {
//			logger.error("Some error have beanAccourd");
//		}
//	}

//	public Map<String, Integer> countUsersByDisplayName(Set<String> displayNames){
//		return  null;
//	}

	@Override public Users findUsersByFilter(UserRestFilter userRestFilter, PageRequest pageRequest,
											 Set<String> relevantUserIds, List<String> fieldsRequired,boolean fetchAlertsOnUsers) {


		UserQuery userQuery = userConverterHelper.convertUiFilterToQueryDto(userRestFilter,pageRequest,relevantUserIds,fetchAlertsOnUsers);
		Users users=null;
		try {
			UsersWrapper usersWrapper = super.getConterollerApi().getUsers(userQuery);
			if (usersWrapper!=null && usersWrapper.getTotal()>0){
				users= new Users(userConverterHelper.convertResponseToUiDto(usersWrapper.getUsers()),usersWrapper.getTotal());

			}
		} catch (ApiException e) {
			logger.error("Can't fetch users. Reson {} "+e.getMessage());

		}

		if (users == null){
			users= new Users(Collections.emptyList(),0);
		}
		return users;
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
//		return null;
//	}

	@Override public int countUsersByFilter(UserRestFilter userRestFilter, Set<String> relevantUsers) {

		Users users = findUsersByFilter(userRestFilter,null,relevantUsers,null,false);
		if (users!=null){
			return new Long(users.getTotalCount()).intValue();
		} else {
			return 0;
		}

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
		return null;
	}




	@Override
	public int updateWatched(UserRestFilter userRestFilter, Set<String> relevantUsers, Boolean watch) {

		UserQuery userQuery = userConverterHelper.convertUiFilterToQueryDto(userRestFilter,null,null,true);


		JsonPatch jsonPatch = userConverterHelper.createPatchOperation(watch);
		if (jsonPatch.getOperations().size()!=1 ){
			throw new RuntimeException("Must have watched value");
		}

		try {
			UserPatchBody userPatchBody = new UserPatchBody();
			userPatchBody.setJsonPatch(jsonPatch);
			userPatchBody.setUserQuery(userQuery);
			super.getConterollerApi().updateUsers(userPatchBody);
		} catch (ApiException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int updateSingleUserWatched(String userId, Boolean watch) {
//		List<Criteria> criteriaList = getCriteriaListByFilterAndUserIds(userRestFilter, relevantUsers);



		JsonPatch jsonPatch = userConverterHelper.createPatchOperation(watch);
		if (jsonPatch.getOperations().size()!=1 ){
			throw new RuntimeException("Must have watched value");
		}

		try {
			super.getConterollerApi().updateUser(userId,jsonPatch);
		} catch (ApiException e) {
			e.printStackTrace();
		}
		return 0;
	}




//	/**
//	 * removes tag with name {@code tagName} from field 'tags' of all users (if exists in tags array)
//	 * @param tagName the name of tag to remove
//	 * @return the amount of modified users
//	 */
//    @Override
//    public int removeTagFromAllUsers(String tagName) {
//		return 1;
//	}
//
//	@Override
//	public int updateUserScoreForUsersNotInIdList(Set<String> userIds, double score) {
////		int updateCount = 0;
////		try {
////			updateCount = userRepository.updateUserScoreForUsersNotInIdList(userIds, score);
////		}catch (Exception e){
////			logger.error("Error updating user score for {} users not in id list to score {}", userIds.size(), score, e);
////		}
////		return updateCount;
//		return 0;
//	}

//	@Override
//	public Map<String,Integer> getDistinctAnomalyType() {
//		Set<String> anomalyTypes=new HashSet<>();
//
//
//		UserQuery userQuery = userConverterHelper.convertUiFilterToQueryDto(new UserRestFilter(),null,null,false);
//		userQuery.addAggregateByItem(UserQuery.AggregateByEnum.I);
//
//		try {
//			Map<String, Map<String, Long>> aggregationData = super.getConterollerApi().getAlerts(alertQuery).getAggregationData();
//			Map<String, Integer> aggregation = aggregationConverterHelper.convertAggregation(aggregationData, AlertQuery.AggregateByEnum.INDICATOR_NAMES.name());
//
//			return  aggregation;
//		} catch (ApiException e) {
//			logger.error("Cannot get indicators per alerts aggregation");
//			return Collections.emptyMap();
//		}
//
//
//	}


	@Override
	public Map<String,Integer> getDistinctAnomalyType() {
		Set<String> anomalyTypes=new HashSet<>();


	UserQuery userQuery= userConverterHelper.convertUiFilterToQueryDto(null,null,null,false );
		userQuery.addAggregateByItem(UserQuery.AggregateByEnum.INDICATORS);

		try {
			Map<String, Map<String, Long>> aggregationData = super.getConterollerApi().getUsers(userQuery).getAggregationData();
			Map<String, Integer> aggregation = aggregationConverterHelper.convertAggregation(aggregationData, UserQuery.AggregateByEnum.INDICATORS.name());

			return  aggregation;
		} catch (ApiException e) {
			logger.error("Cannot get indicators per alerts aggregation");
			return Collections.emptyMap();
		}


	}



	public Map<String,Map<String,Integer>> getSeverityScoreMap(UserRestFilter userRestFilter){
		UserQuery userQuery = userConverterHelper.convertUiFilterToQueryDto(userRestFilter,null,null,false);

		userQuery.setAggregateBy(Arrays.asList(UserQuery.AggregateByEnum.SEVERITY));
		Map<String,Map<String,Integer>> response=new HashMap<>();

		try {
			UsersWrapper usersWrapper = this.getConterollerApi().getUsers(userQuery);
			Map<String,Long> counts = usersWrapper.getAggregationData().get(UserQuery.AggregateByEnum.SEVERITY.name());


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
	public List<User> getUsersByPrefix(String entityName, PageRequest pageRequest) {
		UserRestFilter filter = new UserRestFilter();
//		filter.setSearchFieldContains(entityName);
//		filter.setPrefix(true);
		UserQuery userQuery = userConverterHelper.convertUiFilterToQueryDto(filter,pageRequest,null,false);
		userQuery.setUserName(entityName);
//		userQuery.isPrefix(true);

		Users users = null;
		try {
			UsersWrapper usersWrapper = super.getConterollerApi().getUsers(userQuery);
			List<User> usersList = this.userConverterHelper.convertResponseToUiDto(usersWrapper.getUsers());
			users = new Users(usersList,usersWrapper.getTotal());
		} catch (ApiException e) {
			e.printStackTrace();
		}

//		Users users = findUsersByFilter(filter,pageRequest,null,null);

		if (users==null){
			return null;
		} else {
			return users.getUsers();
		}
	}


	public User findOne(String id){
		return new User();
	}

	@Override
	protected UsersApi getControllerInstance(ApiClient delegatorApiClient) {
		return new UsersApi( delegatorApiClient);
	}
}
