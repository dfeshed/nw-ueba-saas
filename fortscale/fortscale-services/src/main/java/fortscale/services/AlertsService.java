package fortscale.services;

import fortscale.domain.core.Alert;
import fortscale.domain.core.Severity;
import fortscale.domain.core.dao.rest.Alerts;
import org.springframework.data.domain.PageRequest;
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
									  String dateRangeFilter, String entityName, String entityTags);

	/**
	 * returns a the number of all alerts matching filters
	 * @return
	 */
	Long countAlertsByFilters(PageRequest pageRequest, String severityArray, String statusArrayFilter,
									 String dateRangeFilter, String entityName, String entityTags);

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

}