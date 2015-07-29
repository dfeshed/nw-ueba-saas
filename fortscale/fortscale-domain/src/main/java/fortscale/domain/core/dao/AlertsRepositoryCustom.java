package fortscale.domain.core.dao;

import fortscale.domain.core.Alert;
import fortscale.domain.core.dao.rest.Alerts;
import org.springframework.data.domain.PageRequest;

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
	 * @param severityArray comma separated list of severity to filter by
	 * @return
	 */
	public Alerts findAlertsByFilters(PageRequest pageRequest, String severityArray);

	}
