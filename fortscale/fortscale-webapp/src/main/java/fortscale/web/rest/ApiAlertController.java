package fortscale.web.rest;

import au.com.bytecode.opencsv.CSVWriter;
import fortscale.domain.analyst.AnalystAuth;
import fortscale.domain.core.*;
import fortscale.domain.core.dao.rest.Alerts;
import fortscale.domain.dto.DailySeveiryConuntDTO;
import fortscale.domain.dto.DateRange;
import fortscale.services.AlertsService;
import fortscale.services.EvidencesService;
import fortscale.services.LocalizationService;
import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.BaseController;
import fortscale.web.beans.DataBean;
import fortscale.web.beans.request.AlertFilterHelperImpl;
import fortscale.web.beans.request.AlertRestFilter;
import fortscale.web.beans.request.CommentRequest;
import fortscale.web.exceptions.InvalidParameterException;
import fortscale.web.rest.Utils.ResourceNotFoundException;
import fortscale.web.rest.Utils.Shay;
import fortscale.web.rest.entities.AlertStatisticsEntity;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/api/alerts")
public class ApiAlertController extends BaseController {

	public static final String ALERT_NAME = "Alert Name";
	public static final String ENTITY_NAME_COLUMN_NAME = "Entity Name";
	public static final String START_TIME_COLUMN_NAME = "Start Time";
	public static final String NUMBER_OF_INDICATORS_COLUMN_NAME = "# of Indicators";
	public static final String STATUS_COLUMN_NAME = "Status";
	public static final String SEVERITY_COLUMN_NAME = "Severity";
	public static final String ALERTS_CSV_FILE_NAME = "alerts.csv";
	public static final String CSV_CONTENT_TYPE = "text/plain; charset=utf-8";
	private static Logger logger = Logger.getLogger(ApiAlertController.class);

    @Autowired
    public AlertFilterHelperImpl alertFilterHelper;

	public static final String OPEN_STATUS = "Open";
	private static final String TIME_STAMP_START = "startDate";

	@Autowired
	private AlertsService alertsDao;

	@Autowired
	private EvidencesService evidencesDao;

	@Autowired
	LocalizationService localizationService;

