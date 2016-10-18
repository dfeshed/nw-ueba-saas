package fortscale.accumulator.aggregation.store;

import fortscale.accumulator.aggregation.event.AccumulatedAggregatedFeatureEvent;
import fortscale.accumulator.aggregation.metrics.AccumulatedAggregatedFeatureEventsStoreMetrics;
import fortscale.accumulator.aggregation.translator.AccumulatedAggregatedFeatureEventTranslator;
import fortscale.accumulator.translator.BaseAccumulatedFeatureTranslator;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.utils.logging.Logger;
import fortscale.utils.mongodb.FIndex;
import fortscale.utils.monitoring.stats.StatsService;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Instant;
import java.time.Period;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static fortscale.accumulator.util.AccumulatorStoreUtil.getACMExistingCollections;
import static fortscale.accumulator.util.AccumulatorStoreUtil.getRetentionTimeInDays;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * CRUD layer for {@link AccumulatedAggregatedFeatureEvent}
 * Created by barak_schuster on 10/6/16.
 */
public class AccumulatedAggregatedFeatureEventStoreImpl implements AccumulatedAggregatedFeatureEventStore {
    private static final Logger logger = Logger.getLogger(AccumulatedAggregatedFeatureEventStoreImpl.class);

    private final MongoTemplate mongoTemplate;
    private final BaseAccumulatedFeatureTranslator translator;
    private final StatsService statsService;
    private final Period acmDailyRetentionPeriod;
    private final Period acmHourlyRetentionPeriod;
    private Map<String, AccumulatedAggregatedFeatureEventsStoreMetrics> featureMetricsMap;
    private Set<String> existingCollections;

    /**
     * C'tor
     * @param mongoTemplate
     * @param translator
     * @param statsService
     * @param acmDailyRetentionPeriod
     * @param acmHourlyRetentionPeriod
     */
    public AccumulatedAggregatedFeatureEventStoreImpl(MongoTemplate mongoTemplate, AccumulatedAggregatedFeatureEventTranslator translator, StatsService statsService, Period acmDailyRetentionPeriod, Period acmHourlyRetentionPeriod) {
        this.mongoTemplate = mongoTemplate;
        this.translator = translator;
        this.statsService = statsService;
        this.featureMetricsMap = new HashMap<>();
        this.existingCollections = new HashSet<>();
        this.acmDailyRetentionPeriod = acmDailyRetentionPeriod;
        this.acmHourlyRetentionPeriod = acmHourlyRetentionPeriod;

        existingCollections = getACMExistingCollections(mongoTemplate,translator.getAcmCollectionNameRegex());
    }


    private String createCollectionIfNotExist(String featureName) {
        String collectionName = translator.toAcmCollectionName(featureName);
        if (!existingCollections.contains(collectionName)) {
            createCollection(collectionName, featureName);
            existingCollections.add(collectionName);
        }
        return collectionName;
    }

    private void createCollection(String collectionName, String featureName) {
        logger.info("creating new accumulated collection={}", collectionName);
        AccumulatedAggregatedFeatureEventsStoreMetrics metics = getMetrics(featureName);
        metics.createCollection++;
        try {
            mongoTemplate.createCollection(collectionName);

            long retentionTimeInDays=getRetentionTimeInDays(featureName,acmDailyRetentionPeriod,acmHourlyRetentionPeriod);

            mongoTemplate.indexOps(collectionName)
                    .ensureIndex(new FIndex().expire(retentionTimeInDays, TimeUnit.DAYS)
                            .named(AccumulatedAggregatedFeatureEvent.ACCUMULATED_AGGREGATED_FEATURE_EVENT_FIELD_NAME_CREATION_TIME)
                            .on(AccumulatedAggregatedFeatureEvent.ACCUMULATED_AGGREGATED_FEATURE_EVENT_FIELD_NAME_CREATION_TIME, Sort.Direction.DESC));
            mongoTemplate.indexOps(collectionName)
                    .ensureIndex(new Index().named(AccumulatedAggregatedFeatureEvent.ACCUMULATED_AGGREGATED_FEATURE_EVENT_FIELD_NAME_START_TIME)
                            .on(AccumulatedAggregatedFeatureEvent.ACCUMULATED_AGGREGATED_FEATURE_EVENT_FIELD_NAME_START_TIME, Sort.Direction.DESC));
            mongoTemplate.indexOps(collectionName)
                    .ensureIndex(new Index().named(AccumulatedAggregatedFeatureEvent.ACCUMULATED_AGGREGATED_FEATURE_EVENT_FIELD_NAME_END_TIME)
                            .on(AccumulatedAggregatedFeatureEvent.ACCUMULATED_AGGREGATED_FEATURE_EVENT_FIELD_NAME_END_TIME, Sort.Direction.DESC));
            mongoTemplate.indexOps(collectionName)
                    .ensureIndex(new Index().named(AccumulatedAggregatedFeatureEvent.ACCUMULATED_AGGREGATED_FEATURE_EVENT_FIELD_NAME_CONTEXT_ID)
                            .on(AccumulatedAggregatedFeatureEvent.ACCUMULATED_AGGREGATED_FEATURE_EVENT_FIELD_NAME_CONTEXT_ID, Sort.Direction.DESC));

        } catch (Exception e) {
            metics.createFailure++;
            logger.error("failed to create collection={}", collectionName, e);
            throw e;
        }
    }

