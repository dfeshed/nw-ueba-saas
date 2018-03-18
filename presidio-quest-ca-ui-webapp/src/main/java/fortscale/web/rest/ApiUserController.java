package fortscale.web.rest;

import au.com.bytecode.opencsv.CSVWriter;

import fortscale.domain.core.*;

import fortscale.domain.core.dao.TagPair;

import fortscale.domain.core.dao.rest.Users;
import fortscale.domain.rest.UserFilter;
import fortscale.domain.rest.UserRestFilter;
import fortscale.services.*;

import fortscale.temp.HardCodedMocks;
import fortscale.temp.MockScenarioGenerator;

import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.BaseController;
import fortscale.web.beans.*;

import fortscale.web.rest.Utils.ApiUtils;
import org.apache.commons.collections.CollectionUtils;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import java.io.OutputStreamWriter;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/user")
public class ApiUserController extends BaseController{

	public static final int DEFAULT_EXPORT_USERS_SIZE = 1000;
	public static final int FIRST_PAGE_INDEX = 0;
	public static final int USERS_SEARCH_DEFAULT_PAGE_SIZE = 5;
	private static Logger logger = Logger.getLogger(ApiUserController.class);

	public static final String USER_COUNT = "userCount";
	public static final String ADMINISTRATOR_TAG = "administrator";
	public static final String WATCHED_USER = "watched";

	private static final String USER_NAME_COLUMN_NAME = "Username";
	private static final String USERS_CSV_FILE_NAME = "users";
	private static final String DISPLAY_NAME_COLUMN_NAME = "Full Name";
	private static final String USER_ROLE_COLUMN_NAME = "Role";
	private static final String USER_DEPARTMENT_COLUMN_NAME = "Department";
	private static final String USER_WATCHED_COLUMN_NAME = "Watched";
	private static final String USER_RISK_SCORE_COLUMN_NAME = "Risk Score";
	private static final String USER_ALERT_COUNT_COLUMN_NAME = "Total Alerts";
	private static final String USER_DEVICE_COUNT_COLUMN_NAME = "Total Devices";
	private static final String USER_TAGS_COLUMN_NAME = "Tags";
	private static final String ALL_WATCHED = "allWatched";
	private static final String CSV_CONTENT_TYPE = "text/plain; charset=utf-8";
	private List<String> fieldsRequired;

	@Autowired
	private UserServiceFacade userServiceFacade;

	@Autowired
	private UserTagService userTagService;


	@Autowired
	private UserService userService;






	@Autowired
	private UserWithAlertService userWithAlertService;

	private static final String DEFAULT_SORT_FIELD = "username";


	private List<String> extendedSearchfieldsRequired;


	public ApiUserController() {
		fieldsRequired = new ArrayList<>();
		fieldsRequired.add(User.ID_FIELD);
		fieldsRequired.add(User.usernameField);
		fieldsRequired.add(User.followedField);
		fieldsRequired.add(User.displayNameField);
		fieldsRequired.add(User.alertsCountField);
		fieldsRequired.add(User.tagsField);
		fieldsRequired.add(User.sourceMachineCountField);
		fieldsRequired.add(User.scoreField);


		extendedSearchfieldsRequired = new ArrayList<>();
		extendedSearchfieldsRequired.add(User.ID_FIELD);

		extendedSearchfieldsRequired.add(User.usernameField);
		extendedSearchfieldsRequired.add(User.followedField);
		extendedSearchfieldsRequired.add(User.displayNameField);
		extendedSearchfieldsRequired.add(User.scoreField);
	}

	/**
	 * The API to get all users. GET: /api/user
	 */
	@RequestMapping(method = RequestMethod.GET) @ResponseBody @LogException
	public DataBean<List<UserDetailsBean>> getUsers(UserRestFilter userRestFilter) {

		PageRequest pageRequest = createPaging(userRestFilter);

		Users users = getUsers(userRestFilter, pageRequest, null);

		// Build the response
		DataBean<List<UserDetailsBean>> usersList = getUsersDetails(users.getUsers());
		usersList.setOffset(pageRequest.getPageNumber() * pageRequest.getPageSize());
		usersList.setTotal(new Long(users.getTotalCount()).intValue());
//		if (BooleanUtils.isTrue(userRestFilter.getAddAlertsAndDevices())) {
//			addAlertsAndDevices(usersList.getData());
//		}
		if (BooleanUtils.isTrue(userRestFilter.getAddAllWatched())) {
			addAllWatched(usersList, userRestFilter);
		}

		return usersList;
	}



