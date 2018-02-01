package presidio.ade.domain.store.accumulator.smart;

import com.mongodb.DBObject;
import com.mongodb.MongoCommandException;
import fortscale.utils.logging.Logger;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import fortscale.utils.pagination.ContextIdToNumOfItems;
import fortscale.utils.store.StoreManager;
import fortscale.utils.store.StoreManagerAware;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Query;
import presidio.ade.domain.record.AdeRecord;
import presidio.ade.domain.record.accumulator.AccumulatedSmartRecord;
import presidio.ade.domain.record.aggregated.AdeContextualAggregatedRecord;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;


public class SmartAccumulationDataStoreMongoImpl implements SmartAccumulationDataStore, StoreManagerAware {
    private static final Logger logger = Logger.getLogger(SmartAccumulationDataStoreMongoImpl.class);

    private final MongoTemplate mongoTemplate;
    private final SmartAccumulatedDataToCollectionNameTranslator translator;
    private final MongoDbBulkOpUtil mongoDbBulkOpUtil;
    private StoreManager storeManager;
    private final long selectorPageSize;

    public SmartAccumulationDataStoreMongoImpl(MongoTemplate mongoTemplate,
                                               SmartAccumulatedDataToCollectionNameTranslator translator,
                                               MongoDbBulkOpUtil mongoDbBulkOpUtil,
                                               long selectorPageSize) {
        this.mongoTemplate = mongoTemplate;
        this.translator = translator;
        this.mongoDbBulkOpUtil = mongoDbBulkOpUtil;
        this.selectorPageSize = selectorPageSize;
    }

    @Override
    public void store(List<? extends AdeContextualAggregatedRecord> records, String configurationName) {
        logger.info("Store accumulated smart records");
        SmartAccumulatedRecordsMetaData metadata = new SmartAccumulatedRecordsMetaData(configurationName);
        String collectionName = getCollectionName(metadata);
        mongoDbBulkOpUtil.insertUnordered(records, collectionName);
        storeManager.registerWithTtl(getStoreName(), collectionName);
    }

    /**
     * @param metadata metadata
     * @return collection name
     */
    protected String getCollectionName(SmartAccumulatedRecordsMetaData metadata) {
        return translator.toCollectionName(metadata);
    }


    @Override
    public List<AccumulatedSmartRecord> findAccumulatedEventsByContextIdAndStartTimeRange(String configurationName,
                                                                                          String contextId,
                                                                                          Instant startTimeFrom,
                                                                                          Instant startTimeTo) {
        logger.debug("getting accumulated events for smart record name={}", configurationName);

        SmartAccumulatedRecordsMetaData metadata = new SmartAccumulatedRecordsMetaData(configurationName);
        String collectionName = getCollectionName(metadata);

        Query query = new Query()
                .addCriteria(where(AdeContextualAggregatedRecord.CONTEXT_ID_FIELD)
                        .is(contextId))
                .addCriteria(where(AdeRecord.START_INSTANT_FIELD)
                        .gte(startTimeFrom)
                        .lt(startTimeTo));
        List<AccumulatedSmartRecord> accumulatedSmartRecords =
                mongoTemplate.find(query, AccumulatedSmartRecord.class, collectionName);


        logger.debug("found {} accumulated events", accumulatedSmartRecords.size());
        return accumulatedSmartRecords;
    }

    @Override
    public Set<String> findDistinctContextsByTimeRange(String configurationName, Instant startInstant, Instant endInstant) {

        logger.debug("finding distinct contexts by configurationName={} startTime={} endTime={}",
                configurationName, startInstant, endInstant);

        SmartAccumulatedRecordsMetaData metadata = new SmartAccumulatedRecordsMetaData(configurationName);
        String collectionName = getCollectionName(metadata);

        Date startDate = Date.from(startInstant);
        Date endDate = Date.from(endInstant);
        Set<String> distinctContexts;
        try {
            Query query = new Query();
            query.addCriteria(where(AdeRecord.START_INSTANT_FIELD).gte(startDate).lt(endDate));
            distinctContexts = (Set<String>) mongoTemplate.getCollection(collectionName)
                    .distinct(AdeContextualAggregatedRecord.CONTEXT_ID_FIELD, query.getQueryObject())
                    .stream().collect(Collectors.toSet());
        } catch (MongoCommandException e) {
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
     * @param startDate startDate
     * @param endDate endDate
     * @param skip skip
     * @param limit limit
     * @param collectionName collectionName
     * @param allowDiskUse allowDiskUse
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

        List<DBObject> aggrResult = mongoTemplate
                .aggregate(aggregation, collectionName, DBObject.class)
                .getMappedResults();

        return aggrResult.stream()
                .map(result -> (String) result.get(AdeContextualAggregatedRecord.CONTEXT_ID_FIELD))
                .collect(Collectors.toSet());
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
    public void remove(String collectionName, Instant start, Instant end){
        Query query = new Query()
                .addCriteria(where(AdeRecord.START_INSTANT_FIELD).gte(start).lt(end));
        mongoTemplate.remove(query, collectionName);
    }
}
