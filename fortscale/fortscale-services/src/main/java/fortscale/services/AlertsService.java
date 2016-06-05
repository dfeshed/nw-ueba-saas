package fortscale.services;

import fortscale.domain.core.Alert;
import fortscale.domain.core.DataSourceAnomalyTypePair;
import fortscale.domain.core.Severity;
import fortscale.domain.core.dao.rest.Alerts;
import fortscale.domain.dto.DailySeveiryConuntDTO;
import fortscale.domain.dto.DateRange;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;

/**
 * Service that handles Alerts and stores them in MongoDB
 *
 */
public interface AlertsService {

	/**
	 * Create new alert in Mongo
	 * @param alert	The alert
	 */
	void saveAlertInRepository(Alert alert);



	/**
	 * returns a list of all alerts
	 * @return
	 */
	Alerts findAll(PageRequest pageRequest);

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
								String feedbackArrayFilter, String dateRangeFilter, String entityName,
								String entityTags, String entityId, Set<DataSourceAnomalyTypePair> indicatorTypes);

	/**
	 * returns a the number of all alerts matching filters
	 * @return
	 */
	Long countAlertsByFilters(PageRequest pageRequest, String severityArray, String statusArrayFilter,
								String feedbackArrayFilter, String dateRangeFilter, String entityName,
								String entityTags, String entityId, Set<DataSourceAnomalyTypePair> indicatorTypes);

	/**
	 * Add alert to Alerts repository
	 * @param alert
	 */
	void add(Alert alert);

	/**
	 * delete specific alert by id
	 */
	void delete(String id);

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
	public Map<String, Integer> groupCount(String fieldName, String severityArrayFilter, String statusArrayFilter,
										   String feedbackArrayFilter, String dateRangeFilter, String entityName,
										   String entityTags, String entityId, Set<DataSourceAnomalyTypePair> indicatorTypes);

	List<Alert> getAlertSummary(List<String> severities, long endDate);

    List<Alert> getAlertsByTimeRange(DateRange dateRange, List<String> severities);

	void removeRedundantAlertsForUser(String username, String alertId);

    List<Alert> getAlertsByUsername(String userName);

    Set<DataSourceAnomalyTypePair> getDistinctAnomalyType();

    List<DailySeveiryConuntDTO> getAlertsCountByDayAndSeverity(DateRange alertStartRange);

    Set<String> getDistinctUserNamesFromAlertsRelevantToUserScore();

    Set<Alert> getAlertsRelevantToUserScore(String userName);
}