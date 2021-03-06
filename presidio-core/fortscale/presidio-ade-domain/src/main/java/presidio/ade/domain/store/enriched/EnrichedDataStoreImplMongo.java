package presidio.ade.domain.store.enriched;

import fortscale.utils.logging.Logger;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import fortscale.utils.mongodb.util.MongoReflectionUtils;
import fortscale.utils.pagination.ContextIdToNumOfItems;
import fortscale.utils.store.StoreManager;
import fortscale.utils.store.record.StoreMetadataProperties;
import fortscale.utils.time.TimeRange;
import org.bson.Document;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import presidio.ade.domain.record.AdeRecord;
import presidio.ade.domain.record.enriched.AdeEventTypeToAdeEnrichedRecordClassResolver;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.store.AdeDataStoreCleanupParams;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

public class EnrichedDataStoreImplMongo implements StoreManagerAwareEnrichedDataStore {
    private static final Logger logger = Logger.getLogger(EnrichedDataStoreImplMongo.class);

    private final MongoTemplate mongoTemplate;
    private final EnrichedDataAdeToCollectionNameTranslator translator;
    private final AdeEventTypeToAdeEnrichedRecordClassResolver adeEventTypeToAdeEnrichedRecordClassResolver;
    private final MongoDbBulkOpUtil mongoDbBulkOpUtil;
    private final long contextIdToNumOfItemsPageSize;
    private StoreManager storeManager;

    public EnrichedDataStoreImplMongo(
            MongoTemplate mongoTemplate,
            EnrichedDataAdeToCollectionNameTranslator translator,
            AdeEventTypeToAdeEnrichedRecordClassResolver adeEventTypeToAdeEnrichedRecordClassResolver,
            MongoDbBulkOpUtil mongoDbBulkOpUtil,
            long contextIdToNumOfItemsPageSize) {

        this.mongoTemplate = mongoTemplate;
        this.translator = translator;
        this.adeEventTypeToAdeEnrichedRecordClassResolver = adeEventTypeToAdeEnrichedRecordClassResolver;
        this.mongoDbBulkOpUtil = mongoDbBulkOpUtil;
        this.contextIdToNumOfItemsPageSize = contextIdToNumOfItemsPageSize;
    }

    @Override
    public void store(EnrichedRecordsMetadata recordsMetadata, List<? extends EnrichedRecord> records, StoreMetadataProperties storeMetadataProperties) {
        logger.info("storing by recordsMetadata={}", recordsMetadata);
        String collectionName = translator.toCollectionName(recordsMetadata);
        mongoDbBulkOpUtil.insertUnordered(records, collectionName);
        storeManager.registerWithTtl(getStoreName(), collectionName, storeMetadataProperties);
    }

    @Override
    public <U extends EnrichedRecord> List<U> readRecords(EnrichedRecordsMetadata recordsMetadata, Set<String> contextIds, String contextType, int numOfItemsToSkip, int numOfItemsToRead) {
        String adeEventType = recordsMetadata.getAdeEventType();
        Class<? extends AdeRecord> pojoClass = adeEventTypeToAdeEnrichedRecordClassResolver.getClass(adeEventType);

        Query query = buildQuery(recordsMetadata, contextIds, contextType, numOfItemsToSkip, numOfItemsToRead, pojoClass);

        String collectionName = translator.toCollectionName(recordsMetadata);

        // the pojoClass is Class<? extends AdeRecord>, while find method should get Class<U>
        @SuppressWarnings("unchecked")
        List<U> enrichedRecordList = mongoTemplate.find(query, (Class<U>) pojoClass, collectionName);

        return enrichedRecordList;
    }


    @Override
    public <U extends EnrichedRecord> List<U> readSortedRecords(EnrichedRecordsMetadata recordsMetadata, Set<String> contextIds, String contextType, int numOfItemsToSkip, int numOfItemsToRead, String sortBy) {
        String adeEventType = recordsMetadata.getAdeEventType();
        Class<? extends AdeRecord> pojoClass = adeEventTypeToAdeEnrichedRecordClassResolver.getClass(adeEventType);

        Query query = buildQuery(recordsMetadata, contextIds, contextType, numOfItemsToSkip, numOfItemsToRead, pojoClass);

        //Get field name
        String sortByFieldName = getFieldName(pojoClass, sortBy);
        query.with(new Sort(Sort.Direction.ASC, sortByFieldName));

        String collectionName = translator.toCollectionName(recordsMetadata);

        // the pojoClass is Class<? extends AdeRecord>, while find method should get Class<U>
        @SuppressWarnings("unchecked")
        List<U> enrichedRecordList = mongoTemplate.find(query, (Class<U>) pojoClass, collectionName);

        return enrichedRecordList;
    }

