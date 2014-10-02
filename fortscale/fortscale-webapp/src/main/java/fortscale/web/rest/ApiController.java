package fortscale.web.rest;

import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import fortscale.dataqueries.querydto.DataQueryDTO;
import fortscale.dataqueries.querygenerators.DataQueryRunner;
import fortscale.dataqueries.querygenerators.DataQueryRunnerFactory;
import fortscale.dataqueries.querygenerators.exceptions.InvalidQueryException;
import fortscale.services.UserServiceFacade;
import fortscale.services.exceptions.InvalidValueException;
import fortscale.services.fe.ClassifierService;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import fortscale.services.exceptions.UnknownResourceException;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.beans.DataBean;
import fortscale.web.beans.UserIdBean;

@Controller
@RequestMapping("/api/**")
public class ApiController {

	@Autowired
	private JdbcOperations impalaJdbcTemplate;

	@Autowired
	private DataQueryRunnerFactory dataQueryRunnerFactory;

    @Autowired
    private ClassifierService classifierService;
	
    @Autowired
	private UserServiceFacade userServiceFacade;
    
	private Cache<String, DataBean<List<Map<String, Object>>>> investigateQueryCache;

	/**
	 * Limit for results of 1 query in the cache
	 */
	protected static final Integer CACHE_LIMIT = 200;
	
