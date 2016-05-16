package fortscale.streaming.service.alert;

import fortscale.aggregation.feature.event.AggrEvent;
import fortscale.aggregation.feature.event.AggrEventEvidenceFilteringStrategyEnum;
import fortscale.aggregation.feature.event.AggrFeatureEventBuilderService;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.domain.core.*;
import fortscale.services.EvidencesService;
import fortscale.services.TagService;
import fortscale.streaming.alert.event.wrappers.EnrichedFortscaleEvent;
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

import java.util.*;

/**
 * Created by shays on 25/04/2016.
 */
public class EvidencesForAlertResolverService {


    private final static String USER_ENTITY_KEY = "normalized_username";
    private final static String F_FEATURE_VALUE = "F";
    private final static String P_FEATURE_VALUE = "P";
    private final static String ENTITY_NAME_FIELD = "normalized_username";
    private static Logger logger = Logger.getLogger(EvidencesForAlertResolverService.class);

    // general evidence creation setting
    @Value("${fortscale.smart.f.score}") private int fFeatureTresholdScore;
    @Value("${fortscale.smart.p.count}") private int pFeatureTreshholdCount;

    /**
     * Evidence service (for Mongo export)
     */
    @Autowired
    protected EvidencesService evidencesService;

    /**
     * Aggregated feature event builder service
     */
    @Autowired
    private AggrFeatureEventBuilderService aggrFeatureEventBuilderService;

    @Autowired
    private TagService tagService;

    /**
     * Aggregated feature configuration service
     */
    @Autowired
    protected AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;

    public List<Evidence> handleTags(Set<String> userTags, EntityType entityType, String userName, Long startDate, Long endDate) {

        return createTagEvidences(entityType, userName, startDate, endDate, userTags);
    }


    public List<Evidence> handleEntityEvents(List<EnrichedFortscaleEvent> eventList) {

        if (eventList == null || eventList.isEmpty()) {
            return Collections.emptyList();
        }

        // use set in order to merge duplicate indicators derived from P/F values that exist in more than one entity event
        Set<Evidence> existingIndicators = new HashSet<>();
        Set<Evidence> newIndicators = new HashSet<>();

        for (EnrichedFortscaleEvent event : eventList) {
            handleEntityEvent(event, existingIndicators, newIndicators);
        }

        createNonExistingEvidencesInDB(newIndicators);

        List<Evidence> evidencesList = new ArrayList<>();

        evidencesList.addAll(existingIndicators);
        evidencesList.addAll(newIndicators);

        return evidencesList;
    }

    public List<Evidence> handleNotifications(List<EnrichedFortscaleEvent> eventList) {
        List<Evidence> notifications = new ArrayList<>();

        for (EnrichedFortscaleEvent event : eventList) {
            Evidence notification = handleNotification(event);

            notifications.add(notification);
        }

        return notifications;
    }

    public void createNonExistingEvidencesInDB(Set<Evidence> newEvidencesForAlert) {
        if (newEvidencesForAlert.isEmpty()) {
            return;
        }

        for (Evidence evidence : newEvidencesForAlert) {
            try {
                logger.info("Saving non-existing F/P indicator based on entity event. New indicator: {}", evidence);
                evidencesService.saveEvidenceInRepository(evidence);
            } catch (DuplicateKeyException e) {
                logger.warn("Got duplication for evidence {}. Going to drop it.", evidence.toString());
                // In case this evidence is duplicated, we don't send it to output topic and continue to next score
            } catch (Exception e) {
                logger.warn("Error while writing evidence to repository. Error: " + e.getMessage());
            }
        }
    }

    private void handleEntityEvent(EnrichedFortscaleEvent smartEvent, Set<Evidence> existingEvidencesForAlert, Set<Evidence> newEvidencesForAlert) {
        Object aggregatedFeatureEvents = smartEvent.getAggregatedFeatureEvents();

        if (aggregatedFeatureEvents != null){
            List<JSONObject> aggregatedFeatureEventList = (List<JSONObject>) aggregatedFeatureEvents;

            if (aggregatedFeatureEventList.isEmpty()) {
                logger.warn("Received an Entity Event with no aggregated features: {}", smartEvent);

                return;
            }

            for (JSONObject aggregatedFeatureEvent : aggregatedFeatureEventList) {
                AggrEvent aggrEvent = aggrFeatureEventBuilderService.buildEvent(aggregatedFeatureEvent);

                handleAggregatedFeature(aggrEvent, existingEvidencesForAlert, newEvidencesForAlert);
            }
        }
        else {
            logger.warn("Received an Entity Event with no aggregated features: {}", smartEvent);
        }
    }

    private Evidence handleNotification(EnrichedFortscaleEvent notificationEvent) {
        // create a reference to the notification object in mongo

        Evidence e = new Evidence(notificationEvent.getId());
        e.setAnomalyTypeFieldName(notificationEvent.getAnomalyTypeFieldName());
        e.setDataEntitiesIds(notificationEvent.getDataEntitiesIds());
        return e;
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

        if (tags == null || tags.isEmpty()) {
            return Collections.emptyList();
        }

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
}
