package fortscale.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import fortscale.common.dataentity.DataEntitiesConfig;
import fortscale.services.UserServiceFacade;
import fortscale.common.dataentity.DataEntity;
import fortscale.common.dataentity.DataEntityField;

import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.beans.DataBean;
import fortscale.web.beans.UserIdBean;
import org.apache.commons.lang.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/api/**")
public class ApiController{

	private static Logger logger = Logger.getLogger(ApiController.class);


	@Autowired
	protected DataEntitiesConfig dataEntitiesConfig;


    @Autowired
	private UserServiceFacade userServiceFacade;



	/**
	 *  The format of the dates in the exported file
	 */
	@Value("${export.data.date.format:MMM dd yyyy HH:mm:ss 'GMT'Z}")
	private String exportDateFormat;


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
        throw new RuntimeException(builder.toString());
    }
	
	
	@RequestMapping(value="/normalizedUsernameToId", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<UserIdBean>> normalizedUsernameToId(@RequestParam(required=true) String normalizedUsername) {
		DataBean<List<UserIdBean>> ret = new DataBean<List<UserIdBean>>();
		List<UserIdBean> idList = new LinkedList<UserIdBean>();
			
		// translate the normalized username to user id
//		String userId = userServiceFacade.findByNormalizedUserName(normalizedUsername);
		String userId = "Shay";
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
//	@RequestMapping(value="/investigate", method=RequestMethod.GET)
//	@ResponseBody
//	@LogException
//	public DataBean<List<Map<String, Object>>> investigate(@RequestParam(required=true) String query,
//			@RequestParam(required=false) String countQuery,
//			@RequestParam(defaultValue="false") boolean useCache,
//			@RequestParam(required=false) Integer page, // starting from 0
//			@RequestParam(defaultValue="20") Integer pageSize,
//			Model model){
//
//		// Add offset and limit according to page
//		Integer offsetInLimit = null;
//
//		// the request shouldn't include "limit" if page was sent, but in order to be on the safe size, we check it
//		boolean usePaging = (page != null && !query.toLowerCase().contains(" limit "));
//
//		if (usePaging) {
//			if (page < 0) throw new InvalidValueException("Page number must be greater than 0");
//			if (pageSize > CACHE_LIMIT) throw new InvalidValueException("Page size must be less than " + CACHE_LIMIT);
//			int location = page * pageSize;
//			offsetInLimit = (location % CACHE_LIMIT);
//			int offsetInQuery = (location / CACHE_LIMIT) * CACHE_LIMIT; // casting to int creates "floor"
//			query += " LIMIT " + CACHE_LIMIT + " OFFSET " + offsetInQuery;
//		}
//
//		// check if the query is in the cache before returning results
//		if (useCache) {
//			DataBean<List<Map<String, Object>>> cachedResults = investigateQueryCache.getIfPresent(query);
//			if (cachedResults!=null) {
//				if (usePaging) {
//					// take only relevant page from cache
//					return createDataForPage(pageSize, offsetInLimit, cachedResults);
//				} else {
//					return cachedResults;
//				}
//			}
//		}
//
//		// perform the query
//		DataBean<List<Map<String, Object>>> retBean = new DataBean<>();
//		List<Map<String, Object>> resultsMap = impalaJdbcTemplate.query(query, new ColumnMapRowMapper());
//		int total = resultsMap.size();
//		if(countQuery != null) {
//			total = impalaJdbcTemplate.queryForObject(countQuery, Integer.class);
//		}
//		retBean.setData(resultsMap);
//		retBean.setTotal(total);
//		DataBean<List<Map<String, Object>>> retBeanForPage = retBean;
//
//		// take only relevant page from results
//		if (usePaging) {
//			retBeanForPage = createDataForPage(pageSize, offsetInLimit, retBean);
//		}
//
//		// cache results if needed, store results with up to 200 rows in the cache to protect memory
//		if (useCache && resultsMap.size() <= CACHE_LIMIT) // Query real size is 200.
//			investigateQueryCache.put(query, retBean);
//
//		return retBeanForPage;
//	}

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
            throw new RuntimeException("Can't get entities. Error: " + error.getMessage());
        }

        return entities;
    }


	/**
	 * download query content to a csv file
	 */
