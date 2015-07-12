package fortscale.services;

import fortscale.domain.core.*;

import java.util.Date;
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
	public void saveAlertInRepository(Alert alert);

	/**
	 * returns a conversion map from score to severity
	 * @return
	 */
	public NavigableMap<Integer, Severity> getScoreToSeverity();
}
