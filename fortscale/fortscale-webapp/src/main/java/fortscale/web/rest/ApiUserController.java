package fortscale.web.rest;

import fortscale.common.exceptions.InvalidValueException;
import fortscale.domain.ad.UserMachine;
import fortscale.domain.core.Alert;
import fortscale.domain.core.Severity;
import fortscale.domain.core.Tag;
import fortscale.domain.core.User;
import fortscale.domain.core.activities.UserActivitySourceMachineDocument;
import fortscale.domain.core.dao.TagPair;
import fortscale.domain.core.dao.UserRepository;
import fortscale.services.*;
import fortscale.services.types.PropertiesDistribution;
import fortscale.services.types.PropertiesDistribution.PropertyEntry;
import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.BaseController;
import fortscale.web.beans.*;
import fortscale.domain.rest.UserRestFilter;
import fortscale.web.rest.Utils.UserDeviceUtils;
import fortscale.web.rest.Utils.UserRelatedEntitiesUtils;
import javafx.util.Pair;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/api/user")
public class ApiUserController extends BaseController{
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

	private static final String DEFAULT_SORT_FIELD = "username";


	/**
	 * The API to get all users. GET: /api/user
	 */
	@RequestMapping(method = RequestMethod.GET) @ResponseBody @LogException
	public DataBean<List<UserDetailsBean>> getUsers(UserRestFilter userRestFilter) {

		Sort sortUserDesc = createSorting(userRestFilter.getSortField(), userRestFilter.getSortDirection());
		PageRequest pageRequest = createPaging(userRestFilter.getSize(), userRestFilter.getFromPage(), sortUserDesc);

		List<User> users = userService.findUsersByFilter(userRestFilter, pageRequest);

		setSeverityOnUsersList(users);
		DataBean<List<UserDetailsBean>> usersList = getUsersDetails(users);
		usersList.setOffset(pageRequest.getPageNumber() * pageRequest.getPageSize());
		usersList.setTotal(userService.countUsersByFilter(userRestFilter));

		if (userRestFilter.getAddAlertsAndDevices() != null && userRestFilter.getAddAlertsAndDevices()) {
			setAdditionalInformation(usersList.getData());
		}

		return usersList;
	}

	@RequestMapping(value="/count", method=RequestMethod.GET)
	public DataBean<Integer> countUsers(UserRestFilter userRestFilter) {
		Integer count = userService.countUsersByFilter(userRestFilter);

		DataBean<Integer> bean = new DataBean<>();
		bean.setData(count);
		bean.setTotal(count);

		return bean;
	}

	private void setAdditionalInformation(List<UserDetailsBean> users) {
		for (UserDetailsBean userDetailsBean: users) {
			User user = userDetailsBean.getUser();
			Set<Alert> usersAlerts = alertsService.getOpenAlertsByUsername(user.getUsername());
			userDetailsBean.setAlerts(usersAlerts);
			List<UserActivitySourceMachineDocument> userSourceMachines
					= userActivityService.getUserActivitySourceMachineEntries(user.getId(), Integer.MAX_VALUE);
			userDetailsBean.setDevices(userDeviceUtils.convertDeviceDocumentsResponse(userSourceMachines, null));
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
	public DataBean<List<UserDetailsBean>> details(@PathVariable List<String> ids){

		// Get Users
		List<User> users = userRepository.findByIds(ids);
		setSeverityOnUsersList(users);
		// Return detailed users
		return getUsersDetails(users);
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
	public ResponseEntity<String> updateTags(@RequestBody String body) {
		JSONArray params = new JSONObject(body).getJSONArray("tags");
		String errorMessage = "{json body is not in proper format: Array<{name: String, displayName: String, isFixed: "+
			"boolean, createsIndicator: boolean}>}";
		List<Tag> tags;
		try {
			tags = new ObjectMapper().readValue(params.toString(), new TypeReference<List<Tag>>(){});
		} catch (IOException e) {
			return new ResponseEntity(errorMessage, HttpStatus.BAD_REQUEST);
		}
		for (Tag tag: tags) {
			if (!tagService.updateTag(tag)) {
				return new ResponseEntity("{failed to update tag}", HttpStatus.INTERNAL_SERVER_ERROR);
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

/*	@RequestMapping(value="/{id}/scores", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<IUserScore>> userScores(@PathVariable String id, Model model){
		DataBean<List<IUserScore>> ret = new DataBean<List<IUserScore>>();
		List<IUserScore> userScores = userServiceFacade.getUserScores(id);
		ret.setData(userScores);
		ret.setTotal(userScores.size());
		return ret;
	}

	@RequestMapping(value="/{uid}/classifier/{classifierId}/scorehistory", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<IUserScoreHistoryElement>> userClassifierScoreHistory(@PathVariable String uid, @PathVariable String classifierId,
			@RequestParam(required=false) List<Long> dateRange,
			@RequestParam(defaultValue="0") Integer tzShift,
			@RequestParam(defaultValue="10") Integer limit,
			Model model){
		if(dateRange == null || dateRange.size() == 0){
			dateRange = new ArrayList<>();
			dateRange.add(DateTime.now(DateTimeZone.UTC).withTimeAtStartOfDay().minusDays(limit-1).getMillis());
			dateRange.add(DateTime.now(DateTimeZone.UTC).withTimeAtStartOfDay().plusDays(1).getMillis());
		} else{
			if(dateRange.size()!=2 || (dateRange.get(0)>=dateRange.get(1))){
				logger.error("dateRange paramter {} is not in the list format [start,end]", dateRange);
				throw new InvalidValueException(String.format("dateRange paramter %s is not in the list format [start,end]", dateRange));
			}
		}
		DataBean<List<IUserScoreHistoryElement>> ret = new DataBean<List<IUserScoreHistoryElement>>();
		List<IUserScoreHistoryElement> userScoreHistory = userServiceFacade.getUserScoresHistory(uid, classifierId, dateRange.get(0), dateRange.get(1), tzShift);

		Collections.reverse(userScoreHistory);
		ret.setData(userScoreHistory);
		ret.setTotal(userScoreHistory.size());
		return ret;
	}


	@RequestMapping(value="/{uid}/classifier/total/explanation", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<IUserScore>> userTotalScoreExplanation(@PathVariable String uid,
			@RequestParam(required=true) String date, Model model){
		DataBean<List<IUserScore>> ret = new DataBean<List<IUserScore>>();
		List<IUserScore> userScores = userServiceFacade.getUserScoresByDay(uid, Long.parseLong(date));

		ret.setData(userScores);
		ret.setTotal(userScores.size());
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
			double userScore = user.getScore();
			Severity userSeverity;
			try {
				userSeverity = userScoreService.getUserSeverityForScore(userScore);

			} catch (RuntimeException e){
				logger.error("Cannot find user severtiy for score: "+userScore);
				userSeverity = Severity.Low; // Handle fallback
			}
			user.setScoreSeverity(userSeverity);

		}
	}
}
