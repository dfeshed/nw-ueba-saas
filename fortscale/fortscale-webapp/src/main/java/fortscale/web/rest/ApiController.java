package fortscale.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import fortscale.services.UserServiceFacade;
import fortscale.services.dataentity.DataEntitiesConfig;
import fortscale.services.dataentity.DataEntity;
import fortscale.services.dataqueries.OrderByComparator;
import fortscale.services.dataentity.DataEntity;
import fortscale.services.dataentity.DataEntityField;
import fortscale.services.dataqueries.querydto.DataQueryDTO;
import fortscale.services.dataqueries.querydto.QuerySort;
import fortscale.services.dataqueries.querygenerators.DataQueryRunner;
import fortscale.services.dataqueries.querygenerators.DataQueryRunnerFactory;
import fortscale.services.dataqueries.querygenerators.exceptions.InvalidQueryException;
import fortscale.services.exceptions.InvalidValueException;
import fortscale.services.exceptions.UnknownResourceException;
import fortscale.services.fe.ClassifierService;
import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.BaseController;
import fortscale.web.beans.DataBean;
import fortscale.web.beans.UserIdBean;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/api/**")
public class ApiController extends BaseController {

	@Autowired
	private JdbcOperations impalaJdbcTemplate;

	@Autowired
	private DataQueryRunnerFactory dataQueryRunnerFactory;

    @Autowired
    private ClassifierService classifierService;
	
    @Autowired
	private UserServiceFacade userServiceFacade;

    @Autowired
    private DataEntitiesConfig dataEntitiesConfig;

	private Cache<String, DataBean<List<Map<String, Object>>>> investigateQueryCache;

	/**
	 * Pretty Names for the fields we write to the exported CSV file
	 */
	private Map<String, String> fieldsPrettyNamesExportMap = new HashMap<>();

	/**
	 * Limit for results of 1 query in the cache
	 */
	protected static final Integer CACHE_LIMIT = 200;
	
	public ApiController() {
		// initialize investigate caching 
		//CacheBuilder<String, DataBean<List<Map<String, Object>>>>
		investigateQueryCache = CacheBuilder.newBuilder().expireAfterWrite(15, TimeUnit.MINUTES).maximumSize(300).build();

		// init fields names
		initFieldsName();
	}

	/*
	 * Current implementation assume each field id has the same name for all entities.
	 * since the query send to export is not link to a specific entity, we can't connect the field using the entity.
	 * in the future if we will have to use the same field id with different field names, we will have to change the implementation.
	 */
	private void initFieldsName() {

		try {
			for(DataEntity dataEntity : dataEntitiesConfig.getAllLogicalEntities()) {
				for(DataEntityField dataEntityField : dataEntity.getFields()) {
					fieldsPrettyNamesExportMap.put(dataEntityField.getId(),dataEntityField.getName());
				}
			}
		} catch (Exception e) {
			throw new InvalidValueException("Can't get entities. Error: " + e.getMessage());
		}
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
	public DataBean<List<Map<String, Object>>> investigate(@RequestParam(required=true) String query,
			@RequestParam(required=false) String countQuery,
			@RequestParam(defaultValue="false") boolean useCache,
			@RequestParam(required=false) Integer page, // starting from 0
			@RequestParam(defaultValue="20") Integer pageSize,
			Model model){

		// Add offset and limit according to page
		Integer offsetInLimit = null;

		// the request shouldn't include "limit" if page was sent, but in order to be on the safe size, we check it
		boolean usePaging = (page != null && !query.toLowerCase().contains(" limit "));

		if (usePaging) {
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
				if (usePaging) {
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
		if (usePaging) {
			retBeanForPage = createDataForPage(pageSize, offsetInLimit, retBean);
		}
		
		// cache results if needed, store results with up to 200 rows in the cache to protect memory
		if (useCache && resultsMap.size() <= CACHE_LIMIT) // Query real size is 200.
			investigateQueryCache.put(query, retBean);
			
		return retBeanForPage;
	}

    /**
     * @return List of entities available to the front-end
     */
    @RequestMapping(value="/getEntities", method=RequestMethod.GET)
    @ResponseBody
    @LogException
    public DataBean<List<DataEntity>> getEntities(){
        DataBean<List<DataEntity>> entities = new DataBean<List<DataEntity>>();
        try {
            entities.setData(dataEntitiesConfig.getAllLogicalEntities());
        }
        catch(Exception error){
            throw new InvalidValueException("Can't get entities. Error: " + error.getMessage());
        }

        return entities;
    }


	/**
	 * download query content to a csv file
	 */
	@RequestMapping(value="/exportEvents", method = RequestMethod.GET)
	@LogException
	public void export(@RequestParam(required=true) String dataQuery,
					   @RequestParam(defaultValue = "10000") int numResults,
					   @RequestParam(defaultValue = "true") boolean dumpHeaders,
					   @RequestParam(defaultValue = ",") String delimiter,
					   HttpServletRequest request,
					   HttpServletResponse response) throws IOException {

		DateTime now = DateTime.now();
		response.setContentType("text/csv");
		response.setHeader("content-Disposition",
				String.format("attachment; filename=export_%d%02d%02d.csv", now.getYear(), now.getMonthOfYear(), now.getDayOfMonth()));


		ServletOutputStream output = response.getOutputStream();

		int pageSize = 20;
		int currentPageNum = 0;
		int maxPages = (int) ((numResults-1) / pageSize) + 1;
		List<String> fields = new LinkedList<String>();

		// run the query in pages, keep running in loop until we exhausted all results or reached all pages
		DataBean<List<Map<String, Object>>> page = dataQuery(dataQuery, false, true, currentPageNum, pageSize);
		while (currentPageNum < maxPages && !page.getData().isEmpty()) {

			// if we are on the first page dump the headers row
			if (currentPageNum==0) {
				// write the headers line to the output
				if (!page.getData().isEmpty()) {
					// copy the list of field names to the fields list so we will write them in consistent
					// order for each row, and dump them to the output header row
					List<String> prettyFields = new LinkedList<>();
					for (String field : page.getData().get(0).keySet()) {
						fields.add(field);
						String prettyFieldName = fieldsPrettyNamesExportMap.get(field);
						prettyFields.add(prettyFieldName == null ? field : prettyFieldName);
					}

					if (dumpHeaders) {
						String headerLine = Joiner.on(delimiter).join(prettyFields);
						output.println(headerLine);
					}
				}
			}


			// dump the page content
			convertQueryResultsToText(page.getData(), fields, delimiter, output);
			output.flush();


			// advance query to the next page
			currentPageNum++;
			page = dataQuery(dataQuery, false, true, currentPageNum, pageSize);
		}
	}

	// receives a batch of rows and convert them into csv row that is written to the given output stream. Used by the export data query method
	private void convertQueryResultsToText(List<Map<String, Object>> rows, List<String> fields, String delimiter, ServletOutputStream output) throws IOException {
		SimpleDateFormat sdf = new SimpleDateFormat();
		for (Map<String, Object> row : rows) {
			StringBuilder sb = new StringBuilder();
			for (String field : fields) {
				// convert the field to text
				Object value = row.get(field);
				String strValue = getValueAsString(sdf, field, value);
				strValue.replaceAll(delimiter, "");

				sb.append(strValue);
				sb.append(delimiter);
			}
			// get rid of the last delimiter char (this does not copy the inner array as opposed to deleteCharAt)
			sb.setLength(sb.length() - 1);

			output.println(sb.toString());
		}
	}

	private String getValueAsString(SimpleDateFormat sdf, String field, Object value) {
		String strValue;
		if (value==null) {
			strValue = "";
		} else if (value instanceof Date) {
			strValue = sdf.format(value);
		} else if (value instanceof Long && field.equals("date_time_unix")) {
			strValue = sdf.format((Long) value * 1000);
		} else if (value instanceof Long && field.equals("daytime")) {
			long longVal = (Long) value;
			long seconds = longVal % 60;
			long minutes = longVal / 60;
			long hours = minutes / 60;
			minutes = minutes % 60;
			strValue = String.format("%02d:%02d:%02d", hours, minutes, seconds);
		} else if (value instanceof Double && field.contains("score")) {
			strValue = String.format("%.0f", (Double)value);
		} else {
			strValue = value.toString();
		}
		return strValue;
	}

	/**
     *
     * @param dataQuery	The query object from the client.
     * 						This query shouldn't contain the "LIMIT" and "OFFSET" in case of paging
     * @param useCache		Whether to use cache (optional, defaults to true).
     *
     * @param page			The requested page number (starting from 0). Not mandatory.
     * 						If null no paging will be used
     * @param pageSize		The page size. Not mandatory. "20" by default.
     * 						Relevant only if "page" was requested
     * @return				List of results according to the query
     */
    @RequestMapping(value="/dataQuery", method=RequestMethod.GET)
    @ResponseBody
    @LogException
    public DataBean<List<Map<String, Object>>> dataQuery(@RequestParam(required=true) String dataQuery,
                                                         @RequestParam(defaultValue="false") boolean requestTotal,
                                                         @RequestParam(defaultValue="true") boolean useCache,
                                                         @RequestParam(required=false) Integer page, // starting from 0
                                                         @RequestParam(defaultValue="20") Integer pageSize){

        Logger logger = Logger.getLogger(DataQueryDTO.class);
        ObjectMapper mapper = new ObjectMapper();
        DataQueryDTO dataQueryObject;
        DataQueryRunner dataQueryRunner;
		int location;
		Integer offsetInLimit = null;
		int offsetInQuery = 0;

		//prepare the offset and limit according to page
		if (page != null) {
			if (page < 0) throw new InvalidValueException("Page number must be greater than 0");
			if (pageSize > CACHE_LIMIT) throw new InvalidValueException("Page size must be less than " + CACHE_LIMIT);
			location = page * pageSize;
			offsetInLimit = (location % CACHE_LIMIT);
			offsetInQuery = (location / CACHE_LIMIT) * CACHE_LIMIT; // casting to int creates "floor"
		}

		//will hold the final result
		List<DataBean<List<Map<String, Object>>>> results = new ArrayList<>();

		//will mark how to order the final result
		List<QuerySort> orderByFinalResult;


        try {
            dataQueryObject = mapper.readValue(dataQuery, DataQueryDTO.class);
			orderByFinalResult = dataQueryObject.getSort();
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            throw new InvalidValueException("Couldn't parse dataQuery.");
        }

        // create and run query
        try {
            dataQueryRunner = dataQueryRunnerFactory.getDataQueryRunner(dataQueryObject);
        }
        catch(Exception error){
            throw new InvalidValueException("Couldn't create query generator: " + error.getMessage());
        }


        try {

			//translate the data query if needed (in case he have base entity referring break it to n data queries  for each leaf that extend this base entities )
			List<DataQueryDTO> translatedDataQuery = dataQueryRunner.translateAbstarctDataQuery(dataQueryObject,dataEntitiesConfig);


			//Execute each dto in the translated queries list and combine the result in the end
			for (DataQueryDTO partOfTranslatedQuyre : translatedDataQuery)
			{
				// Generates query
				String query = dataQueryRunner.generateQuery(partOfTranslatedQuyre);

				// Add offset and limit according to page
				if (page != null) {
					query += " LIMIT " + CACHE_LIMIT + " OFFSET " + offsetInQuery;
				}


				boolean cacheUsed=false;
				// check if the query is in the cache before returning results
				if (useCache) {
					DataBean<List<Map<String, Object>>> cachedResults = investigateQueryCache.getIfPresent(query);
					if (cachedResults!=null) {
						if (page != null) {
							// take only relevant page from cache
							results.add(createDataForPage(pageSize, offsetInLimit, cachedResults));
						} else {
							results.add(cachedResults);
						}
						cacheUsed = true;
					}
				}

				if ( (useCache && !cacheUsed) ||!useCache )
				{
					// execute Query
					DataBean<List<Map<String, Object>>> retBean = new DataBean<>();
					List<Map<String, Object>> resultsMap = dataQueryRunner.executeQuery(query);


					//add the type to the entity (ssh , vpn, ad ...)
					if (partOfTranslatedQuyre.getEntities().length > 0) {
						String type = partOfTranslatedQuyre.getEntities()[0].equals("kerberos_logins") ? "AD" : partOfTranslatedQuyre.getEntities()[0];
						for (Map<String, Object> rowMap : resultsMap) {
							rowMap.put("type", type);
						}
					}

					retBean.setData(resultsMap);
					DataBean<List<Map<String, Object>>> retBeanForPage = retBean;

					// TODO: Add a QA authority to the analyst or something, so this isn't returned for all analysts:
					// if (getThisAnalystAuth().getAuthorities())
					Map<String, Object> info = new HashMap<>();
					info.put("query", query);

					int total = resultsMap.size();

					// If the API caller requested a total count, generate a query for it and set the total to that instead of the current results:
					if(requestTotal) {
						String totalQuery = dataQueryRunner.generateTotalQuery(dataQueryObject);
						total = impalaJdbcTemplate.queryForInt(totalQuery);
						info.put("totalQuery", totalQuery);
					}

					retBean.setTotal(total);
					retBeanForPage.setInfo(info);

					// take only relevant page from results
					if (page != null) {
						retBeanForPage = createDataForPage(pageSize, offsetInLimit, retBean);
					}


					// cache results if needed, store results with up to 200 rows in the cache to protect memory
					if (useCache && retBean.getData().size() <= CACHE_LIMIT) // Query real size is 200.
						investigateQueryCache.put(query, retBean);

					results.add(retBeanForPage);

				}


			}



            return collectResults(results,page,offsetInQuery,orderByFinalResult,pageSize);
        }
        catch (InvalidQueryException e) {
            throw new InvalidValueException("Invalid query to parse. Error: " + e.getMessage());
        }

		catch (Exception e)
		{
			throw new InvalidValueException("Invalid query to parse. Error: " + e.getMessage());
		}
    }


	/**
	 * This method responsible to collect list of DataBean results into one union DataBean result
	 * @param results
	 * @return
	 */
	private DataBean<List<Map<String, Object>>> collectResults(List<DataBean<List<Map<String, Object>>>> results, Integer page,int offsetInQuery,List<QuerySort> orderByFinalResult,Integer pageSize )
	{
		DataBean<List<Map<String, Object>>> result = new DataBean<>();


		Map<String, Object> info = new HashMap<>();
		List<Map<String, Object>> unionResult = new ArrayList<>();
		int unionTotal = 0;


		for (DataBean<List<Map<String, Object>>> queryResult : results)
		{
			unionResult.addAll(queryResult.getData());
			info.putAll(queryResult.getInfo());
			unionTotal+=queryResult.getTotal();

		}

		//sort the result depend on orderByFinalResult
		Collections.sort(unionResult,new OrderByComparator(orderByFinalResult));
		if (page != null) {
			result.setData(unionResult.subList(offsetInQuery, pageSize));
		}
		else{
			result.setData(unionResult);
		}
		return result;

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
