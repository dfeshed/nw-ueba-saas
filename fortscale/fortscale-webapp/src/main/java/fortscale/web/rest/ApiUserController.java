package fortscale.web.rest;

import au.com.bytecode.opencsv.CSVWriter;
import fortscale.common.exceptions.InvalidValueException;
import fortscale.domain.ad.UserMachine;
import fortscale.domain.core.*;
import fortscale.domain.core.activities.UserActivitySourceMachineDocument;
import fortscale.domain.core.dao.TagPair;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.rest.UserFilter;
import fortscale.domain.rest.UserRestFilter;
import fortscale.services.*;
import fortscale.services.types.PropertiesDistribution;
import fortscale.services.types.PropertiesDistribution.PropertyEntry;
import fortscale.services.users.util.UserDeviceUtils;
import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.BaseController;
import fortscale.web.beans.*;
import fortscale.web.rest.Utils.UserRelatedEntitiesUtils;
import fortscale.services.users.util.activity.UserActivityData;
import javafx.util.Pair;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
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

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.core.Response;
import java.io.OutputStreamWriter;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static fortscale.web.rest.ApiAlertController.CSV_CONTENT_TYPE;

@Controller
@RequestMapping("/api/user")
public class ApiUserController extends BaseController{

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
	private List<String> fieldsRequired;

	@Autowired
	private UserServiceFacade userServiceFacade;

	@Autowired
	private UserTagService userTagService;

	@Autowired
	private TagService tagService;

	@Autowired
	private UserService userService;

	@Autowired
	private UserScoreService userScoreService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	UserRelatedEntitiesUtils userRelatedEntitiesUtils;

	@Autowired
	private UserDeviceUtils userDeviceUtils;

	@Autowired
	private AlertsService alertsService;

	@Autowired
	private UserActivityService userActivityService;

	@Autowired
	private UserWithAlertService userWithAlertService;

	private static final String DEFAULT_SORT_FIELD = "username";

	public ApiUserController() {
		fieldsRequired = new ArrayList<>();
		fieldsRequired.add(User.ID_FIELD);
		fieldsRequired.add(User.usernameField);
		fieldsRequired.add(User.followedField);
		fieldsRequired.add(User.displayNameField);
		fieldsRequired.add(User.alertsCountField);
		fieldsRequired.add(User.adPositionFieldName);
		fieldsRequired.add(User.adDepartmentFieldName);
		fieldsRequired.add(User.tagsField);
		fieldsRequired.add(User.sourceMachineCountField);
	}