	@RequestMapping(value="/count", method=RequestMethod.GET)
	public DataBean<Integer> countUsers(UserRestFilter userRestFilter) {
		Integer count = userWithAlertService.countUsersByFilter(userRestFilter);
		DataBean<Integer> bean = new DataBean<>();
		bean.setData(count);
		bean.setTotal(count);
		return bean;
	}

	@RequestMapping(value = "/{filterName}/favoriteFilter", method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response> addFavoriteFilter(@RequestBody UserFilter userFilter,
			@PathVariable String filterName) {
		try {
			userService.saveFavoriteFilter(userFilter, filterName);
		} catch (DuplicateKeyException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(Response.status(javax.ws.rs.core.Response.Status.CONFLICT).
							entity("The filter name already exists").build());
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Response.status(Response.Status.
					INTERNAL_SERVER_ERROR).build());
		}

		return ResponseEntity.status(HttpStatus.OK).body(Response.status(Response.Status.OK).build());
	}

	@RequestMapping(value = "/favoriteFilter/{filterId}", method = RequestMethod.DELETE)
	public ResponseEntity<Response> deleteFavoriteFilter(@PathVariable String filterId) {
		long lineDeleted = userService.deleteFavoriteFilter(filterId);
		if (lineDeleted > 0) {
			return ResponseEntity.status(HttpStatus.OK).body(Response.status(Response.Status.OK).build());
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST).entity("No documents deleted").
						build());
	}

	@RequestMapping(value = "/favoriteFilter", method = RequestMethod.GET)
	public DataBean<List<FavoriteUserFilter>> getFavoriteFilters() {
		List<FavoriteUserFilter> allFavoriteFilters = userService.getAllFavoriteFilters();
		DataBean<List<FavoriteUserFilter>> result = new DataBean<>();
		result.setData(allFavoriteFilters);
		result.setTotal(allFavoriteFilters.size());
		return result;

	}

