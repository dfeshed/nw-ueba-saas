package presidio.ui.presidiouiapp.rest;

import au.com.bytecode.opencsv.CSVWriter;
import fortscale.domain.core.*;
import fortscale.domain.core.Alert;
import fortscale.domain.core.AlertFeedback;
import fortscale.domain.core.AlertStatus;
import fortscale.domain.core.dao.rest.Alerts;
import fortscale.domain.dto.DailySeveiryConuntDTO;
import fortscale.domain.dto.DateRange;
import fortscale.services.AlertsService;
import fortscale.services.LocalizationService;
import fortscale.services.exception.UserNotFoundExeption;
import fortscale.utils.logging.Logger;

import presidio.ui.presidiouiapp.BaseController;
import presidio.ui.presidiouiapp.beans.DataBean;
import presidio.ui.presidiouiapp.beans.ValueCountBean;
import presidio.ui.presidiouiapp.beans.request.AlertFilterHelperImpl;
import fortscale.domain.rest.AlertRestFilter;
import presidio.ui.presidiouiapp.beans.request.AlertUpdateStatusRequest;
import presidio.ui.presidiouiapp.beans.request.CommentFeedbackRequest;
import presidio.ui.presidiouiapp.rest.Utils.ResourceNotFoundException;
import presidio.ui.presidiouiapp.rest.entities.AlertStatisticsEntity;
import io.swagger.annotations.*;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@Api(value="/api/alerts", protocols = "HTTP, HTTPS")
@RequestMapping("/api/alerts")
public class ApiAlertController extends BaseController {

	private static final String ALERT_NAME = "Alert Name";
	private static final String ENTITY_NAME_COLUMN_NAME = "Entity Name";
	private static final String START_TIME_COLUMN_NAME = "Start Time";
	private static final String NUMBER_OF_INDICATORS_COLUMN_NAME = "# of Indicators";
	private static final String FEEDBACK_COLUMN_NAME = "Feedback";
	private static final String SEVERITY_COLUMN_NAME = "Severity";
	private static final String ALERTS_CSV_FILE_NAME = "alerts.csv";
	private static final String CSV_CONTENT_TYPE = "text/plain; charset=utf-8";
	public static final String UNAUTHENTICATED_USER_NAME = "Unauthenticated User";
	public static final String AUTHENTICATED_USER_HEADER_PARAM_NAME = "Authenticated_User";
	public static Logger logger = Logger.getLogger(ApiAlertController.class);

	private static final String OPEN_STATUS = "Open";


	private AlertFilterHelperImpl alertFilterHelper;
	private LocalizationService localizationService;
	private AlertsService alertsService;

	public ApiAlertController(AlertFilterHelperImpl alertFilterHelper, LocalizationService localizationService, AlertsService alertsService) {

		this.alertFilterHelper = alertFilterHelper;
		this.localizationService = localizationService;
		this.alertsService = alertsService;
	}



	@RequestMapping(value="/exist-anomaly-types", method = RequestMethod.GET)
	@ResponseBody
	//@LogException
	public Map<String,Integer> getDistinctAnomalyType () {
		Map<String,Integer> anomalyTypePairs =  alertsService.getDistinctAnomalyType();
		List<String> response = new ArrayList<>();

		return anomalyTypePairs;
	}


	@RequestMapping(value="/exist-alert-types", method = RequestMethod.GET)
	@ResponseBody
	//@LogException
	public DataBean<Set<ValueCountBean>> getDistinctAlertNames(@RequestParam(required=true, value = "ignore_rejected")Boolean ignoreRejected){
		Set<ValueCountBean> alertTypesNameAndCount = new HashSet<>();

		for (Map.Entry<String, Integer> alertTypeToCountEntry : alertsService.getAlertsTypesCounted(ignoreRejected).entrySet()){
			alertTypesNameAndCount.add(new ValueCountBean(alertTypeToCountEntry.getKey(), alertTypeToCountEntry.getValue()));
		}

		DataBean<Set<ValueCountBean>> result = new DataBean<>();

		result.setData(alertTypesNameAndCount);
		result.setTotal(alertTypesNameAndCount.size());

		return result;
	}

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
	//@LogException
	public void exportAlertsToCsv(HttpServletRequest httpRequest, HttpServletResponse httpResponse, Locale locale,
								  AlertRestFilter alertRestFilter)  throws  Exception{
		/*
			Set response type as CSV
		 */
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"",
				ALERTS_CSV_FILE_NAME);
		httpResponse.setHeader(headerKey, headerValue);
		httpResponse.setContentType(CSV_CONTENT_TYPE);