	public ApiController() {
		// initialize investigate caching 
		//CacheBuilder<String, DataBean<List<Map<String, Object>>>>
		investigateQueryCache = CacheBuilder.newBuilder().expireAfterWrite(15, TimeUnit.MINUTES).maximumSize(300).build();
	}
	
	
	@RequestMapping("/**")
	@LogException
    public void unmappedRequest(HttpServletRequest request) {
		StringBuilder builder = new StringBuilder("There is no resource for path ");
        builder.append(request.getRequestURI());
        @SuppressWarnings("unchecked")
		Map<String, String[]> map = request.getParameterMap();
        if(map.size() > 0){
        	builder.append(" parameters: ");
        
	        for(Map.Entry<String, String[]> entry: map.entrySet()){
	        	builder.append(entry.getKey()).append("=").append(StringUtils.join(entry.getValue(), ',')).append(" ");
	        }
        }
        throw new UnknownResourceException(builder.toString());
    }
	
	
	@RequestMapping(value="/normalizedUsernameToId", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<UserIdBean>> normalizedUsernameToId(@RequestParam(required=true) String normalizedUsername) {
		DataBean<List<UserIdBean>> ret = new DataBean<List<UserIdBean>>();
		List<UserIdBean> idList = new LinkedList<UserIdBean>();
			
		// translate the normalized username to user id
		String userId = userServiceFacade.findByNormalizedUserName(normalizedUsername);
		idList.add(new UserIdBean(userId));
		
		ret.setData(idList);
		ret.setTotal(idList.size());
		return ret;
	}
	
	
	/**
	 * 
	 * @param query			The SQL query from the client. 
	 * 						This query shouldn't contain the "LIMIT" and "OFFSET" in case of paging
	 * @param countQuery	The count query. Not mandatory.
	 * @param useCache		"True" if we wish to use existing results from cache (if exist).
	 * 						Not mandatory. "False" by default
	 * @param page			The requested page number (starting from 0). Not mandatory. 
	 * 						If null no paging will be used
	 * @param pageSize		The page size. Not mandatory. "20" by default. 
	 * 						Relevant only if "page" was requested
	 * @param model			The model
	 * @return				List of results according to the query and paging
	 */
	@RequestMapping(value="/investigate", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	// TODO this method should be deleted after moving everything to use investigateObject
	public DataBean<List<Map<String, Object>>> investigate(@RequestParam(required=true) String query,
			@RequestParam(required=false) String countQuery,
			@RequestParam(defaultValue="false") boolean useCache,
			@RequestParam(required=false) Integer page, // starting from 0
			@RequestParam(defaultValue="20") Integer pageSize,
			Model model){

		// Add offset and limit according to page
		Integer offsetInLimit = null;
		if (page != null) {
			if (page < 0) throw new InvalidValueException("Page number must be greater than 0");
			if (pageSize > CACHE_LIMIT) throw new InvalidValueException("Page size must be less than " + CACHE_LIMIT);
			int location = page * pageSize;
			offsetInLimit = (location % CACHE_LIMIT);
			int offsetInQuery = (location / CACHE_LIMIT) * CACHE_LIMIT; // casting to int creates "floor"
			query += " LIMIT " + CACHE_LIMIT + " OFFSET " + offsetInQuery;
		}

		// check if the query is in the cache before returning results
		if (useCache) {
			DataBean<List<Map<String, Object>>> cachedResults = investigateQueryCache.getIfPresent(query);
			if (cachedResults!=null) {
				if (page != null) {
					// take only relevant page from cache
					return createDataForPage(pageSize, offsetInLimit, cachedResults);
				} else {
					return cachedResults;
				}
			}
		}
		
		// perform the query
		DataBean<List<Map<String, Object>>> retBean = new DataBean<>();
		List<Map<String, Object>> resultsMap = impalaJdbcTemplate.query(query, new ColumnMapRowMapper());
		int total = resultsMap.size();
		if(countQuery != null) {
			total = impalaJdbcTemplate.queryForInt(countQuery);
		}
		retBean.setData(resultsMap);
		retBean.setTotal(total);
		DataBean<List<Map<String, Object>>> retBeanForPage = retBean;

		// take only relevant page from results
		if (page != null) {
			retBeanForPage = createDataForPage(pageSize, offsetInLimit, retBean);
		}
		
		// cache results if needed, store results with up to 200 rows in the cache to protect memory
		if (useCache && resultsMap.size() <= CACHE_LIMIT) // Query real size is 200.
			investigateQueryCache.put(query, retBean);
			
		return retBeanForPage;
	}

	/**
	 *
	 * @param queryObject	The query object from the client.
	 * 						This query shouldn't contain the "LIMIT" and "OFFSET" in case of paging
	 * @param page			The requested page number (starting from 0). Not mandatory.
	 * 						If null no paging will be used
	 * @param pageSize		The page size. Not mandatory. "20" by default.
	 * 						Relevant only if "page" was requested
	 * @param model			The model
	 * @return				List of results according to the query and paging
	 */
	@RequestMapping(value="/investigateObject", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<Map<String, Object>>> investigateObject(@RequestParam(required=true) String queryObject,
					@RequestParam(defaultValue="true") boolean useCache,
					@RequestParam(required=false) Integer page, // starting from 0
					@RequestParam(defaultValue="20") Integer pageSize,
					Model model){


		// TODO - create DTO from queryObject
		DataQueryDTO temporary = new DataQueryDTO();


		// create and run query
		DataQueryRunner dataQueryRunner = dataQueryRunnerFactory.getDataQueryRunner(temporary);
		try {
			// Generates query
			String query = dataQueryRunner.generateQuery(temporary);

			// Add offset and limit according to page
			Integer offsetInLimit = null;
			if (page != null) {
				if (page < 0) throw new InvalidValueException("Page number must be greater than 0");
				if (pageSize > CACHE_LIMIT) throw new InvalidValueException("Page size must be less than " + CACHE_LIMIT);
				int location = page * pageSize;
				offsetInLimit = (location % CACHE_LIMIT);
				int offsetInQuery = (location / CACHE_LIMIT) * CACHE_LIMIT; // casting to int creates "floor"
				query += " LIMIT " + CACHE_LIMIT + " OFFSET " + offsetInQuery;
			}

			// check if the query is in the cache before returning results
			if (useCache) {
				DataBean<List<Map<String, Object>>> cachedResults = investigateQueryCache.getIfPresent(query);
				if (cachedResults!=null) {
					if (page != null) {
						// take only relevant page from cache
						return createDataForPage(pageSize, offsetInLimit, cachedResults);
					} else {
						return cachedResults;
					}
				}
			}


			// execute Query
			DataBean<List<Map<String, Object>>> retBean = dataQueryRunner.executeQuery(query);

			DataBean<List<Map<String, Object>>> retBeanForPage = retBean;

			// take only relevant page from results
			if (page != null) {
				retBeanForPage = createDataForPage(pageSize, offsetInLimit, retBean);
			}

			// cache results if needed, store results with up to 200 rows in the cache to protect memory
			if (useCache && retBean.getData().size() <= CACHE_LIMIT) // Query real size is 200.
				investigateQueryCache.put(query, retBean);

			return retBeanForPage;
		}
		catch (InvalidQueryException e) {
			throw new InvalidValueException("Invalid query to parse");
		}

	}

	/**
	 *
	 * @param pageSize		Page size
	 * @param offsetInLimit	The offset of the results (from the cached results)
	 * @param cachedResults	The cached results
	 * @return Results for the specific page
	 */
	private DataBean<List<Map<String, Object>>> createDataForPage(
					Integer pageSize, Integer offsetInLimit,
					DataBean<List<Map<String, Object>>> cachedResults) {

		DataBean<List<Map<String, Object>>> retBean = new DataBean<>();
		if (offsetInLimit < cachedResults.getData().size()) {
			retBean.setData(cachedResults.getData().subList(offsetInLimit, Math.min(offsetInLimit + pageSize, cachedResults.getData().size())));
			retBean.setTotal(cachedResults.getTotal());
		} else {
			retBean.setData(Collections.<Map<String,Object>>emptyList());
			retBean.setTotal(0);
		}
		return retBean;
	}

	@RequestMapping(value="/getLatestRuntime", method=RequestMethod.GET)
    @ResponseBody
    @LogException
    public Long getLatestRuntime(@RequestParam(required=true) String tableName,	Model model){
        return classifierService.getLatestRuntime(tableName);
    }
		
	@RequestMapping(value="/selfCheck", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public Date selfCheck(){
		return new Date();
	}
	
}
