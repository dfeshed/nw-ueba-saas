package fortscale.streaming.alert.subscribers;

import fortscale.aggregation.feature.event.AggrEvent;
import fortscale.aggregation.feature.event.AggrEventEvidenceFilteringStrategyEnum;
import fortscale.aggregation.feature.event.AggrFeatureEventBuilderService;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.domain.core.*;
import fortscale.services.*;
import fortscale.streaming.alert.event.wrappers.EnrichedFortscaleEvent;
import fortscale.streaming.alert.subscribers.evidence.applicable.AlertFilterApplicableEvidencesService;
import fortscale.streaming.alert.subscribers.evidence.applicable.AlertTypesHisotryCache;
import fortscale.streaming.alert.subscribers.evidence.decider.AlertDeciderServiceImpl;
import fortscale.streaming.alert.subscribers.evidence.filter.EvidenceFilter;
import fortscale.streaming.alert.subscribers.evidence.filter.FilterByHighScorePerUnqiuePValue;
import fortscale.streaming.alert.subscribers.evidence.filter.FilterByHighestScore;
import fortscale.streaming.task.EvidenceCreationTask;
import fortscale.utils.time.TimestampUtils;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import parquet.org.slf4j.Logger;
import parquet.org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Wraps Esper Statement and Listener. No dependency on Esper libraries.
 */
public class AlertCreationSubscriber extends AbstractSubscriber {

	/**
	 * Logger
	 */
	private static Logger logger = LoggerFactory.getLogger(AlertCreationSubscriber.class);
	static String USER_ENTITY_KEY = "normalized_username";
	final String F_FEATURE_VALUE = "F";
	final String P_FEATURE_VALUE = "P";
	final String ENTITY_NAME_FIELD = "normalized_username";
	final String NOTIFICATION_EVIDENCE_TYPE = "Notification";
	/**
	 * Alerts service (for Mongo export)
	 */
	@Autowired protected AlertsService alertsService;

	@Autowired private UserService userService;

	@Autowired private TagService tagService;


	/**
	 * Computer service (for resolving id)
	 */
	@Autowired protected ComputerService computerService;

	/**
	 * Evidence service (for Mongo export)
	 */
	@Autowired protected EvidencesService evidencesService;

	/**
	 * Aggregated feature configuration service
	 */
	@Autowired protected AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;

	/**
	 * Aggregated feature event builder service
	 */
	@Autowired private AggrFeatureEventBuilderService aggrFeatureEventBuilderService;

	@Autowired
	private AlertFilterApplicableEvidencesService evidencesApplicableToAlertService;

	@Autowired
	private AlertDeciderServiceImpl decider;

	@Autowired
	private AlertTypesHisotryCache alertTypesHisotryCache;

	@Autowired
	private UserTagsCacheService userTagsCacheService;

	@Autowired
	@Qualifier("defaultTagToSeverityMapping")
	private TagsToSeverityMapping defaultTagToSeverityMapping;

	@Autowired
	@Qualifier("priviligedTagToSeverityMapping")
	private TagsToSeverityMapping privilegedTagToSeverityMapping;

