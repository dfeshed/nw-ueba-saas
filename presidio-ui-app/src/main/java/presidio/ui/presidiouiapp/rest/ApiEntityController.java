package presidio.ui.presidiouiapp.rest;

import au.com.bytecode.opencsv.CSVWriter;

import fortscale.domain.core.*;

import fortscale.domain.core.dao.TagPair;

import fortscale.domain.core.dao.rest.Entities;
import fortscale.domain.rest.EntityFilter;
import fortscale.domain.rest.EntityRestFilter;
import fortscale.services.*;

import fortscale.temp.HardCodedMocks;
import fortscale.temp.MockScenarioGenerator;

import fortscale.utils.logging.Logger;





import org.apache.commons.collections.CollectionUtils;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import presidio.ui.presidiouiapp.BaseController;
import presidio.ui.presidiouiapp.beans.AlertTypesCountBean;
import presidio.ui.presidiouiapp.beans.DataBean;
import presidio.ui.presidiouiapp.beans.DataListWrapperBean;
import presidio.ui.presidiouiapp.beans.EntityDetailsBean;
import presidio.ui.presidiouiapp.rest.Utils.ApiUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import java.io.OutputStreamWriter;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/entity")
public class ApiEntityController extends BaseController {

	public static final int DEFAULT_EXPORT_USERS_SIZE = 1000;
	public static final int FIRST_PAGE_INDEX = 0;
	public static final int USERS_SEARCH_DEFAULT_PAGE_SIZE = 5;
	private static Logger logger = Logger.getLogger(ApiEntityController.class);

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


	private EntityServiceFacade entityServiceFacade;
	private EntityTagService entityTagService;
	private EntityService entityService;
	private EntityWithAlertService entityWithAlertService;

	private static final String DEFAULT_SORT_FIELD = "username";


	private List<String> extendedSearchfieldsRequired;

	public ApiEntityController(EntityServiceFacade entityServiceFacade, EntityTagService entityTagService, EntityService entityService, EntityWithAlertService entityWithAlertService) {
		this.entityServiceFacade = entityServiceFacade;
		this.entityTagService = entityTagService;
		this.entityService = entityService;
		this.entityWithAlertService = entityWithAlertService;

		initRequiredFields();
	}

	private void initRequiredFields() {
		fieldsRequired = new ArrayList<>();
		fieldsRequired.add(Entity.ID_FIELD);
		fieldsRequired.add(Entity.usernameField);
		fieldsRequired.add(Entity.followedField);
		fieldsRequired.add(Entity.displayNameField);
		fieldsRequired.add(Entity.alertsCountField);
		fieldsRequired.add(Entity.tagsField);
		fieldsRequired.add(Entity.sourceMachineCountField);
		fieldsRequired.add(Entity.scoreField);


		extendedSearchfieldsRequired = new ArrayList<>();
		extendedSearchfieldsRequired.add(Entity.ID_FIELD);

		extendedSearchfieldsRequired.add(Entity.usernameField);
		extendedSearchfieldsRequired.add(Entity.followedField);
		extendedSearchfieldsRequired.add(Entity.displayNameField);
		extendedSearchfieldsRequired.add(Entity.scoreField);
	}


	/**
	 * The API to get all users. GET: /api/user
	 */
	@RequestMapping(method = RequestMethod.GET) @ResponseBody 
	public DataBean<List<EntityDetailsBean>> getEntities(EntityRestFilter entityRestFilter) {

		PageRequest pageRequest = createPaging(entityRestFilter);

		Entities entities = getEntities(entityRestFilter, pageRequest, null);

		// Build the response
		DataBean<List<EntityDetailsBean>> usersList = getEntitiesDetails(entities.getEntities());
		usersList.setOffset(pageRequest.getPageNumber() * pageRequest.getPageSize());
		usersList.setTotal(new Long(entities.getTotalCount()).intValue());
//		if (BooleanUtils.isTrue(entityRestFilter.getAddAlertsAndDevices())) {
//			addAlertsAndDevices(usersList.getData());
//		}
		if (BooleanUtils.isTrue(entityRestFilter.getAddAllWatched())) {
			addAllWatched(usersList, entityRestFilter);
		}

		return usersList;
	}



	@RequestMapping(value="/count", method=RequestMethod.GET)
	@ResponseBody