    private Query buildQuery(EnrichedRecordsMetadata recordsMetadata, Set<String> contextIds, String contextType,
                             int numOfItemsToSkip, int numOfItemsToRead, Class<? extends AdeRecord> pojoClass) {

        Instant startDate = recordsMetadata.getStartInstant();
        Instant endDate = recordsMetadata.getEndInstant();
        // Get type of context
        String fieldName = getFieldName(pojoClass, contextType);
        Criteria dateTimeCriteria = Criteria.where(EnrichedRecord.START_INSTANT_FIELD).gte(startDate).lt(endDate);
        Criteria contextCriteria = Criteria.where(fieldName).in(contextIds);
        return new Query(dateTimeCriteria).addCriteria(contextCriteria).skip(numOfItemsToSkip).limit(numOfItemsToRead);
    }

    @Override
    public void cleanup(AdeDataStoreCleanupParams cleanupParams) {
        logger.info("cleanup by cleanupParams={}", cleanupParams);
        Collection<String> collectionNames = translator.toCollectionNames(cleanupParams);
        Query cleanupQuery = toCleanupQuery(cleanupParams);

        for (String collectionName : collectionNames) {
            mongoTemplate.remove(cleanupQuery, collectionName);
        }
    }

    /**
     * @param cleanupParams to build the remove query
     * @return cleanup query by cleanup params
     */
    private Query toCleanupQuery(AdeDataStoreCleanupParams cleanupParams) {
        Criteria dateTimeCriteria = Criteria.where(EnrichedRecord.START_INSTANT_FIELD).gte(cleanupParams.getStartDate()).lt(cleanupParams.getEndDate());
        return new Query(dateTimeCriteria);
    }


    @Override
    public List<ContextIdToNumOfItems> aggregateContextToNumOfEvents(
            EnrichedRecordsMetadata recordsMetadata, String contextType, boolean filterNullContext) {

        Date startDate = Date.from(recordsMetadata.getStartInstant());
        Date endDate = Date.from(recordsMetadata.getEndInstant());
        String adeEventType = recordsMetadata.getAdeEventType();
        Class pojoClass = adeEventTypeToAdeEnrichedRecordClassResolver.getClass(adeEventType);
        String fieldName = getFieldName(pojoClass, contextType);
        String collectionName = translator.toCollectionName(recordsMetadata);

        try {
            return aggregateContextIdToNumOfItems(startDate, endDate, fieldName, -1, 0, collectionName, false, filterNullContext);
        } catch (InvalidDataAccessApiUsageException e) {
            long nextPageIndex = 0;
            List<ContextIdToNumOfItems> subList;
            List<ContextIdToNumOfItems> results = new LinkedList<>();

            do {
                subList = aggregateContextIdToNumOfItems(startDate, endDate, fieldName,
                        nextPageIndex * contextIdToNumOfItemsPageSize, contextIdToNumOfItemsPageSize, collectionName, true, filterNullContext);
                results.addAll(subList);
                nextPageIndex++;
            } while (subList.size() == contextIdToNumOfItemsPageSize);

            return results;
        }
    }

    private List<ContextIdToNumOfItems> aggregateContextIdToNumOfItems(
            Date startDate, Date endDate, String fieldName, long skip, long limit, String collectionName, boolean allowDiskUse, boolean filterNullContext) {

        List<AggregationOperation> aggregationOperations = new LinkedList<>();
        aggregationOperations.add(match(where(AdeRecord.START_INSTANT_FIELD).gte(startDate).lt(endDate)));

        if(filterNullContext){
            aggregationOperations.add(match(where(fieldName).ne(null)));
        }

        aggregationOperations.add(group(fieldName).count().as(ContextIdToNumOfItems.TOTAL_NUM_OF_ITEMS_FIELD));
        aggregationOperations.add(project(ContextIdToNumOfItems.TOTAL_NUM_OF_ITEMS_FIELD)
                .and("_id").as(ContextIdToNumOfItems.CONTEXT_ID_FIELD)
                .andExclude("_id"));

        if (skip >= 0 && limit > 0) {
            aggregationOperations.add(sort(Direction.ASC, ContextIdToNumOfItems.CONTEXT_ID_FIELD));
            aggregationOperations.add(skip(skip));
            aggregationOperations.add(limit(limit));
        }

        Aggregation aggregation = newAggregation(aggregationOperations).withOptions(Aggregation.newAggregationOptions().
                allowDiskUse(allowDiskUse).build());

        return mongoTemplate.aggregate(aggregation, collectionName, ContextIdToNumOfItems.class).getMappedResults();
    }

