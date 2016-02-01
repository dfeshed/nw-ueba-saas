package fortscale.domain.core.dao;

import fortscale.domain.core.Alert;
import fortscale.domain.core.dao.rest.Alerts;
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
	 * @return Alerts object with list of alerts that apply to the filter
	 */
	Alerts findAlertsByFilters(PageRequest pageRequest, String severityArray, String statusArrayFilter,
							   String feedbackArrayFilter, String dateRangeFilter, String entityName,
							   Set<String> entitiesIds);

	/**
	 *
	 * @param pageRequest
	 * @param severityArray   	  name of the field to access severity property
	 * @param statusArrayFilter   name of the field to access status property
	 * @param feedbackArrayFilter comma separated list of severity attributes to include
	 * @param statusArrayFilter   comma separated list of status attributes to include
	 * @param feedbackArrayFilter comma separated list of feedback attributes to include
	 * @param dateRangeFilter 	  range of dates to filter
	 * @param entitiesIds 	 	  set of entity ids to filter by
	 * @param severityArray comma separated list of severity to filter by
	 * @return count of alert objects that apply to the filter
	 */
	Long countAlertsByFilters(PageRequest pageRequest, String severityArray, String statusArrayFilter,
							  String feedbackArrayFilter, String dateRangeFilter, String entityName,
							  Set<String> entitiesIds);



	/**
	 * This method "select count group by " query for alert table.
	 * @param fieldName - the "group by" field
	 * @param severityArrayFilter - filter alerts by severity
	 * @param statusArrayFilter - filter alerts by status
	 * @param feedbackArrayFilter  - filter alerts by feedback
	 * @param dateRangeFilter -  - filter alerts by date range
	 * @param entityName - filter alerts by entity name
	 * @param entitiesIds -  - filter alerts by entitiesIds
	 * @return - * @return map from value (from the field) and count of the instances of value
	 */
	public Map<String, Integer> groupCount(String fieldName, String severityArrayFilter, String statusArrayFilter,
										   String feedbackArrayFilter, String dateRangeFilter, String entityName,
										   Set<String> entitiesIds);

	List<Alert> getAlertSummary(List<String> severities, long endDate);

}