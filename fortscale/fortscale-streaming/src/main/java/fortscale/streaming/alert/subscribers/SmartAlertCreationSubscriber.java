package fortscale.streaming.alert.subscribers;

import fortscale.domain.core.*;
import fortscale.services.AlertsService;
import fortscale.services.ComputerService;
import fortscale.services.EvidencesService;
import fortscale.services.UserService;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import parquet.org.slf4j.Logger;
import parquet.org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by tomerd on 30/08/2015.
 */
public class SmartAlertCreationSubscriber extends AbstractSubscriber {

	//TODO: Move to esper rule
	static String ALERT_TITLE = "SMART alert";
	static String USER_ENTITY_KEY = "normalized_username";
	final String F_FEATURE_VALUE ="F";
	final String P_FEATURE_VALUE ="P";
	final String AGGREGATED_FEATURE_TYPE_KEY = "aggregated_feature_type";

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
								AlertStatus.Open, AlertFeedback.None, "", entityId);

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
					String entityName = entities.getAsString(USER_ENTITY_KEY);
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
		List<Evidence> evidenceList = new ArrayList<>();

		for (JSONObject aggregatedFeatureEvent : entityEvent.getAggregated_feature_events())
		{
			evidenceList.add(createEvidenceFromAggregatedFeature(aggregatedFeatureEvent));
		}

		return evidenceList;
	}

	private Evidence createEvidenceFromAggregatedFeature(JSONObject aggregatedFeatureEvent) {
		String featureType = getFeatureType(aggregatedFeatureEvent);
		switch (featureType) {
		case F_FEATURE_VALUE:
			return getFFeature(aggregatedFeatureEvent);
		// As for now, we do not create evidences for P features.
		// P is only used for the joker score
		//case P_FEATURE_VALUE:
			//return getPFeature(aggregatedFeatureEvent);
		default:
			logger.debug("Illegal feature type. Feature type: " + featureType);
			break;
		}

		return null;
	}

	private String getFeatureType(JSONObject aggregatedFeatureEvent) {
		return aggregatedFeatureEvent.getAsString(AGGREGATED_FEATURE_TYPE_KEY);
	}

	private Evidence getPFeature(JSONObject aggregatedFeatureEvent) {
		// plcaeholder for P features
		return null;
	}

	private Evidence getFFeature(JSONObject aggregatedFeatureEvent) {
		Evidence fEvidence;
		fEvidence = findFEvidence(aggregatedFeatureEvent);

		// In case we found previously created evidence in the repository, return it
		if (fEvidence != null) {
			return fEvidence;
		}

		// Else, create the evidence in the repository and return it
		return createFEvidence(aggregatedFeatureEvent);
	}

	/**
	 * Find evidnece in the repository for F feature
	 * @param aggregatedFeatureEvent
	 * @return
	 */
	private Evidence findFEvidence(JSONObject aggregatedFeatureEvent) {
		if (aggregatedFeatureEvent == null){
			return null;
		}

		EntityType entityType = EntityType.User;
		String entityValue = getEntityValue(aggregatedFeatureEvent);
		Long startDate = (Long) aggregatedFeatureEvent.get("start_time_unix");
		Long endDate = (Long) aggregatedFeatureEvent.get("date_time_unix");
		String dataEntities = getDataSource(aggregatedFeatureEvent);
		String featureName = aggregatedFeatureEvent.getAsString("bucket_conf_name");
		return evidencesService.findFEvidence(entityType, entityValue, startDate, endDate, dataEntities, featureName);
	}

	private String getEntityValue(JSONObject aggregatedFeatureEvent) {
		JSONObject entities = (JSONObject) JSONValue.parse((String) aggregatedFeatureEvent.get("context"));
		return entities.getAsString(USER_ENTITY_KEY);
	}

	private String getDataSource(JSONObject aggregatedFeatureEvent) {
		JSONArray dataSources = (JSONArray) JSONValue.parse((String) aggregatedFeatureEvent.get("data_sources"));
		return dataSources.get(0).toString();
	}

	/**
	 * Create evidence for F feature
	 * @param aggregatedFeatureEvent
	 * @return
	 */
	private Evidence createFEvidence(JSONObject aggregatedFeatureEvent) {
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
