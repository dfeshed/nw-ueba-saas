package presidio.ade.domain.store.enriched;

import fortscale.utils.logging.Logger;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import presidio.ade.domain.record.ContextIdToNumOfEvents;
import presidio.ade.domain.record.AdeRecordTypeToClass;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.store.AdeDataStoreCleanupParams;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.*;

public class EnrichedDataStoreImplMongo implements EnrichedDataStore {
    private static final Logger logger = Logger.getLogger(EnrichedDataStoreImplMongo.class);

    private final MongoTemplate mongoTemplate;
    private final EnrichedDataToCollectionNameTranslator translator;

    public EnrichedDataStoreImplMongo(MongoTemplate mongoTemplate, EnrichedDataToCollectionNameTranslator translator) {
        this.mongoTemplate = mongoTemplate;
        this.translator = translator;
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
        Class pojoClass = AdeRecordTypeToClass.getPojoClass(dataSource);

        //Get type of context
        Field field = ReflectionUtils.findField(pojoClass, contextType);
        String type = (String) ReflectionUtils.getField(field, null);

        Criteria dateTimeCriteria = Criteria.where(EnrichedRecord.DATE_TIME_FIELD).gte(startDate).lt(endDate);
        Criteria contextCriteria = Criteria.where(type).in(contextIds);
        Query query = new Query(dateTimeCriteria).addCriteria(contextCriteria).skip(numOfItemsToSkip).limit(numOfItemsToRead);
        String collectionName = translator.toCollectionName(recordsMetadata);
        List<U> enrichedRecordList = mongoTemplate.find(query, pojoClass, collectionName);

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
        return null;
    }


    @Override
    public Map<String, Integer> aggregateContextToNumOfEvents(EnrichedRecordsMetadata recordsMetadata, String contextType) {

        Instant startDate = recordsMetadata.getStartInstant();
        Instant endDate = recordsMetadata.getEndInstant();
        String dataSource = recordsMetadata.getDataSource();

        //Get pojoClass by dataSource
        Class pojoClass = AdeRecordTypeToClass.getPojoClass(dataSource);

        //Get type of context
        Field field = ReflectionUtils.findField(pojoClass, contextType);
        String type = (String) ReflectionUtils.getField(field, null);


        String collectionName = translator.toCollectionName(recordsMetadata);

        //Create Aggregation on context ids
        Aggregation agg = newAggregation(
                match(where(EnrichedRecord.DATE_TIME_FIELD).gte(startDate).lt(endDate)),
                Aggregation.group(type).count().as("totalNumOfEvents")
        );

        AggregationResults<ContextIdToNumOfEvents> result = mongoTemplate.aggregate(agg, collectionName, ContextIdToNumOfEvents.class);
        List<ContextIdToNumOfEvents> enrichedRecordList = result.getMappedResults();

        //Create map of context id to total amount of events
        Map<String, Integer> contextIdToNumOfEvents = new HashMap<>();
        for (ContextIdToNumOfEvents enrichedRecord : enrichedRecordList) {
            contextIdToNumOfEvents.put(enrichedRecord.getContextId(), enrichedRecord.getTotalNumOfEvents());
        }

        return contextIdToNumOfEvents;
    }


}
