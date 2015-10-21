package fortscale.web.rest;

import fortscale.domain.core.User;
import fortscale.domain.core.dao.AlertsRepositoryImpl;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.core.dao.UserRepositoryCustom;
import fortscale.domain.core.dao.UserRepositoryImpl;
import fortscale.services.UserService;
import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.DataQueryController;
import fortscale.web.beans.DataBean;
import fortscale.web.rest.Utils.ApiUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@RequestMapping("/api/entities")
public class ApiEntityController extends DataQueryController{

	public static final String UNIQUE_DISPLAY_NAME = "uniqueDisplayName";
	private static Logger logger = Logger.getLogger(ApiEntityController.class);
	private static final int DEFAULT_PAGE_SIZE = 10;

	/**
	 * DB repository for fetching users information
	 */
	@Autowired
	private UserService usersDao;

	//TODO: Support more entities (computers etc)

	@RequestMapping(method = RequestMethod.GET)
	@LogException
	public @ResponseBody
	DataBean<List<Map<String, String>>> getEntities(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
			@RequestParam(required=false, value = "entity_name") String entityName,
			@RequestParam(required=false, value = "entity_id") String entityId,
			@RequestParam(required=false, value = "page") Integer fromPage,
			@RequestParam(required=false, value = "size")  Integer size) {

		DataBean<List<Map<String, String>>> response = new DataBean<>();
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


		List<Map<String, String>> entities = new ArrayList<>();
		// Read users
		if (entityName != null) {
			entityName = ApiUtils.stringReplacement(entityName);
			entities = usersDao.getUsersByPrefix(entityName, pageRequest);

		} else if (entityId != null) {
			entities = usersDao.getUsersByIds(entityId, pageRequest);
		}

		addUniqueEntitiesDescription(entities);

		response.setData(entities);
		return  response;
	}

	/**
	 * Look for other entities with the same name,
	 * and add "UNIQUE_DISPLAY_NAME" entry to each entity.
	 * @param entities
	 */
	private void addUniqueEntitiesDescription(List<Map<String, String>> entities){

		if (CollectionUtils.isEmpty(entities)){
			return;
		}


		//For each entity in the response, check how many suers with the same display name exists in DB
		Set<String> displayNames = new HashSet<>();

		for (Map<String, String> entity: entities){
			String userDisplayName = fetchDisplayNameFromEntity(entity);
			displayNames.add(userDisplayName);

		}
		Map<String, Integer> usersToNumberOfUsers =  usersDao.countUsersByDisplayName(displayNames);



		for (Map<String, String> entity: entities) {

			String dipslayName = entity.get(User.usernameField);
			String uniqueDisplayName = null;
			//Update the unique display name according to how many instances of this display name exists.
			if (usersToNumberOfUsers.get(dipslayName) > 1) {
				uniqueDisplayName = dipslayName + " (" + entity.get(UserRepositoryImpl.NORMALIZED_USER_NAME) + ")";
			} else {
				uniqueDisplayName = dipslayName;
			}

			entity.put(UNIQUE_DISPLAY_NAME, uniqueDisplayName);
		}

	}

	/**
	 * Fetch the entity name from the entity. Currenctly its only USER
	 * @param entity
	 * @return
	 */
	private String fetchDisplayNameFromEntity(Map<String, String> entity){
		String displayName = entity.get(User.usernameField);
		return  displayName;
	}

}