		int pageSize = 10000; //Fetch only first 10000 rows :) (pageSize 0 is no longer accepted by PageRequest)
		DataBean<List<Alert>> alerts= getAlerts(httpRequest, httpResponse, alertRestFilter);

		CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(httpResponse
				.getOutputStream()));

		//Add headers - csvWriter.writeNext();
		String[] tableTitleRow = {ALERT_NAME,
				ENTITY_NAME_COLUMN_NAME,
				START_TIME_COLUMN_NAME,
				NUMBER_OF_INDICATORS_COLUMN_NAME,
				FEEDBACK_COLUMN_NAME,
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
					alert.getFeedback().getPrettyValue(),
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
	//@LogException
	public @ResponseBody
	DataBean<List<Alert>> getAlerts(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
									AlertRestFilter filter) {

		PageRequest pageRequest = alertFilterHelper.getPageRequest(filter);


		Alerts alerts;
		Long count;

		//if no filter, call findAll()
		if (alertFilterHelper.isFilterEmpty(filter)) {
			alerts = alertsService.findAll(pageRequest,true);
			//total count of the total items in query.
			count = alerts.getTotalCount();

		} else {

			//Todo: pass the filter itself and not list of values for both findAlertsByFilters  countAlertsByFilters
			alerts = alertsService.findAlertsByFilters(pageRequest, filter.getSeverity(), filter.getStatus(), filter.getFeedback(),
					filter.getAlertStartRange(), filter.getEntityName(),
					filter.getEntityTags(), filter.getEntityId(), filter.getIndicatorTypes(), filter.getEntityType(), true,filter.isLoadComments());
			count = alerts.getTotalCount();
		}

		if (alerts.getAlerts()==null){
			alerts.setAlerts(new ArrayList<>());
		}
		for (Alert alert : alerts.getAlerts()) {
			updateEvidenceFields(alert);
		}

		DataBean<List<Alert>> entities = new DataBean<>();
		entities.setData(alerts.getAlerts());

		entities.setTotal(count.intValue());
		entities.setOffset(alertFilterHelper.getOffset(filter));

		if (filter.isTotalSeverityCount()) {
			Map<String, Object> info = new HashMap<>();

			info.put("total_severity_count", countSeverities(filter));
			entities.setInfo(info);
		}
		return entities;
	}

	private Map<Severity, Integer> countSeverities (AlertRestFilter filter) {
		Map<Severity, Integer> severitiesCount = new HashMap<>();

		//Todo: pass the filter itself and not list of values to groupCount

		Map<String, Integer> severitiesCountResult = alertsService.groupCount(SEVERITY_COLUMN_NAME.toLowerCase(),
				filter.getSeverity(), filter.getStatus(), filter.getFeedback(), filter.getAlertStartRange(), filter.getEntityName(),
				filter.getEntityTags(), filter.getEntityId(), filter.getIndicatorTypes(), filter.getEntityType());
		for (Severity iSeverity : Severity.values()) {
			Integer statusCount = severitiesCountResult.get(iSeverity.name().toUpperCase());
			if (statusCount == null){
				statusCount = 0;
			}
			severitiesCount.put(iSeverity, statusCount);
		}

		return severitiesCount;
	}

	/**
	 * Statistics about system alerts
	 * @param  startRange  - return alert from the timeRange last days
	 * @return
	 */
	@RequestMapping(value="/statistics", method = RequestMethod.GET)
	@ResponseBody
	//@LogException
	public DataBean<AlertStatisticsEntity> getStatistics(
			@RequestParam(required=true, value = "start_range") DateRange startRange)
	{

		AlertStatisticsEntity results = new AlertStatisticsEntity(	);

		//Add statuses
		Map<String,Integer> statusCounts = new HashMap<>();
		statusCounts.put(AlertStatus.Open.name(), 0);
		statusCounts.put(AlertStatus.Closed.name(), 0);
		//this temporary map is designed to map the 3 values (Approved, Rejected and None) into 2 values (Open, Closed)
		//since we changed the status/feedback only on the UI
		Map<String,Integer> tempCounts = alertsService.groupCount(FEEDBACK_COLUMN_NAME.toLowerCase(), null, null,
				null, startRange, null, null, null, null, null);
		for (Map.Entry<String, Integer> entry: tempCounts.entrySet()) {
			if (!entry.getKey().equalsIgnoreCase(AlertFeedback.None.name())) {
				statusCounts.put(AlertStatus.Closed.name(), entry.getValue() +
						statusCounts.get(AlertStatus.Closed.name()));
			} else {
				statusCounts.put(AlertStatus.Open.name(), entry.getValue() + statusCounts.get(AlertStatus.Open.name()));
			}
		}
		results.setAlertStatus(statusCounts);

		//Add severities
		Map<String,Integer> severityCounts = alertsService.groupCount(SEVERITY_COLUMN_NAME.toLowerCase(), null, OPEN_STATUS, null,
				startRange,null, null, null, null, null);

		results.setAlertOpenSeverity(severityCounts);


		DataBean<AlertStatisticsEntity> toReturn = new DataBean<AlertStatisticsEntity>();
		toReturn.setData(results);

		return toReturn;
	}

	private String getAnalystUserName(HttpServletRequest request){

		String username = request.getHeader(AUTHENTICATED_USER_HEADER_PARAM_NAME);
		if (StringUtils.isBlank(username)){
			logger.warn("User updating system is not authenticated");
			return UNAUTHENTICATED_USER_NAME;
		} else {
			return username;
		}

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
	//@LogException
	@ResponseBody
	public Alert addAlert(@Valid @RequestBody Alert alert) throws Exception{
		throw new RuntimeException("NOT SUPPORTED");

	}


	/**
	 * //This API gets a single alert  GET: /api/alerts/{alertId}
	 * @param id
	 * @return
	 */
	@RequestMapping(value="{id}", method = RequestMethod.GET)
	@ResponseBody
	//@LogException
	public DataBean<Alert> getAlertsById(@PathVariable String id)
	{
		Alert alert = alertsService.getAlertById(id);
		if (alert == null || alert.getId() == null) {
			throw new ResourceNotFoundException("Can't get alert of id: " + id);
		}
		updateEvidenceFields(alert);
		DataBean<Alert> toReturn = new DataBean<Alert>();
		toReturn.setData(alert);

		return toReturn;
	}

	/***
	 * API to update alert status
	 * @param id
	 * @param request
	 * @throws JSONException
	 */
	@RequestMapping(value="{id}", method = RequestMethod.PATCH)
	//@LogException
	public ResponseEntity<?> updateStatus(HttpServletRequest httpRequest, @PathVariable String id,
										  @Valid @RequestBody AlertUpdateStatusRequest request){
		// Getting the relevant alert
		Alert alert = alertsService.getAlertById(id);
		String analystUserName = null;

		if (alert == null){
			return new ResponseEntity(String.format("Alert with id {} not found", id), HttpStatus.BAD_REQUEST);
		}

		try {
			analystUserName = getAnalystUserName(httpRequest);
		} catch (Exception e) {
			return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
		}

		try {
			alert= alertsService.updateAlertStatus(alert, request.getStatus(), request.getFeedback(), analystUserName);
			return new ResponseEntity(alert,HttpStatus.OK);
		} catch (UserNotFoundExeption userNotFoundExeption){
			return new ResponseEntity(String.format("User with id {} not found", alert.getEntityId()), HttpStatus.BAD_REQUEST);
		}


	}


	/**
	 * A URL for checking the controller
	 * @return
	 */
	@RequestMapping(value="/selfCheck", method=RequestMethod.GET)
	@ResponseBody
	//@LogException
	public Date selfCheck(){
		return new Date();
	}

	@ResponseBody
	@RequestMapping(value="/alert-by-day-and-severity", method = RequestMethod.GET)
	public List<DailySeveiryConuntDTO> getAlertsCountByDayAndSeverity(

			@RequestParam(required=false, value = "alert_start_range") DateRange alertStartRange
	){
		List<DailySeveiryConuntDTO> result =  alertsService.getAlertsCountByDayAndSeverity(alertStartRange);
		result.sort(new Comparator<DailySeveiryConuntDTO>() {
			@Override
			public int compare(DailySeveiryConuntDTO o1, DailySeveiryConuntDTO o2) {
				Long day1 = o1.getDay();
				Long day2 = o2.getDay();
				return day1.compareTo(day2);
			}

		});
		return result;
	}

}