    @Override
    public void insert(Collection<AccumulatedAggregatedFeatureEvent> events, String featureName) {
        logger.debug("inserting {} events to accumulated feature={}", events.size(), featureName);
        getMetrics(featureName).insert++;

        // TODO: 10/6/16 replace in bulk operation at fortscale 3.0
        try {
            if(!events.isEmpty()) {
                String collectionName = createCollectionIfNotExist(featureName);
                mongoTemplate.insert(events, collectionName);
            }
        } catch (Exception e) {
            getMetrics(featureName).insertFailure++;
            logger.error("exception occurred while inserting to accumulated collection of feature={}", featureName, e);
            throw e;
        }
    }

    @Override
    public Instant getLastAccumulatedEventStartTime(String featureName) {
        logger.debug("getting last accumulated event for featureName={}",featureName);

        String collectionName = translator.toAcmCollectionName(featureName);
        Query query = new Query();
        Sort sort = new Sort(Sort.Direction.DESC,
                AccumulatedAggregatedFeatureEvent.ACCUMULATED_AGGREGATED_FEATURE_EVENT_FIELD_NAME_START_TIME);
        query.with(sort);
        AccumulatedAggregatedFeatureEvent accumulatedAggregatedFeatureEvent =
                mongoTemplate.findOne(query, AccumulatedAggregatedFeatureEvent.class, collectionName);

        if (accumulatedAggregatedFeatureEvent != null) {
            Instant startTime = accumulatedAggregatedFeatureEvent.getStart_time();
            logger.debug("feature={} last accumulated event start time={}",featureName,startTime);
            return startTime;
        }

        logger.debug("no accumulated events found for feature={}",featureName);
        return null;
    }

    @Override
    public List<AccumulatedAggregatedFeatureEvent> findAccumulatedEventsByContextIdAndStartTimeRange(
			AggregatedFeatureEventConf aggregatedFeatureEventConf,
			String contextId,
			Instant startTimeFrom,
			Instant startTimeTo) {
		logger.debug("getting accumulated events for featureName={}", aggregatedFeatureEventConf.getName());

        String collectionName = translator.toAcmCollectionName(aggregatedFeatureEventConf.getName());
        Query query = new Query()
                .addCriteria(where(AccumulatedAggregatedFeatureEvent.ACCUMULATED_AGGREGATED_FEATURE_EVENT_FIELD_NAME_CONTEXT_ID)
                        .is(contextId))
                .addCriteria(where(AccumulatedAggregatedFeatureEvent.ACCUMULATED_AGGREGATED_FEATURE_EVENT_FIELD_NAME_START_TIME)
                        .gte(startTimeFrom)
                        .lt(startTimeTo));
        List<AccumulatedAggregatedFeatureEvent> accumulatedAggregatedFeatureEvents =
                mongoTemplate.find(query, AccumulatedAggregatedFeatureEvent.class, collectionName);
        AccumulatedAggregatedFeatureEventsStoreMetrics metrics = getMetrics(aggregatedFeatureEventConf.getName());
        metrics.retrieveCalls++;
        metrics.retrievedObjects += accumulatedAggregatedFeatureEvents.size();

        logger.debug("found {} accumulated events", accumulatedAggregatedFeatureEvents.size());
        return accumulatedAggregatedFeatureEvents;
    }

    /**
     * creates metrics for feature if none exists and returns it
     *
     * @param featureName
     * @return feature CRUD metrics
     */
    public AccumulatedAggregatedFeatureEventsStoreMetrics getMetrics(String featureName) {
        if (!featureMetricsMap.containsKey(featureName)) {
            AccumulatedAggregatedFeatureEventsStoreMetrics metrics =
                    new AccumulatedAggregatedFeatureEventsStoreMetrics(statsService, featureName);
            featureMetricsMap.put(featureName, metrics);
        }
        return featureMetricsMap.get(featureName);
    }
}
