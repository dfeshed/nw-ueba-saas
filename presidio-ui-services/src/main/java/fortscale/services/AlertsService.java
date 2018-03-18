package fortscale.services;

import fortscale.domain.core.Alert;
import fortscale.domain.core.AlertFeedback;
import fortscale.domain.core.AlertStatus;
import fortscale.domain.core.alert.analystfeedback.AnalystRiskFeedback;
import fortscale.domain.core.dao.rest.Alerts;
import fortscale.domain.dto.DailySeveiryConuntDTO;
import fortscale.domain.dto.DateRange;
import fortscale.services.exception.UserNotFoundExeption;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Service that handles Alerts and stores them in MongoDB
 *
 */
public interface AlertsService {



	/**
	 * returns a list of all alerts
	 * @return
	 */
	Alerts findAll(PageRequest pageRequest,boolean expand);

	/**
	 * returns a the number of all alerts
	 * @return
	 */
	Long count(PageRequest pageRequest);

	/**
	 * returns a list of all alerts matching filters
	 * @return
	 */
	Alerts findAlertsByFilters(PageRequest pageRequest, String severityArray, String statusArrayFilter,
							   String feedbackArrayFilter, DateRange dateRangeFilter, String entityName,
							   String entityTags, String entityId, Set<String> indicatorTypes, boolean expand,boolean loadComments);

	/**
	 * returns a the number of all alerts matching filters
	 * @return
	 */
	Long countAlertsByFilters(String severityArray, String statusArrayFilter,
							  String feedbackArrayFilter, DateRange dateRangeFilter, String entityName,
							  String entityTags, String entityId, Set<String> indicatorTypes);





	/**
	 * find specific alert by id
	 * @return
	 */
	Alert getAlertById(String id);

	/**
	 * This method "select count group by " query for alert table.
	 * @param fieldName - the "group by" field
	 * @param severityArrayFilter - filter alerts by severity
	 * @param statusArrayFilter - filter alerts by status
	 * @param feedbackArrayFilter  - filter alerts by feedback
	 * @param dateRangeFilter -  - filter alerts by date range
	 * @param entityName - filter alerts by entity name
	 * @param entityTags
	 * @param entityId -
	 * @param indicatorTypes A list of possible indicator ids
	 * @return - * @return map from value (from the field) and count of the instances of value
	 */
	Map<String, Integer> groupCount(String fieldName, String severityArrayFilter, String statusArrayFilter,
									String feedbackArrayFilter, DateRange dateRangeFilter, String entityName,
									String entityTags, String entityId, Set<String> indicatorTypes);


	/**
	 * Get list of alert types, and return the alert type + how many alerts have it
	 * @param ignoreRejected
	 * @return
	 */
	Map<String, Integer> getAlertsTypesCounted(Boolean ignoreRejected);

	Map<Set<String>, Set<String>> getAlertsTypesByUser(Boolean ignoreRejected);


	Map<String, Integer> getAlertsTypes();

	List<Alert> getAlertsByTimeRange(DateRange dateRange, List<String> severities);


	Alerts getAlertsByUsername(String userName);

	Map<String,Integer> getDistinctAnomalyType();

	List<DailySeveiryConuntDTO> getAlertsCountByDayAndSeverity(DateRange alertStartRange);



	Alerts getAlertsRelevantToUserScore(String userId);

	List<Alert> getOpenAlertsByUsername(String userName);

//	Set<String> getDistinctAlertNames(Boolean ignoreRejected);


	AnalystRiskFeedback updateAlertStatus(Alert alert, AlertStatus alertStatus, AlertFeedback alertFeedback, String analystUserName) throws UserNotFoundExeption;

	void saveAlertInRepository(Alert alert);
}