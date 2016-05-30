package fortscale.web.rest;

import au.com.bytecode.opencsv.CSVWriter;
import fortscale.domain.core.*;
import fortscale.domain.core.dao.rest.Alerts;
import fortscale.services.AlertsService;
import fortscale.services.EvidencesService;
import fortscale.services.LocalizationService;
import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.BaseController;
import fortscale.web.beans.DataBean;
import fortscale.web.exceptions.InvalidParameterException;
import fortscale.web.rest.Utils.ResourceNotFoundException;
import fortscale.web.rest.entities.AlertStatisticsEntity;
import fortscale.utils.spring.SpringPropertiesUtil;
import org.datanucleus.store.types.backed.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/api/alerts")
public class ApiAlertController extends BaseController {



	private static final int DEFAULT_PAGE_SIZE = 20;
	public static final String ALERT_NAME = "Alert Name";
	public static final String ENTITY_NAME_COLUMN_NAME = "Entity Name";
	public static final String START_TIME_COLUMN_NAME = "Start Time";
	public static final String NUMBER_OF_INDICATORS_COLUMN_NAME = "# of Indicators";
	public static final String STATUS_COLUMN_NAME = "Status";
	public static final String SEVERITY_COLUMN_NAME = "Severity";
	public static final String ALERTS_CSV_FILE_NAME = "alerts.csv";
	public static final String CSV_CONTENT_TYPE = "text/plain; charset=utf-8";
	private static Logger logger = Logger.getLogger(ApiAlertController.class);


	public static final String OPEN_STATUS = "Open";
	private static final String TIME_STAMP_START = "startDate";

	private static final String EVIDENCE_MESSAGE = "fortscale.message.evidence.";

	@Autowired
	private AlertsService alertsDao;

	@Autowired
	private EvidencesService evidencesDao;

	@Autowired
	LocalizationService localizationService;

    @Autowired
    private AlertsService alertsService;
	/**
	 *  The format of the dates in the exported file
	 */
	@Value("${export.data.date.format:MMM dd yyyy HH:mm:ss 'GMT'Z}")
	private String exportDateFormat;