    /**
     * Validates that the context type field indexed in the store, otherwise create the index
     *
     * @param adeEventType data source name
     * @param contextType  type of context, field that the aggregateContextToNumOfEvents and findScoredEnrichedRecords methods use to query.
     */
    @Override
    public void ensureContextAndDateTimeIndex(String adeEventType, String contextType) {
        // Get pojoClass by adeEventType
        Class pojoClass = adeEventTypeToAdeEnrichedRecordClassResolver.getClass(adeEventType);
        // Get type of context
        String fieldName = getFieldName(pojoClass, contextType);
        String collectionName = translator.toCollectionName(adeEventType);

        // Used by the readRecords method (no sorting)
        Document indexOptions = new Document(); // Keeps entries ordered
        indexOptions.put(fieldName, 1); // Ascending
        indexOptions.put(EnrichedRecord.START_INSTANT_FIELD, 1); // Ascending
        CompoundIndexDefinition indexDefinition = new CompoundIndexDefinition(indexOptions);
        indexDefinition.named(fieldName + "_" + EnrichedRecord.START_INSTANT_FIELD);
        mongoTemplate.indexOps(collectionName).ensureIndex(indexDefinition);

        // Used by the readSortedRecords method (sorting by startInstant)
        indexOptions = new Document(); // Keeps entries ordered
        indexOptions.put(EnrichedRecord.START_INSTANT_FIELD, 1); // Ascending
        indexOptions.put(fieldName, 1); // Ascending
        indexDefinition = new CompoundIndexDefinition(indexOptions);
        indexDefinition.named(EnrichedRecord.START_INSTANT_FIELD + "_" + fieldName);
        mongoTemplate.indexOps(collectionName).ensureIndex(indexDefinition);
    }


    /**
     * Get field name
     * If annotation exist return field name of annotation, otherwise return original field name.
     *
     * @param pojoClass class that contain the field
     * @param name      - field name
     * @return field name
     */
    private String getFieldName(Class pojoClass, String name) {
        return MongoReflectionUtils.findFieldNameRecursively(pojoClass, name);
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

    @Override
    public long countRecords(EnrichedRecordsMetadata recordsMetadata, String contextType, String contextId) {

        Instant startDate = recordsMetadata.getStartInstant();
        Instant endDate = recordsMetadata.getEndInstant();
        Criteria dateTimeCriteria = Criteria.where(EnrichedRecord.START_INSTANT_FIELD).gte(startDate).lt(endDate);

        String adeEventType = recordsMetadata.getAdeEventType();
        Class<? extends AdeRecord> pojoClass = adeEventTypeToAdeEnrichedRecordClassResolver.getClass(adeEventType);
        String fieldName = getFieldName(pojoClass, contextType);
        Criteria contextCriteria = Criteria.where(fieldName).is(contextId);

        Query query = new Query(dateTimeCriteria).addCriteria(contextCriteria);

        String collectionName = translator.toCollectionName(recordsMetadata);
        return mongoTemplate.count(query, collectionName);
    }

    @Override
    public long count(String adeEventType, TimeRange timeRange, Map<String, String> context) {
        Class pojoClass = adeEventTypeToAdeEnrichedRecordClassResolver.getClass(adeEventType);
        Query query = getQuery(pojoClass, timeRange, context);
        String collectionName = translator.toCollectionName(adeEventType);
        return mongoTemplate.count(query, collectionName);
    }

    @Override
    public List<EnrichedRecord> find(String adeEventType, TimeRange timeRange, Map<String, String> context, long skip, int limit) {
        Class pojoClass = adeEventTypeToAdeEnrichedRecordClassResolver.getClass(adeEventType);
        Query query = getQuery(pojoClass, timeRange, context).skip(skip).limit(limit);
        String collectionName = translator.toCollectionName(adeEventType);
        @SuppressWarnings("unchecked")
        List<EnrichedRecord> enrichedRecords = mongoTemplate.find(query, pojoClass, collectionName);
        return enrichedRecords;
    }

    private Query getQuery(Class pojoClass, TimeRange timeRange, Map<String, String> context) {
        Query query = new Query(where(EnrichedRecord.START_INSTANT_FIELD).gte(timeRange.getStart()).lt(timeRange.getEnd()));
        context.forEach((contextKey, contextValue) -> {
            String fieldName = getFieldName(pojoClass, contextKey);
            query.addCriteria(where(fieldName).is(contextValue));
        });
        return query;
    }
}