	/**
	 * The API to get all users. GET: /api/user
	 */
	@RequestMapping(method = RequestMethod.GET) @ResponseBody @LogException
	public DataBean<List<UserDetailsBean>> getUsers(UserRestFilter userRestFilter) {
		PageRequest pageRequest = createPaging(userRestFilter.getSize(), userRestFilter.getFromPage(),
				userRestFilter.getSortField(), userRestFilter.getSortDirection());

		List<User> users = getUsers(userRestFilter, pageRequest, null);

		DataBean<List<UserDetailsBean>> usersList = getUsersDetails(users);
		usersList.setOffset(pageRequest.getPageNumber() * pageRequest.getPageSize());
		usersList.setTotal(userWithAlertService.countUsersByFilter(userRestFilter));
		if (BooleanUtils.isTrue(userRestFilter.getAddAlertsAndDevices())) {
			addAlertsAndDevices(usersList.getData());
		}
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

	@RequestMapping(value="/search", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public  DataBean<List<UserSearchBean>> search(@RequestParam(required = true) String prefix,
			@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size) {
		List<User> users = userServiceFacade.findBySearchFieldContaining(prefix, page, size);
		List<UserSearchBean> data = users.stream().map(UserSearchBean::new).collect(Collectors.toList());
		DataBean<List<UserSearchBean>> ret = new DataBean<>();
		ret.setData(data);
		ret.setTotal(data.size());
		return ret;
	}

	@RequestMapping(value="/extendedSearch", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public  DataBean<List<User>> extendedSearch(UserRestFilter userRestFilter){
		List<User> users = userWithAlertService.findFromCacheUsersByFilter(userRestFilter);

		DataBean<List<User>> result = new DataBean<>();
		result.setData(users);

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
		User user = userRepository.findByUsername(username);
		setSeverityOnUsersList(Arrays.asList(user));
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
		List<User> users = userRepository.findByIds(ids);
		setSeverityOnUsersList(users);
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
		User user = userRepository.findOne(id);
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
			throw new InvalidValueException(String.format("param %s is invalid", params.toString()));
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
	@RequestMapping(value="/{addTag}/{tagNames}/tagUsers", method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@LogException
	public ResponseEntity<TaggedUsersCount> addRemoveTagByFilter(@RequestBody UserRestFilter userRestFilter,
			@PathVariable Boolean addTag, @PathVariable List<String> tagNames) throws JSONException {
		TaggedUsersCount result = new TaggedUsersCount();
		if (CollectionUtils.isEmpty(tagNames)) {
			return new ResponseEntity(result, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		List<User> usersByFilter = userWithAlertService.findUsersByFilter(userRestFilter, null, null);
		int count = 0;
		for (User user: usersByFilter) {
			try {
				addTagToUser(user, tagNames, addTag);
				count++;
			} catch (Exception ex) {
				result.count = count;
				return new ResponseEntity(result, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		result.count = count;
		return new ResponseEntity(result, HttpStatus.OK);
	}

	@RequestMapping(value="/followedUsers", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<String>> followedUsers() {
		List<String> userIds = userRepository.findByFollowed(true).stream().map(User::getId).
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
		List<User> users = userRepository.findByIds(ids);
		setSeverityOnUsersList(users);
		return userDetails(users);
	}

	@RequestMapping(value="/usersTagsCount", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<TagPair>> usersTagsCount() {
		List<TagPair> result = new ArrayList();
		Map<String, Long> items = userService.groupByTags();
		result.addAll(items.entrySet().stream().map(entry -> new TagPair(entry.getKey(), entry.getValue())).
				collect(Collectors.toList()));
		DataBean<List<TagPair>> ret = new DataBean();
		ret.setData(result);
		ret.setTotal(result.size());
		return ret;
	}

	@RequestMapping(value="/tagUsers", method=RequestMethod.GET)
	@LogException
	public ResponseEntity<Response> tagUsers() {
		try {
			//TODO - make this asynchronous
			userTagService.update();
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Response.status(Response.Status.
					INTERNAL_SERVER_ERROR).build());
		}
		return new ResponseEntity("{}", HttpStatus.OK);
	}

	@RequestMapping(value="/user_tags", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<Tag>> getAllTags() {
		List<Tag> result = tagService.getAllTags();
		DataBean<List<Tag>> ret = new DataBean();
		ret.setData(result);
		ret.setTotal(result.size());
		return ret;
	}

	@RequestMapping(value="/user_tags", method=RequestMethod.POST)
	@LogException
	public ResponseEntity<String> updateTags(@RequestBody @Valid List<Tag> tags) {
		for (Tag tag: tags) {
			if (!tagService.updateTag(tag)) {
				return new ResponseEntity("{failed to update tag}", HttpStatus.INTERNAL_SERVER_ERROR);
			//if update was successful and tag is no longer active - remove that tag from all users
			} else if (!tag.getActive()) {
				String tagName = tag.getName();
				Set<String> usernames = userService.findUsernamesByTags(new String[] { tagName });
				if (CollectionUtils.isNotEmpty(usernames)) {
					logger.info("tag {} became inactive, removing from {} users", tagName, usernames.size());
					for (String username : usernames) {
						userTagService.removeUserTags(username, Arrays.asList(new String[] { tagName }));
					}
				}
			}
		}
		return new ResponseEntity("{}", HttpStatus.ACCEPTED);	}

	@RequestMapping(value="/followedUsersDetails", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<UserDetailsBean>> followedUsersDetails(Model model) {
		List<User> users = userRepository.findByFollowed(true);
		setSeverityOnUsersList(users);
		return userDetails(users);
	}

	@RequestMapping(value="/{id}/machines", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<UserMachine>> userMachines(@PathVariable String id) {
		List<UserMachine> userMachines = userServiceFacade.getUserMachines(id);
		DataBean<List<UserMachine>> ret = new DataBean<>();
		ret.setData(userMachines);
		ret.setTotal(userMachines.size());
		return ret;
	}

	@RequestMapping(value="/usersMachines", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<UserMachinesBean>> usersMachines(@RequestParam(required = true) List<String> ids) {
		List<User> users = userRepository.findByIds(ids);
		return usersMachinesAux(users);
	}

	/**
	 * Gets the destination machines operating systems distribution for a user
	 */
	@RequestMapping(value="/{uid}/destination/{param}/distribution", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<Collection<PropertyEntry>> getDestinationPropertyDistribution(@PathVariable String uid,
			@PathVariable String param, @RequestParam(defaultValue="50") int minScore,
			@RequestParam(required = false) Long latestDate, @RequestParam(required = false) Long earliestDate,
			@RequestParam(defaultValue="10") int maxValues) {
		PropertiesDistribution distribution = userServiceFacade.getDestinationComputerPropertyDistribution(uid, param,
				latestDate, earliestDate, maxValues, minScore);
		// convert the distribution properties to data bean
		DataBean<Collection<PropertyEntry>> ret = new DataBean<>();
		if (distribution.isConclusive()) {
			ret.setData(distribution.getPropertyValues());
			ret.setTotal(distribution.getNumberOfValues());
		} else {
			ret.setWarning(DataWarningsEnum.NON_CONCLUSIVE_DATA);
		}
		return ret;
	}

	/**
	 * rest for /{normalized_username}/related_entities.
	 *
	 * @param normalized_username User's normalized username
	 * @param timePeriodInDays    Time period in days
	 * @param limit               The max amount of returned data
	 * @param dataEntitiesString  A CSV of required data entities
	 * @param featureName         Feature name. i.e. "destination_machine", "source_machine", "country"
	 * @return
	 */
	@RequestMapping(value = "/{normalized_username}/related_entities", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<Pair<String, Double>>> getRelatedEntities(
			@PathVariable String normalized_username,
			@RequestParam(required = false, defaultValue = "90", value = "time_range") Integer timePeriodInDays,
			@RequestParam(required = false, defaultValue = "5", value = "limit") Integer limit,
			@RequestParam(required = true, value = "data_entities") String dataEntitiesString,
			@RequestParam(required = true, value = "feature_name") String featureName) {
		List<Pair<String, Double>> relatedEntitiesList = userRelatedEntitiesUtils
				.getRelatedEntitiesList(dataEntitiesString, normalized_username, limit, timePeriodInDays, featureName);
		DataBean<List<Pair<String, Double>>> response = new DataBean<>();
		response.setData(relatedEntitiesList);
		return response;
	}

	@RequestMapping(value = "/severityBar", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<Map<String, Map<String, Integer>>> getSeverityBarInfo(UserRestFilter userRestFilter){
		DataBean<Map<String, Map<String, Integer>>> dataBean = new DataBean<>();
		Map<String, Map<String, Integer>> severityBarMap = new HashMap<>();
		if (userRestFilter.getMinScore() == null) {
			userRestFilter.setMinScore(0d);
		}

		List<User> scoredUsers = userWithAlertService.findUsersByFilter(userRestFilter, null, null);

		if (CollectionUtils.isNotEmpty(scoredUsers)) {
			scoredUsers.stream().forEach(user -> {
				setSeverityOnUser(user);
				Map<String, Integer> severityData = severityBarMap.get(user.getScoreSeverity().name());
				if (MapUtils.isEmpty(severityData)) {
					severityData = new HashMap<>();
					severityData.put(USER_COUNT, 0);
					severityData.put(ADMINISTRATOR_TAG, 0);
					severityData.put(WATCHED_USER, 0);
					severityBarMap.put(user.getScoreSeverity().name(), severityData);
				}
				severityData.put(USER_COUNT, severityData.get(USER_COUNT) + 1);
				if (user.getFollowed()) {
					severityData.put(WATCHED_USER, severityData.get(WATCHED_USER) + 1);
				}
				if (user.getTags().contains(Tag.ADMIN_TAG)) {
					severityData.put(ADMINISTRATOR_TAG, severityData.get(ADMINISTRATOR_TAG) + 1);
				}
			});
			dataBean.setData(severityBarMap);
			dataBean.setTotal(scoredUsers.size());
		}
		return dataBean;
	}

	@RequestMapping(value="/exist-alert-types", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<Set<AlertTypesCountBean>> getDistinctAlertNames(@RequestParam(required=true,
			value = "ignore_rejected") Boolean ignoreRejected) {
		Set<AlertTypesCountBean> alertTypesNameAndCount = alertsService.
				getAlertsTypesByUser(ignoreRejected).entrySet().stream().map(alertTypeToCountEntry ->
				new AlertTypesCountBean(alertTypeToCountEntry.getKey(), alertTypeToCountEntry.getValue().size())).
				collect(Collectors.toSet());
		DataBean<Set<AlertTypesCountBean>> result = new DataBean<>();
		result.setData(alertTypesNameAndCount);
		result.setTotal(alertTypesNameAndCount.size());
		return result;
	}

	@RequestMapping(method = RequestMethod.GET , value = "/export")
	@LogException
	public void exportUsersToCsv(UserRestFilter filter, HttpServletResponse httpResponse)  throws  Exception{

		List<User> users = getUsers(filter, null, fieldsRequired);

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
			String[] userRow = {user.getUsername(), user.getDisplayName(), user.getAdInfo().getPosition(),
					user.getAdInfo().getDepartment(), BooleanUtils.toStringTrueFalse(user.getFollowed()),
					String.valueOf(user.getScore()), String.valueOf(user.getAlertsCount()),
					String.valueOf(user.getSourceMachineCount()), StringUtils.join(user.getTags(), ',')};
			csvWriter.writeNext(userRow);
		});
		csvWriter.close();

	}

	@RequestMapping(value="/{watch}/followUsers", method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@LogException
	public Response followUsersByFilter(@RequestBody UserRestFilter userRestFilter, @PathVariable Boolean watch) {
		if (userRestFilter.getSize() == null) {
			userRestFilter.setSize(Integer.MAX_VALUE);
		}
		DataBean<List<UserDetailsBean>> users = getUsers(userRestFilter);
		if (CollectionUtils.isNotEmpty(users.getData())) {
			users.getData().forEach(userDetailsBean -> {
				User user = userDetailsBean.getUser();
				user.setFollowed(watch);
				userService.saveUser(user);
			});
		}
		return Response.status(Response.Status.OK).build();
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

	private List<User> getUsers(UserRestFilter userRestFilter, PageRequest pageRequest, List<String> fieldsRequired) {

		if (StringUtils.isNotEmpty(userRestFilter.getSearchValue())){
			List<User> usersFromCache = userWithAlertService.findFromCacheUsersByFilter(userRestFilter);
			List<String> userIds = new ArrayList<>();

			usersFromCache.forEach(user -> {
				userIds.add(user.getId());
			});

			userRestFilter.setUserIds(userIds);
		}

		List<User> users = userWithAlertService.findUsersByFilter(userRestFilter, pageRequest, fieldsRequired);

		setSeverityOnUsersList(users);
		return users;
	}

	private class TaggedUsersCount {

		public int count;

	}

	private void addAllWatched(DataBean<List<UserDetailsBean>> usersList, UserRestFilter userRestFilter) {
		Map<String, Object> info = usersList.getInfo();
		if (info == null) {
			info = new HashMap<>();
		}
		Boolean oldIsWatched = userRestFilter.getIsWatched();
		userRestFilter.setIsWatched(true);
		info.put(ALL_WATCHED, usersList.getTotal() == userWithAlertService.countUsersByFilter(userRestFilter));
		userRestFilter.setIsWatched(oldIsWatched);
		usersList.setInfo(info);
	}

	private void addAlertsAndDevices(List<UserDetailsBean> users) {
		for (UserDetailsBean userDetailsBean: users) {
			User user = userDetailsBean.getUser();
			List<Alert> usersAlerts = alertsService.getOpenAlertsByUsername(user.getUsername());
			userDetailsBean.setAlerts(usersAlerts);

			List<UserActivitySourceMachineDocument> userSourceMachines = getDevices(user);
			List<UserActivityData.DeviceEntry> deviceEntries = userDeviceUtils.convertDeviceDocumentsResponse(userSourceMachines, 3);
			userDetailsBean.setDevices(deviceEntries);
		}
	}

	private List<UserActivitySourceMachineDocument> getDevices(User user) {
		List<UserActivitySourceMachineDocument> userSourceMachines;
		try {
            userSourceMachines = userActivityService.getUserActivitySourceMachineEntries(user.getId(),
                    Integer.MAX_VALUE);
        } catch (Exception ex) {
            logger.warn("failed to get user source machines");
            userSourceMachines = new ArrayList<>();
        }
		return userSourceMachines;
	}

	private PageRequest createPaging(Integer size, Integer fromPage, String sortField, String sortDirection) {

		Sort sortUserDesc = createSorting(sortField, sortDirection);

		// Create paging
		Integer pageSize = 10;
		if (size != null) {
			pageSize = size;
		}
		Integer pageNumber = 0;
		if (fromPage != null) {
			pageNumber = fromPage - 1;
		}
		return new PageRequest(pageNumber, pageSize, sortUserDesc);
	}

	private Sort createSorting(String sortField, String sortDirection) {
		// Create sorting
		Sort sortUserDesc;
		Sort.Direction sortDir = Sort.Direction.ASC;
		if (sortField != null) {
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
				userServiceFacade.fillUserRelatedDns(user, userRelatedDnsSet);
				userServiceFacade.fillDnToUsersMap(userRelatedDnsSet, dnToUserMap);
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
		userServiceFacade.fillUserRelatedDns(user, userRelatedDnsSet);
		userServiceFacade.fillDnToUsersMap(userRelatedDnsSet, dnToUserMap);
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
		return ret;
	}

	private DataBean<List<UserDetailsBean>> userDetails(List<User> users) {
		List<UserDetailsBean> userDetailsBeans = new ArrayList<>();
		Set<String> userRelatedDnsSet = new HashSet<>();
		Map<String, User> dnToUserMap = new HashMap<>();
		for (User user: users) {
			userServiceFacade.fillUserRelatedDns(user, userRelatedDnsSet);
		}
		userServiceFacade.fillDnToUsersMap(userRelatedDnsSet, dnToUserMap);
		for (User user: users) {
			UserDetailsBean userDetailsBean = createUserDetailsBean(user, dnToUserMap, true);
			userDetailsBeans.add(userDetailsBean);
		}
		DataBean<List<UserDetailsBean>> ret = new DataBean<>();
		ret.setData(userDetailsBeans);
		ret.setTotal(userDetailsBeans.size());
		return ret;
	}

	private DataBean<List<UserMachinesBean>> usersMachinesAux(List<User> users) {
		List<UserMachinesBean> usersMachinesList = new ArrayList<>();
		for (User user: users) {
			List<UserMachine> userMachines = userServiceFacade.getUserMachines(user.getId());
			usersMachinesList.add(new UserMachinesBean(user.getId(), userMachines));
		}
		DataBean<List<UserMachinesBean>> ret = new DataBean<>();
		ret.setData(usersMachinesList);
		ret.setTotal(usersMachinesList.size());
		return ret;
	}

	private void setSeverityOnUsersList(List<User> users) {
		users.forEach(this::setSeverityOnUser);
	}

	private void setSeverityOnUser(User user) {
		double userScore = user.getScore();
		Severity userSeverity;
		try {
			userSeverity = userScoreService.getUserSeverityForScore(userScore);
		} catch (RuntimeException ex) {
			logger.error("Cannot find user severity for score: " + userScore);
			userSeverity = Severity.Low; // Handle fallback
		}
		user.setScoreSeverity(userSeverity);
	}

}