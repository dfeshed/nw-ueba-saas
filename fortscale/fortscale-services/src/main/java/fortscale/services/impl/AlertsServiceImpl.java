package fortscale.services.impl;

import fortscale.domain.core.Alert;
import fortscale.domain.core.Severity;
import fortscale.domain.core.dao.AlertsRepository;
import fortscale.domain.core.dao.rest.Alerts;
import fortscale.services.AlertsService;
import fortscale.services.UserService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Services for managing the alerts
 *
 * Date: 6/23/2015.
 */
@Service("alertsService")
public class AlertsServiceImpl implements AlertsService, InitializingBean {

	/**
	 * Mongo repository for alerts
	 */
	@Autowired
	private AlertsRepository alertsRepository;

	/**
	 * Mongo repository for users
	 */
	@Autowired
	private UserService userService;


	// Severity thresholds for alerts
	@Value("${alert.severity.medium:80}")
	protected int medium;
	@Value("${alert.severity.high:90}")
	protected int high;
	@Value("${alert.severity.critical:95}")
	protected int critical;

	/**
	 * Keeps mapping between score and severity
	 */
	private NavigableMap<Integer,Severity> scoreToSeverity = new TreeMap<>();


	@Override
	public void afterPropertiesSet() throws Exception {
		// init scoring to severity map
		scoreToSeverity.put(0, Severity.Low);
		scoreToSeverity.put(medium, Severity.Medium);
		scoreToSeverity.put(high, Severity.High);
		scoreToSeverity.put(critical, Severity.Critical);
	}



	@Override
	public void saveAlertInRepository(Alert alert) {
		saveAlert(alert);
	}

	/**
	 * Saves Alerts in Mongo
	 * @param alert the alert to save
	 * @return the saved alert
	 */
	private Alert saveAlert(Alert alert){
		return alertsRepository.save(alert);
	}

	public NavigableMap<Integer, Severity> getScoreToSeverity() {
		return scoreToSeverity;
	}

	@Override
	public Alerts findAll(PageRequest pageRequest) {
		return alertsRepository.findAll(pageRequest);
	}

	@Override
	public Long count(PageRequest pageRequest) {
		return alertsRepository.count(pageRequest);
	}

	private Set<String> getUserIds(String entityTags, String entityId) {
		Set<String> ids = null;
		if (entityTags != null) {
			String[] tagsFilter = entityTags.split(",");
			ids = userService.findIdsByTags(tagsFilter, entityId);
		}

		return ids;
	}


	@Override
	public Alerts findAlertsByFilters(PageRequest pageRequest, String severityArray, String statusArrayFilter,
			String feedbackArrayFilter, String dateRangeFilter, String entityName, String entityTags, String entityId,
									  List<String> indicatorIds) {
		Set<String> ids = getUserIds(entityTags, entityId);
		if (ids == null && entityId != null) {
			ids = new HashSet<>();
			ids.addAll(Arrays.asList(entityId.split(",")));
		}

		return alertsRepository.findAlertsByFilters(pageRequest, severityArray, statusArrayFilter, feedbackArrayFilter, dateRangeFilter,
				entityName, ids, indicatorIds);
	}


	@Override
	public Long countAlertsByFilters(PageRequest pageRequest, String severityArray, String statusArrayFilter,
			String feedbackArrayFilter, String dateRangeFilter, String entityName, String entityTags, String entityId,
									 List<String> indicatorIds) {
		Set<String> ids = getUserIds(entityTags, entityId);
		if (ids == null && entityId != null) {
			ids = new HashSet<>();
			ids.addAll(Arrays.asList(entityId.split(",")));
		}

		return alertsRepository.countAlertsByFilters(pageRequest, severityArray, statusArrayFilter, feedbackArrayFilter, dateRangeFilter,
				entityName, ids, indicatorIds);
	}

	@Override
	public void add(Alert alert) {
		alertsRepository.add(alert);
	}

	@Override
	public void delete(String id) {
		alertsRepository.delete(id);
	}

	@Override
	public Alert getAlertById(String id) {
		return alertsRepository.getAlertById(id);
	}

	@Override
	public Map<String, Integer> groupCount(String fieldName, String severityArrayFilter, String statusArrayFilter,
										   String feedbackArrayFilter, String dateRangeFilter, String entityName,
										   String entityTags, String entityId, List<String> indicatorIds){

		Set<String> ids = getUserIds(entityTags, entityId);
		if (ids == null && entityId != null) {
			ids = new HashSet<>();
			ids.addAll(Arrays.asList(entityId.split(",")));
		}

		return alertsRepository.groupCount(fieldName,severityArrayFilter, statusArrayFilter, feedbackArrayFilter,
						dateRangeFilter, entityName, ids, indicatorIds);
	}

	@Override
	public List<Alert> getAlertSummary(List<String> severities, long endDate) {
		return alertsRepository.getAlertSummary(severities, endDate);
	}

	@Override
	public void removeRedundantAlertsForUser(String username, String alertId) {
		alertsRepository.removeRedundantAlertsForUser(username, alertId);
	}
}
