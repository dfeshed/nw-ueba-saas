package fortscale.domain.core.dao;

import fortscale.domain.core.dao.rest.Alert;
import fortscale.domain.core.dao.rest.Alerts;
import org.springframework.data.domain.PageRequest;

import javax.servlet.http.HttpServletRequest;

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

	}
