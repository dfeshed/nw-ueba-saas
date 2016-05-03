package fortscale.streaming.alert.subscribers;

import fortscale.aggregation.feature.event.AggrEvent;
import fortscale.aggregation.feature.event.AggrEventEvidenceFilteringStrategyEnum;
import fortscale.aggregation.feature.event.AggrFeatureEventBuilderService;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.domain.core.*;
import fortscale.services.*;
import fortscale.streaming.alert.subscribers.evidence.filter.EvidenceFilter;
import fortscale.streaming.alert.subscribers.evidence.filter.FilterByHighScorePerUnqiuePValue;
import fortscale.streaming.alert.subscribers.evidence.filter.FilterByHighestScore;
import fortscale.streaming.task.EvidenceCreationTask;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimestampUtils;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * create alert from smart events. smart events may appear in combination with other evidences, such as tags and notifications.
 * Created by tomerd on 30/08/2015.
 */
public class SmartAlertCreationSubscriber extends AbstractSubscriber {


	static String USER_ENTITY_KEY = "normalized_username";
	final String F_FEATURE_VALUE = "F";
	final String P_FEATURE_VALUE = "P";
	final String ENTITY_NAME_FIELD = "normalized_username";
	final String NOTIFICATION_EVIDENCE_TYPE = "Notification";

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(SmartAlertCreationSubscriber.class);

	/**
	 * Aggregated feature configuration service
	 */
	@Autowired protected AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
	
	/**
	 * Aggregated feature event builder service
	 */
	@Autowired private AggrFeatureEventBuilderService aggrFeatureEventBuilderService;

	/**
	 * Tags service
	 */
	@Autowired protected TagService tagService;

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

	/**
	 * Alert forwarding service (for forwarding new alerts)
	 */
	@Autowired private ForwardingService forwardingService;

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

	//<editor-fold desc="Esper update methods">
	/**
	 * Create alert directly from rule without tags
	 * @param title
	 * @param severity
	 * @param entityType
	 * @param entityName
	 * @param aggregatedFeatureEvents
	 * @param startTime
	 * @param endTime
	 * @param score
	 */
	public void update(String title, String severity, EntityType entityType, String entityName,
			List<JSONObject> aggregatedFeatureEvents, long startTime, long endTime, Double score) {
		// Create empty tags list
		List<String> tags = new ArrayList<>();
		update(title, severity, entityType, entityName, aggregatedFeatureEvents, startTime, endTime, score, tags);
	}

	/**
	 * Create alert directly from rule
	 * @param title
	 * @param severity
	 * @param entityType
	 * @param entityName
	 * @param aggregatedFeatureEvents
	 * @param startTime
	 * @param endTime
	 * @param score
	 * @param tags
	 */
	public void update(String title, String severity, EntityType entityType, String entityName,
			List<JSONObject> aggregatedFeatureEvents, long startTime, long endTime, Double score,List<String> tags) {

		// Convert to miliseconds


		startTime = TimestampUtils.convertToMilliSeconds(startTime);
		endTime =TimestampUtils.convertToMilliSeconds(endTime);

		// Create the evidences list
		List<Evidence> evidences = createEvidencesList(startTime, endTime, entityName, entityType,
				aggregatedFeatureEvents, tags);

		// Get alert parameters
		Integer roundScore = score.intValue();
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

		// Create Severity Enum from String value
		Severity severityEnum = Severity.valueOf(severity);

		// Create the alert
		Alert alert = new Alert(title, startTime, endTime, EntityType.User, entityName, evidences, evidences.size(),
				roundScore,	severityEnum, AlertStatus.Open, AlertFeedback.None, "", entityId, null);

		//Save alert to mongoDB
		alertsService.saveAlertInRepository(alert);

		forwardingService.forwardNewAlert(alert);

	}

	//</editor-fold>

	//<editor-fold desc="General evidences handling">
	/**
	 * Create single evidence
	 *
	 * @param aggregatedFeatureEvent
	 * @return
	 */
	private List<Evidence> createEvidencesFromAggregatedFeature(AggrEvent aggregatedFeatureEvent) {
		// Depended on the feature type, get the evidence
		switch (aggregatedFeatureEvent.getFeatureType()) {
		case F_FEATURE_VALUE:
			return getFFeature(aggregatedFeatureEvent);
		case P_FEATURE_VALUE:
			return getPFeature(aggregatedFeatureEvent);
		default:
			logger.debug("Illegal feature type. Feature type: " + aggregatedFeatureEvent.getFeatureType());
			break;
		}

		return null;
	}

