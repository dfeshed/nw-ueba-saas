package fortscale.web;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import fortscale.domain.analyst.AnalystAuth;
import fortscale.services.dataentity.DataEntitiesConfig;
import fortscale.services.dataqueries.OrderByComparator;
import fortscale.services.dataqueries.querydto.DataQueryDTO;
import fortscale.services.dataqueries.querydto.QuerySort;
import fortscale.services.dataqueries.querygenerators.DataQueryRunner;
import fortscale.services.dataqueries.querygenerators.DataQueryRunnerFactory;
import fortscale.services.dataqueries.querygenerators.exceptions.InvalidQueryException;
import fortscale.services.exceptions.InvalidValueException;
import fortscale.utils.logging.Logger;
import fortscale.web.beans.DataBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;
import java.util.concurrent.TimeUnit;

public abstract class DataQueryController extends BaseController {
	private static Logger logger = Logger.getLogger(DataQueryController.class);

	/**
	 * Limit for results of 1 query in the cache
	 */
	public static final Integer CACHE_LIMIT = 200;

	@Autowired
	protected JdbcOperations impalaJdbcTemplate;

	@Autowired
	protected DataQueryRunnerFactory dataQueryRunnerFactory;

	@Autowired
	protected DataEntitiesConfig dataEntitiesConfig;

	protected Cache<String, DataBean<List<Map<String, Object>>>> investigateQueryCache;

	public DataQueryController() {
		// initialize investigate caching
		//CacheBuilder<String, DataBean<List<Map<String, Object>>>>
		investigateQueryCache = CacheBuilder.newBuilder().expireAfterWrite(15, TimeUnit.MINUTES).maximumSize(300).build();
	}

	protected DataBean<List<Map<String,Object>>> dataQueryHandler(DataQueryDTO dataQueryObject, boolean requestTotal, boolean useCache, Integer page, Integer pageSize){

		DataQueryRunner dataQueryRunner;
		int location;
		Integer offsetInLimit = null;

		//will hold the final result
		List<DataBean<List<Map<String, Object>>>> results = new ArrayList<>();

		//will mark how to order the final result
		List<QuerySort> orderByFinalResult;


		try {

			orderByFinalResult = dataQueryObject.getSort();
			//prepare the offset and limit according to page
			if (page != null) {
				if (page < 0) throw new InvalidValueException("Page number must be greater than 0");
				if (pageSize > CACHE_LIMIT) throw new InvalidValueException("Page size must be less than " + CACHE_LIMIT);
				location = page * pageSize;
				offsetInLimit = (location % CACHE_LIMIT);
				Integer offsetInQuery = (location / CACHE_LIMIT) * CACHE_LIMIT; // casting to int creates "floor"
				// Add offset and limit according to page
				dataQueryObject.setLimit(CACHE_LIMIT);
				dataQueryObject.setOffset(offsetInQuery);
			}
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
						String type = dataEntitiesConfig.getAllLeafeEntities().get(partOfTranslatedQuyre.getEntities()[0]).getName();
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
			return collectResults(results,page,dataQueryObject.getOffset(),dataQueryObject.getLimit(), orderByFinalResult,pageSize);
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
	 *
	 * @param pageSize		Page size
	 * @param offsetInLimit	The offset of the results (from the cached results)
	 * @param cachedResults	The cached results
	 * @return Results for the specific page
	 */
	protected DataBean<List<Map<String, Object>>> createDataForPage(
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

	/**
	 * This method responsible to collect list of DataBean results into one union DataBean result
	 * @param results
	 * @return
	 */
	protected DataBean<List<Map<String, Object>>> collectResults(List<DataBean<List<Map<String, Object>>>> results, Integer page,int offsetInQuery, Integer limitInQuery ,List<QuerySort> orderByFinalResult,Integer pageSize ) {
		if (results.size() == 1) {
			return results.get(0);
		}

		DataBean<List<Map<String, Object>>> result = new DataBean<>();
		Map<String, Object> info = new HashMap<>();
		List<Map<String, Object>> unionResult = new ArrayList<>();

		for (DataBean<List<Map<String, Object>>> queryResult : results) {
			if (queryResult.getData() != null) {
				unionResult.addAll(queryResult.getData());
			}
			if (queryResult.getInfo() != null) {
				info.putAll(queryResult.getInfo());
			}
		}

		//sort the result depend on orderByFinalResult
		Collections.sort(unionResult, new OrderByComparator(orderByFinalResult));
		if (unionResult.size() > limitInQuery) {
			unionResult = unionResult.subList(offsetInQuery, limitInQuery);
		}
		if (page != null) {
			if (unionResult.size() > pageSize) {
				unionResult = unionResult.subList(offsetInQuery, pageSize);
			}
		}
		result.setData(unionResult);
		result.setTotal(result.getData().size());
		result.setInfo(info);
		return result;
	}

}
