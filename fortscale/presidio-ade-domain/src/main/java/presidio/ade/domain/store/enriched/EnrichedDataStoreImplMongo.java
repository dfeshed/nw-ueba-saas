package presidio.ade.domain.store.enriched;


import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import fortscale.utils.logging.Logger;
import fortscale.utils.pagination.ContextIdToNumOfItems;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.ReflectionUtils;
import presidio.ade.domain.record.AdeRecord;
import presidio.ade.domain.record.enriched.DataSourceToAdeEnrichedRecordClassResolver;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.store.AdeDataStoreCleanupParams;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.query.Criteria.where;

public class EnrichedDataStoreImplMongo implements EnrichedDataStore {
    private static final Logger logger = Logger.getLogger(EnrichedDataStoreImplMongo.class);

    private final MongoTemplate mongoTemplate;
    private final EnrichedDataAdeToCollectionNameTranslator translator;
    private final DataSourceToAdeEnrichedRecordClassResolver dataSourceToAdeEnrichedRecordClassResolver;

    public EnrichedDataStoreImplMongo(MongoTemplate mongoTemplate, EnrichedDataAdeToCollectionNameTranslator translator, DataSourceToAdeEnrichedRecordClassResolver dataSourceToAdeEnrichedRecordClassResolver) {
        this.mongoTemplate = mongoTemplate;
        this.translator = translator;
        this.dataSourceToAdeEnrichedRecordClassResolver = dataSourceToAdeEnrichedRecordClassResolver;
    }

    @Override
    public void store(EnrichedRecordsMetadata recordsMetadata, List<? extends EnrichedRecord> records) {
        logger.info("storing by recordsMetadata={}", recordsMetadata);
        String collectionName = translator.toCollectionName(recordsMetadata);
        mongoTemplate.insert(records, collectionName);
    }

    @Override
    public <U extends EnrichedRecord> List<U> readRecords(EnrichedRecordsMetadata recordsMetadata, Set<String> contextIds, String contextType, int numOfItemsToSkip, int numOfItemsToRead) {

        Instant startDate = recordsMetadata.getStartInstant();
        Instant endDate = recordsMetadata.getEndInstant();
        String dataSource = recordsMetadata.getDataSource();
        Class<? extends AdeRecord> pojoClass = dataSourceToAdeEnrichedRecordClassResolver.getClass(dataSource);

        //Get type of context
        String fieldName = getFieldName(pojoClass, contextType);

        Criteria dateTimeCriteria = Criteria.where(EnrichedRecord.DATE_TIME_FIELD).gte(startDate).lt(endDate);
        Criteria contextCriteria = Criteria.where(fieldName).in(contextIds);
        Query query = new Query(dateTimeCriteria).addCriteria(contextCriteria).skip(numOfItemsToSkip).limit(numOfItemsToRead);
        String collectionName = translator.toCollectionName(recordsMetadata);

        // the pojoClass is Class<? extends AdeRecord>, while find method should get Class<U>
        @SuppressWarnings("unchecked")
        List<U> enrichedRecordList = mongoTemplate.find(query, (Class<U>) pojoClass, collectionName);

        return enrichedRecordList;
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
        Criteria dateTimeCriteria = Criteria.where(EnrichedRecord.DATE_TIME_FIELD).gte(cleanupParams.getStartDate()).lt(cleanupParams.getEndDate());
        return new Query(dateTimeCriteria);
    }


    @Override
    public List<ContextIdToNumOfItems> aggregateContextToNumOfEvents(EnrichedRecordsMetadata recordsMetadata, String contextType) {

        Instant startDate = recordsMetadata.getStartInstant();
        Instant endDate = recordsMetadata.getEndInstant();
        String dataSource = recordsMetadata.getDataSource();

        //Get pojoClass by dataSource
        Class pojoClass = dataSourceToAdeEnrichedRecordClassResolver.getClass(dataSource);
        //Get type of context
        String fieldName = getFieldName(pojoClass, contextType);

        String collectionName = translator.toCollectionName(recordsMetadata);

        //Create Aggregation on context ids
        Aggregation agg = newAggregation(
                match(where(EnrichedRecord.DATE_TIME_FIELD).gte(Date.from(startDate)).lt(Date.from(endDate))),
                Aggregation.group(fieldName).count().as(ContextIdToNumOfItems.TOTAL_NUM_OF_ITEMS_FIELD),
                Aggregation.project(ContextIdToNumOfItems.TOTAL_NUM_OF_ITEMS_FIELD).and("_id").as(ContextIdToNumOfItems.CONTEXT_ID_FIELD).andExclude("_id")
        );

        AggregationResults<ContextIdToNumOfItems> result = mongoTemplate.aggregate(agg, collectionName, ContextIdToNumOfItems.class);
        //Create list of ContextIdToNumOfItems, which contain contextId and totalNumOfEvents
        return result.getMappedResults();
    }

    /**
     * Validates that the context type field indexed in the store, otherwise create the index
     *
     * @param dataSource  data source name
     * @param contextType type of context, field that the aggregateContextToNumOfEvents and readRecords methods use to query.
     */
    @Override
    public void ensureContextAndDateTimeIndex(String dataSource, String contextType) {
        //Get pojoClass by dataSource
        Class pojoClass = dataSourceToAdeEnrichedRecordClassResolver.getClass(dataSource);

        //Get type of context
        String fieldName = getFieldName(pojoClass, contextType);

        String collectionName = translator.toCollectionName(dataSource);

        //used for readRecords
        DBObject indexOptions = new BasicDBObject();

        //  IndexDirection.ASCENDING = 1
        //  IndexDirection.DESCENDING = -1
        indexOptions.put(fieldName, 1);
        indexOptions.put(EnrichedRecord.DATE_TIME_FIELD, 1);
        CompoundIndexDefinition indexDefinition = new CompoundIndexDefinition(indexOptions);

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
        Field field = ReflectionUtils.findField(pojoClass, name);
        String fieldName = field.getName();
        if (field.isAnnotationPresent(org.springframework.data.mongodb.core.mapping.Field.class)) {
            fieldName = field.getAnnotation(org.springframework.data.mongodb.core.mapping.Field.class).value();
        }
        return fieldName;
    }


}
