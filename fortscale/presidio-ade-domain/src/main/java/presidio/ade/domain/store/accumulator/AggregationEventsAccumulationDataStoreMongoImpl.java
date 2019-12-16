package presidio.ade.domain.store.accumulator;

import fortscale.utils.logging.Logger;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import fortscale.utils.store.StoreManager;
import fortscale.utils.store.StoreManagerAware;
import fortscale.utils.store.record.StoreMetadataProperties;
import fortscale.utils.time.TimeRange;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import presidio.ade.domain.record.AdeRecord;
import presidio.ade.domain.record.accumulator.AccumulatedAggregationFeatureRecord;
import presidio.ade.domain.record.aggregated.AdeContextualAggregatedRecord;
import presidio.ade.domain.store.AdeDataStoreCleanupParams;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;


public class AggregationEventsAccumulationDataStoreMongoImpl implements AggregationEventsAccumulationDataStore, StoreManagerAware {
    private static final Logger logger = Logger.getLogger(AggregationEventsAccumulationDataStoreMongoImpl.class);

    private final MongoTemplate mongoTemplate;
    private final AccumulatedDataToCollectionNameTranslator translator;
    private final MongoDbBulkOpUtil mongoDbBulkOpUtil;
    private StoreManager storeManager;
    private final long selectorPageSize;

    public AggregationEventsAccumulationDataStoreMongoImpl(MongoTemplate mongoTemplate, AccumulatedDataToCollectionNameTranslator translator, MongoDbBulkOpUtil mongoDbBulkOpUtil, long selectorPageSize) {
        this.mongoTemplate = mongoTemplate;
        this.translator = translator;
        this.mongoDbBulkOpUtil = mongoDbBulkOpUtil;
        this.selectorPageSize = selectorPageSize;
    }

    @Override
    public void store(List<AccumulatedAggregationFeatureRecord> records, StoreMetadataProperties storeMetadataProperties) {
        Map<String, List<AccumulatedAggregationFeatureRecord>> featureToAggrList = records.stream().collect(Collectors.groupingBy(AccumulatedAggregationFeatureRecord::getFeatureName));

        featureToAggrList.keySet().forEach(
                feature ->
                {
                    AccumulatedRecordsMetaData metadata = new AccumulatedRecordsMetaData(feature);
                    String collectionName = getCollectionName(metadata);
                    List<AccumulatedAggregationFeatureRecord> aggrRecords = featureToAggrList.get(feature);
                    mongoDbBulkOpUtil.insertUnordered(aggrRecords, collectionName);
                    storeManager.registerWithTtl(getStoreName(), collectionName, storeMetadataProperties);
                }
        );
    }

    protected String getCollectionName(AccumulatedRecordsMetaData metadata) {
        return translator.toCollectionName(metadata);
    }

    @Override
    public Set<String> findDistinctAcmContextsByTimeRange(
            String aggregatedFeatureName, TimeRange timeRange) {

        AccumulatedRecordsMetaData metadata = new AccumulatedRecordsMetaData(aggregatedFeatureName);
        String collectionName = getCollectionName(metadata);

        Date startDate = Date.from(timeRange.getStart());
        Date endDate = Date.from(timeRange.getEnd());
        Set<String> distinctContexts = new HashSet<>();
        try {
            Criteria startTimeCriteria = Criteria.where(AdeRecord.START_INSTANT_FIELD).gte(startDate).lt(endDate);
            Query query = new Query(startTimeCriteria);
            mongoTemplate.getCollection(collectionName)
                    .distinct(AdeContextualAggregatedRecord.CONTEXT_ID_FIELD, query.getQueryObject(), String.class)
                    .into(distinctContexts);
        } catch (Exception e) {
            long nextPageIndex = 0;
            Set<String> subList;
            distinctContexts = new HashSet<>();
            do {
                subList = aggregateContextIds(startDate, endDate,
                        nextPageIndex * selectorPageSize, selectorPageSize, collectionName, true);
                distinctContexts.addAll(subList);
                nextPageIndex++;
            } while (subList.size() == selectorPageSize);
        }

        logger.debug("found {} distinct contexts", distinctContexts.size());
        return distinctContexts;
    }