//	@RequestMapping(value="/search", method=RequestMethod.GET)
//	@ResponseBody
//	@LogException
//	public  DataBean<List<UserSearchBean>> search(@RequestParam(required = true) String prefix,
//			@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size) {
//		List<User> users = userServiceFacade.findBySearchFieldContaining(prefix, page, size);
//		List<UserSearchBean> data = users.stream().map(UserSearchBean::new).collect(Collectors.toList());
//		DataBean<List<UserSearchBean>> ret = new DataBean<>();
//		ret.setData(data);
//		ret.setTotal(data.size());
//		return ret;
//	}

	@RequestMapping(value="/exist-anomaly-types", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public Map<String,Integer> getDistinctAnomalyType () {
		Map<String,Integer> anomalyTypePairs =  userService.getDistinctAnomalyType();


		return anomalyTypePairs;
	}

	@RequestMapping(value="/extendedSearch", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<UserDetailsBean>> extendedSearch(UserRestFilter userRestFilter){
		DataBean<List<UserDetailsBean>> result = new DataBean<>();

		if (StringUtils.isNotEmpty(userRestFilter.getSearchValue())) {

			Sort sorting = createSorting("", userRestFilter.getSortDirection());
			PageRequest pageRequest = new PageRequest(FIRST_PAGE_INDEX, USERS_SEARCH_DEFAULT_PAGE_SIZE, sorting);

//			List<User> users = userWithAlertService.findUsersWithSearchValue(userRestFilter, pageRequest, extendedSearchfieldsRequired);
			userRestFilter.setSearchFieldContains(userRestFilter.getSearchValue());
			Users usersObject = userService.findUsersByFilter(userRestFilter,pageRequest,null,null,false);
			List<User> users = usersObject!=null?usersObject.getUsers():new ArrayList();
			// Add severity to the users
//			setSeverityOnUsersList(users);

			result = getUsersDetails(users);

			return result;

		}

		return result;
	}

	/**
	 * Search user data by user name. This function is the same as details() but the parameter is username and notuserid
	 * @param username the name of the user
	 * @return a {@link DataBean} that holds a list of details{@link UserDetailsBean}
	 */
	@RequestMapping(value="/{username}/userdata", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<UserDetailsBean>> userDataByName(@PathVariable String username) {
		User user = userService.findByUsername(username);
//		setSeverityOnUsersList(Arrays.asList(user));
		return getUsersDetails(user);
	}

	/**
	 * Search user's data by user id (uuid is auto-generated in MongoDB)
	 * @param ids the lost of user id from mongoDB
	 * @return a {@link DataBean} that holds a list of {@link UserDetailsBean}
	 */
	@RequestMapping(value="/{ids}/details", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<UserDetailsBean>> details(@PathVariable List<String> ids,
			@RequestParam(required = false, value = "add_alerts_and_devices") Boolean addAlertsAndDevices) {
		// Get Users
		List<User> users = userService.findByIds(ids);
//		setSeverityOnUsersList(users);
		DataBean<List<UserDetailsBean>> usersDetails = getUsersDetails(users);
		if (BooleanUtils.isTrue(addAlertsAndDevices)) {
			addAlertsAndDevices(usersDetails.getData());
		}
		// Return detailed users
		return usersDetails;
	}

	/**
	 * API to update user tags
	 * @param body
	 * @return
	 */
	@RequestMapping(value="{id}", method = RequestMethod.POST)
	@LogException
	public Response addRemoveTag(@PathVariable String id, @RequestBody String body) throws JSONException {
		User user = userService.findOne(id);
		JSONObject params = new JSONObject(body);
		String tag;
		boolean addTag;
		if (params.has("add")) {
			tag = params.getString("add");
			addTag = true;
		} else if (params.has("remove")) {
			tag = params.getString("remove");
			addTag = false;
		} else {
			throw new RuntimeException(String.format("param %s is invalid", params.toString()));
		}
		try {
			addTagToUser(user, Arrays.asList(new String[] { tag }), addTag);
		} catch (Exception ex) {
			return Response.status(Response.Status.BAD_REQUEST).entity(ex.getLocalizedMessage()).build();
		}
		return Response.status(Response.Status.OK).build();
	}

	/**
	 * API to update users tags by filter
	 * @return
	 */
	@RequestMapping(value="/{addTag}/{tagNames}/tagUsers", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@LogException
	@ResponseBody
	public ResponseEntity<UsersCount> addRemoveTagByFilter(@RequestBody UserRestFilter userRestFilter,
														   @PathVariable Boolean addTag, @PathVariable List<String> tagNames) throws JSONException {

		UsersCount result = new UsersCount();
		if (CollectionUtils.isEmpty(tagNames)) {
			return new ResponseEntity(result, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		try {
			result.count = userWithAlertService.updateTags(userRestFilter, addTag, tagNames);
			return new ResponseEntity(result, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}


	}

	@RequestMapping(value="/followedUsers", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<String>> followedUsers() {
		List<String> userIds = userService.findByFollowed().stream().map(User::getId).
				collect(Collectors.toList());
		DataBean<List<String>> ret = new DataBean<>();
		ret.setData(userIds);
		ret.setTotal(userIds.size());
		return ret;
	}

	@RequestMapping(value="/usersDetails", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<UserDetailsBean>> usersDetails(@RequestParam(required = true) List<String> ids) {
		List<User> users = userService.findByIds(ids);
//		setSeverityOnUsersList(users);
		return userDetails(users);
	}

	@RequestMapping(value="/usersTagsCount", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<TagPair>> usersTagsCount() {
		List<TagPair> result = new ArrayList();
		Map<String, Long> items = userService.groupByTags(false);
		result.addAll(items.entrySet().stream().map(entry -> new TagPair(entry.getKey(), entry.getValue())).
				collect(Collectors.toList()));
		DataBean<List<TagPair>> ret = new DataBean();
		ret.setData(result);
		ret.setTotal(result.size());
		return ret;
	}




	@RequestMapping(value="/followedUsersDetails", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<UserDetailsBean>> followedUsersDetails(Model model) {
		Set<User> users = userService.findByFollowed();

//		setSeverityOnUsersList(new ArrayList<>(users));
		return userDetails(new ArrayList<>(users));
	}

//	@RequestMapping(value="/{id}/machines", method=RequestMethod.GET)
//	@ResponseBody
//	@LogException
//	public DataBean<List<UserMachine>> userMachines(@PathVariable String id) {
//		List<UserMachine> userMachines = userServiceFacade.getUserMachines(id);
//		DataBean<List<UserMachine>> ret = new DataBean<>();
//		ret.setData(userMachines);
//		ret.setTotal(userMachines.size());
//		return ret;
//	}

//	@RequestMapping(value="/usersMachines", method=RequestMethod.GET)
//	@ResponseBody
//	@LogException
//	public DataBean<List<UserMachinesBean>> usersMachines(@RequestParam(required = true) List<String> ids) {
//		List<User> users = userService.findByIds(ids);
//		return usersMachinesAux(users);
//	}

	/**
	 * Gets the destination machines operating systems distribution for a user
	 */
//	@RequestMapping(value="/{uid}/destination/{param}/distribution", method=RequestMethod.GET)
//	@ResponseBody
//	@LogException
//	public DataBean<Collection<PropertyEntry>> getDestinationPropertyDistribution(@PathVariable String uid,
//			@PathVariable String param, @RequestParam(defaultValue="50") int minScore,
//			@RequestParam(required = false) Long latestDate, @RequestParam(required = false) Long earliestDate,
//			@RequestParam(defaultValue="10") int maxValues) {
//		PropertiesDistribution distribution = userServiceFacade.getDestinationComputerPropertyDistribution(uid, param,
//				latestDate, earliestDate, maxValues, minScore);
//		// convert the distribution properties to data bean
//		DataBean<Collection<PropertyEntry>> ret = new DataBean<>();
//		if (distribution.isConclusive()) {
//			ret.setData(distribution.getPropertyValues());
//			ret.setTotal(distribution.getNumberOfValues());
//		} else {
//			ret.addWarning(DataWarningsEnum.NON_CONCLUSIVE_DATA);
//		}
//		return ret;
//	}


	@RequestMapping(value = "/severityBar", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<Map<String, Map<String, Integer>>> getSeverityBarInfo(UserRestFilter userRestFilter){
		DataBean<Map<String, Map<String, Integer>>> dataBean = new DataBean<>();


		dataBean.setData(userService.getSeverityScoreMap(userRestFilter));


		dataBean.setTotal(userService.countUsersByFilter(userRestFilter,null));
		return dataBean;


	}

	@RequestMapping(value="/exist-alert-types", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<Set<AlertTypesCountBean>> getDistinctAlertNames(@RequestParam(required=true,
			value = "ignore_rejected") Boolean ignoreRejected) {
		//TODO: ignore rejected is not supported
		Set<AlertTypesCountBean> alertTypesNameAndCount = userService.
				getAlertsTypes().entrySet().stream().map(alertTypeToCountEntry -> {
					String alertName = (String)(alertTypeToCountEntry.getKey());
					Set<String> alertsWithCommonName = new HashSet<String>(Arrays.asList(alertName));
					return new AlertTypesCountBean(alertsWithCommonName, alertTypeToCountEntry.getValue());
			}).
				collect(Collectors.toSet());
		DataBean<Set<AlertTypesCountBean>> result = new DataBean<>();
		result.setData(alertTypesNameAndCount);
		result.setTotal(alertTypesNameAndCount.size());
		return result;
	}

	@RequestMapping(method = RequestMethod.GET , value = "/export")
	@LogException
	public void exportUsersToCsv(UserRestFilter filter, HttpServletResponse httpResponse)  throws  Exception{
		PageRequest pageRequest = new PageRequest(FIRST_PAGE_INDEX, DEFAULT_EXPORT_USERS_SIZE);

		List<User> users = getUsers(filter, pageRequest, fieldsRequired).getUsers();

		/*
			Set response type as CSV
		 */
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s_%s.csv\"",
				USERS_CSV_FILE_NAME, ZonedDateTime.now().toString());
		httpResponse.setHeader(headerKey, headerValue);
		httpResponse.setContentType(CSV_CONTENT_TYPE);
		filter.setAddAlertsAndDevices(true);
		CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(httpResponse.getOutputStream()));
		String[] tableTitleRow = { USER_NAME_COLUMN_NAME, DISPLAY_NAME_COLUMN_NAME, USER_ROLE_COLUMN_NAME,
				USER_DEPARTMENT_COLUMN_NAME, USER_WATCHED_COLUMN_NAME, USER_RISK_SCORE_COLUMN_NAME,
				USER_ALERT_COUNT_COLUMN_NAME, USER_DEVICE_COUNT_COLUMN_NAME, USER_TAGS_COLUMN_NAME };

		csvWriter.writeNext(tableTitleRow);
		users.stream().forEach(user -> {
			String[] userRow = {user.getUsername(), user.getDisplayName(), "",
					"", BooleanUtils.toStringTrueFalse(user.getFollowed()),
					String.valueOf(user.getScore()), String.valueOf(user.getAlertsCount()),
					String.valueOf(user.getSourceMachineCount()), StringUtils.join(user.getTags(), ',')};
			csvWriter.writeNext(userRow);
		});
		csvWriter.close();

	}

	@RequestMapping(value="/{watch}/followUsers", method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@LogException
	public ResponseEntity<UsersCount> followUsersByFilter(@RequestBody UserRestFilter userRestFilter, @PathVariable Boolean watch) {

		if (userRestFilter.getSize() == null) {
			userRestFilter.setSize(Integer.MAX_VALUE);
		}

		int usersUpdated = userWithAlertService.followUsersByFilter(userRestFilter, watch);

		return ResponseEntity.status(HttpStatus.OK).body(new UsersCount(usersUpdated));
	}

	@RequestMapping(value="/{fieldName}/distinctValues", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<String>> getDistinctValues(@PathVariable String fieldName) {

		List<String> distinctValues = userService.getDistinctValuesByFieldName(fieldName);

		DataBean<List<String>> result = new DataBean<>();
		result.setData(distinctValues);
		result.setTotal(distinctValues.size());

		return result;
	}

	/**
	 * Getting the relevant users according to the filter requested
	 * @param userRestFilter
	 * @param pageRequest
	 * @param fieldsRequired
	 * @return
	 */
	private Users getUsers(UserRestFilter userRestFilter, PageRequest pageRequest, List<String> fieldsRequired) {

//		// Get the relevant users by filter requested
//		List<User> users = userWithAlertService.findUsersByFilter(userRestFilter, pageRequest, fieldsRequired);
//
//		// Add severity to the users
//		setSeverityOnUsersList(users);

//		List<User> users = new ArrayList<>();
//		User user1 = new UsersMockBuilder(1).setWatched(true).createInstance();
//		User user2 = new UsersMockBuilder(2).createInstance();
//
//		users.addAll(Arrays.asList(new User[]{user1,user2}));
//		return users;

		return userService.findUsersByFilter(userRestFilter,pageRequest,null,fieldsRequired,true);
	}

	public class UsersCount {

		public int count;

		public UsersCount(int usersUpdated) {
			this.count = usersUpdated;
		}

		public UsersCount() {
		}
	}


	@RequestMapping(value="/entities", method = RequestMethod.GET)
	@LogException
	public @ResponseBody
	DataBean<Set<Map<String, String>>> getEntities(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
													@RequestParam(required=false, value = "entity_name") String entityName,
													@RequestParam(required=false, value = "entity_id") String entityId,
													@RequestParam(required=false, value = "page") Integer fromPage,
													@RequestParam(required=false, value = "size")  Integer size) {

		final int DEFAULT_PAGE_SIZE = 10;
		DataBean<Set<Map<String, String>>> response = new DataBean<>();
		if ((entityName == null || entityName.isEmpty()) && (entityId == null || entityId.isEmpty())) {
			logger.debug("Received empty entity name and empty entity id when trying to read entity list");
			return response;
		}

		//if pageForMongo is not set, get first pageForMongo
		//Mongo pages start with 0. While on the API the first page is 1.
		int pageForMongo;
		if (fromPage == null) {
			pageForMongo = 0;
		} else {
			pageForMongo = fromPage -1;
		}

		// In case we didn't receive page size, set the default
		if (size == null){
			size = DEFAULT_PAGE_SIZE;
		}

		PageRequest pageRequest = new PageRequest(pageForMongo, size);


		Set<Map<String, String>> entities = new HashSet<>();
		List<User> users=new ArrayList<>();
		// Read users
		if (entityName != null) {
			entityName = ApiUtils.stringReplacement(entityName);
			 users = userService.getUsersByPrefix(entityName, pageRequest);
		} else if (entityId != null) {
			 users = Arrays.asList(userService.getUserById(entityId));
		}
		users.forEach(user -> {
			Map<String,String> userMap = new HashMap<>();
			userMap.put("id",user.getId());
			userMap.put("normalizedUserName",user.getUsername());
			userMap.put("username",user.getUsername());
			userMap.put("uniqueDisplayName",user.getUsername());
			boolean userAlreadyExists=entities.stream()
											   .filter(entityEntry-> entityEntry.get("id").equals(user.getId()))
											   .count()>0;
			if (!userAlreadyExists) {
				entities.add(userMap);
			}

		});

//		addUniqueEntitiesDescription(entities);

		response.setData(entities);
		return  response;
	}


	/**
	 * Add indicator on the responce if all users (relevant to this filter) are "watched"
	 * @param usersList
	 * @param userRestFilter
	 */
	private void addAllWatched(DataBean<List<UserDetailsBean>> usersList, UserRestFilter userRestFilter) {
		Map<String, Object> info = usersList.getInfo();
		if (info == null) {
			info = new HashMap<>();
		}
		Boolean oldIsWatched = userRestFilter.getIsWatched();
		userRestFilter.setIsWatched(true);

		//Workaround. Need to remove after Presidio GA 1.0

		List<String> originalUsersTag = userRestFilter.getUserTags();
		userRestFilter.setUserTags(null);

		//End of workaround
		info.put(ALL_WATCHED, usersList.getTotal() == userService.countUsersByFilter(userRestFilter,null));

		//Revert changes
		userRestFilter.setIsWatched(oldIsWatched);
		userRestFilter.setUserTags(originalUsersTag);
		//end of revert changes
		usersList.setInfo(info);
	}

	private void addAlertsAndDevices(List<UserDetailsBean> users) {
		for (UserDetailsBean userDetailsBean: users) {
//			User user = userDetailsBean.getUser();
//			List<Alert> usersAlerts = alertsService.getOpenAlertsByUsername(user.getUsername());
			userDetailsBean.setAlerts(MockScenarioGenerator.generateMocksAlertsList());
//			userDetailsBean.getUser().setAlertsCount(HardCodedMocks.DEFAULT_USER_COUNT);
			userDetailsBean.getUser().setSourceMachineCount(HardCodedMocks.DEFAULT_USER_COUNT);



//			List<UserActivitySourceMachineDocument> userSourceMachines = getDevices(user);
		}

	}

//	private List<UserActivitySourceMachineDocument> getDevices(User user) {
//		List<UserActivitySourceMachineDocument> userSourceMachines;
//		try {
//            userSourceMachines = userActivityService.getUserActivitySourceMachineEntries(user.getId(),
//                    Integer.MAX_VALUE);
//        } catch (Exception ex) {
//            logger.warn("failed to get user source machines");
//            userSourceMachines = new ArrayList<>();
//        }
//		return userSourceMachines;
//	}

	private PageRequest createPaging(UserRestFilter userRestFilter) {

		Sort sortUserDesc = createSorting(userRestFilter.getSortField(), userRestFilter.getSortDirection());

		// Create paging
		Integer pageSize = 10;
		if (userRestFilter.getSize() != null) {
			pageSize = userRestFilter.getSize();
		}
		Integer pageNumber = 0;
		if (userRestFilter.getFromPage() != null) {
			pageNumber = userRestFilter.getFromPage() - 1;
		}
		return new PageRequest(pageNumber, pageSize, sortUserDesc);
	}

	private Sort createSorting(String sortField, String sortDirection) {
		// Create sorting
		Sort sortUserDesc;
		Sort.Direction sortDir = Sort.Direction.ASC;
		if (StringUtils.isNotBlank(sortField)) {
			if (sortDirection != null) {
				sortDir = Sort.Direction.valueOf(sortDirection);
			}
			sortUserDesc = new Sort(new Sort.Order(sortDir, sortField));
			// If there the api get sortField, which different from DEFAULT_SORT_FIELD, add
			// DEFAULT_SORT_FIELD as secondary sort
			if (!DEFAULT_SORT_FIELD.equals(sortField)) {
				Sort secondarySort = new Sort(new Sort.Order(Sort.Direction.ASC, DEFAULT_SORT_FIELD));
				sortUserDesc = sortUserDesc.and(secondarySort);
			}
		} else {
			sortUserDesc = new Sort(new Sort.Order(Sort.Direction.ASC, DEFAULT_SORT_FIELD));
		}
		return sortUserDesc;
	}

	private void addTagToUser(User user, List<String> tags, boolean addTag) throws Exception {
		if (addTag) {
			userTagService.addUserTags(user.getUsername(), tags);
		} else {
			userTagService.removeUserTags(user.getUsername(), tags);
		}
	}

	private DataBean<List<UserDetailsBean>> getUsersDetails(List<User> users) {
		List<UserDetailsBean> detailsUsers = new ArrayList<>();
		if (users != null) {
			users.forEach(user -> {
				Set<String> userRelatedDnsSet = new HashSet<>();
				Map<String, User> dnToUserMap = new HashMap<>();
//				userServiceFacade.fillUserRelatedDns(user, userRelatedDnsSet);
//				userServiceFacade.fillDnToUsersMap(userRelatedDnsSet, dnToUserMap);
				UserDetailsBean detailsUser = createUserDetailsBean(user, dnToUserMap, true);
				detailsUsers.add(detailsUser);
			});
		}
		DataBean<List<UserDetailsBean>> ret = new DataBean<>();
		ret.setData(detailsUsers);
		return ret;
	}

	private DataBean<List<UserDetailsBean>> getUsersDetails(User user) {
		if(user == null) {
			return null;
		}
		Set<String> userRelatedDnsSet = new HashSet<>();
		Map<String, User> dnToUserMap = new HashMap<>();
//		userServiceFacade.fillUserRelatedDns(user, userRelatedDnsSet);
//		userServiceFacade.fillDnToUsersMap(userRelatedDnsSet, dnToUserMap);
		UserDetailsBean ret = createUserDetailsBean(user, dnToUserMap, true);
		return new DataListWrapperBean<>(ret);
	}

	private UserDetailsBean createUserDetailsBean(User user, Map<String, User> dnToUserMap, boolean isWithThumbnail) {
		User manager = userServiceFacade.getUserManager(user, dnToUserMap);
		List<User> directReports = userServiceFacade.getUserDirectReports(user, dnToUserMap);
		UserDetailsBean ret =  new UserDetailsBean(user, manager, directReports,userServiceFacade);
		if(isWithThumbnail) {
			ret.setThumbnailPhoto(userServiceFacade.getUserThumbnail(user));
		}
		ret.setAlerts(user.getAlerts());
		return ret;
	}

	private DataBean<List<UserDetailsBean>> userDetails(List<User> users) {
		List<UserDetailsBean> userDetailsBeans = new ArrayList<>();
		Set<String> userRelatedDnsSet = new HashSet<>();
		Map<String, User> dnToUserMap = new HashMap<>();
//		for (User user: users) {
//			userServiceFacade.fillUserRelatedDns(user, userRelatedDnsSet);
//		}
//		userServiceFacade.fillDnToUsersMap(userRelatedDnsSet, dnToUserMap);
		for (User user: users) {
			UserDetailsBean userDetailsBean = createUserDetailsBean(user, dnToUserMap, true);
			userDetailsBeans.add(userDetailsBean);
		}
		DataBean<List<UserDetailsBean>> ret = new DataBean<>();
		ret.setData(userDetailsBeans);
		ret.setTotal(userDetailsBeans.size());
		return ret;
	}

//	private DataBean<List<UserMachinesBean>> usersMachinesAux(List<User> users) {
//		List<UserMachinesBean> usersMachinesList = new ArrayList<>();
////		for (User user: users) {
//////			List<UserMachine> userMachines = userServiceFacade.getUserMachines(user.getId());
//////			usersMachinesList.add(new UserMachinesBean(user.getId(), userMachines));
////		}
//		DataBean<List<UserMachinesBean>> ret = new DataBean<>();
//		ret.setData(usersMachinesList);
//		ret.setTotal(usersMachinesList.size());
//		return ret;
//	}

	private void setSeverityOnUsersList(List<User> users) {
		users.forEach(this::setSeverityOnUser);
	}

	private void setSeverityOnUser(User user) {
//		double userScore = user.getScore();
//		Severity userSeverity;
//		try {
//			userSeverity = userScoreService.getUserSeverityForScore(userScore);
//		} catch (RuntimeException ex) {
//			logger.error("Cannot find user severity for score: " + userScore);
//			userSeverity = Severity.Low; // Handle fallback
//		}
//		user.setScoreSeverity(userSeverity);
		user.setScoreSeverity(Severity.Critical);
	}

}