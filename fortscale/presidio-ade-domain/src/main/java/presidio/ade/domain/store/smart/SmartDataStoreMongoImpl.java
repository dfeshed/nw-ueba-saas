package presidio.ade.domain.store.smart;

import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import fortscale.utils.pagination.ContextIdToNumOfItems;
import fortscale.utils.store.StoreManager;
import fortscale.utils.store.StoreManagerAware;
import fortscale.utils.store.record.StoreMetadataProperties;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import presidio.ade.domain.record.AdeRecord;
import presidio.ade.domain.record.aggregated.AdeContextualAggregatedRecord;
import presidio.ade.domain.record.aggregated.SmartRecord;

import java.time.Instant;
import java.util.*;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * A mongo based implementation for the {@link SmartDataStore}.
 *
 * @author Lior Govrin
 */
public class SmartDataStoreMongoImpl implements SmartDataStore, StoreManagerAware {

    private final MongoDbBulkOpUtil mongoDbBulkOpUtil;
    private final SmartDataToCollectionNameTranslator translator;
    private final MongoTemplate mongoTemplate;
    private StoreManager storeManager;

    public SmartDataStoreMongoImpl(MongoDbBulkOpUtil mongoDbBulkOpUtil, SmartDataToCollectionNameTranslator translator, MongoTemplate mongoTemplate) {
        this.mongoDbBulkOpUtil = mongoDbBulkOpUtil;
        this.translator = translator;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void storeSmartRecords(String smartRecordConfName, Collection<SmartRecord> smartRecords, StoreMetadataProperties storeMetadataProperties) {
        String collectionName = translator.toCollectionName(smartRecordConfName);
        mongoDbBulkOpUtil.insertUnordered(new ArrayList<>(smartRecords), collectionName);
        storeManager.registerWithTtl(getStoreName(), collectionName, storeMetadataProperties);
    }

    @Override
    public List<ContextIdToNumOfItems> aggregateContextIdToNumOfEvents(SmartRecordsMetadata smartRecordsMetadata, int scoreThreshold) {

        Instant startDate = smartRecordsMetadata.getStartInstant();
        Instant endDate = smartRecordsMetadata.getEndInstant();

        String collectionName = translator.toCollectionName(smartRecordsMetadata);

        //Create Aggregation on context ids
        Aggregation agg = newAggregation(
                match(where(AdeRecord.START_INSTANT_FIELD).gte(Date.from(startDate)).lt(Date.from(endDate))),
                match(where(SmartRecord.SMART_SCORE_FIELD).gte(scoreThreshold)),
                Aggregation.group(AdeContextualAggregatedRecord.CONTEXT_ID_FIELD).count().as(ContextIdToNumOfItems.TOTAL_NUM_OF_ITEMS_FIELD),
                Aggregation.project(ContextIdToNumOfItems.TOTAL_NUM_OF_ITEMS_FIELD).and("_id").as(ContextIdToNumOfItems.CONTEXT_ID_FIELD).andExclude("_id")
        );

        AggregationResults<ContextIdToNumOfItems> result = mongoTemplate.aggregate(agg, collectionName, ContextIdToNumOfItems.class);
        //Create list of ContextIdToNumOfItems, which contain contextId and totalNumOfEvents
        return result.getMappedResults();
    }

    @Override
    public List<SmartRecord> readRecords(SmartRecordsMetadata smartRecordsMetadata, Set<String> contextIds, int numOfItemsToSkip, int numOfItemsToRead, int scoreThreshold) {
        Instant startDate = smartRecordsMetadata.getStartInstant();
        Instant endDate = smartRecordsMetadata.getEndInstant();
        String collectionName = translator.toCollectionName(smartRecordsMetadata);

        Criteria dateTimeCriteria = Criteria.where(AdeRecord.START_INSTANT_FIELD).gte(startDate).lt(endDate);
        Criteria contextCriteria = Criteria.where(AdeContextualAggregatedRecord.CONTEXT_ID_FIELD).in(contextIds);
        Criteria scoreCriteria = Criteria.where(SmartRecord.SMART_SCORE_FIELD).gte(scoreThreshold);
        Query query = new Query(dateTimeCriteria).addCriteria(contextCriteria).addCriteria(scoreCriteria).skip(numOfItemsToSkip).limit(numOfItemsToRead);

        return mongoTemplate.find(query, SmartRecord.class, collectionName);
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
