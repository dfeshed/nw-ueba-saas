package fortscale.services;

import fortscale.domain.core.Alert;
import fortscale.domain.core.Severity;
import fortscale.domain.core.dao.AlertsRepositoryImpl;
import fortscale.domain.core.dao.rest.Alerts;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

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
	 * returns a conversion map from score to severity
	 * @return
	 */
	NavigableMap<Integer, Severity> getScoreToSeverity();

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
								String entityTags, String entityId);

	/**
	 * returns a the number of all alerts matching filters
	 * @return
	 */
	Long countAlertsByFilters(PageRequest pageRequest, String severityArray, String statusArrayFilter,
								String feedbackArrayFilter, String dateRangeFilter, String entityName,
								String entityTags, String entityId);

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
	 *
	 * @param fieldName - the filed which we like to group by
	 * @param fromDate - the date which all the alerts start time should be greated then
	 * @param toDate - the date which all the alerts start time should be smaller then
	 * @return map from value (from the field) and count of the instances of value
	 */
	public Map<String, Integer> groupCount(String fieldName, long fromDate, long toDate);

}