	@Value("#{'${fortscale.tags.priviliged:admin,executive,service}'.split(',')}")
	private Set<String> privilegedTags;

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
	 * Listener method called when Esper has detected a pattern match.
	 * Creates an alert and saves it in mongo. this includes the references to its evidences, which are already in mongo.
	 * Map array holds one map for each user for a certain hour/day
	 */
	public void update(Map[] insertStream, Map[] removeStream) {
		if (insertStream != null) {

			for (Map eventStreamByUserAndTimeframe : insertStream) {
				try {
					EntityType entityType = (EntityType) eventStreamByUserAndTimeframe.get(Evidence.entityTypeField);
					String entityName = (String) eventStreamByUserAndTimeframe.get(Evidence.entityNameField);
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
							logger.warn("Cannot handle entity of type {}", entityType);

							continue;
						}
					}

					Map[] eventList = (Map[]) eventStreamByUserAndTimeframe.get("eventList");
					List<EnrichedFortscaleEvent> evidencesOrEntityEvents = convertToFortscaleEventList(eventList);

					Long startDate = (Long) eventStreamByUserAndTimeframe.get("startDate");
					Long endDate = (Long) eventStreamByUserAndTimeframe.get("endDate");

					//create the list of evidences to apply to the decider
					List<EnrichedFortscaleEvent> evidencesEligibleForDecider = evidencesApplicableToAlertService.createIndicatorListApplicableForDecider(
							evidencesOrEntityEvents,startDate,endDate);

					String title = decider.decideName(evidencesEligibleForDecider);
					Integer roundScore = decider.decideScore(evidencesEligibleForDecider);

					Severity severity = getSeverity(entityId, roundScore);

					if (title != null && severity != null) {
						List<Evidence> attachedNotifications = handleNotifications(Arrays.stream(eventList).
								filter(event -> (event.get("evidenceType") == EvidenceType.Notification)).collect(Collectors.toList()));

						List<Evidence> attachedEntityEventIndicators = handleEntityEvents(Arrays.stream(eventList).
								filter(event -> (event.get("evidenceType") == EvidenceType.Smart)).collect(Collectors.toList()));

						List<Evidence> attachedTags = handleTags(entityType, entityName, entityId, startDate, endDate);

						List<Evidence> finalIndicatorsListForAlert = new ArrayList<>();

						finalIndicatorsListForAlert.addAll(attachedNotifications);
						finalIndicatorsListForAlert.addAll(attachedEntityEventIndicators);
						finalIndicatorsListForAlert.addAll(attachedTags);

						Alert alert = new Alert(title, startDate, endDate, entityType, entityName, finalIndicatorsListForAlert,
								roundScore, severity, AlertStatus.Open, AlertFeedback.None, "", entityId);

						//Save alert to mongoDB
						alertsService.saveAlertInRepository(alert);
						alertTypesHisotryCache.updateCache(alert);
					}
				} catch (Exception e) {
					logger.error("Exception while handling stream event: ", e);
				}
			}
		}
	}

	private List<Evidence> handleTags(EntityType entityType, String entityName, String entityId, Long startDate, Long endDate) {
		Set<String> userTags = userTagsCacheService.getUserTags(entityId);

		List<Evidence> tagsEvidences = createTagEvidences(entityType, entityName, startDate, endDate, userTags);

		return tagsEvidences;
	}

	private Severity getSeverity(String entityId, Integer roundScore) {
		Severity severity;
		Set<String> userTags= userTagsCacheService.getUserTags(entityId);

		if (!Collections.disjoint(userTags, privilegedTags)){
			//Regular user. No priviliged tags
			severity = defaultTagToSeverityMapping.getSeverityByScore(roundScore);
		} else {
			//Privileged
			severity = privilegedTagToSeverityMapping.getSeverityByScore(roundScore);
		}
		return severity;
	}

	private List<EnrichedFortscaleEvent> convertToFortscaleEventList(Map[] eventList ) {
		List<EnrichedFortscaleEvent>  evidenceOrEntityEvents= new ArrayList<>();

		for (Map eventAttributes : eventList) {
			EnrichedFortscaleEvent evidenceOrEntityEvent = new EnrichedFortscaleEvent();
			evidenceOrEntityEvent.fromMap(eventAttributes);
			evidenceOrEntityEvents.add(evidenceOrEntityEvent);
		}
		return  evidenceOrEntityEvents;
	}


	private List<Evidence> handleEntityEvents(List<Map> eventList) {

		Set<Evidence> existingIndicators = new HashSet<>();
		Set<Evidence> newIndicators = new HashSet<>();

		for (Map event : eventList) {
			handleEntityEvent(event, existingIndicators, newIndicators);
		}

		createNewEvidencesInDB(newIndicators);

		List<Evidence> uniqueEvidenceFinalList = new ArrayList<>();

		uniqueEvidenceFinalList.addAll(existingIndicators);
		uniqueEvidenceFinalList.addAll(newIndicators);

		return uniqueEvidenceFinalList;
	}

	private List<Evidence> handleNotifications(List<Map> eventList) {
		List<Evidence> notifications = new ArrayList<>();

		for (Map event : eventList) {
			Evidence notification = handleNotification(event);

			notifications.add(notification);
		}

		return notifications;
	}

	private void createNewEvidencesInDB(Set<Evidence> newEvidencesForAlert) {

		for (Evidence evidence : newEvidencesForAlert) {
			try {
				evidencesService.saveEvidenceInRepository(evidence);
			} catch (DuplicateKeyException e) {
				logger.warn("Got duplication for evidence {}. Going to drop it.", evidence.toString());
				// In case this evidence is duplicated, we don't send it to output topic and continue to next score
			} catch (Exception e) {
				logger.warn("Error while writing evidence to repository. Error: " + e.getMessage());
			}
		}
	}

	private void handleEntityEvent(Map smartEvent, Set<Evidence> existingEvidencesForAlert, Set<Evidence> newEvidencesForAlert) {
		Object aggregatedFeatureEvents = smartEvent.get("aggregatedFeatureEvents");

		if (aggregatedFeatureEvents != null && aggregatedFeatureEvents instanceof List){
			List<JSONObject> aggregatedFeatureEventList = (List<JSONObject>) aggregatedFeatureEvents;

			// Iterate through the features
			for (JSONObject aggregatedFeatureEvent : aggregatedFeatureEventList) {
				AggrEvent aggrEvent = aggrFeatureEventBuilderService.buildEvent(aggregatedFeatureEvent);

				handleAggregatedFeature(aggrEvent, existingEvidencesForAlert, newEvidencesForAlert);
			}
		}
	}

	private Evidence handleNotification(Map notificationEvent) {
		String id = (String) notificationEvent.get("id");

		// create a reference to the notification object in mongo
		Evidence notification = new Evidence(id);

		return notification;
	}

	private void handleAggregatedFeature(AggrEvent aggregatedFeatureEvent, Set<Evidence> existingEvidencesForAlert, Set<Evidence> newEvidencesForAlert) {
		// Depended on the feature type, get the evidence
		switch (aggregatedFeatureEvent.getFeatureType()) {
			case F_FEATURE_VALUE:
				handleFFeature(aggregatedFeatureEvent, existingEvidencesForAlert, newEvidencesForAlert);
				break;
			case P_FEATURE_VALUE:
				handlePFeature(aggregatedFeatureEvent, existingEvidencesForAlert, newEvidencesForAlert);
				break;
			default:
				logger.warn("Illegal feature type: " + aggregatedFeatureEvent.getFeatureType());
				break;
		}
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
											  Set<String> tags) {
		List<Evidence> tagEvidences = new ArrayList<>();

		// Iterate the tags list and create evidence for each tag
		for (String tagStr : tags) {
			Tag tag = tagService.getTag(tagStr);
			if (tag != null && tag.getCreatesIndicator()) {
				Evidence evidence = evidencesService.createTagEvidence(entityType, Evidence.entityTypeFieldNameField,
						entityName, startDate, endDate, tagStr);
				tagEvidences.add(evidence);
			}
		}

		return  tagEvidences;
	}
	//</editor-fold>

	//<editor-fold desc="P features handling">
	/**
	 * Handle P feature - fetch existing evidence or create a new evidence
	 *
	 * @param aggregatedFeatureEvent
	 * @param existingEvidencesForAlert
	 *@param newEvidencesForAlert @return
	 */
	private void handlePFeature(AggrEvent aggregatedFeatureEvent, Set<Evidence> existingEvidencesForAlert, Set<Evidence> newEvidencesForAlert) {
		if (aggregatedFeatureEvent.getAggregatedFeatureValue() <= pFeatureTreshholdCount) {
			return;
		}

		// Get the parameters for reading evidences
		EntityType entityType = EntityType.User;
		String entityValue = aggregatedFeatureEvent.getContext().get(USER_ENTITY_KEY);
		Long startDate = TimestampUtils.convertToMilliSeconds(aggregatedFeatureEvent.getStartTimeUnix());
		Long endDate = TimestampUtils.convertToMilliSeconds(aggregatedFeatureEvent.getEndTimeUnix());
		String dataSource = (String) aggregatedFeatureEvent.getDataSources().get(0);
		String anomalyType = aggregatedFeatureEventsConfService.getAnomalyType(aggregatedFeatureEvent.getAggregatedFeatureName());

		// Read evidences from mongo
		List<Evidence> existingPEvidences = findExistingPEvidences(entityType, entityValue, startDate, endDate, dataSource, anomalyType);

		// Filter results
		if (existingPEvidences != null && existingPEvidences.size() > 0) {
			filterPEvidences(existingPEvidences, aggregatedFeatureEvent);
		}

		if (existingPEvidences != null) {
			existingEvidencesForAlert.addAll(existingPEvidences);
		}
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
	 * @param entityType
	 * @param entityValue
	 * @param startDate
	 * @param endDate
	 * @param dataSource
	 * @param anomalyType
	 * @return
	 */
	private List<Evidence> findExistingPEvidences(EntityType entityType, String entityValue, Long startDate, Long endDate,
												  String dataSource, String anomalyType) {
		return evidencesService.findFeatureEvidences(entityType, entityValue, startDate, endDate, dataSource, anomalyType);
	}
	//</editor-fold>

	//<editor-fold desc="F features handling ">
	/**
	 * Handle F feature - fetch existing evidence or create a new evidence
	 *
	 * @param aggregatedFeatureEvent
	 * @param existingEvidencesForAlert
	 *@param newEvidencesForAlert @return
	 */
	private void handleFFeature(AggrEvent aggregatedFeatureEvent, Set<Evidence> existingEvidencesForAlert, Set<Evidence> newEvidencesForAlert) {

		// Filter features with low score
		if (aggregatedFeatureEvent.getScore() < fFeatureTresholdScore) {
			return;
		}

		// Read common information for finding and creation evidence
		String entityValue = aggregatedFeatureEvent.getContext().get(USER_ENTITY_KEY);
		String dataSource = aggregatedFeatureEvent.getDataSources().get(0);

		// try to fetch evidence from repository
		List<Evidence> fEvidences = findFEvidences(EntityType.User, entityValue,
				TimestampUtils.convertToMilliSeconds(aggregatedFeatureEvent.getStartTimeUnix()),
				TimestampUtils.convertToMilliSeconds(aggregatedFeatureEvent.getEndTimeUnix()),
				dataSource, aggregatedFeatureEvent.getAggregatedFeatureName());

		// In case we found previously created evidence in the repository, return it
		if (fEvidences != null && !fEvidences.isEmpty()) {
			existingEvidencesForAlert.addAll(fEvidences);
		}
		else {
			// Else, create the evidence in the repository and return it
			List<Evidence> transientFEvidence = createTransientFEvidence(EntityType.User, entityValue,
					TimestampUtils.convertToMilliSeconds(aggregatedFeatureEvent.getStartTimeUnix()),
					TimestampUtils.convertToMilliSeconds(aggregatedFeatureEvent.getEndTimeUnix()),
					aggregatedFeatureEvent.getDataSources(), aggregatedFeatureEvent.getScore(),
					aggregatedFeatureEvent.getAggregatedFeatureName(), aggregatedFeatureEvent);

			newEvidencesForAlert.addAll(transientFEvidence);
		}
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
	private List<Evidence> createTransientFEvidence(EntityType entityType, String entityName, Long startDate, Long endDate,
													List<String> dataEntities, Double score, String featureName, AggrEvent aggregatedFeatureEvent) {

		EvidenceTimeframe evidenceTimeframe = EvidenceCreationTask.calculateEvidenceTimeframe(EvidenceType.AnomalyAggregatedEvent,
				TimestampUtils.convertToSeconds(startDate),
				TimestampUtils.convertToSeconds(endDate));

		Evidence evidence = evidencesService.createTransientEvidence(entityType, ENTITY_NAME_FIELD, entityName,
				EvidenceType.AnomalyAggregatedEvent, new Date(startDate), new Date(endDate), dataEntities, score,
				aggregatedFeatureEvent.getAggregatedFeatureValue().toString(), featureName,
				(int)aggregatedFeatureEvent.getAggregatedFeatureInfo().get("total"), evidenceTimeframe);

		// To keep the structure, create a single evidence list..
		List<Evidence> evidences = new ArrayList<>();
		evidences.add(evidence);
		return evidences;
	}

	public void setEvidencesApplicableToAlertService(AlertFilterApplicableEvidencesService evidencesApplicableToAlertService) {
		this.evidencesApplicableToAlertService = evidencesApplicableToAlertService;
	}

	public void setDecider(AlertDeciderServiceImpl decider) {
		this.decider = decider;
	}

}