	/**
	 * Create list of evidences
	 * @param startDate
	 * @param endDate
	 * @param entityName
	 * @param entityType
	 * @param aggregated_feature_events
	 * @param tags
	 * @return
	 */
	private List<Evidence> createEvidencesList(Long startDate, Long endDate, String entityName, EntityType entityType,
			List<JSONObject> aggregated_feature_events, List<String> tags) {
		// New evidence list
		List<Evidence> evidenceList = new ArrayList<>();

		// Iterate through the features
		for (JSONObject aggregatedFeatureEvent : aggregated_feature_events) {
			AggrEvent aggrEvent = aggrFeatureEventBuilderService.buildEvent(aggregatedFeatureEvent);
			// Get the evidence and add it to list
			List<Evidence> evidences = createEvidencesFromAggregatedFeature(aggrEvent);
			if (evidences != null) {
				evidenceList.addAll(evidences);
			}
		}

		// Read notifications evidence from repository
		List<Evidence> notificationEvidences = findNotificationEvidences(startDate, endDate, entityName);
		if (notificationEvidences != null) {
			evidenceList.addAll(notificationEvidences);
		}

		// Create tag evidences
		List<Evidence> tagsEvidences =  createTagEvidences(entityType, entityName, startDate, endDate, tags);
		if (tagsEvidences != null) {
			evidenceList.addAll(tagsEvidences);
		}

		return evidenceList;
	}

	//</editor-fold>

	//<editor-fold desc="Notification and tag evidence handling">
	private List<Evidence> findNotificationEvidences(Long startTime, long endTime, String entityValue) {
		return evidencesService.findByEndDateBetweenAndEvidenceTypeAndEntityName(startTime, endTime, NOTIFICATION_EVIDENCE_TYPE, entityValue);
	}

	/**
	 * Create tag evidences
	 * @param entityType
	 * @param entityName
	 * @param startDate
	 * @param endDate
	 * @param tags
	 * @return
	 */
	private List<Evidence> createTagEvidences(EntityType entityType, String entityName, Long startDate, long endDate,
			List<String> tags) {

		// Create new evidence list
		List<Evidence> evidences = new ArrayList<>();

		// Iterate the tags list and create evidence for each tag
		for (String tagStr : tags) {
			Tag tag = tagService.getTag(tagStr);
			if (tag != null && tag.getCreatesIndicator()) {
				Evidence evidence = evidencesService.createTagEvidence(entityType, Evidence.entityTypeFieldNameField,
						entityName, startDate, endDate, tagStr);
				evidences.add(evidence);
			}
		}

		return  evidences;
	}
	//</editor-fold>

	//<editor-fold desc="P features handling">
	/**
	 * Handle P feature - fetch existing evidence or create a new evidence
	 *
	 * @param aggregatedFeatureEvent
	 * @return
	 */
	private List<Evidence> getPFeature(AggrEvent aggregatedFeatureEvent) {
		List<Evidence> pEvidences = new ArrayList<>();

		if (aggregatedFeatureEvent.getAggregatedFeatureValue() <= pFeatureTreshholdCount) {
			return pEvidences;
		}

		// Fetch evidences from repository
		pEvidences = findPEvidences(aggregatedFeatureEvent);

		return pEvidences;
	}

	/**
	 * Filter the Evidences list, created from P features
	 * @param pEvidences
	 * @param aggregatedFeatureEvent
	 */
	private void filterPEvidences(List<Evidence> pEvidences, AggrEvent aggregatedFeatureEvent) {

		// Get the filtering strategy
		String featureName = aggregatedFeatureEvent.getAggregatedFeatureName();
		AggrEventEvidenceFilteringStrategyEnum filteringStrategy =
				aggregatedFeatureEventsConfService.getEvidenceReadingStrategy(featureName);

		EvidenceFilter evidenceFilter;

		switch (filteringStrategy) {
		case HIGHEST_SCORE:
			evidenceFilter = new FilterByHighestScore();
			break;
		case HIGHEST_SCORE_PER_VALUE:
			evidenceFilter = new FilterByHighScorePerUnqiuePValue();
			break;
		default:
			// If a filter function is not define, do not filter
			logger.warn("No evidence filter was define; Not filtering evidences");
			return;
		}

		evidenceFilter.filterList(pEvidences, aggregatedFeatureEvent);
	}

	