    /**
     * Aggregate distinct contextIds
     *
     * @param startDate      startDate
     * @param endDate        endDate
     * @param skip           skip
     * @param limit          limit
     * @param collectionName collectionName
     * @param allowDiskUse   allowDiskUse
     * @return set of distinct contextIds
     */
    private Set<String> aggregateContextIds(
            Date startDate, Date endDate, long skip, long limit, String collectionName, boolean allowDiskUse) {

        List<AggregationOperation> aggregationOperations = new LinkedList<>();
        aggregationOperations.add(match(where(AdeRecord.START_INSTANT_FIELD).gte(startDate).lt(endDate)));

        aggregationOperations.add(group(AdeContextualAggregatedRecord.CONTEXT_ID_FIELD));
        aggregationOperations.add(project(AdeContextualAggregatedRecord.CONTEXT_ID_FIELD).and("_id").as(AdeContextualAggregatedRecord.CONTEXT_ID_FIELD)
                .andExclude("_id"));

        if (skip >= 0 && limit > 0) {
            aggregationOperations.add(sort(Sort.Direction.ASC, AdeContextualAggregatedRecord.CONTEXT_ID_FIELD));
            aggregationOperations.add(skip(skip));
            aggregationOperations.add(limit(limit));
        }

        Aggregation aggregation = newAggregation(aggregationOperations).withOptions(Aggregation.newAggregationOptions().
                allowDiskUse(allowDiskUse).build());

        List<Document> aggrResult = mongoTemplate
                .aggregate(aggregation, collectionName, Document.class)
                .getMappedResults();

        return aggrResult.stream()
                .map(result -> (String) result.get(AdeContextualAggregatedRecord.CONTEXT_ID_FIELD))
                .collect(Collectors.toSet());
    }

    @Override
    public List<AccumulatedAggregationFeatureRecord> findAccumulatedEventsByContextIdAndStartTimeRange(
            String aggregatedFeatureName,
            String contextId,
            Instant startTimeFrom,
            Instant startTimeTo) {
        Query query = new Query();
        addContextIdCriteria(query, contextId);
        addStartTimeRangeCriteria(query, startTimeFrom, startTimeTo);

        return findAccumulatedEventsByQuery(aggregatedFeatureName, query);
    }

    @Override
    public List<AccumulatedAggregationFeatureRecord> findAccumulatedEventsByStartTimeRange(
            String aggregatedFeatureName,
            Instant startTimeFrom,
            Instant startTimeTo) {
        Query query = new Query();
        addStartTimeRangeCriteria(query, startTimeFrom, startTimeTo);

        return findAccumulatedEventsByQuery(aggregatedFeatureName, query);
    }

    private void addContextIdCriteria(Query query, String contextId){
        query.addCriteria(where(AdeContextualAggregatedRecord.CONTEXT_ID_FIELD)
                .is(contextId));
    }

    private void addStartTimeRangeCriteria(Query query, Instant startTimeFrom, Instant startTimeTo){
        query.addCriteria(where(AdeRecord.START_INSTANT_FIELD)
                .gte(startTimeFrom)
                .lt(startTimeTo));
    }

    private List<AccumulatedAggregationFeatureRecord> findAccumulatedEventsByQuery(
            String aggregatedFeatureName,
            Query query){
        logger.debug("getting accumulated events for featureName={}", aggregatedFeatureName);

        String collectionName = getCollectionName(new AccumulatedRecordsMetaData(aggregatedFeatureName));

        List<AccumulatedAggregationFeatureRecord> accumulatedAggregatedFeatureEvents =
                mongoTemplate.find(query, AccumulatedAggregationFeatureRecord.class, collectionName);

        logger.debug("found {} accumulated events", accumulatedAggregatedFeatureEvents.size());
        return accumulatedAggregatedFeatureEvents;
    }


    @Override
    public void cleanup(AdeDataStoreCleanupParams cleanupParams) {
        // todo
    }

    @Override
    public void setStoreManager(StoreManager storeManager) {
        this.storeManager = storeManager;
    }

    @Override
    public void remove(String collectionName, Instant until) {
        Query query = new Query()
                .addCriteria(where(AdeRecord.START_INSTANT_FIELD).lt(until));
        mongoTemplate.remove(query, collectionName);
    }

    @Override
    public void remove(String collectionName, Instant start, Instant end) {
        Query query = new Query()
                .addCriteria(where(AdeRecord.START_INSTANT_FIELD).gte(start).lt(end));
        mongoTemplate.remove(query, collectionName);
    }
}
