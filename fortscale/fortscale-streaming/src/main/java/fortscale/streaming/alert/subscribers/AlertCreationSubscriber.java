package fortscale.streaming.alert.subscribers;

import fortscale.aggregation.feature.event.AggrEvent;
import fortscale.aggregation.feature.event.AggrEventEvidenceFilteringStrategyEnum;
import fortscale.aggregation.feature.event.AggrFeatureEventBuilderService;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.domain.core.*;
import fortscale.services.*;
import fortscale.streaming.alert.event.wrappers.EnrichedFortscaleEvent;
import fortscale.streaming.alert.subscribers.evidence.applicable.AlertTypesHisotryCache;
import fortscale.streaming.alert.subscribers.evidence.applicable.AlertFilterApplicableEvidencesService;
import fortscale.streaming.alert.subscribers.evidence.decider.AlertDeciderServiceImpl;
import fortscale.streaming.alert.subscribers.evidence.filter.EvidenceFilter;
import fortscale.streaming.alert.subscribers.evidence.filter.FilterByHighScorePerUnqiuePValue;
import fortscale.streaming.alert.subscribers.evidence.filter.FilterByHighestScore;
import fortscale.streaming.task.EvidenceCreationTask;
import fortscale.utils.time.TimestampUtils;
import net.minidev.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import parquet.org.slf4j.Logger;
import parquet.org.slf4j.LoggerFactory;

