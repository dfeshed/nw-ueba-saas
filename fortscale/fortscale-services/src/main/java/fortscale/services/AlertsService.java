package fortscale.services;

import fortscale.domain.core.AlertStatus;
import fortscale.domain.core.EntityType;
import fortscale.domain.core.dao.rest.Alert;

import java.util.Date;
import java.util.Map;

/**
 * All services that handle inner cache and want to allow direct access to the cache.
 * Main use is for streaming task updates of the cache after update have arrived to kafka update topic.
 *
 */
public interface AlertsService {

	/**
	 * Create new object (transient) of alert
	 * @param entityType    The type of the entity
	 * @param entityName    The name of the entity
	 * @param date            The date of the alert (single date for single event)
	 * @param rule that assigned rule that generates the alert
	 * @param evidences the list of evidences that are associated to to the alerts   \
	 * @param cause the cause of the alert
	 * @param score                The score
	 * @param status the status of the alert
	 * @param comment a comment for the user to enter
	 * @return	New alert
	 */
	public Alert createTransientAlert(EntityType entityType, String entityName, Date date,
									  String rule, Map<Long, String> evidences, String cause, Integer score, AlertStatus status, String comment);

	/**
	 * Create new alert in Mongo
	 * @param alert	The alert
	 */
	public void saveAlertInRepository(Alert alert);
}