	public DataBean<Integer> countEntities(EntityRestFilter entityRestFilter) {
		Integer count = entityWithAlertService.countEntitiesByFilter(entityRestFilter);
		DataBean<Integer> bean = new DataBean<>();
		bean.setData(count);
		bean.setTotal(count);
		return bean;
	}

	@RequestMapping(value = "/{filterName}/favoriteFilter", method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	
	public ResponseEntity<Response> addFavoriteFilter(@RequestBody EntityFilter entityFilter,
			@PathVariable String filterName) {
		try {
			entityService.saveFavoriteFilter(entityFilter, filterName);
		} catch (DuplicateKeyException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(Response.status(Response.Status.CONFLICT).
							entity("The filter name already exists").build());
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Response.status(Response.Status.
					INTERNAL_SERVER_ERROR).build());
		}

		return ResponseEntity.status(HttpStatus.OK).body(Response.status(Response.Status.OK).build());
	}

	@RequestMapping(value = "/favoriteFilter/{filterId}", method = RequestMethod.DELETE)
	@ResponseBody
	
	public ResponseEntity<Response> deleteFavoriteFilter(@PathVariable String filterId) {
		long lineDeleted = entityService.deleteFavoriteFilter(filterId);
		if (lineDeleted > 0) {
			return ResponseEntity.status(HttpStatus.OK).body(Response.status(Response.Status.OK).build());
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(Response.status(Response.Status.BAD_REQUEST).entity("No documents deleted").
						build());
	}

	@RequestMapping(value = "/favoriteFilter", method = RequestMethod.GET)
	@ResponseBody
	
	public DataBean<List<FavoriteEntityFilter>> getFavoriteFilters() {
		List<FavoriteEntityFilter> allFavoriteFilters = entityService.getAllFavoriteFilters();
		DataBean<List<FavoriteEntityFilter>> result = new DataBean<>();
		result.setData(allFavoriteFilters);
		result.setTotal(allFavoriteFilters.size());
		return result;

	}


	@RequestMapping(value="/exist-anomaly-types", method = RequestMethod.GET)
	@ResponseBody
//	//@LogException
	public Map<String,Integer> getDistinctAnomalyType () {
		Map<String,Integer> anomalyTypePairs =  entityService.getDistinctAnomalyType();


		return anomalyTypePairs;
	}

	@RequestMapping(value="/extendedSearch", method=RequestMethod.GET)
	@ResponseBody
//	//@LogException
	public DataBean<List<EntityDetailsBean>> extendedSearch(EntityRestFilter entityRestFilter){
		DataBean<List<EntityDetailsBean>> result = new DataBean<>();

		if (StringUtils.isNotEmpty(entityRestFilter.getSearchValue())) {

			Sort sorting = createSorting("", entityRestFilter.getSortDirection());
			PageRequest pageRequest = new PageRequest(FIRST_PAGE_INDEX, USERS_SEARCH_DEFAULT_PAGE_SIZE, sorting);

//			List<Entity> entities = entityWithAlertService.findUsersWithSearchValue(entityRestFilter, pageRequest, extendedSearchfieldsRequired);
			entityRestFilter.setSearchFieldContains(entityRestFilter.getSearchValue());
			Entities entitiesObject = entityService.findEntitiesByFilter(entityRestFilter,pageRequest,null,null,false);
			List<Entity> entities = entitiesObject !=null? entitiesObject.getEntities():new ArrayList();
			// Add severity to the entities
//			setSeverityOnEntitiesList(entities);

			result = getEntitiesDetails(entities);

			return result;

		}

		return result;
	}

	/**
	 * Search user data by user name. This function is the same as details() but the parameter is username and notuserid
	 * @param username the name of the user
	 * @return a {@link DataBean} that holds a list of details{@link EntityDetailsBean}
	 */
	@RequestMapping(value="/{username}/userdata", method=RequestMethod.GET)
	@ResponseBody
//	//@LogException
	public DataBean<List<EntityDetailsBean>> entityDataByName(@PathVariable String username) {
		Entity entity = entityService.findByUsername(username);
//		setSeverityOnEntitiesList(Arrays.asList(entity));
		return getEntitiesDetails(entity);
	}

	/**
	 * Search user's data by user id (uuid is auto-generated in MongoDB)
	 * @param ids the lost of user id from mongoDB
	 * @return a {@link DataBean} that holds a list of {@link EntityDetailsBean}
	 */
	@RequestMapping(value="/{ids}/details", method=RequestMethod.GET)
	@ResponseBody
//	//@LogException
	public DataBean<List<EntityDetailsBean>> details(@PathVariable List<String> ids,
													 @RequestParam(required = false, value = "add_alerts_and_devices") Boolean addAlertsAndDevices) {
		// Get Entities
		List<Entity> entities = entityService.findByIds(ids);
//		setSeverityOnEntitiesList(entities);
		DataBean<List<EntityDetailsBean>> usersDetails = getEntitiesDetails(entities);
		if (BooleanUtils.isTrue(addAlertsAndDevices)) {
			addAlertsAndDevices(usersDetails.getData());
		}
		// Return detailed entities
		return usersDetails;
	}

	/**
	 * API to update user tags
	 * @param body
	 * @return
	 */
	@RequestMapping(value="{id}", method = RequestMethod.POST)
//	//@LogException
	public Response addRemoveTag(@PathVariable String id, @RequestBody String body) throws JSONException {
		Entity entity = entityService.findOne(id);
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
			addTagToEntity(entity, Arrays.asList(new String[] { tag }), addTag);
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
//	//@LogException
	@ResponseBody
	public ResponseEntity<EntitiesCount> addRemoveTagByFilter(@RequestBody EntityRestFilter entityRestFilter,
															  @PathVariable Boolean addTag, @PathVariable List<String> tagNames) throws JSONException {

		EntitiesCount result = new EntitiesCount();
		if (CollectionUtils.isEmpty(tagNames)) {
			return new ResponseEntity(result, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		try {
			result.count = entityWithAlertService.updateTags(entityRestFilter, addTag, tagNames);
			return new ResponseEntity(result, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}


	}

	@RequestMapping(value="/followedUsers", method=RequestMethod.GET)
	@ResponseBody
//	//@LogException
	public DataBean<List<String>> followedEntities() {
		List<String> userIds = entityService.findByFollowed().stream().map(Entity::getId).
				collect(Collectors.toList());
		DataBean<List<String>> ret = new DataBean<>();
		ret.setData(userIds);
		ret.setTotal(userIds.size());
		return ret;
	}

	@RequestMapping(value="/usersDetails", method=RequestMethod.GET)
	@ResponseBody
//	//@LogException
	public DataBean<List<EntityDetailsBean>> entitiesDetails(@RequestParam(required = true) List<String> ids) {
		List<Entity> entities = entityService.findByIds(ids);
//		setSeverityOnEntitiesList(entities);
		return entityDetails(entities);
	}

	@RequestMapping(value="/usersTagsCount", method=RequestMethod.GET)
	@ResponseBody
//	//@LogException
	public DataBean<List<TagPair>> entitiesTagsCount() {
		List<TagPair> result = new ArrayList();
		Map<String, Long> items = entityService.groupByTags(false);
		result.addAll(items.entrySet().stream().map(entry -> new TagPair(entry.getKey(), entry.getValue())).
				collect(Collectors.toList()));
		DataBean<List<TagPair>> ret = new DataBean();
		ret.setData(result);
		ret.setTotal(result.size());
		return ret;
	}




	@RequestMapping(value="/followedUsersDetails", method=RequestMethod.GET)
	@ResponseBody
//	//@LogException
	public DataBean<List<EntityDetailsBean>> followedEntitiesDetails(Model model) {
		Set<Entity> entities = entityService.findByFollowed();

//		setSeverityOnEntitiesList(new ArrayList<>(entities));
		return entityDetails(new ArrayList<>(entities));
	}


	@RequestMapping(value = "/severityBar", method = RequestMethod.GET)
	@ResponseBody
	////@LogException
	public DataBean<Map<String, Map<String, Integer>>> getSeverityBarInfo(EntityRestFilter entityRestFilter){
		DataBean<Map<String, Map<String, Integer>>> dataBean = new DataBean<>();


		dataBean.setData(entityService.getSeverityScoreMap(entityRestFilter));


		dataBean.setTotal(entityService.countEntitiesByFilter(entityRestFilter,null));
		return dataBean;


	}

	@RequestMapping(value="/exist-alert-types", method = RequestMethod.GET)
	@ResponseBody
	////@LogException
	public DataBean<Set<AlertTypesCountBean>> getDistinctAlertNames(@RequestParam(required=true,
			value = "ignore_rejected") Boolean ignoreRejected) {
		//TODO: ignore rejected is not supported
		Set<AlertTypesCountBean> alertTypesNameAndCount = entityService.
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
	//@LogException
	public void exportEntitiesToCsv(EntityRestFilter filter, HttpServletResponse httpResponse)  throws  Exception{
		PageRequest pageRequest = new PageRequest(FIRST_PAGE_INDEX, DEFAULT_EXPORT_USERS_SIZE);

		List<Entity> entities = getEntities(filter, pageRequest, fieldsRequired).getEntities();

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
		entities.stream().forEach(entity -> {
			String[] userRow = {entity.getUsername(), entity.getDisplayName(), "",
					"", BooleanUtils.toStringTrueFalse(entity.getFollowed()),
					String.valueOf(entity.getScore()), String.valueOf(entity.getAlertsCount()),
					String.valueOf(entity.getSourceMachineCount()), StringUtils.join(entity.getTags(), ',')};
			csvWriter.writeNext(userRow);
		});
		csvWriter.close();

	}

	@RequestMapping(value="/{watch}/followUsers", method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	//@LogException
	public ResponseEntity<EntitiesCount> followEntitiesByFilter(@RequestBody EntityRestFilter entityRestFilter, @PathVariable Boolean watch) {

		if (entityRestFilter.getSize() == null) {
			entityRestFilter.setSize(Integer.MAX_VALUE);
		}

		int entitiesUpdated;
		if (entityRestFilter.getUserIds() !=null && entityRestFilter.getUserIds().size()==1){//Update single user
			entitiesUpdated = entityService.updateSingleEntityWatched(entityRestFilter.getUserIds().get(0),watch);
		} else { //Update fy filer
			entitiesUpdated = entityWithAlertService.followEntitiesByFilter(entityRestFilter, watch);
		}
		return ResponseEntity.status(HttpStatus.OK).body(new EntitiesCount(entitiesUpdated));
	}

	@RequestMapping(value="/{fieldName}/distinctValues", method=RequestMethod.GET)
	@ResponseBody
	//@LogException
	public DataBean<List<String>> getDistinctValues(@PathVariable String fieldName) {

		List<String> distinctValues = entityService.getDistinctValuesByFieldName(fieldName);

		DataBean<List<String>> result = new DataBean<>();
		result.setData(distinctValues);
		result.setTotal(distinctValues.size());

		return result;
	}

	/**
	 * Getting the relevant users according to the filter requested
	 * @param entityRestFilter
	 * @param pageRequest
	 * @param fieldsRequired
	 * @return
	 */
	private Entities getEntities(EntityRestFilter entityRestFilter, PageRequest pageRequest, List<String> fieldsRequired) {

		return entityService.findEntitiesByFilter(entityRestFilter,pageRequest,null,fieldsRequired,true);
	}

	public class EntitiesCount {

		public int count;

		public EntitiesCount(int usersUpdated) {
			this.count = usersUpdated;
		}

		public EntitiesCount() {
		}
	}


	@RequestMapping(value="/entities", method = RequestMethod.GET)
	//@LogException
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
		List<Entity> users=new ArrayList<>();
		// Read users
		if (entityName != null) {
			entityName = ApiUtils.stringReplacement(entityName);
			 users = entityService.getEntitiesByPrefix(entityName, pageRequest);
		} else if (entityId != null) {
			 users = Arrays.asList(entityService.getEntityById(entityId));
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
	 * @param entityRestFilter
	 */
	private void addAllWatched(DataBean<List<EntityDetailsBean>> usersList, EntityRestFilter entityRestFilter) {
		Map<String, Object> info = usersList.getInfo();
		if (info == null) {
			info = new HashMap<>();
		}
		Boolean oldIsWatched = entityRestFilter.getIsWatched();
		entityRestFilter.setIsWatched(true);

		//Workaround. Need to remove after Presidio GA 1.0

		List<String> originalUsersTag = entityRestFilter.getEntityTags();
		entityRestFilter.setEntityTags(null);

		//End of workaround
		info.put(ALL_WATCHED, usersList.getTotal() == entityService.countEntitiesByFilter(entityRestFilter,null));

		//Revert changes
		entityRestFilter.setIsWatched(oldIsWatched);
		entityRestFilter.setEntityTags(originalUsersTag);
		//end of revert changes
		usersList.setInfo(info);
	}

	private void addAlertsAndDevices(List<EntityDetailsBean> users) {
		for (EntityDetailsBean entityDetailsBean : users) {
//			Entity user = entityDetailsBean.getEntity();
//			List<Alert> usersAlerts = alertsService.getOpenAlertsByUsername(user.getUsername());
			entityDetailsBean.setAlerts(MockScenarioGenerator.generateMocksAlertsList());
//			entityDetailsBean.getEntity().setAlertsCount(HardCodedMocks.DEFAULT_USER_COUNT);
			entityDetailsBean.getEntity().setSourceMachineCount(HardCodedMocks.DEFAULT_USER_COUNT);



//			List<UserActivitySourceMachineDocument> userSourceMachines = getDevices(user);
		}

	}

	private PageRequest createPaging(EntityRestFilter entityRestFilter) {

		Sort sortUserDesc = createSorting(entityRestFilter.getSortField(), entityRestFilter.getSortDirection());

		// Create paging
		Integer pageSize = 10;
		if (entityRestFilter.getSize() != null) {
			pageSize = entityRestFilter.getSize();
		}
		Integer pageNumber = 0;
		if (entityRestFilter.getFromPage() != null) {
			pageNumber = entityRestFilter.getFromPage() - 1;
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

	private void addTagToEntity(Entity entity, List<String> tags, boolean addTag) throws Exception {
		if (addTag) {
			entityTagService.addEntityTags(entity.getUsername(), tags);
		} else {
			entityTagService.removeEntityTags(entity.getUsername(), tags);
		}
	}

	private DataBean<List<EntityDetailsBean>> getEntitiesDetails(List<Entity> entities) {
		List<EntityDetailsBean> detailsUsers = new ArrayList<>();
		if (entities != null) {
			entities.forEach(user -> {
				Set<String> userRelatedDnsSet = new HashSet<>();
				Map<String, Entity> dnToUserMap = new HashMap<>();
//				entityServiceFacade.fillUserRelatedDns(user, userRelatedDnsSet);
//				entityServiceFacade.fillDnToUsersMap(userRelatedDnsSet, dnToUserMap);
				EntityDetailsBean detailsUser = createEntityDetailsBean(user, dnToUserMap, true);
				detailsUsers.add(detailsUser);
			});
		}
		DataBean<List<EntityDetailsBean>> ret = new DataBean<>();
		ret.setData(detailsUsers);
		return ret;
	}

	private DataBean<List<EntityDetailsBean>> getEntitiesDetails(Entity entity) {
		if(entity == null) {
			return null;
		}
		Set<String> userRelatedDnsSet = new HashSet<>();
		Map<String, Entity> dnToUserMap = new HashMap<>();
//		entityServiceFacade.fillUserRelatedDns(entity, userRelatedDnsSet);
//		entityServiceFacade.fillDnToUsersMap(userRelatedDnsSet, dnToUserMap);
		EntityDetailsBean ret = createEntityDetailsBean(entity, dnToUserMap, true);
		return new DataListWrapperBean<>(ret);
	}

	private EntityDetailsBean createEntityDetailsBean(Entity entity, Map<String, Entity> dnToUserMap, boolean isWithThumbnail) {
		Entity manager = entityServiceFacade.getUserManager(entity, dnToUserMap);
		List<Entity> directReports = entityServiceFacade.getUserDirectReports(entity, dnToUserMap);
		EntityDetailsBean ret =  new EntityDetailsBean(entity, manager, directReports, entityServiceFacade);
		if(isWithThumbnail) {
			ret.setThumbnailPhoto(entityServiceFacade.getUserThumbnail(entity));
		}
		ret.setAlerts(entity.getAlerts());
		return ret;
	}

	private DataBean<List<EntityDetailsBean>> entityDetails(List<Entity> entities) {
		List<EntityDetailsBean> entityDetailsBeans = new ArrayList<>();
		Set<String> userRelatedDnsSet = new HashSet<>();
		Map<String, Entity> dnToUserMap = new HashMap<>();
//		for (Entity user: entities) {
//			entityServiceFacade.fillUserRelatedDns(user, userRelatedDnsSet);
//		}
//		entityServiceFacade.fillDnToUsersMap(userRelatedDnsSet, dnToUserMap);
		for (Entity entity : entities) {
			EntityDetailsBean entityDetailsBean = createEntityDetailsBean(entity, dnToUserMap, true);
			entityDetailsBeans.add(entityDetailsBean);
		}
		DataBean<List<EntityDetailsBean>> ret = new DataBean<>();
		ret.setData(entityDetailsBeans);
		ret.setTotal(entityDetailsBeans.size());
		return ret;
	}

}