    @Autowired
    private AlertsService alertsService;

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
									AlertRestFilter filter) {

		PageRequest pageRequest = alertFilterHelper.getPageRequest(filter);


		Alerts alerts;
		Long count;

		//if no filter, call findAll()
		if (alertFilterHelper.isFilterEmpty(filter)) {
			alerts = alertsDao.findAll(pageRequest);
			//total count of the total items in query.
			count = alertsDao.count(pageRequest);

		} else {

			//Todo: pass the filter itself and not list of values for both findAlertsByFilters  countAlertsByFilters
			alerts = alertsDao.findAlertsByFilters(pageRequest, filter.getSeverity(), filter.getStatus(), filter.getFeedback(), filter.getAlertStartRange(), filter.getEntityName(),
					filter.getEntityTags(), filter.getEntityId(), filter.getAnomalyTypesAsSet());
			count = alertsDao.countAlertsByFilters(pageRequest, filter.getSeverity(), filter.getStatus(), filter.getFeedback(), filter.getAlertStartRange(), filter.getEntityName(),
					filter.getEntityTags(), filter.getEntityId(), filter.getAnomalyTypesAsSet());
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

		Map<String, Integer> severitiesCountResult = alertsDao.groupCount(SEVERITY_COLUMN_NAME.toLowerCase(),
				filter.getSeverity(), filter.getStatus(), filter.getFeedback(), filter.getAlertStartRange(), filter.getEntityName(),
				filter.getEntityTags(), filter.getEntityId(), filter.getAnomalyTypesAsSet());
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
	 * @param  startRange  - return alert from the timeRange last days
	 * @return
	 */
	@RequestMapping(value="/statistics", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<AlertStatisticsEntity> getStatistics(
			@RequestParam(required=true, value = "start_range") DateRange startRange)
	{

		AlertStatisticsEntity results = new AlertStatisticsEntity(	);

		//Add statuses
		Map<String,Integer> statusCounts = alertsService.groupCount(STATUS_COLUMN_NAME.toLowerCase(), null, null, null, startRange,null, null,null, null);
		results.setAlertStatus(statusCounts);

		//Add severities
		Map<String,Integer> severityCounts = alertsService.groupCount(SEVERITY_COLUMN_NAME.toLowerCase(), null, OPEN_STATUS, null, startRange,null, null, null, null);

		results.setAlertOpenSeverity(severityCounts);


		DataBean<AlertStatisticsEntity> toReturn = new DataBean<AlertStatisticsEntity>();
		toReturn.setData(results);

		return toReturn;
	}

	private void validateUserName(HttpSession session, String analystUserName){
		SecurityContextImpl securityContext = (SecurityContextImpl)(session.getAttribute("SPRING_SECURITY_CONTEXT"));

		if (securityContext.getAuthentication()==null){
			throw new RuntimeException("User is not logged in");
		}

		Authentication authentication = securityContext.getAuthentication();
		if (authentication == null){
			throw new RuntimeException("User is not logged in");
		}

		AnalystAuth analyst = (AnalystAuth)authentication.getPrincipal();
		if (analyst == null){
			throw new RuntimeException("User is not logged in");
		}
		String analystName = analyst.getUsername();
		if (StringUtils.isBlank(analystName)){
			throw new RuntimeException("User is not logged in");
		}

		if (!analystName.equals(analystUserName)){
			throw new RuntimeException("User cannot send comment in behalf of other user");
		}



	}

	@RequestMapping(method = RequestMethod.POST, value = "/{id}/comments", consumes = MediaType.APPLICATION_JSON_VALUE) @LogException @ResponseBody
	public ResponseEntity<?> addComment(HttpServletRequest httpRequest, @PathVariable String id, @RequestBody @Valid CommentRequest request) {

		long timeStamp = System.currentTimeMillis();
		Alert alert = alertsService.getAlertById(id);


		try {
			validateUserName(httpRequest.getSession(), request.getAnalystUserName());
		} catch (Exception e){
			return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
		}

		if (alert == null){
			return new ResponseEntity("Alert id doesn't exist " + id, HttpStatus.BAD_REQUEST);
		}

		Comment c= alert.addComment(request.getAnalystUserName(), request.getCommentText(), timeStamp);


		alertsService.saveAlertInRepository(alert);
	return new ResponseEntity(c,HttpStatus.CREATED);
	}

	@RequestMapping(method = RequestMethod.PATCH, value = "{id}/comments/{commentId}") @LogException @ResponseBody
	public ResponseEntity updateComment(@PathVariable String id, @PathVariable String commentId,
			@RequestBody @Valid CommentRequest request) {
		long timeStamp = System.currentTimeMillis();
		Alert alert = alertsService.getAlertById(id);

		if (alert == null){
			return new ResponseEntity("Alert id doesn't exist " + id, HttpStatus.BAD_REQUEST);
		}

		Comment oldComment = alert.getComment(commentId);
		if (oldComment == null){
			return new ResponseEntity("Alert doesn't have comment with id " + commentId, HttpStatus.BAD_REQUEST);
		}

		if (!oldComment.getCommentText().equals(request.getCommentText())) {
			oldComment.setCommentText(request.getCommentText());
			oldComment.setAnalystUserName(request.getAnalystUserName());
			oldComment.setUpdateDate(timeStamp);
			alertsService.saveAlertInRepository(alert);
		}

		return new ResponseEntity(oldComment,HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "{id}/comments/{commentId}")
	@LogException
	@ResponseBody
	public ResponseEntity deleteComment(@PathVariable String id, @PathVariable String commentId) {
		Alert alert = alertsService.getAlertById(id);

		if (alert == null){
			return new ResponseEntity("Alert id doesn't exist " + id, HttpStatus.BAD_REQUEST);
		}

		Comment commentToDelete = alert.getComment(commentId);
		alert.getComments().remove(commentToDelete);
		alertsService.saveAlertInRepository(alert);

		return new ResponseEntity(HttpStatus.OK);
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

	}

	/**
	 * The API to delete a single alert. Delete: /api/alerts/alertId
	 * @param id
	 */
	@RequestMapping(value="{id}", method = RequestMethod.DELETE)
	@ResponseBody
	@LogException
	public void deleteAlert(@PathVariable String id) {
        alertsService.delete(id);
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
		Alert alert = alertsService.getAlertById(id);
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
		Alert alert = alertsService.getAlertById(id);
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
            alertsService.saveAlertInRepository(alert);
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

    @ResponseBody
    @RequestMapping(value="/alert-by-day-and-severity", method = RequestMethod.GET)
    public List<DailySeveiryConuntDTO> getAlertsCountByDayAndSeverity(

            @RequestParam(required=false, value = "alert_start_range") DateRange alertStartRange
    ){
        List<DailySeveiryConuntDTO> result =  alertsService.getAlertsCountByDayAndSeverity(alertStartRange);

        return result;
    }


    /**
     * A URL for checking the controller
     * @return
     */
    @RequestMapping(value="/shay", method=RequestMethod.GET)
    @ResponseBody
    public DataBean<Shay> shay(@Valid Shay s){

        DataBean<Shay> response = new DataBean<>();
        response.setData(s);
        return response;
    }
}