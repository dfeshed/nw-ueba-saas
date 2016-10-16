package fortscale.accumulator.entityEvent.store;

import fortscale.accumulator.entityEvent.event.AccumulatedEntityEvent;
import fortscale.accumulator.entityEvent.metrics.AccumulatedEntityEventStoreMetrics;
import fortscale.accumulator.entityEvent.translator.AccumulatedEntityEventTranslator;
import fortscale.accumulator.translator.BaseAccumulatedFeatureTranslator;
import fortscale.entity.event.EntityEventConf;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Instant;
import java.util.*;

import static fortscale.accumulator.util.AccumulatorStoreUtil.getACMExistingCollections;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * CRUD layer for {@link AccumulatedEntityEvent}
 * Created by barak_schuster on 10/6/16.
 */
public class AccumulatedEntityEventStoreImpl implements AccumulatedEntityEventStore {
    private static final Logger logger = Logger.getLogger(AccumulatedEntityEventStoreImpl.class);

    private final MongoTemplate mongoTemplate;
    private final BaseAccumulatedFeatureTranslator translator;
    private final StatsService statsService;
    private final Map<String,AccumulatedEntityEventStoreMetrics> featureMetricsMap;
    private Set<String> existingCollections;

    /**
     * C'tor
     * @param mongoTemplate
     * @param translator
     * @param statsService
     */
    public AccumulatedEntityEventStoreImpl(MongoTemplate mongoTemplate, AccumulatedEntityEventTranslator translator, StatsService statsService)
    {
        this.mongoTemplate = mongoTemplate;
        this.translator = translator;
        this.statsService = statsService;
        this.featureMetricsMap = new HashMap<>();
        this.existingCollections = new HashSet<>();

        existingCollections = getACMExistingCollections(mongoTemplate,translator.getAcmCollectionNameRegex());
    }

    @Override
    public void insert(Collection<AccumulatedEntityEvent> events, String featureName) {
        logger.debug("inserting {} events to accumulated feature={}", events.size(), featureName);
        getMetrics(featureName).insert++;

        // TODO: 10/6/16 replace in bulk operation at fortscale 3.0
        try {
            String collectionName = createCollectionIfNotExist(featureName);
            mongoTemplate.insert(events, collectionName);
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
        Sort sort = new Sort(Sort.Direction.DESC, AccumulatedEntityEvent.ACCUMULATED_ENTITY_EVENT_FIELD_NAME_START_TIME);
        query.with(sort);
        AccumulatedEntityEvent accumulatedEvent =
                mongoTemplate.findOne(query, AccumulatedEntityEvent.class, collectionName);

        if (accumulatedEvent != null) {
            Instant startTime = accumulatedEvent.getStart_time();
            logger.debug("feature={} last accumulated event start time={}",featureName,startTime);
            return startTime;
        }

        logger.debug("no accumulated events found for feature={}",featureName);
        return null;
    }

    @Override
    public List<AccumulatedEntityEvent> findAccumulatedEventsByContextIdAndStartTimeRange(EntityEventConf entityEventConf,
                                                                                          String contextId,
                                                                                          Instant startTimeFrom,
                                                                                          Instant startTimeTo) {
        logger.debug("getting accumulated events for entity event name={}", entityEventConf.getName());

        String collectionName = translator.toAcmCollectionName(entityEventConf.getName());
        Query query = new Query()
                .addCriteria(where(AccumulatedEntityEvent.ACCUMULATED_ENTITY_EVENT_FIELD_NAME_CONTEXT_ID)
                        .is(contextId))
                .addCriteria(where(AccumulatedEntityEvent.ACCUMULATED_ENTITY_EVENT_FIELD_NAME_START_TIME)
                        .gte(startTimeFrom)
                        .lt(startTimeTo));
        List<AccumulatedEntityEvent> accumulatedEntityEvents =
                mongoTemplate.find(query, AccumulatedEntityEvent.class, collectionName);
        AccumulatedEntityEventStoreMetrics metrics = getMetrics(entityEventConf.getName());
        metrics.retrieveCalls++;
        metrics.retrievedObjects += accumulatedEntityEvents.size();

        logger.debug("found {} accumulated events", accumulatedEntityEvents.size());
        return accumulatedEntityEvents;
    }

    /**
     * creates metrics for feature if none exists and returns it
     *
     * @param featureName
     * @return feature CRUD metrics
     */
    public AccumulatedEntityEventStoreMetrics getMetrics(String featureName) {
        if (!featureMetricsMap.containsKey(featureName)) {
            AccumulatedEntityEventStoreMetrics metrics =
                    new AccumulatedEntityEventStoreMetrics(statsService, featureName);
            featureMetricsMap.put(featureName, metrics);
        }
        return featureMetricsMap.get(featureName);
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
        AccumulatedEntityEventStoreMetrics metics = getMetrics(featureName);
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
}
