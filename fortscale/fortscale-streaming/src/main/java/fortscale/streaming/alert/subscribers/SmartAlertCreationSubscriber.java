package fortscale.streaming.alert.subscribers;

import fortscale.domain.core.*;
import fortscale.services.AlertsService;
import fortscale.services.ComputerService;
import fortscale.services.EvidencesService;
import fortscale.services.UserService;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import parquet.org.slf4j.Logger;
import parquet.org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by tomerd on 30/08/2015.
 */
public class SmartAlertCreationSubscriber extends AbstractSubscriber {

	//TODO: Move to esper rule
	static String ALERT_TITLE = "SMART alert";
	static String USER_ENTITY_KEY = "normalized_username";

	/**
	 * Logger
	 */
	private static Logger logger = LoggerFactory.getLogger(SmartAlertCreationSubscriber.class);

	/**
	 * Alerts service (for Mongo export)
	 */
	@Autowired protected AlertsService alertsService;

	/**
	 * Evidence service (for Mongo export)
	 */
	@Autowired protected EvidencesService evidencesService;

	/**
	 * Computer service (for resolving id)
	 */
	@Autowired protected ComputerService computerService;

	@Autowired private UserService userService;

	/**
	 * Create alert from entity event
	 * @param entityEvent
	 */
	public void update(EntityEvent entityEvent) {
		// Create the evidences list
		List<Evidence> evidences = createEvidencesList(entityEvent);

		// Get alert parameters
		Integer roundScore = ((Double) (entityEvent.getScore())).intValue();
		Severity severity = alertsService.getScoreToSeverity().floorEntry(roundScore).getValue();
		EntityType entityType = EntityType.User;
		String entityName = entityEvent.getContext().get(USER_ENTITY_KEY);
		String entityId;
		switch (entityType) {
		case User: {
			entityId = userService.getUserId(entityName);
			break;
		}
		case Machine: {
			entityId = computerService.getComputerId(entityName);
			break;
		}
		default: {
			entityId = "";
		}
		//TODO - handle the rest of the entity types
		}

		// Create the alert
		Alert alert = new Alert(ALERT_TITLE, entityEvent.getStart_time_unix(), entityEvent.getEnd_time_unix(),
								EntityType.User, entityName, evidences, roundScore, severity,
								AlertStatus.Open, AlertFeedback.None,  "", entityId);

		//Save alert to mongoDB
		alertsService.saveAlertInRepository(alert);
	}

	/**
	 * Create alert from stream events
	 * @param insertStream
	 */
	public void update(Map[] insertStream) {
		if (insertStream != null) {
			for (Map insertStreamOutput : insertStream) {
				try {
					// Create evidences list
					List<Evidence> evidences = createEvidencesList(insertStreamOutput);

					// Get alert parameters
					//TODO: take from esper rule
					String title = ALERT_TITLE; //(String) insertStreamOutput.get("title");
					Long startDate = (Long) insertStreamOutput.get("start_time_unix");
					Long endDate = (Long) insertStreamOutput.get("end_time_unix");
					// TODO: missing!
					EntityType entityType = EntityType.User;
					JSONObject entities = (JSONObject) JSONValue.parse((String) insertStreamOutput.get("context"));
					String entityName = entities.getAsString("normalized_username");
					String entityId;
					switch (entityType) {
					case User: {
						entityId = userService.getUserId(entityName);
						break;
					}
					case Machine: {
						entityId = computerService.getComputerId(entityName);
						break;
					}
					default: {
						entityId = "";
					}
					//TODO - handle the rest of the entity types
					}

					Double score = (Double) insertStreamOutput.get("score");
					Integer roundScore = score.intValue();
					Severity severity = alertsService.getScoreToSeverity().floorEntry(roundScore).getValue();

					// Create the alert
					Alert alert = new Alert(title, startDate, endDate, entityType, entityName, evidences,
											roundScore, severity, AlertStatus.Open, AlertFeedback.None, "", entityId);

					//Save alert to mongoDB
					alertsService.saveAlertInRepository(alert);
				} catch (RuntimeException ex) {
					logger.error(ex.getMessage(), ex);
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * Create evidences list from entity event
	 * @param entityEvent
	 * @return
	 */
	private List<Evidence> createEvidencesList(EntityEvent entityEvent) {
		return null;
	}

	/**
	 * Create evidences list from Map
	 * @param insertStreamOutput
	 * @return
	 */
	private List<Evidence> createEvidencesList(Map insertStreamOutput) {
		return null;
	}
}
