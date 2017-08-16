package presidio.ade.domain.store.accumulator;

import fortscale.utils.logging.Logger;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import presidio.ade.domain.record.AdeRecord;
import presidio.ade.domain.record.accumulator.AccumulatedAggregationFeatureRecord;
import presidio.ade.domain.record.aggregated.AdeContextualAggregatedRecord;
import presidio.ade.domain.store.AdeDataStoreCleanupParams;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;


public class AggregationEventsAccumulationDataStoreMongoImpl implements AggregationEventsAccumulationDataStore {
    private static final Logger logger = Logger.getLogger(AggregationEventsAccumulationDataStoreMongoImpl.class);

    private final MongoTemplate mongoTemplate;
    private final AccumulatedDataToCollectionNameTranslator translator;
    private final MongoDbBulkOpUtil mongoDbBulkOpUtil;

    public AggregationEventsAccumulationDataStoreMongoImpl(MongoTemplate mongoTemplate, AccumulatedDataToCollectionNameTranslator translator, MongoDbBulkOpUtil mongoDbBulkOpUtil) {
        this.mongoTemplate = mongoTemplate;
        this.translator = translator;
        this.mongoDbBulkOpUtil = mongoDbBulkOpUtil;
    }

    @Override
    public void store(List<? extends AccumulatedAggregationFeatureRecord> records) {
        Map<String, ? extends List<? extends AccumulatedAggregationFeatureRecord>> featureToAggrList = records.stream().collect(Collectors.groupingBy(AccumulatedAggregationFeatureRecord::getFeatureName));

        featureToAggrList.keySet().forEach(
                feature ->
                {
                    AccumulatedRecordsMetaData metadata = new AccumulatedRecordsMetaData(feature);
                    String collectionName = getCollectionName(metadata);
                    List<? extends AccumulatedAggregationFeatureRecord> aggrRecords = featureToAggrList.get(feature);
                    mongoDbBulkOpUtil.insertUnordered(aggrRecords, collectionName);
                }
        );
    }

    protected String getCollectionName(AccumulatedRecordsMetaData metadata) {
        return translator.toCollectionName(metadata);
    }

    @Override
    public Set<String> findDistinctAcmContextsByTimeRange(
            String aggregatedFeatureName, Date startTime, Date endTime) {


        AccumulatedRecordsMetaData metadata = new AccumulatedRecordsMetaData(aggregatedFeatureName);
        String collectionName = getCollectionName(metadata);

        Criteria startTimeCriteria = Criteria.where(AdeRecord.START_INSTANT_FIELD).gte(startTime);
        Criteria endTimeCriteria = Criteria.where(AdeContextualAggregatedRecord.END_INSTANT_FIELD).lte(endTime);
        Query query = new Query(startTimeCriteria).addCriteria(endTimeCriteria);
        Set<String> distinctContexts = (Set<String>) mongoTemplate.getCollection(collectionName).distinct(AdeContextualAggregatedRecord.CONTEXT_ID_FIELD, query.getQueryObject()).stream().collect(Collectors.toSet());

        return distinctContexts;
    }

    @Override
    public List<AccumulatedAggregationFeatureRecord> findAccumulatedEventsByContextIdAndStartTimeRange(
            String aggregatedFeatureName,
            String contextId,
            Instant startTimeFrom,
            Instant startTimeTo) {
        logger.debug("getting accumulated events for featureName={}", aggregatedFeatureName);


        AccumulatedRecordsMetaData metadata = new AccumulatedRecordsMetaData(aggregatedFeatureName);
        String collectionName = getCollectionName(metadata);

        Query query = new Query()
                .addCriteria(where(AdeContextualAggregatedRecord.CONTEXT_ID_FIELD)
                        .is(contextId))
                .addCriteria(where(AdeRecord.START_INSTANT_FIELD)
                        .gte(startTimeFrom)
                        .lt(startTimeTo));
        List<AccumulatedAggregationFeatureRecord> accumulatedAggregatedFeatureEvents =
                mongoTemplate.find(query, AccumulatedAggregationFeatureRecord.class, collectionName);

        logger.debug("found {} accumulated events", accumulatedAggregatedFeatureEvents.size());
        return accumulatedAggregatedFeatureEvents;
    }


    @Override
    public void cleanup(AdeDataStoreCleanupParams cleanupParams) {
        // todo
    }
}
