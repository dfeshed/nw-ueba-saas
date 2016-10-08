package fortscale.acumulator.aggregation.store;

import fortscale.acumulator.aggregation.event.AccumulatedAggregatedFeatureEvent;
import fortscale.acumulator.aggregation.metrics.AccumulatedAggregatedFeatureEventsStoreMetrics;
import fortscale.acumulator.translator.AccumulatedFeatureTranslator;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * CRUD layer for {@link AccumulatedAggregatedFeatureEvent}
 * Created by barak_schuster on 10/6/16.
 */
public class AccumulatedAggregatedFeatureEventStoreImpl implements AccumulatedAggregatedFeatureEventStore {
    private static final Logger logger = Logger.getLogger(AccumulatedAggregatedFeatureEventStoreImpl.class);
    private static final String ACCUMULATED_COLLECTION_NAME_REGEX = String.format(".*%s$", AccumulatedFeatureTranslator.ACCUMULATED_COLLECTION_SUFFIX);

    private final MongoTemplate mongoTemplate;
    private final AccumulatedFeatureTranslator translator;
    private final StatsService statsService;
    private Map<String,AccumulatedAggregatedFeatureEventsStoreMetrics> featureMetricsMap;
    private Set <String> existingCollections;

    /**
     * C'tor
     * @param mongoTemplate
     * @param translator
     * @param statsService
     */
    public AccumulatedAggregatedFeatureEventStoreImpl(MongoTemplate mongoTemplate, AccumulatedFeatureTranslator translator, StatsService statsService)
    {
        this.mongoTemplate = mongoTemplate;
        this.translator = translator;
        this.statsService = statsService;
        this.featureMetricsMap = new HashMap<>();
        this.existingCollections = new HashSet<String>();

        existingCollections = getExistingCollections();
    }

    private Set<String> getExistingCollections() {
        return mongoTemplate.getCollectionNames().stream().filter(x -> x.matches(ACCUMULATED_COLLECTION_NAME_REGEX)).collect(Collectors.toSet());
    }

    private String createCollectionIfNotExist(String featureName){
        String collectionName = translator.toAcmAggrCollection(featureName);
        if(!existingCollections.contains(collectionName))
        {
            createCollection(collectionName, featureName);
            existingCollections.add(collectionName);
        }
        return collectionName;
    }

    private void createCollection(String collectionName, String featureName) {
        logger.info("creating new accumulated collection={}",collectionName);
        AccumulatedAggregatedFeatureEventsStoreMetrics metics = getMetics(featureName);
        metics.createCollection++;
        try {
            mongoTemplate.createCollection(collectionName);
            // TODO: 10/8/16 indexes and retention
        }
        catch (Exception e)
        {
            metics.createFailure++;
            logger.error("failed to create collection={}",collectionName,e);
            throw e;
        }

    }

    @Override
    public void insert(Collection<AccumulatedAggregatedFeatureEvent> events, String featureName) {
        logger.debug("inserting {} events to accumulated feature={}",events.size(),featureName);
        getMetics(featureName).insert++;

        // TODO: 10/6/16 replace in bulk operation at fortscale 3.0
        try {
            String collectionName = createCollectionIfNotExist(featureName);
            mongoTemplate.insert(events, collectionName);
        }
        catch (Exception e)
        {
            getMetics(featureName).insertFailure++;
            logger.error("exception occurred while inserting to accumulated collection of feature={}",featureName,e);
            throw e;
        }
    }

    /**
     * creates metrics for feature if none exists and returns it
     * @param featureName
     * @return feature CRUD metrics
     */
    public AccumulatedAggregatedFeatureEventsStoreMetrics getMetics(String featureName)
    {
        if(!featureMetricsMap.containsKey(featureName))
        {
            AccumulatedAggregatedFeatureEventsStoreMetrics metrics =
                    new AccumulatedAggregatedFeatureEventsStoreMetrics(statsService, featureName);
            featureMetricsMap.put(featureName,metrics);
        }
        return featureMetricsMap.get(featureName);
    }
}