	/**
	 * the api to return all alerts in export format
	 * @param httpRequest
	 * @param httpResponse
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET , value = "/export")
	@LogException
	public void exportAlertsToCsv(HttpServletRequest httpRequest, HttpServletResponse httpResponse, Locale locale,
								  @RequestParam(required=false, value = "sort_field") String sortField,
								  @RequestParam(required=false, value = "sort_direction") String sortDirection,
								  @RequestParam(required=false, value = "page") Integer fromPage,
								  @RequestParam(required=false, value = "severity") String severity,
								  @RequestParam(required=false, value = "status") String status,
								  @RequestParam(required=false, value = "feedback") String feedback,
								  @RequestParam(required=false, value = "alert_start_range") String alertStartRange,
								  @RequestParam(required=false, value = "entity_name") String entityName,
								  @RequestParam(required=false, value = "entity_tags") String entityTags,
								  @RequestParam(required=false, value = "entity_id") String entityId,
								  @RequestParam(required=false, value = "total_severity_count") boolean totalSeverityCount,
								  @RequestParam(required=false, value = "indcator_types") String indicatorTypes

	)  throws  Exception{

		/*
			Set response type as CSV
		 */
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"",
				ALERTS_CSV_FILE_NAME);
		httpResponse.setHeader(headerKey, headerValue);
		httpResponse.setContentType(CSV_CONTENT_TYPE);


		int pageSize = 10000; //Fetch only first 10000 rows :) (pageSize 0 is no longer accepted by PageRequest)
		DataBean<List<Alert>> alerts= getAlerts(httpRequest, httpResponse, sortField, sortDirection, pageSize,
												fromPage, severity,	status, feedback, alertStartRange,entityName,
												entityTags, entityId, totalSeverityCount, indicatorTypes);


		CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(httpResponse
				.getOutputStream()));

		//Add headers - csvWriter.writeNext();
		String[] tableTitleRow = {ALERT_NAME,
				ENTITY_NAME_COLUMN_NAME,
				START_TIME_COLUMN_NAME,
				NUMBER_OF_INDICATORS_COLUMN_NAME,
				STATUS_COLUMN_NAME,
				SEVERITY_COLUMN_NAME};

		csvWriter.writeNext(tableTitleRow);
		SimpleDateFormat simpleDateFormat = createSimpleDateFormat(locale);

		//Add each row
		for (Alert alert : alerts.getData()){
			String evidencesSizeAsString = alert.getEvidences() ==null ? "" : alert.getEvidences().size()+"" ;

            //Decorate alert name
            String localizedName = localizationService.getAlertName(alert);
            String alertName = String.format("%s (%s)",localizedName, alert.getTimeframe().name());

            String[] alertRow = {alertName,
					alert.getEntityName(),
					simpleDateFormat.format(new Date(alert.getStartDate())),
					evidencesSizeAsString,
					alert.getStatus().name(),
					alert.getSeverity().name()};
			csvWriter.writeNext(alertRow);

		}

		csvWriter.close();

	}

	/**
	 * Create date formatter according to locale and timezone
	 * @param locale locale Add a comment to this line
	 * @return the formatter
	 */
	private SimpleDateFormat createSimpleDateFormat(Locale locale) {

		SimpleDateFormat sdf = new SimpleDateFormat(exportDateFormat, locale);
		return sdf;
	}

	private static final String ANOMALY_TYPES_MAJOR_DELIMITER = "@@@";
	private static final String ANOMALY_TYPES_MINOR_DELIMITER = "@@";

	/**
	 * Takes indicatorTypes as revieved from the front end, and parses it into  List<DataSourceAnomalyTypePair>
	 * @param indicatorTypes string received from the front end. A csv of parseble values,
	 *                          representing data source id to list of anomaly type field names
	 * @return a List object with parsed values
	 */
	private List<DataSourceAnomalyTypePair> digestIndicatorTypes(String indicatorTypes) {
        if (indicatorTypes == null || indicatorTypes.length() ==  0){
            return  null;
        }
		List<DataSourceAnomalyTypePair> anomalyTypesList = new ArrayList<>();

		Arrays.asList(indicatorTypes.split(",")).forEach(indicatorTypeString -> {

			String[] breakdown = indicatorTypeString.split(ANOMALY_TYPES_MAJOR_DELIMITER);

			String dataSourceId = breakdown[0];
			List<String> anomalyTypes = new ArrayList<>();

            if(breakdown.length > 1) { //User select data source + indicator
                Arrays.asList(breakdown[1].split(ANOMALY_TYPES_MINOR_DELIMITER)).forEach(anomalyType -> {
                    anomalyTypesList.add(new DataSourceAnomalyTypePair(dataSourceId, anomalyType));
                });
            } else { // User select only data source, and need all the indicator for the data source
                anomalyTypesList.add(new DataSourceAnomalyTypePair(dataSourceId, null));
            }

		});
		return anomalyTypesList;
	}

	/**
	 * the api to return all alerts. GET: /api/alerts
	 * @param httpRequest
	 * @param httpResponse
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	@LogException
	public @ResponseBody
	DataBean<List<Alert>> getAlerts(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
										  @RequestParam(required=false, value = "sort_field") String sortField,
										  @RequestParam(required=false, value = "sort_direction") String sortDirection,
										  @RequestParam(required=false, value = "size")  Integer size,
										  @RequestParam(required=false, value = "page") Integer fromPage,
										  @RequestParam(required=false, value = "severity") String severity,
										  @RequestParam(required=false, value = "status") String status,
										  @RequestParam(required=false, value = "feedback") String feedback,
										  @RequestParam(required=false, value = "alert_start_range") String alertStartRange,
										  @RequestParam(required=false, value = "entity_name") String entityName,
										  @RequestParam(required=false, value = "entity_tags") String entityTags,
										  @RequestParam(required=false, value = "entity_id") String entityId,
										  @RequestParam(required=false, value = "total_severity_count") boolean totalSeverityCount,
									      @RequestParam(required=false, value = "indicator_types") String indicatorTypes ) {

		Sort sortByTSDesc;
		Sort.Direction sortDir = Sort.Direction.DESC;
		if (sortField != null) {
			if (sortDirection != null){
				sortDir = Sort.Direction.valueOf(sortDirection);
			}
			sortByTSDesc = new Sort(new Sort.Order(sortDir, sortField));


			 // If there the api get sortField, which different from TIME_STAMP_START, add
			 // TIME_STAMP_START as secondary sort
			 if (!TIME_STAMP_START.equals(sortField)) {
				 Sort secondarySort = new Sort(new Sort.Order(Sort.Direction.DESC, TIME_STAMP_START));
				 sortByTSDesc = sortByTSDesc.and(secondarySort);
			 }
		} else {
			sortByTSDesc = new Sort(new Sort.Order(Sort.Direction.DESC, TIME_STAMP_START));
		}
		//if pageForMongo is not set, get first pageForMongo
		//Mongo pages start with 0. While on the API the first page is 1.
		int pageForMongo;
		if (fromPage == null) {
			pageForMongo = 0;
		} else {
			pageForMongo = fromPage -1;
		}
		if (size == null){
			size = DEFAULT_PAGE_SIZE;
		}

		Alerts alerts;
		Long count;
		Map<Severity, Long> severitiesCount;
		severitiesCount = null;

		PageRequest pageRequest = new PageRequest(pageForMongo, size, sortByTSDesc);



        List<DataSourceAnomalyTypePair> anomalyTypes = digestIndicatorTypes(indicatorTypes);
		//if no filter, call findAll()
		if (severity == null && status == null  && feedback == null &&  alertStartRange == null &&
				entityName == null && entityTags == null && entityId == null && indicatorTypes == null) {
			alerts = alertsDao.findAll(pageRequest);
			//total count of the total items in query.
			count = alertsDao.count(pageRequest);

		} else {

			// Get a list of evidence ids that qualify by anomalyTypeFieldName
//			if (indicatorTypes != null) {
//				indicatorIds = evidencesDao.getEvidenceIdsByAnomalyTypeFiledNames(digestIndicatorTypes(indicatorTypes));
//			}

 			alerts = alertsDao.findAlertsByFilters(pageRequest, severity, status, feedback, alertStartRange, entityName,
					entityTags, entityId, anomalyTypes);
			count = alertsDao.countAlertsByFilters(pageRequest, severity, status, feedback, alertStartRange, entityName,
					entityTags, entityId, anomalyTypes);
		}

		for (Alert alert : alerts.getAlerts()) {
			updateEvidenceFields(alert);
		}

		DataBean<List<Alert>> entities = new DataBean<>();
		entities.setData(alerts.getAlerts());

		entities.setTotal(count.intValue());
		entities.setOffset(pageForMongo * size);

		if (totalSeverityCount) {
			Map<String, Object> info = new HashMap<>();
			info.put("total_severity_count", countSeverities(pageRequest, severity, status, feedback, alertStartRange,
					entityName, entityTags, entityId, anomalyTypes));
			entities.setInfo(info);
		}
		return entities;
	}



	private Map<Severity, Integer> countSeverities (PageRequest pageRequest, String severity, String status,
												 String feedback, String alertStartRange, String entityName,
												 String entityTags, String entityId, List<DataSourceAnomalyTypePair> anomalyTypes) {
		Map<Severity, Integer> severitiesCount = new HashMap<>();
		Map<String, Integer> severitiesCountResult = alertsDao.groupCount(SEVERITY_COLUMN_NAME.toLowerCase(),severity, status, feedback, alertStartRange, entityName,entityTags, entityId, anomalyTypes);
		for (Severity iSeverity : Severity.values()) {
			Integer statusCount = severitiesCountResult.get(iSeverity.name());
			if (statusCount == null){
				statusCount = 0;
			}
			severitiesCount.put(iSeverity, statusCount);
		}

		return severitiesCount;
	}


	/**
	 * Statistics about system alerts
	 * @param  timeRange  - return alert from the timeRange last days
	 * @return
	 */
	@RequestMapping(value="/statistics", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<AlertStatisticsEntity> getStatistics(
			@RequestParam(required=true, value = "start_range") String timeRange)
	{

		AlertStatisticsEntity results = new AlertStatisticsEntity(	);

		//Add statuses
		Map<String,Integer> statusCounts = alertsDao.groupCount(STATUS_COLUMN_NAME.toLowerCase(), null, null, null, timeRange,null, null,null, null);
		results.setAlertStatus(statusCounts);

		//Add severities
		Map<String,Integer> severityCounts = alertsDao.groupCount(SEVERITY_COLUMN_NAME.toLowerCase(), null, OPEN_STATUS, null, timeRange,null, null, null, null);

		results.setAlertOpenSeverity(severityCounts);


		DataBean<AlertStatisticsEntity> toReturn = new DataBean<AlertStatisticsEntity>();
		toReturn.setData(results);

		return toReturn;
	}

	private void updateEvidenceFields(Alert alert){
		if(alert != null && alert.getEvidences() != null) {
			for (Evidence evidence : alert.getEvidences()) {
				if (evidence != null && evidence.getAnomalyTypeFieldName() != null) {

					String name = localizationService.getIndicatorName(evidence);
					String anomalyType = (name!=null ? name.toString(): evidence.getAnomalyTypeFieldName());
					evidence.setAnomalyType(anomalyType);
					evidence.setName(anomalyType);
				}
			}
		}
	}


	/**
	 * The API to insert one alert. POST: /api/alerts
	 * @param alert
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = RequestMethod.POST)
	@LogException
	@ResponseBody
	public Alert addAlert(@Valid @RequestBody Alert alert) throws Exception{
		throw new RuntimeException("NOT SUPPORTED");
//		alertsDao.add(alert);
//		return alert;
	}

	/**
	 * The API to update a single alert. PUT: /api/alerts/alertId
	 * @param id
	 * @param alert
	 */
	@RequestMapping(value="{id}",method = RequestMethod.PUT)
	@ResponseBody
	@LogException
	public void putAlert(@PathVariable String id, @RequestBody Alert alert) {
//		alertsDao.update(alert);
	}

	/**
	 * The API to delete a single alert. Delete: /api/alerts/alertId
	 * @param id
	 */
	@RequestMapping(value="{id}", method = RequestMethod.DELETE)
	@ResponseBody
	@LogException
	public void deleteAlert(@PathVariable String id) {
		alertsDao.delete(id);
	}

	/**
	 * //This API gets a single alert  GET: /api/alerts/{alertId}
	 * @param id
	 * @return
	 */
	@RequestMapping(value="{id}", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<Alert> getAlertsById(@PathVariable String id)
	{
		Alert alert = alertsDao.getAlertById(id);
		if (alert == null || alert.getId() == null) {
			throw new ResourceNotFoundException("Can't get alert of id: " + id);
		}
		updateEvidenceFields(alert);
		DataBean<Alert> toReturn = new DataBean<Alert>();
		toReturn.setData(alert);

		return toReturn;
	}

	/**
	 * API to update alert status
	 * @param body
	 * @return
	 */
	@RequestMapping(value="{id}", method = RequestMethod.PATCH)
	@LogException
	public void updateStatus(@PathVariable String id, @RequestBody String body) throws JSONException {
		Alert alert = alertsDao.getAlertById(id);
		JSONObject params = new JSONObject(body);
		boolean alertUpdated = false;
		if (params.has("status")) {
			String status = params.getString("status");
			AlertStatus alertStatus = AlertStatus.getByStringCaseInsensitive(status);
			if (alertStatus == null) {
				throw new InvalidParameterException("Invalid AlertStatus: " + status);
			}
			alert.setStatus(alertStatus);
			alertUpdated = true;
		}
		if (params.has("feedback")) {
			String feedback = params.getString("feedback");
			AlertFeedback alertFeedback = AlertFeedback.getByStringCaseInsensitive(feedback);
			if (alertFeedback == null) {
				throw new InvalidParameterException("Invalid AlertFeedback: " + feedback);
			}
			alert.setFeedback(alertFeedback);
			alertUpdated = true;
		}
		if (alertUpdated) {
			alertsDao.saveAlertInRepository(alert);
		}
	}

	/**
	 * A URL for checking the controller
	 * @return
	 */
	@RequestMapping(value="/selfCheck", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public Date selfCheck(){
		return new Date();
	}


    @RequestMapping(value="/exist-anomaly-types", method = RequestMethod.GET)
    @ResponseBody
    @LogException
    public List<String> getDistinctAnomalyType () {
        Set<DataSourceAnomalyTypePair> dataSourceAnomalyTypePairs =  alertsService.getDistinctAnomalyType();
        String seperator  = "@@@";
        //Todo: in version 2.7 change the response to set of objects instead of string with seperator
        List<String> response = new ArrayList<>();
        for (DataSourceAnomalyTypePair anomalyType : dataSourceAnomalyTypePairs){
            response.add(anomalyType.getDataSource()+seperator+anomalyType.getAnomalyType());
        }
        return response;
    }
}