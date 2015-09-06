package fortscale.streaming.alert.subscribers;

import fortscale.aggregation.feature.event.AggrEvent;
import fortscale.aggregation.feature.event.AggrEventEvidenceFilteringStrategyEnum;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.domain.core.*;
import fortscale.services.AlertsService;
import fortscale.services.ComputerService;
import fortscale.services.EvidencesService;
import fortscale.services.UserService;
import fortscale.streaming.alert.subscribers.evidence.filter.EvidenceFilter;
import fortscale.streaming.alert.subscribers.evidence.filter.FilterByHighScore;
import fortscale.streaming.alert.subscribers.evidence.filter.FilterByHighScorePerValue;
import fortscale.streaming.task.EvidenceCreationTask;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import parquet.org.slf4j.Logger;
import parquet.org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by tomerd on 30/08/2015.
 */
@Configurable(preConstruction = true)
public class SmartAlertCreationSubscriber extends AbstractSubscriber {

	//TODO: Move to esper rule
	static String ALERT_TITLE = "SMART alert";


	static String USER_ENTITY_KEY = "normalized_username";
	final String F_FEATURE_VALUE = "F";
	final String P_FEATURE_VALUE = "P";
	final String ENTITY_NAME_FIELD = "normalized_username";
	/**
	 * Logger
	 */
	private static Logger logger = LoggerFactory.getLogger(SmartAlertCreationSubscriber.class);

	/**
	 * Aggregated feature configuration service
	 */
	@Autowired protected AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;

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
	@Value("${fortscale.smart.f.score}") private int fFeatureTresholdScore;
	@Value("${fortscale.smart.p.count}") private int pFeatureTreshholdCount;

	// Reading the json object keys
	@Value("${fortscale.smart.f.field.startdate}") private String startDateKey;
	@Value("${fortscale.smart.f.field.enddate}") private String endDateKey;
	@Value("${fortscale.smart.f.field.featurename}") private String featureNameKey;
	@Value("${fortscale.smart.f.field.datasources}") private String dataSourcesKey;
	@Value("${fortscale.smart.f.field.score}") private String scoreKey;
	@Value("${fortscale.smart.f.field.entities}") private String entitiesKey;
	@Value("${fortscale.smart.f.field.anomalyvalue}") private String anomalyValueKey;


	/**
	 * Create alert from entity event
	 *
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
		Alert alert = new Alert(ALERT_TITLE, entityEvent.getStart_time_unix(), entityEvent.getEnd_time_unix(), EntityType.User, entityName, evidences, roundScore, severity, AlertStatus.Open, AlertFeedback.None, "", entityId);

		//Save alert to mongoDB
		alertsService.saveAlertInRepository(alert);
	}

	/**
	 * Create alert from stream events
	 *
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
					Alert alert = new Alert(title, startDate, endDate, entityType, entityName, evidences, roundScore, severity, AlertStatus.Open, AlertFeedback.None, "", entityId);

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
	 *
	 * @param entityEvent
	 * @return
	 */
	private List<Evidence> createEvidencesList(EntityEvent entityEvent) {
		List<Evidence> evidenceList = new ArrayList<>();

		// Iterate through the features
		for (JSONObject aggregatedFeatureEvent : entityEvent.getAggregated_feature_events()) {
			AggrEvent aggrEvent = new AggrEvent(aggregatedFeatureEvent);

			// Get the evidence and add it to list
			List<Evidence> evidences = createEvidencesFromAggregatedFeature(aggrEvent);
			if (evidences != null) {
				evidenceList.addAll(evidences);
			}
		}

		return evidenceList;
	}

	/**
	 * Create single evidence
	 *
	 * @param aggregatedFeatureEvent
	 * @return
	 */
	private List<Evidence> createEvidencesFromAggregatedFeature(AggrEvent aggregatedFeatureEvent) {
		// Depended on the feature type, get the evidence
		switch (aggregatedFeatureEvent.getAggregatedFeatureType()) {
		case F_FEATURE_VALUE:
			return getFFeature(aggregatedFeatureEvent);
		case P_FEATURE_VALUE:
			return getPFeature(aggregatedFeatureEvent);
		default:
			logger.debug("Illegal feature type. Feature type: " + aggregatedFeatureEvent.getAggregatedFeatureType());
			break;
		}

		return null;
	}

	/**
	 * Handle P feature - fetch existing evidence or create a new evidence
	 *
	 * @param aggregatedFeatureEvent
	 * @return
	 */
	private List<Evidence> getPFeature(AggrEvent aggregatedFeatureEvent) {
		if (aggregatedFeatureEvent.getAggregatedFeatureValue() < pFeatureTreshholdCount) {
			return null;
		}

		// Fetch evidences from repository
		List<Evidence> pEvidences = findPEvidences(aggregatedFeatureEvent);

		filterPEvidences(pEvidences, aggregatedFeatureEvent);

		return pEvidences;
	}

