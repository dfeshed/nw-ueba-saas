package fortscale.web.rest;

import au.com.bytecode.opencsv.CSVWriter;
import fortscale.domain.core.Alert;
import fortscale.domain.core.AlertFeedback;
import fortscale.domain.core.AlertStatus;
import fortscale.domain.core.Evidence;
import fortscale.domain.core.Severity;
import fortscale.domain.core.dao.rest.Alerts;
import fortscale.services.AlertsService;
import fortscale.utils.ConfigurationUtils;
import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.LogException;
import fortscale.utils.time.TimestampUtils;
import fortscale.web.BaseController;
import fortscale.web.beans.DataBean;
import fortscale.web.exceptions.InvalidParameterException;
import fortscale.web.rest.Utils.ApiUtils;
import fortscale.web.rest.entities.AlertStatisticsEntity;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.*;

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

	@Autowired
	private AlertsService alertsDao;


	@Value("${fortscale.evidence.type.map}")
	private String evidenceTypeProperty;

	@Value("${fortscale.evidence.name.text}")
	private String evidenceNameText;

	/**
	 *  The format of the dates in the exported file
	 */
	@Value("${export.data.date.format:MMM dd yyyy HH:mm:ss 'GMT'Z}")
	private String exportDateFormat;


	private Map evidenceTypeMap;

	@PostConstruct
	public void initEvidenceMap(){
		evidenceTypeMap = ConfigurationUtils.getStringMap(evidenceTypeProperty);
	}


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
								  @RequestParam(required=false, value = "total_severity_count") String totalSeverityCount

	)  throws  Exception{

		/*
			Set response type as CSV
		 */
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"",
				ALERTS_CSV_FILE_NAME);
		httpResponse.setHeader(headerKey, headerValue);
		httpResponse.setContentType(CSV_CONTENT_TYPE);


		int pageSize = 0; //Fetch all rows.
		DataBean<List<Alert>> alerts= getAlerts(httpRequest, httpResponse, sortField, sortDirection, pageSize,
												fromPage, severity,	status, feedback, alertStartRange,entityName,
												entityTags, entityId, totalSeverityCount);


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
			String[] alertRow = {alert.getName(),
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
										  @RequestParam(required=false, value = "total_severity_count") String totalSeverityCount) {

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
		//if no filter, call findAll()
		if (severity == null && status == null  && feedback == null &&  alertStartRange == null && entityName == null && entityTags == null && entityId == null) {
			alerts = alertsDao.findAll(pageRequest);
			//total count of the total items in query.
			count = alertsDao.count(pageRequest);

		} else {
			alerts = alertsDao.findAlertsByFilters(pageRequest, severity, status, feedback, alertStartRange, entityName,
					entityTags, entityId);
			count = alertsDao.countAlertsByFilters(pageRequest, severity, status, feedback, alertStartRange, entityName,
					entityTags, entityId);
		}

		if (totalSeverityCount != null) {
			severitiesCount = countSeverities(pageRequest, severity, status, feedback, alertStartRange, entityName,
					entityTags, entityId);
		}

		for (Alert alert : alerts.getAlerts()) {
			updateEvidenceFields(alert);
		}

		DataBean<List<Alert>> entities = new DataBean<>();
		entities.setData(alerts.getAlerts());

		entities.setTotal(count.intValue());
		entities.setOffset(pageForMongo * size);

		if (totalSeverityCount != null) {
			Map<String, Object> info = new HashMap<>();
			info.put("total_severity_count", severitiesCount);
			entities.setInfo(info);
		}
		return entities;
	}


	private Map<Severity, Long> countSeverities (PageRequest pageRequest, String severity, String status,
												 String feedback, String alertStartRange, String entityName,
												 String entityTags, String entityId) {
		Map<Severity, Long> severitiesCount = new HashMap<>();

		long noCount;
		noCount = 0;

		for (Severity iSeverity : Severity.values()) {
			if (severity == null || severity.isEmpty() ||
					severity.toUpperCase().contains(iSeverity.name().toUpperCase())) {
				severitiesCount.put(iSeverity, alertsDao.countAlertsByFilters(pageRequest, iSeverity.name(), status,
						feedback, alertStartRange, entityName, entityTags, entityId));
			} else {
				severitiesCount.put(iSeverity, noCount);
			}
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

		List<Long> timeRangeList = ApiUtils.splitTimeRangeToFromAndToMiliseconds(timeRange);

		AlertStatisticsEntity results = new AlertStatisticsEntity(	);

		//Add statuses
		Map<String,Integer> statusCounts = alertsDao.groupCount("status", timeRangeList.get(0),
														timeRangeList.get(1), null);
		results.setAlertStatus(statusCounts);

		//Add severities
		Map<String,Integer> severityCounts = alertsDao.groupCount("severity",timeRangeList.get(0),
											timeRangeList.get(1), OPEN_STATUS);
		results.setAlertOpenSeverity(severityCounts);


		DataBean<AlertStatisticsEntity> toReturn = new DataBean<AlertStatisticsEntity>();
		toReturn.setData(results);

		return toReturn;
	}

	private void updateEvidenceFields(Alert alert){
		if(alert != null && alert.getEvidences() != null) {
			for (Evidence evidence : alert.getEvidences()) {
				if (evidence != null && evidence.getAnomalyTypeFieldName() != null) {
					Object name = evidenceTypeMap.get(evidence.getAnomalyTypeFieldName());
					String anomalyType = (name!=null ? name.toString(): evidence.getAnomalyTypeFieldName());
					evidence.setAnomalyType(anomalyType);
					String evidenceName = String.format(evidenceNameText, evidence.getEntityType().toString().
							toLowerCase(), evidence.getEntityName(), anomalyType);
					evidence.setName(evidenceName);
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
	@ResponseBody
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

}