	/**
	 * Find evidneces in the repository for P feature
	 *
	 * @param aggrEvent
	 * @return
	 */
	private List<Evidence> findPEvidences(AggrEvent aggrEvent) {

		// Get the parameters for reading evidences
		EntityType entityType = EntityType.User;
		String entityValue = aggrEvent.getContext().get(USER_ENTITY_KEY);
		Long startDate = TimestampUtils.convertToMilliSeconds(aggrEvent.getStartTimeUnix());
		Long endDate = TimestampUtils.convertToMilliSeconds(aggrEvent.getEndTimeUnix());
		String dataSource = (String) aggrEvent.getDataSources().get(0);
		String anomalyType = aggregatedFeatureEventsConfService.getAnomalyType(aggrEvent.getAggregatedFeatureName());

		// Read evidences from mongo
		List<Evidence> evidences = findPEvidences(entityType, entityValue, startDate, endDate, dataSource, anomalyType);

		// Filter results
		if (evidences != null && evidences.size() > 0) {
			filterPEvidences(evidences, aggrEvent);
		}

		return evidences;
	}

	/**
	 * Find evidneces in the repository for P feature
	 * @param entityType
	 * @param entityValue
	 * @param startDate
	 * @param endDate
	 * @param dataSource
	 * @param anomalyType
	 * @return
	 */
	private List<Evidence> findPEvidences(EntityType entityType, String entityValue, Long startDate, Long endDate,
			String dataSource, String anomalyType) {
		return evidencesService.findFeatureEvidences(entityType, entityValue, startDate, endDate, dataSource, anomalyType);
	}
	//</editor-fold>

	//<editor-fold desc="F features handling ">
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
		List<Evidence> fEvidences = findFEvidences(EntityType.User, entityValue,
				TimestampUtils.convertToMilliSeconds(aggregatedFeatureEvent.getStartTimeUnix()),
				TimestampUtils.convertToMilliSeconds(aggregatedFeatureEvent.getEndTimeUnix()),
				dataSource, aggregatedFeatureEvent.getAggregatedFeatureName());

		// In case we found previously created evidence in the repository, return it
		if (fEvidences != null && !fEvidences.isEmpty()) {
			return fEvidences;
		}

		// Else, create the evidence in the repository and return it
		return createFEvidence(EntityType.User, entityValue,
				TimestampUtils.convertToMilliSeconds(aggregatedFeatureEvent.getStartTimeUnix()),
				TimestampUtils.convertToMilliSeconds(aggregatedFeatureEvent.getEndTimeUnix()),
				aggregatedFeatureEvent.getDataSources(), aggregatedFeatureEvent.getScore(),
				aggregatedFeatureEvent.getAggregatedFeatureName(), aggregatedFeatureEvent);
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
		return evidencesService.findFeatureEvidences(entityType, entityValue, startDate, endDate, dataEntities, featureName);
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

		EvidenceTimeframe evidenceTimeframe = EvidenceCreationTask.calculateEvidenceTimeframe(EvidenceType.AnomalyAggregatedEvent,
				TimestampUtils.convertToSeconds(startDate),
				TimestampUtils.convertToSeconds(endDate));

		Evidence evidence = evidencesService.createTransientEvidence(entityType, ENTITY_NAME_FIELD, entityName,
				EvidenceType.AnomalyAggregatedEvent, new Date(startDate), new Date(endDate), dataEntities, score,
				aggregatedFeatureEvent.getAggregatedFeatureValue().toString(), featureName,
				(int)aggregatedFeatureEvent.getAggregatedFeatureInfo().get("total"), evidenceTimeframe);

		try {
			evidencesService.saveEvidenceInRepository(evidence);
		} catch (DuplicateKeyException e) {
			logger.warn("Got duplication for evidence {}. Going to drop it.", evidence.toString());
			// In case this evidence is duplicated, we don't send it to output topic and continue to next score
			return null;
		} catch (Exception e) {
			logger.warn("Error while writing evidence to repository. Error: " + e.getMessage());
			return null;
		}

		// To keep the structure, create a single evidence list..
		List<Evidence> evidences = new ArrayList<>();
		evidences.add(evidence);
		return evidences;
	}
	//</editor-fold>
}
