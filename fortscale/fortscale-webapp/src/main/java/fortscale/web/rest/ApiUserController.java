package fortscale.web.rest;

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
import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.BaseController;
import fortscale.web.beans.*;
import fortscale.web.rest.Utils.UserDeviceUtils;
import fortscale.web.rest.Utils.UserRelatedEntitiesUtils;
import javafx.util.Pair;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@Controller
@RequestMapping("/api/user")
public class ApiUserController extends BaseController{
	public static final String USER_COUNT = "userCount";
	public static final String ADMINISTRATOR_TAG = "administrator";
	public static final String WATCHED_USER = "watched";
	private static Logger logger = Logger.getLogger(ApiUserController.class);

	@Autowired
	private UserServiceFacade userServiceFacade;

	@Autowired
	private TagService tagService;

	@Autowired
	private UserTaggingService userTaggingService;

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

	/**
	 * The API to get all users. GET: /api/user
	 */
	@RequestMapping(method = RequestMethod.GET) @ResponseBody @LogException
	public DataBean<List<UserDetailsBean>> getUsers(UserRestFilter userRestFilter) {

		Sort sortUserDesc = createSorting(userRestFilter.getSortField(), userRestFilter.getSortDirection());
		PageRequest pageRequest = createPaging(userRestFilter.getSize(), userRestFilter.getFromPage(), sortUserDesc);

		List<User> users = userWithAlertService.findUsersByFilter(userRestFilter, pageRequest);

		setSeverityOnUsersList(users);
		DataBean<List<UserDetailsBean>> usersList = getUsersDetails(users);
		usersList.setOffset(pageRequest.getPageNumber() * pageRequest.getPageSize());
		usersList.setTotal(userWithAlertService.countUsersByFilter(userRestFilter));

		if (userRestFilter.getAddAlertsAndDevices() != null && userRestFilter.getAddAlertsAndDevices()) {
			addAlertsAndDevices(usersList.getData());
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

	@RequestMapping(value = "/favoriteFilter", method = RequestMethod.PUT)
	public ResponseEntity addFavoriteFilter(UserFilter userFilter, @RequestParam(value = "filter_name") String filterName) {
		try {
			userService.saveFavoriteFilter(userFilter, filterName);
		} catch (DuplicateKeyException e) {
			return new ResponseEntity("The filter name already exists", HttpStatus.BAD_REQUEST);
		} catch (Exception e){
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity(HttpStatus.OK);
	}

	@RequestMapping(value = "/favoriteFilter", method = RequestMethod.DELETE)
	public ResponseEntity deleteFavoriteFilter(@RequestParam(value = "filter_name") String filterName) {

		long lineDeleted = userService.deleteFavoriteFilter(filterName);

		if (lineDeleted > 0){
			return new ResponseEntity(HttpStatus.OK);
		}
		return new ResponseEntity("No documents deleted", HttpStatus.BAD_REQUEST);
	}

	@RequestMapping(value = "/favoriteFilter", method = RequestMethod.GET)
	public DataBean<List<FavoriteUserFilter>> getFavoriteFilters() {

		List<FavoriteUserFilter> allFavoriteFilters = userService.getAllFavoriteFilters();
		DataBean<List<FavoriteUserFilter>> result = new DataBean<>();
		result.setData(allFavoriteFilters);
		result.setTotal(allFavoriteFilters.size());

		return result;

	}

	private void addAlertsAndDevices(List<UserDetailsBean> users) {
		for (UserDetailsBean userDetailsBean: users) {
			User user = userDetailsBean.getUser();
			Set<Alert> usersAlerts = alertsService.getOpenAlertsByUsername(user.getUsername());
			userDetailsBean.setAlerts(usersAlerts);
			List<UserActivitySourceMachineDocument> userSourceMachines;
			try {
				userSourceMachines = userActivityService.getUserActivitySourceMachineEntries(user.getId(),
						Integer.MAX_VALUE);
			} catch (Exception ex) {
				logger.warn("failed to get user source machines");
				userSourceMachines = new ArrayList<>();
			}
			userDetailsBean.setDevices(userDeviceUtils.convertDeviceDocumentsResponse(userSourceMachines, 3));
		}
	}

	private PageRequest createPaging(@RequestParam(required = false, value = "size") Integer size,
			@RequestParam(required = false, value = "page") Integer fromPage, Sort sortUserDesc) {
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

	private Sort createSorting(@RequestParam(required = false, value = "sort_field") String sortField,
			@RequestParam(required = false, value = "sort_direction") String sortDirection) {
		// Create sorting
		Sort sortUserDesc;
		Sort.Direction sortDir = Sort.Direction.ASC;
		if (sortField != null) {
			if (sortDirection != null){
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

	@RequestMapping(value="/search", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public  DataBean<List<UserSearchBean>> search(@RequestParam(required=true) String prefix,
			@RequestParam(defaultValue="0") Integer page,
			@RequestParam(defaultValue="10") Integer size
			, Model model){
		List<User> users = userServiceFacade.findBySearchFieldContaining(prefix, page, size);
		List<UserSearchBean> data = new ArrayList<UserSearchBean>();
		for(User user: users){
			data.add(new UserSearchBean(user));
		}

		DataBean<List<UserSearchBean>> ret = new DataBean<List<UserSearchBean>>();
		ret.setData(data);
		ret.setTotal(data.size());
		return ret;
	}

	/**
	 * Search user data by user name. This function is the same as details() but the parameter is username and not userid
	 * @param username the name of the user
	 * @return a {@link DataBean} that holds a list of details{@link UserDetailsBean}
	 */
	@RequestMapping(value="/{username}/userdata", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<UserDetailsBean>> userDataByName(@PathVariable String username){
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
			@RequestParam(required = false, value = "add_alerts_and_devices") Boolean addAlertsAndDevices){

		// Get Users
		List<User> users = userRepository.findByIds(ids);
		setSeverityOnUsersList(users);

		DataBean<List<UserDetailsBean>> usersDetails = getUsersDetails(users);

		if (BooleanUtils.isTrue(addAlertsAndDevices)){
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
	public void addRemoveTag(@PathVariable String id, @RequestBody String body) throws JSONException {
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
		UserTagService userTagService = userTaggingService.getUserTagService(tag);
		if (userTagService == null) {
			userTagService = userTaggingService.getUserTagService(UserTagEnum.custom.getId());
		}
		if (addTag) {
			userTagService.addUserTag(user.getUsername(), tag);
		} else {
			userTagService.removeUserTag(user.getUsername(), tag);
		}
	}

	private DataBean<List<UserDetailsBean>> getUsersDetails(List<User> users) {
		List<UserDetailsBean> detailsUsers = new ArrayList<>();
        if(users != null) {

            users.forEach(user -> {
                Set<String> userRelatedDnsSet = new HashSet<>();
                Map<String, User> dnToUserMap = new HashMap<String, User>();

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
		if(user == null){
			return null;
		}

		Set<String> userRelatedDnsSet = new HashSet<>();
		Map<String, User> dnToUserMap = new HashMap<String, User>();

		userServiceFacade.fillUserRelatedDns(user, userRelatedDnsSet);
		userServiceFacade.fillDnToUsersMap(userRelatedDnsSet, dnToUserMap);

		UserDetailsBean ret = createUserDetailsBean(user, dnToUserMap, true);
		return new DataListWrapperBean<UserDetailsBean>(ret);
	}

	private UserDetailsBean createUserDetailsBean(User user, Map<String, User> dnToUserMap, boolean isWithThumbnail){
		User manager = userServiceFacade.getUserManager(user, dnToUserMap);
		List<User> directReports = userServiceFacade.getUserDirectReports(user, dnToUserMap);
		UserDetailsBean ret =  new UserDetailsBean(user, manager, directReports,userServiceFacade);
		if(isWithThumbnail){
			ret.setThumbnailPhoto(userServiceFacade.getUserThumbnail(user));
		}
		return ret;
	}

	@RequestMapping(value="/followedUsers", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<String>> followedUsers(Model model){
		List<String> userIds = new ArrayList<>();
		for(User user: userRepository.findByFollowed(true)){
			userIds.add(user.getId());
		}

		DataBean<List<String>> ret = new DataBean<>();
		ret.setData(userIds);
		ret.setTotal(userIds.size());
		return ret;
	}

	@RequestMapping(value="/usersDetails", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<UserDetailsBean>> usersDetails(@RequestParam(required=true) List<String> ids, Model model){
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
		for (Map.Entry<String, Long> entry : items.entrySet()) {
			result.add(new TagPair(entry.getKey(), entry.getValue()));
		}
		DataBean<List<TagPair>> ret = new DataBean();
		ret.setData(result);
		ret.setTotal(result.size());
		return ret;
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
				UserTagService userTagService = userTaggingService.getUserTagService(tagName);
				if (userTagService == null) {
					userTagService = userTaggingService.getUserTagService(UserTagEnum.custom.getId());
				}
				Set<String> usernames = userService.findUsernamesByTags(new String[] { tagName });
				if (CollectionUtils.isNotEmpty(usernames)) {
					logger.info("tag {} became inactive, removing from {} users", tagName, usernames.size());
					for (String username : usernames) {
						userTagService.removeUserTag(username, tagName);
					}
				}
			}
		}
		return new ResponseEntity("{}", HttpStatus.ACCEPTED);
	}

	@RequestMapping(value="/followedUsersDetails", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<UserDetailsBean>> followedUsersDetails(Model model){
		List<User> users = userRepository.findByFollowed(true);
		setSeverityOnUsersList(users);
		return userDetails(users);
	}

	private DataBean<List<UserDetailsBean>> userDetails(List<User> users){
		List<UserDetailsBean> userDetailsBeans = new ArrayList<>();

		Set<String> userRelatedDnsSet = new HashSet<>();
		Map<String, User> dnToUserMap = new HashMap<String, User>();
		for(User user: users){
			userServiceFacade.fillUserRelatedDns(user, userRelatedDnsSet);
		}
		userServiceFacade.fillDnToUsersMap(userRelatedDnsSet, dnToUserMap);

		for(User user: users){

			UserDetailsBean userDetailsBean = createUserDetailsBean(user, dnToUserMap, true);
			userDetailsBeans.add(userDetailsBean);
		}
		DataBean<List<UserDetailsBean>> ret = new DataBean<>();
		ret.setData(userDetailsBeans);
		ret.setTotal(userDetailsBeans.size());
		return ret;
	}

	@RequestMapping(value="/{id}/machines", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<UserMachine>> userMachines(@PathVariable String id, Model model){

		List<UserMachine> userMachines = userServiceFacade.getUserMachines(id);

		DataBean<List<UserMachine>> ret = new DataBean<List<UserMachine>>();
		ret.setData(userMachines);
		ret.setTotal(userMachines.size());
		return ret;
	}

	@RequestMapping(value="/usersMachines", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<UserMachinesBean>> usersMachines(@RequestParam(required=true) List<String> ids, Model model){
		List<User> users = userRepository.findByIds(ids);
		return usersMachines(users);
	}

	private DataBean<List<UserMachinesBean>> usersMachines(List<User> users){
		List<UserMachinesBean> usersMachinesList = new ArrayList<>();
		for(User user: users) {
			List<UserMachine> userMachines = userServiceFacade.getUserMachines(user.getId());

			usersMachinesList.add(new UserMachinesBean(user.getId(), userMachines));
		}
		DataBean<List<UserMachinesBean>> ret = new DataBean<>();
		ret.setData(usersMachinesList);
		ret.setTotal(usersMachinesList.size());
		return ret;
	}

	/**
	 * Gets the destination machines operating systems distribution for a user
	 */
	@RequestMapping(value="/{uid}/destination/{param}/distribution", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<Collection<PropertyEntry>> getDestinationPropertyDistribution(
			@PathVariable String uid,
			@PathVariable String param,
			@RequestParam(defaultValue="50") int minScore,
			@RequestParam(required = false) Long latestDate, @RequestParam(required = false) Long earliestDate,
			@RequestParam(defaultValue="10") int maxValues) {

		PropertiesDistribution distribution = userServiceFacade.getDestinationComputerPropertyDistribution(uid, param, latestDate,earliestDate, maxValues, minScore);

		// convert the distribution properties to data bean
		DataBean<Collection<PropertyEntry>> ret = new DataBean<Collection<PropertyEntry>>();
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
	 * @return DataBean<List>
	 */

	/**
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
			@RequestParam(required = true, value = "feature_name") String featureName
	) {

		List<Pair<String, Double>> relatedEntitiesList = userRelatedEntitiesUtils
				.getRelatedEntitiesList(dataEntitiesString, normalized_username, limit, timePeriodInDays, featureName);
		DataBean<List<Pair<String, Double>>> response = new DataBean<>();
		response.setData(relatedEntitiesList);
		return response;
	}

	private void setSeverityOnUsersList(List<User> users){
		for (User user: users){
			setSeverityOnUser(user);
		}
	}

	private void setSeverityOnUser(User user) {
		double userScore = user.getScore();
		Severity userSeverity;
		try {
			userSeverity = userScoreService.getUserSeverityForScore(userScore);

		} catch (RuntimeException e){
			logger.error("Cannot find user severity for score: "+userScore);
			userSeverity = Severity.Low; // Handle fallback
		}
		user.setScoreSeverity(userSeverity);
	}

	@RequestMapping(value = "/severityBar", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<Map<String, Map<String, Integer>>> getSeverityBarInfo(){
		DataBean<Map<String, Map<String, Integer>>> dataBean = new DataBean<>();
		Map<String, Map<String, Integer>> severityBarMap = new HashMap<>();

		UserRestFilter filter = new UserRestFilter();
		filter.setIsScored(true);

		List<User> scoredUsers = userService.findUsersByFilter(filter, null, null);

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

				if (user.getTags().contains(UserTagEnum.admin.name())) {
					severityData.put(ADMINISTRATOR_TAG, severityData.get(ADMINISTRATOR_TAG) + 1);
				}

			});

			dataBean.setData(severityBarMap);
			dataBean.setTotal(scoredUsers.size());
		}


		return dataBean;
	}
}
