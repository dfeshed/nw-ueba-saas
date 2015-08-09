package fortscale.services;

import java.util.NavigableMap;

import fortscale.domain.core.Alert;
import fortscale.domain.core.Severity;

/**
 * Service that handles Alerts and stores them in MongoDB
 *
 */
public interface AlertsService {

	/**
	 * Create new alert in Mongo
	 * @param alert	The alert
	 */
	public void saveAlertInRepository(Alert alert);

	/**
	 * returns a conversion map from score to severity
	 * @return
	 */
	public NavigableMap<Integer, Severity> getScoreToSeverity();
}
