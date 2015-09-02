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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import parquet.org.slf4j.Logger;
import parquet.org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
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
	final String ENTITY_NAME_FIELD  = "normalized_username";

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

	/**
	 * User service (for user resolving)
	 */
	@Autowired private UserService userService;

	// general evidence creation setting
	@Value("${fortscale.smart.f.score}")
	private int fFeatureTresholdScore;
	@Value("${fortscale.smart.p.count}")
	private int pFeatureTreshholdCount;

	// Reading the json object keys
	@Value("${fortscale.smart.f.field.startdate}")
	private String startDateKey;
	@Value("${fortscale.smart.f.field.enddate}")
	private String endDateKey;
	@Value("${fortscale.smart.f.field.featurename}")
	private String featureNameKey;
	@Value("${fortscale.smart.f.field.datasources}")
	private String dataSourcesKey;
	@Value("${fortscale.smart.f.field.score}")
	private String scoreKey;
	@Value("${fortscale.smart.f.field.entities}")
	private String entitiesKey;
	@Value("${fortscale.smart.f.field.anomalyvalue}")
	private String anomalyValueKey;

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
					Long startDate = (Long) insertStreamOutput.get(startDateKey);
					Long endDate = (Long) insertStreamOutput.get(endDateKey);
					// TODO: missing!
					EntityType entityType = EntityType.User;
					JSONObject entities = (JSONObject) JSONValue.parse((String) insertStreamOutput.get(entitiesKey));
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

					Double score = (Double) insertStreamOutput.get(scoreKey);
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

		// Iterate through the features
		for (JSONObject aggregatedFeatureEvent : entityEvent.getAggregated_feature_events())
		{
			// Get the evidence and add it to list
			Evidence evidence = createEvidenceFromAggregatedFeature(aggregatedFeatureEvent);
			if (evidence != null) {
				evidenceList.add(evidence);
			}
		}

		return evidenceList;
	}

	/**
	 * Create single evidence
	 * @param aggregatedFeatureEvent
	 * @return
	 */
	private Evidence createEvidenceFromAggregatedFeature(JSONObject aggregatedFeatureEvent) {

		// Get feature type
		String featureType = getFeatureType(aggregatedFeatureEvent);

		// Depended on the feature type, get the evidence
		switch (featureType) {
		case F_FEATURE_VALUE:
			return getFFeature(aggregatedFeatureEvent);
		case P_FEATURE_VALUE:
			return getPFeature(aggregatedFeatureEvent);
		default:
			logger.debug("Illegal feature type. Feature type: " + featureType);
			break;
		}

		return null;
	}

	/**
	 * Get the feature type out of the JSON representation of the object
	 * @param aggregatedFeatureEvent
	 * @return
	 */
	private String getFeatureType(JSONObject aggregatedFeatureEvent) {
		return aggregatedFeatureEvent.getAsString(AGGREGATED_FEATURE_TYPE_KEY);
	}

	/**
	 * Handle P feature - fetch existing evidence or create a new evidence
	 * @param aggregatedFeatureEvent
	 * @return
	 */
	private Evidence getPFeature(JSONObject aggregatedFeatureEvent) {
		// TODO: plcaeholder for P features
		return null;
	}

	/**
	 * Handle F feature - fetch existing evidence or create a new evidence
	 * @param aggregatedFeatureEvent
	 * @return
	 */
	private Evidence getFFeature(JSONObject aggregatedFeatureEvent) {

		// Filter features with low score
		double score = getScore(aggregatedFeatureEvent);
		if (score < fFeatureTresholdScore) {
			return null;
		}

		// Read common information for finding and creation evidence
		EntityType entityType = EntityType.User;
		String entityValue = getEntityValue(aggregatedFeatureEvent);
		Long startDate = new Long((Integer)aggregatedFeatureEvent.get(startDateKey)) * 1000;
		Long endDate = new Long((Integer)aggregatedFeatureEvent.get(endDateKey)) * 1000;
		String dataEntities = getDataSource(aggregatedFeatureEvent);
		String featureName = aggregatedFeatureEvent.getAsString(featureNameKey);

		// try to fetch evidence from repository
		Evidence fEvidence = findFEvidence(entityType, entityValue, startDate, endDate, dataEntities, featureName);

		// In case we found previously created evidence in the repository, return it
		if (fEvidence != null) {
			return fEvidence;
		}

		// Else, create the evidence in the repository and return it
		return createFEvidence(entityType, entityValue, startDate, endDate, dataEntities, score, featureName, aggregatedFeatureEvent);
	}

	/**
	 * Find evidnece in the repository for F feature
	 * @param entityType
	 * @param entityValue
	 * @param startDate
	 * @param endDate
	 * @param dataEntities
	 * @param featureName
	 * @return
	 */
	private Evidence findFEvidence(EntityType entityType, String entityValue, Long startDate, Long endDate,
			String dataEntities, String featureName) {
		return evidencesService.findFEvidence(entityType, entityValue, startDate, endDate, dataEntities, featureName);
	}

	/**
	 * Read score from JSON
	 * @param aggregatedFeatureEvent
	 * @return
	 */
	private double getScore(JSONObject aggregatedFeatureEvent) {
		return (double)aggregatedFeatureEvent.get(scoreKey);
	}

	/**
	 * Read entities from JSON
	 * @param aggregatedFeatureEvent
	 * @return
	 */
	private String getEntityValue(JSONObject aggregatedFeatureEvent) {
		Map<String, String> entities = (Map)aggregatedFeatureEvent.get(entitiesKey);
		return entities.get(USER_ENTITY_KEY);
	}

	/**
	 * Read data source from JSON
	 * @param aggregatedFeatureEvent
	 * @return
	 */
	private String getDataSource(JSONObject aggregatedFeatureEvent) {
		ArrayList<String> dataSources = (ArrayList)aggregatedFeatureEvent.get(dataSourcesKey);
		return dataSources.get(0);
	}

	/**
	 * Create evidence for F feature and save it to the repository
	 *
	 * @param entityType
	 * @param entityName
	 * @param startDate
	 * @param endDate
	 * @param dataEntities
	 * @param featureName
	 * @param aggregatedFeatureEvent  @return
	 */
	private Evidence createFEvidence(EntityType entityType, String entityName, Long startDate, Long endDate,
			String dataEntities, Double score, String featureName, JSONObject aggregatedFeatureEvent) {

		String anomalyValue = aggregatedFeatureEvent.getAsString(anomalyValueKey);
		List<String> dataEntitiesArray = new ArrayList<>();
		dataEntitiesArray.add(dataEntities);

		Evidence evidence = evidencesService.createTransientEvidence(entityType, ENTITY_NAME_FIELD, entityName,
				EvidenceType.AnomalyAggregatedEvent, new Date(startDate), new Date(endDate), dataEntitiesArray, score, anomalyValue,featureName,
				1, null);

		try {
			evidencesService.saveEvidenceInRepository(evidence);
		} catch (DuplicateKeyException e) {
			logger.warn("Got duplication for evidence {}. Going to drop it.", evidence.toString());
			// In case this evidence is duplicated, we don't send it to output topic and continue to next score
			return null;
		}

		return evidence;
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
