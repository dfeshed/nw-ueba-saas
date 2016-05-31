package fortscale.domain.core.dao;

import fortscale.domain.core.Alert;
import fortscale.domain.core.DataSourceAnomalyTypePair;
import fortscale.domain.core.dao.rest.Alerts;
import fortscale.domain.dto.DateRange;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AlertsRepositoryCustom {

	/**
	 * Find all alerts according to query that is build on request parameters
	 * @param request
	 * @return list of alerts
	 */
	Alerts findAll(PageRequest request);

	/**
	 * Count alerts according to query that is build on request parameters
	 * @param pageRequest
	 * @return
	 */
	Long count(PageRequest pageRequest);

	/**
	 * Add alert to Alerts repository
	 * @param alert
	 */
	void add(Alert alert);

	/**
	 * Get alert according to id
	 * @param id
	 * @return
	 */
	 Alert getAlertById(String id);

	/**
	 * find alerts by filters
	 * @param pageRequest
	 * @param severityArray   	  name of the field to access severity property
	 * @param statusArrayFilter   name of the field to access status property
	 * @param feedbackArrayFilter comma separated list of severity attributes to include
	 * @param statusArrayFilter   comma separated list of status attributes to include
	 * @param feedbackArrayFilter comma separated list of feedback attributes to include
	 * @param dateRangeFilter 	  range of dates to filter
	 * @param entitiesIds 	 	  set of entity ids to filter by
	 * @param indicatorIds	      A list of indicator ids
	 * @return Alerts object with list of alerts that apply to the filter
	 */
	Alerts findAlertsByFilters(PageRequest pageRequest, String severityArray, String statusArrayFilter,
							   String feedbackArrayFilter, String dateRangeFilter, String entityName,
							   Set<String> entitiesIds, List<DataSourceAnomalyTypePair> indicatorTypes);

	/**
	 *
	 * @param pageRequest
	 * @param severityArray      name of the field to access severity property
	 * @param statusArrayFilter   name of the field to access status property
	 * @param feedbackArrayFilter comma separated list of severity attributes to include
	 * @param dateRangeFilter      range of dates to filter
	 * @param entitiesIds          set of entity ids to filter by
	 * @param indicatorTypes			A list of indicator ids
	 * @return count of alert objects that apply to the filter
	 */
	Long countAlertsByFilters(PageRequest pageRequest, String severityArray, String statusArrayFilter,
							  String feedbackArrayFilter, String dateRangeFilter, String entityName,
							  Set<String> entitiesIds, List<DataSourceAnomalyTypePair> indicatorTypes);



	/**
	 * This method "select count group by " query for alert table.
	 * @param fieldName - the "group by" field
	 * @param severityArrayFilter - filter alerts by severity
	 * @param statusArrayFilter - filter alerts by status
	 * @param feedbackArrayFilter  - filter alerts by feedback
	 * @param dateRangeFilter -  - filter alerts by date range
	 * @param entityName - filter alerts by entity name
	 * @param entitiesIds -  - filter alerts by entitiesIds
	 * @param indicatorTypes	A list of indicator ids
	 * @return - * @return map from value (from the field) and count of the instances of value
	 */
	public Map<String, Integer> groupCount(String fieldName, String severityArrayFilter, String statusArrayFilter,
										   String feedbackArrayFilter, String dateRangeFilter, String entityName,
										   Set<String> entitiesIds, List<DataSourceAnomalyTypePair> indicatorTypes);

	List<Alert> getAlertSummary(List<String> severities, long endDate);

	List<Alert> getAlertsByTimeRange(DateRange dateRange, List<String> severities, boolean excludeEvidences);
	/**
	 *
	 * This method deletes all alerts for the user EXCEPT the given alert id
	 *
	 * @param username
	 * @param alertId
	 */
	void removeRedundantAlertsForUser(String username, String alertId);


	/**
	 * Count how many alerts we have with the same name , in the same time
	 *
	 * @return number of alerts
	 */
	long buildQueryForAlertByTimeAndName(String alertName, long startTime, long endTime);

    Set<DataSourceAnomalyTypePair> getDataSourceAnomalyTypePairs();

    Set<String> getDistinctUserNamesFromAlertsRelevantToUserScore();
}