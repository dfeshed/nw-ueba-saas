package fortscale.accumulator.aggregation.store;

import fortscale.accumulator.aggregation.event.AccumulatedAggregatedFeatureEvent;
import fortscale.accumulator.aggregation.metrics.AccumulatedAggregatedFeatureEventsStoreMetrics;
import fortscale.accumulator.translator.AccumulatedFeatureTranslator;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Instant;
import java.util.*;

import static fortscale.accumulator.aggregation.event.AccumulatedAggregatedFeatureEvent.ACCUMULATED_AGGREGATED_FEATURE_EVENT_FIELD_NAME_START_TIME;
import static fortscale.accumulator.util.AccumulatorStoreUtil.getACMExistingCollections;

/**
 * CRUD layer for {@link AccumulatedAggregatedFeatureEvent}
 * Created by barak_schuster on 10/6/16.
 */
public class AccumulatedAggregatedFeatureEventStoreImpl implements AccumulatedAggregatedFeatureEventStore {
    private static final Logger logger = Logger.getLogger(AccumulatedAggregatedFeatureEventStoreImpl.class);

    private final MongoTemplate mongoTemplate;
    private final AccumulatedFeatureTranslator translator;
    private final StatsService statsService;
    private Map<String, AccumulatedAggregatedFeatureEventsStoreMetrics> featureMetricsMap;
    private Set<String> existingCollections;

    /**
     * C'tor
     *
     * @param mongoTemplate
     * @param translator
     * @param statsService
     */
    public AccumulatedAggregatedFeatureEventStoreImpl(MongoTemplate mongoTemplate, AccumulatedFeatureTranslator translator, StatsService statsService) {
        this.mongoTemplate = mongoTemplate;
        this.translator = translator;
        this.statsService = statsService;
        this.featureMetricsMap = new HashMap<>();
        this.existingCollections = new HashSet<String>();
        String eventType = translator.getAggregatedFeatureNameTranslationService().getEventType();
        String collectionNameRegex = String.format(".*%s.*%s$", eventType, AccumulatedFeatureTranslator.ACCUMULATED_COLLECTION_SUFFIX);
        existingCollections = getACMExistingCollections(mongoTemplate,collectionNameRegex);
    }



    private String createCollectionIfNotExist(String featureName) {
        String collectionName = translator.toAcmAggrCollection(featureName);
        if (!existingCollections.contains(collectionName)) {
            createCollection(collectionName, featureName);
            existingCollections.add(collectionName);
        }
        return collectionName;
    }

    private void createCollection(String collectionName, String featureName) {
        logger.info("creating new accumulated collection={}", collectionName);
        AccumulatedAggregatedFeatureEventsStoreMetrics metics = getMetics(featureName);
        metics.createCollection++;
        try {
            mongoTemplate.createCollection(collectionName);
            // TODO: 10/8/16 indexes and retention
        } catch (Exception e) {
            metics.createFailure++;
            logger.error("failed to create collection={}", collectionName, e);
            throw e;
        }
    }

    @Override
    public void insert(Collection<AccumulatedAggregatedFeatureEvent> events, String featureName) {
        logger.debug("inserting {} events to accumulated feature={}", events.size(), featureName);
        getMetics(featureName).insert++;

        // TODO: 10/6/16 replace in bulk operation at fortscale 3.0
        try {
            String collectionName = createCollectionIfNotExist(featureName);
            mongoTemplate.insert(events, collectionName);
        } catch (Exception e) {
            getMetics(featureName).insertFailure++;
            logger.error("exception occurred while inserting to accumulated collection of feature={}", featureName, e);
            throw e;
        }
    }

    @Override
    public Instant getLastAccumulatedEventStartTime(String featureName) {
        logger.debug("getting last accumulated event for featureName={}",featureName);

        String collectionName = translator.toAcmAggrCollection(featureName);
        Query query = new Query();
        Sort sort = new Sort(Sort.Direction.DESC, ACCUMULATED_AGGREGATED_FEATURE_EVENT_FIELD_NAME_START_TIME);
        query.with(sort);
        AccumulatedAggregatedFeatureEvent accumulatedAggregatedFeatureEvent =
                mongoTemplate.findOne(query, AccumulatedAggregatedFeatureEvent.class, collectionName);

        if (accumulatedAggregatedFeatureEvent != null) {
            Instant startTime = accumulatedAggregatedFeatureEvent.getStartTime();
            logger.debug("feature={} last accumulated event start time={}",featureName,startTime);
            return startTime;
        }

        logger.debug("no accumulated events found for feature={}",featureName);
        return null;
    }

    /**
     * creates metrics for feature if none exists and returns it
     *
     * @param featureName
     * @return feature CRUD metrics
     */
    public AccumulatedAggregatedFeatureEventsStoreMetrics getMetics(String featureName) {
        if (!featureMetricsMap.containsKey(featureName)) {
            AccumulatedAggregatedFeatureEventsStoreMetrics metrics =
                    new AccumulatedAggregatedFeatureEventsStoreMetrics(statsService, featureName);
            featureMetricsMap.put(featureName, metrics);
        }
        return featureMetricsMap.get(featureName);
    }
}
