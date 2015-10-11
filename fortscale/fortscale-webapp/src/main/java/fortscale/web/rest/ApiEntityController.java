package fortscale.web.rest;

import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.core.dao.UserRepositoryCustom;
import fortscale.services.UserService;
import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.DataQueryController;
import fortscale.web.beans.DataBean;
import fortscale.web.rest.Utils.ApiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/entities")
public class ApiEntityController extends DataQueryController{

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

		DataBean<List<Map<String, String>>> entities = new DataBean<>();

		if ((entityName == null || entityName.isEmpty()) && (entityId == null || entityId.isEmpty())) {
			logger.debug("Received empty entity name and empty entity id when trying to read entity list");
			return entities;
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


		// Read users
		if (entityName != null) {
			entityName = ApiUtils.stringReplacement(entityName);
			entityName = entityName.replace("*", "\\*");
			entities.setData(usersDao.getUsersByPrefix(entityName, pageRequest));
		} else if (entityId != null) {
			entities.setData(usersDao.getUsersByIds(entityId, pageRequest));
		}

		return  entities;
	}

}