//	@RequestMapping(value="/exportEvents", method = RequestMethod.GET)
//	@LogException
//	public void export(@RequestParam(required=true) String dataQuery,
//					   @RequestParam(defaultValue = "10000") int numResults,
//					   @RequestParam(defaultValue = "true") boolean dumpHeaders,
//					   @RequestParam(defaultValue = ",") String delimiter,
//			           @RequestParam(required = false) String timezoneOffsetMins,
//					   @RequestParam(required = false) String returnFields,
//					   HttpServletRequest request,
//					   HttpServletResponse response, Locale locale) throws IOException {
//
//
//		DataQueryDTO dataQueryObject;
//
//		int pageSize = CACHE_LIMIT;
//		int currentPageNum = 0;
//		int maxPages = (int) ((numResults-1) / pageSize) + 1;
//		List<String> fields = new LinkedList<String>();
//
//		ObjectMapper mapper = new ObjectMapper();
//		try {
//			dataQueryObject = mapper.readValue(dataQuery, DataQueryDTOImpl.class);
//		} catch (Exception e) {
//			logger.error(e.getMessage(),e);
//			throw new InvalidValueException("Couldn't parse dataQuery.");
//		}
//
//		DateTime now = DateTime.now();
//		response.setContentType("text/csv; charset=UTF-8");
//		response.setCharacterEncoding("UTF-8");
//		response.setHeader("content-Disposition",
//				String.format("attachment; filename=export_%s_%d%02d%02d_%d.csv", dataEntitiesConfig.getEntityFromOverAllCache(dataQueryObject.getEntities()[0]).getId(),
//						now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), now.getMillis()));
//
//		PrintWriter writer = response.getWriter();
//
//		// build a list of the output fields
//		if (StringUtils.isNotEmpty(returnFields)) {
//			// get the list of fields from the REST parameter
//			fields.addAll(Arrays.asList(returnFields.split(",")));
//		} else {
//			// get the list of fields from the entities configuration, using the main entity in the query
//			// if the query contains several entities take the non-user entity, otherwise take the single entity
//			String entity = dataQueryObject.getEntities()[0];
//			if (dataQueryObject.getEntities().length>1) {
//				for (String item : dataQueryObject.getEntities()) {
//					if (!"users".equals(item)) {
//						entity = item;
//						break;
//					}
//				}
//			}
//
//			// get the default fields for that entity
//			DataEntity dataEntity = dataEntitiesConfig.getEntityFromOverAllCache(entity);
//			if (dataEntity==null) {
//				logger.error("Could not get metadata for entity {}", entity);
//				throw new InvalidValueException("Query with invalid entity type");
//			}
//
//			for (DataEntityField field : dataEntity.getFields()) {
//				if (field.getIsDefaultEnabled() && (field.getAttributes()==null || !field.getAttributes().contains("internal"))) {
//					fields.add(field.getId());
//				}
//			}
//		}
//
//
//		// run the query in pages, keep running in loop until we exhausted all results or reached all pages
//		DataBean<List<Map<String, Object>>> page = dataQueryHandler(dataQueryObject, false, true, currentPageNum, pageSize);
//		while (currentPageNum < maxPages && !page.getData().isEmpty()) {
//
//			// if we are on the first page dump the headers row
//			if (currentPageNum==0 && dumpHeaders) {
//				// copy the list of field names to the fields list so we will write them in consistent
//				// order for each row, and dump them to the output header row
//				List<String> prettyFields = new LinkedList<>();
//				for (String field : fields)
//					prettyFields.add(getFieldDisplayName(field, dataQueryObject));
//				String headerLine = Joiner.on(delimiter).join(prettyFields);
//				writer.println(headerLine);
//			}
//
//			// dump the page content
//			convertQueryResultsToText(page.getData(), fields, delimiter, writer, locale, timezoneOffsetMins);
//			writer.flush();
//
//			// advance query to the next page
//			currentPageNum++;
//			page = dataQueryHandler(dataQueryObject, false, true, currentPageNum, pageSize);
//		}
//	}
//
//	/**
//	 * Get the field name in an entity and return the display name for it. The field format might be either
//	 * "<field id>" or "<entity id>.<field id>". If entity if was not passed, we will use the first entity from
//	 * the query that has that field.
//	 */
//	private String getFieldDisplayName(String field, DataQueryDTO dataQueryObject) {
//		String entity = (field.contains(".")? field.split("\\.")[0] : null);
//		String fieldId = (field.contains(".")? field.split("\\.")[1] : field);
//
//		// get the relevant data entity
//		DataEntity dataEntity = null;
//		if (entity!=null) {
//			dataEntity = dataEntitiesConfig.getEntityFromOverAllCache(entity);
//		} else {
//			for (String dataEntityId : dataQueryObject.getEntities()) {
//				dataEntity = dataEntitiesConfig.getEntityFromOverAllCache(dataEntityId);
//				if (dataEntity!=null && dataEntity.getField(fieldId)!=null && dataEntity.getField(fieldId).getName()!=null)
//					break;;
//			}
//		}
//
//		// get the field name from the data entity
//		DataEntityField dataEntityField = dataEntity.getField(fieldId);
//		if (dataEntityField!=null && dataEntityField.getName()!=null)
//			return dataEntityField.getName();
//		else
//			return fieldId;
//
//	}
//
//	// receives a batch of rows and convert them into csv row that is written to the given output stream. Used by the export data query method
//	private void convertQueryResultsToText(List<Map<String, Object>> rows, List<String> fields, String delimiter, PrintWriter output, Locale locale, String timezoneOffsetMinsStr) throws IOException {
//		SimpleDateFormat sdf = createSimpleDateFormat(locale, timezoneOffsetMinsStr);
//		for (Map<String, Object> row : rows) {
//			StringBuilder sb = new StringBuilder();
//			for (String field : fields) {
//				// convert the field to text
//				String fieldName = (field.contains(".")? field.substring(field.indexOf(".")+1): field);
//
//				Object value = row.get(fieldName);
//				String strValue = getValueAsString(sdf, field, value);
//
//				sb.append(escapeValue(strValue));
//				sb.append(delimiter);
//			}
//			// get rid of the last delimiter char (this does not copy the inner array as opposed to deleteCharAt)
//			sb.setLength(sb.length() - 1);
//
//			output.println(sb.toString());
//		}
//	}
//
//	/**
//	 * Escapes string value of a field to be used in csv.
//	 * 1. If the value contains a comma, newline or double quote, then the String value should be returned enclosed in double quotes.
//	 * 2. Any double quote characters in the value should be escaped with another double quote.
//	 * 3. If the value does not contain a comma, newline or double quote, then the String value should be returned unchanged.
//	 */
//	private String escapeValue(String value) {
//		boolean encloseInQuotes = ( value.contains(",") || value.contains("\n") || value.contains("\"") );
//
//		// escape double quotes with additional double quotes
//		value = value.replaceAll("\"", "\"\"");
//
//		if (encloseInQuotes)
//			return String.format("\"%s\"", value);
//		else
//			return  value;
//	}
//
//	/**
//	 * Create date formatter according to locale and timezone
//	 * @param locale locale Add a comment to this line
//	 * @param timezoneOffsetMinsStr timezone offset in minutes
//	 * @return the formatter
//	 */
//	private SimpleDateFormat createSimpleDateFormat(Locale locale, String timezoneOffsetMinsStr) {
//
//		SimpleDateFormat sdf = new SimpleDateFormat(exportDateFormat, locale);
//
//		// calculate timezone according to offset in minutes
//		int timezoneOffset = 0;
//		int minutes = 0;
//		if (timezoneOffsetMinsStr != null) {
//			try {
//				Integer timezoneOffsetMins = Integer.valueOf(timezoneOffsetMinsStr);
//				timezoneOffset = timezoneOffsetMins / 60;
//				minutes = Math.abs(timezoneOffsetMins % 60);
//			} catch (NumberFormatException e) {
//				logger.warn("Failed to parse timezone offset {}", timezoneOffsetMinsStr);
//			}
//		}
//
//		// set timezone
//		String timeZone = String.format("GMT%s%02d:%02d", (timezoneOffset >= 0 ? "+" : ""), timezoneOffset, minutes);
//		sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
//
//		return sdf;
//				}
//
//	private String getValueAsString(SimpleDateFormat sdf, String field, Object value) {
//		String strValue;
//		if (value==null) {
//			strValue = "";
//		} else if (value instanceof Date) {
//			strValue = sdf.format(value);
//		} else if (value instanceof Long && field.equals("date_time_unix")) {
//			strValue = sdf.format(TimestampUtils.convertToMilliSeconds((Long)value));
//		} else if (value instanceof Long && field.equals("daytime")) {
//			long longVal = (Long) value;
//			long seconds = longVal % 60;
//			long minutes = longVal / 60;
//			long hours = minutes / 60;
//			minutes = minutes % 60;
//			strValue = String.format("%02d:%02d:%02d", hours, minutes, seconds);
//		} else if (value instanceof Double && field.contains("score")) {
//			strValue = String.format("%.0f", (Double)value);
//		} else {
//			strValue = value.toString();
//		}
//		return strValue;
//	}
//
//	/**
//     *
//     * @param dataQuery	The query object from the client.
//     * 						This query shouldn't contain the "LIMIT" and "OFFSET" in case of paging
//     * @param useCache		Whether to use cache (optional, defaults to true).
//     *
//     * @param page			The requested page number (starting from 0). Not mandatory.
//     * 						If null no paging will be used
//     * @param pageSize		The page size. Not mandatory. "20" by default.
//     * 						Relevant only if "page" was requested
//     * @return				List of results according to the query
//     */
//    @RequestMapping(value="/dataQuery", method=RequestMethod.GET)
//    @ResponseBody
//    @LogException
//    public DataBean<List<Map<String, Object>>> dataQuery(@RequestParam(required=true) String dataQuery,
//                                                         @RequestParam(defaultValue="false") boolean requestTotal,
//                                                         @RequestParam(defaultValue="true") boolean useCache,
//                                                         @RequestParam(required=false) Integer page, // starting from 0
//                                                         @RequestParam(defaultValue="20") Integer pageSize){
//		ObjectMapper mapper = new ObjectMapper();
//		try {
//			DataQueryDTO dataQueryObject = mapper.readValue(dataQuery, DataQueryDTOImpl.class);
//		return dataQueryHandler(dataQueryObject, requestTotal, useCache, page, pageSize);
//		} catch (Exception e) {
//			logger.error(e.getMessage(),e);
//			throw new InvalidValueException("Couldn't parse dataQuery.");
//		}
//    }
//
//	@RequestMapping(value="/selfCheck", method=RequestMethod.GET)
//	@ResponseBody
//	@LogException
//	public Date selfCheck(){
//		return new Date();
//	}
//
}