	private void filterPEvidences(List<Evidence> pEvidences, AggrEvent aggregatedFeatureEvent) {
		String featureName = aggregatedFeatureEvent.getAggregatedFeatureName();
		AggrEventEvidenceFilteringStrategyEnum filteringStrategy =
				aggregatedFeatureEventsConfService.getEvidenceReadingStrategy(featureName);

		EvidenceFilter evidenceFilter;

		switch (filteringStrategy) {
		case HIGHESTSCORE:
			evidenceFilter = new FilterByHighScore();
			break;
		case HIGHESTSCOREPERVALUE:
			evidenceFilter = new FilterByHighScorePerValue();
			break;
		default:
			// If a filter function is not define, do not filter
			return;
		}

		evidenceFilter.filterList(pEvidences, aggregatedFeatureEvent);
	}

	/**
	 * Handle F feature - fetch existing evidence or create a new evidence
	 *
	 * @param aggregatedFeatureEvent
	 * @return
	 */
	private List<Evidence> getFFeature(AggrEvent aggregatedFeatureEvent) {

		// Filter features with low score
		if (aggregatedFeatureEvent.getScore() < fFeatureTresholdScore) {
			return null;
		}

		// Read common information for finding and creation evidence
		String entityValue = aggregatedFeatureEvent.getContext().get(USER_ENTITY_KEY);
		String dataSource = (String) aggregatedFeatureEvent.getDataSources().get(0);


		// try to fetch evidence from repository
		List<Evidence> fEvidences = findFEvidences(EntityType.User, entityValue, aggregatedFeatureEvent.getStartTime() * 1000, aggregatedFeatureEvent.getEndTime() * 1000, dataSource, aggregatedFeatureEvent.getAggregatedFeatureName());

		// In case we found previously created evidence in the repository, return it
		if (fEvidences != null && !fEvidences.isEmpty()) {
			return fEvidences;
		}

		// Else, create the evidence in the repository and return it
		return createFEvidence(EntityType.User, entityValue, aggregatedFeatureEvent.getStartTime(), aggregatedFeatureEvent.getEndTime(), aggregatedFeatureEvent.getDataSourcesAsList(), aggregatedFeatureEvent.getScore(), aggregatedFeatureEvent.getAggregatedFeatureName(), aggregatedFeatureEvent);
	}

	/**
	 * Find evidneces in the repository for P feature
	 * @param aggrEvent
	 * @return
	 */
	private List<Evidence> findPEvidences(AggrEvent aggrEvent) {
		EntityType entityType = EntityType.User;
		String entityValue = aggrEvent.getContext().get(USER_ENTITY_KEY);
		Long startDate = aggrEvent.getStartTime() * 1000;
		Long endDate = aggrEvent.getEndTime() * 1000;
		String dataSource = (String) aggrEvent.getDataSources().get(0);
		String anomalyType =  aggregatedFeatureEventsConfService.getAnomalyType(aggrEvent.getAggregatedFeatureName());

		return findPEvidences(entityType, entityValue, startDate, endDate, dataSource, anomalyType);
	}

	private List<Evidence> findPEvidences(EntityType entityType, String entityValue, Long startDate, Long endDate,
			String dataSource, String anomalyType) {
		return evidencesService.findFeatureEvidences(entityType, entityValue, startDate, endDate, dataSource, anomalyType);
	}

	/**
	 * Find evidneces in the repository for F feature
	 *
	 * @param entityType
	 * @param entityValue
	 * @param startDate
	 * @param endDate
	 * @param dataEntities
	 * @param featureName
	 * @return
	 */
	private List<Evidence> findFEvidences(EntityType entityType, String entityValue, Long startDate, Long endDate,
			String dataEntities, String featureName) {
		return evidencesService.findFeatureEvidences(entityType, entityValue, startDate, endDate,
				dataEntities, featureName);
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
	 * @param aggregatedFeatureEvent @return
	 */
	private List<Evidence> createFEvidence(EntityType entityType, String entityName, Long startDate, Long endDate,
			List<String> dataEntities, Double score, String featureName, AggrEvent aggregatedFeatureEvent) {

		EvidenceTimeframe evidenceTimeframe = EvidenceCreationTask.calculateEvidenceTimeframe(
				EvidenceType.AnomalyAggregatedEvent, startDate, endDate);

		Evidence evidence = evidencesService.createTransientEvidence(entityType, ENTITY_NAME_FIELD, entityName,
				EvidenceType.AnomalyAggregatedEvent, new Date(startDate * 1000), new Date(endDate * 1000), dataEntities, score,
				aggregatedFeatureEvent.getAggregatedFeatureValue().toString(), featureName, 1, evidenceTimeframe);

		try {
			evidencesService.saveEvidenceInRepository(evidence);
		} catch (DuplicateKeyException e) {
			logger.warn("Got duplication for evidence {}. Going to drop it.", evidence.toString());
			// In case this evidence is duplicated, we don't send it to output topic and continue to next score
			return null;
		} catch (Exception e){
			logger.warn("Error while writing evidence to repository. Error: " + e.getMessage());
			return null;
		}

		// To keep the structure, create a single evidence list..
		List<Evidence> evidences = new ArrayList<>();
		evidences.add(evidence);
		return evidences;
	}

	/**
	 * Create evidences list from Map
	 *
	 * @param insertStreamOutput
	 * @return
	 */
	private List<Evidence> createEvidencesList(Map insertStreamOutput) {
		return null;
	}
}