import java.util.*;

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
			//list of evidences to go into the Alert
			List<Evidence> evidencesInAlert = new ArrayList<>();
			//list of evidences to use for obtaining name and score
			List<EnrichedFortscaleEvent> evidencesEligibleForDecider = new ArrayList<>();


			for (Map insertStreamOutput : insertStream) {
				try {
					Long startDate = (Long) insertStreamOutput.get("startDate");
					Long endDate = (Long) insertStreamOutput.get("endDate");

					String title = (String) insertStreamOutput.get("title");
					String anomalyTypeFieldName = (String) insertStreamOutput.get("anomalyTypeFieldName");
					String evidenceType = (String) insertStreamOutput.get("evidneceType");


					EntityType entityType = (EntityType) insertStreamOutput.get(Evidence.entityTypeField);
					String entityName = (String) insertStreamOutput.get(Evidence.entityNameField);
					String entityId;
					switch (entityType) {
						case User: {
							/*TODO: retrieve tags - verify that tags exists in cache
							  Create "light pojo" for user name/id and tags
							 */
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
					}
					//TODO: change the MAP to an object EnrichedFortscaleEvent that will hold all event's field
					//idList holds the individual indicator for each user
					Map[] idList = (Map[]) insertStreamOutput.get("idList");
					List<EnrichedFortscaleEvent> evidencesOrEntityEvents = convertToObject(idList);
					//create the list of evidences to apply to the decider
					evidencesEligibleForDecider = evidencesApplicableToAlertService.createIndicatorListApplicableForDecider(
																		evidencesOrEntityEvents,startDate,endDate);


//					Double score = (Double) insertStreamOutput.get("score");
//					Integer roundScore = score.intValue();
					//Severity severity = Severity.Low;



					//if this is a statement containing tags
					/*if (insertStreamOutput.containsKey("tags") && insertStreamOutput.get("tags") != null) {
						String tagStr = (String) insertStreamOutput.get("tag");
						Tag tag = tagService.getTag(tagStr);
						if (tag != null && tag.getCreatesIndicator()) {
							Evidence tagEvidence = evidencesService.createTagEvidence(entityType,
									Evidence.entityTypeFieldNameField, entityName, startDate, endDate, tagStr);
							evidencesInAlert.add(tagEvidence);
						}
					}*/

//					LinkedList<DeciderCommand> deciderLinkedList = decider.getDecidersLinkedList();
//					DeciderCommand deciderCommand = deciderLinkedList.getFirst();
//					if (deciderCommand != null){
//						//TODO: Idan / Galia do we need to same the title as the anomaly type field name and only change the name in the UI
//						//Or the name shuld come from configuration????
//						title = deciderCommand.getName(evidencesEligibleForDecider, deciderLinkedList);
//						roundScore = deciderCommand.getScore(evidencesEligibleForDecider, deciderLinkedList);
//						severity = alertsService.getScoreToSeverity().floorEntry(roundScore).getValue();
//					}





					title = decider.decideName(evidencesEligibleForDecider);
					Integer roundScore = decider.decideScore(evidencesEligibleForDecider);

					Severity severity = getSeverity(entityId, roundScore);

					//String title = decider.decideName(evidencesEligibleForDecider);

					if (title != null && severity != null) {
						//create the list of evidences to enter into the alert
						evidencesInAlert = createIndicatorListForAlert(idList, startDate, endDate, entityType, entityName);


						Alert alert = new Alert(title, startDate, endDate, entityType, entityName, evidencesInAlert, evidencesInAlert.size(),
								roundScore, severity, AlertStatus.Open, AlertFeedback.None, "", entityId);

						//Save alert to mongoDB
						alertsService.saveAlertInRepository(alert);
						alertTypesHisotryCache.updateCache(alert);
					}
				} catch (RuntimeException ex) {
					logger.error(ex.getMessage(), ex);
					ex.printStackTrace();
				}

			}
		}
	}

	private Severity getSeverity(String entityId, Integer roundScore) {
		Severity severity;Set<String> userTags= userTagsCacheService.getUserTags(entityId);

		if (!Collections.disjoint(userTags, privilegedTags)){
            //Regular user. No priviliged tags
            severity = defaultTagToSeverityMapping.getSeverityByScore(roundScore);
        } else {
			//Privileged
            severity = privilegedTagToSeverityMapping.getSeverityByScore(roundScore);
        }
		return severity;
	}

	private List<EnrichedFortscaleEvent> convertToObject(Map[] idList ) {
		List<EnrichedFortscaleEvent>  evidenceOrEntityEvents= new ArrayList<>();

		for (int i=0; i<idList.length;i++){
			EnrichedFortscaleEvent evidenceOrEntityEvent = new EnrichedFortscaleEvent();
			evidenceOrEntityEvent.fromMap(idList[i] );
			evidenceOrEntityEvents.add(evidenceOrEntityEvent);
		}
		return  evidenceOrEntityEvents;
	}


	private List<Evidence> createIndicatorListForAlert(Map[] idList, Long startDate, Long endDate, EntityType entityType, String entityName) {

		List<Evidence> evidences = new ArrayList<>();
		for (Map map : idList) {
            //create new Evidence with the evidence id. it creates reference to the evidence object in mongo.
            String id = (String)map.get("id");
            if (!StringUtils.isEmpty(id)) {
                Evidence evidence = new Evidence(id);
                evidences.add(evidence);
            } else {
                Object aggregatedFeatureEvents = map.get("aggregatedFeatureEvents");
                if (aggregatedFeatureEvents != null && aggregatedFeatureEvents instanceof List){
                    //build evidences from Smart
                    List<Evidence> evidencesList = createEvidencesList(startDate, endDate, entityName, entityType,
                            (List)aggregatedFeatureEvents, null);
					//TODO: avoid duplicate Inddicator id's
                    evidences.addAll(evidencesList);
                }
            }

        }
		return evidences;
	}

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
		/*List<Evidence> tagsEvidences =  createTagEvidences(entityType, entityName, startDate, endDate, tags);
		if (tagsEvidences != null) {
			evidenceList.addAll(tagsEvidences);
		}*/

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

	public void setEvidencesApplicableToAlertService(AlertFilterApplicableEvidencesService evidencesApplicableToAlertService) {
		this.evidencesApplicableToAlertService = evidencesApplicableToAlertService;
	}

	public void setDecider(AlertDeciderServiceImpl decider) {
		this.decider = decider;
	}

}