package fortscale.services.impl;

import fortscale.domain.core.AlertStatus;
import fortscale.domain.core.EntityType;
import fortscale.domain.core.Severity;
import fortscale.domain.core.dao.AlertsRepository;
import fortscale.domain.core.Alert;
import fortscale.services.AlertsService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Services for managing the alerts
 *
 * Date: 6/23/2015.
 */
@Service("AlertsService")
public class AlertsServiceImpl implements AlertsService, InitializingBean {

	/**
	 * Mongo repository for alerts
	 */
	@Autowired
	private AlertsRepository alertsRepository;


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
	public Alert createTransientAlert(EntityType entityType, String entityName, Date date,
			String rule, Map<String, String> evidences, String cause, Integer score, AlertStatus status, String comment) {

		// calculate severity
		Severity severity = scoreToSeverity.get(scoreToSeverity.floorKey(score));
		String uuid = System.currentTimeMillis() + entityName + entityType;

		return new Alert(uuid, date.getTime(), date.getTime(), entityType, entityName,  rule, evidences, cause, score, severity, status, comment);
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
}
