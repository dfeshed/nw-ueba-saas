package presidio.ade.domain.store.aggr.smart;

import fortscale.utils.logging.Logger;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import fortscale.utils.pagination.ContextIdToNumOfItems;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import presidio.ade.domain.record.aggregated.*;
import presidio.ade.domain.record.enriched.EnrichedRecord;

import java.time.Instant;
import java.util.*;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.query.Criteria.where;

public class SmartRecordStoreMongoImpl implements SmartRecordDataStore {
    private static final Logger logger = Logger.getLogger(SmartRecordStoreMongoImpl.class);

    private final MongoTemplate mongoTemplate;
    private final SmartDataToCollectionNameTranslator translator;
    private final MongoDbBulkOpUtil mongoDbBulkOpUtil;

    public SmartRecordStoreMongoImpl(MongoTemplate mongoTemplate, SmartDataToCollectionNameTranslator translator, MongoDbBulkOpUtil mongoDbBulkOpUtil) {
        this.mongoTemplate = mongoTemplate;
        this.translator = translator;
        this.mongoDbBulkOpUtil = mongoDbBulkOpUtil;
    }

    @Override
    public List<ContextIdToNumOfItems> aggregateContextIdToNumOfEvents(SmartRecordsMetadata smartRecordsMetadata) {

        Instant startDate = smartRecordsMetadata.getStartInstant();
        Instant endDate = smartRecordsMetadata.getEndInstant();

        String collectionName = translator.toCollectionName(smartRecordsMetadata);

        //Create Aggregation on context ids
        Aggregation agg = newAggregation(
                match(where(EnrichedRecord.START_INSTANT_FIELD).gte(Date.from(startDate)).lt(Date.from(endDate))),
                Aggregation.group(AdeContextualAggregatedRecord.CONTEXT_ID_FIELD).count().as(ContextIdToNumOfItems.TOTAL_NUM_OF_ITEMS_FIELD),
                Aggregation.project(ContextIdToNumOfItems.TOTAL_NUM_OF_ITEMS_FIELD).and("_id").as(ContextIdToNumOfItems.CONTEXT_ID_FIELD).andExclude("_id")
        );

        AggregationResults<ContextIdToNumOfItems> result = mongoTemplate.aggregate(agg, collectionName, ContextIdToNumOfItems.class);
        //Create list of ContextIdToNumOfItems, which contain contextId and totalNumOfEvents
        return result.getMappedResults();
    }

    @Override
    public List<SmartRecord> readRecords(SmartRecordsMetadata smartRecordsMetadata, Set<String> contextIds, int numOfItemsToSkip, int numOfItemsToRead) {
        Instant startDate = smartRecordsMetadata.getStartInstant();
        Instant endDate = smartRecordsMetadata.getEndInstant();
        String collectionName = translator.toCollectionName(smartRecordsMetadata);

        Criteria dateTimeCriteria = Criteria.where(EnrichedRecord.START_INSTANT_FIELD).gte(startDate).lt(endDate);
        Criteria contextCriteria = Criteria.where(AdeContextualAggregatedRecord.CONTEXT_ID_FIELD).in(contextIds);
        Query query = new Query(dateTimeCriteria).addCriteria(contextCriteria).skip(numOfItemsToSkip).limit(numOfItemsToRead);

        List<SmartRecord> smartRecords = mongoTemplate.find(query, SmartRecord.class, collectionName);

        return smartRecords;
    }

    @Override
    public void ensureContextIdIndex(String configurationName) {

        String collectionName = translator.toCollectionName(configurationName);

        mongoTemplate.indexOps(collectionName).ensureIndex(new Index()
                .on(AdeContextualAggregatedRecord.CONTEXT_ID_FIELD, Sort.Direction.ASC));
    }
}
