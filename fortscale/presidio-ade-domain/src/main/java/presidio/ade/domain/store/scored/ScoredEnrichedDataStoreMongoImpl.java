package presidio.ade.domain.store.scored;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import fortscale.common.feature.MultiKeyFeature;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import fortscale.utils.store.StoreManager;
import fortscale.utils.store.StoreManagerAware;
import fortscale.utils.store.record.StoreMetadataProperties;
import fortscale.utils.time.TimeRange;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.util.Pair;
import presidio.ade.domain.record.AdeScoredRecord;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.domain.store.AdeDataStoreCleanupParams;
import presidio.ade.domain.store.ScoredDataReader;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord.*;
import static presidio.ade.domain.record.enriched.BaseEnrichedContext.EVENT_ID_FIELD_NAME;
import static presidio.ade.domain.store.ScoredDataReaderMongoUtils.*;

/**
 * @author Yaron DL
 * @author Lior Govrin
 */
public class ScoredEnrichedDataStoreMongoImpl implements
        ScoredEnrichedDataStore,
        StoreManagerAware,
        ScoredDataReader<AdeScoredEnrichedRecord> {

    private final MongoTemplate mongoTemplate;
    private final AdeScoredEnrichedRecordToCollectionNameTranslator translator;
    private final MongoDbBulkOpUtil mongoDbBulkOpUtil;

    private StoreManager storeManager;

    public ScoredEnrichedDataStoreMongoImpl(
            MongoTemplate mongoTemplate,
            AdeScoredEnrichedRecordToCollectionNameTranslator translator,
            MongoDbBulkOpUtil mongoDbBulkOpUtil) {

        this.mongoTemplate = mongoTemplate;
        this.translator = translator;
        this.mongoDbBulkOpUtil = mongoDbBulkOpUtil;
    }

    @Override
    public void store(
            List<? extends AdeScoredEnrichedRecord> recordList,
            StoreMetadataProperties storeMetadataProperties) {

        Map<String, List<AdeScoredEnrichedRecord>> collectionNameToRecordList = new HashMap<>();

        for (AdeScoredEnrichedRecord record : recordList) {
            String collectionName = translator.toCollectionName(record);
            List<AdeScoredEnrichedRecord> collectionRecordList = collectionNameToRecordList
                    .computeIfAbsent(collectionName, key -> new ArrayList<>());
            collectionRecordList.add(record);
        }

        for (Map.Entry<String, List<AdeScoredEnrichedRecord>> entry : collectionNameToRecordList.entrySet()) {
            List<AdeScoredEnrichedRecord> batchToSave = entry.getValue();
            String collectionName = entry.getKey();
            mongoDbBulkOpUtil.insertUnordered(batchToSave, collectionName);
            storeManager.registerWithTtl(getStoreName(), collectionName, storeMetadataProperties);
        }
    }

    @Override
    public void cleanup(AdeDataStoreCleanupParams cleanupParams) {
        // TODO: Implement.
    }

    @Override
    public List<AdeScoredEnrichedRecord> findScoredEnrichedRecords(
            List<String> eventIds,
            String adeEventType,
            Double scoreThreshold) {

        String collectionName = translator.toCollectionName(adeEventType);
        Criteria scoreFilter = Criteria.where(SCORE_FIELD_NAME).gt(scoreThreshold);
        Criteria eventIdsFilter = Criteria
                .where(String.format("%s.%s", CONTEXT_FIELD_NAME, EVENT_ID_FIELD_NAME))
                .in(eventIds);
        Query query = Query.query(eventIdsFilter).addCriteria(scoreFilter);
        return mongoTemplate.find(query, AdeScoredEnrichedRecord.class, collectionName);
    }

    /**
     * This method is a hack. Should be removed!!!
     *
     * @param adeEventType         type of {@link AdeScoredEnrichedRecord} - symbolize the scored feature name
     * @param contextFieldAndValue i.e. "userId", "someUser"
     * @param timeRange            time line filtering param
     * @param distinctFieldName    field to retrieve distinct values on
     * @param scoreThreshold       distinct values would be fetched only for
     *                             records having score greater then this value
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<String> findScoredEnrichedRecordsDistinctFeatureValues(
            String adeEventType,
            Pair<String, String> contextFieldAndValue,
            TimeRange timeRange,
            String distinctFieldName,
            Double scoreThreshold) {

        String collectionName = translator.toCollectionName(adeEventType);
        Criteria contextFilter = Criteria
                .where(String.format("%s.%s", CONTEXT_FIELD_NAME, contextFieldAndValue.getFirst()))
                .is(contextFieldAndValue.getSecond());
        String contextualDistinctField = distinctFieldName;

        if (!START_INSTANT_FIELD.equals(distinctFieldName)) {
            contextualDistinctField = String.format("%s.%s", CONTEXT_FIELD_NAME, distinctFieldName);
        }

        Criteria timeRangeFilter = Criteria
                .where(START_INSTANT_FIELD)
                .gte(timeRange.getStartAsDate())
                .lt(timeRange.getEndAsDate());
        Criteria scoreFilter = Criteria.where(SCORE_FIELD_NAME).gt(scoreThreshold);
        Query query = Query.query(contextFilter).addCriteria(timeRangeFilter).addCriteria(scoreFilter);
        DBCollection collection = mongoTemplate.getCollection(collectionName);
        DBObject queryObject = query.getQueryObject();
        return collection.distinct(contextualDistinctField, queryObject);
    }

    @Override
    public void setStoreManager(StoreManager storeManager) {
        this.storeManager = storeManager;
    }

    @Override
    public void remove(String collectionName, Instant until) {
        Query query = new Query().addCriteria(where(START_INSTANT_FIELD).lt(until));
        mongoTemplate.remove(query, collectionName);
    }

    @Override
    public void remove(String collectionName, Instant start, Instant end) {
        Query query = new Query().addCriteria(where(START_INSTANT_FIELD).gte(start).lt(end));
        mongoTemplate.remove(query, collectionName);
    }

    @Override
    public long countScoredRecords(
            TimeRange timeRange, MultiKeyFeature contextFieldNameToValueMap, int scoreThreshold, String adeEventType) {

        Query query = new Query();
        addTimeRangeCriterion(query, START_INSTANT_FIELD, timeRange);
        addContextFieldCriteria(query, CONTEXT_FIELD_NAME + ".", contextFieldNameToValueMap);
        addScoreThresholdCriterion(query, SCORE_FIELD_NAME, scoreThreshold);
        String collectionName = translator.toCollectionName(adeEventType);
        return mongoTemplate.count(query, AdeScoredEnrichedRecord.class, collectionName);
    }

    @Override
    public List<AdeScoredEnrichedRecord> readScoredRecords(
            TimeRange timeRange, MultiKeyFeature contextFieldNameToValueMap, int scoreThreshold, String adeEventType,
            int skip, int limit) {

        Query query = new Query();
        addTimeRangeCriterion(query, START_INSTANT_FIELD, timeRange);
        addContextFieldCriteria(query, CONTEXT_FIELD_NAME + ".", contextFieldNameToValueMap);
        addScoreThresholdCriterion(query, SCORE_FIELD_NAME, scoreThreshold);
        String collectionName = translator.toCollectionName(adeEventType);
        query.skip(skip).limit(limit);
        return mongoTemplate.find(query, AdeScoredEnrichedRecord.class, collectionName);
    }

    @Override
    public AdeScoredRecord readFirstScoredRecord(
            TimeRange timeRange,
            String adeEventType,
            MultiKeyFeature contextFieldNameToValueMap,
            MultiKeyFeature additionalFieldNameToValueMap,
            int scoreThreshold) {

        return readScoredRecord(
                timeRange,
                adeEventType,
                contextFieldNameToValueMap,
                additionalFieldNameToValueMap,
                scoreThreshold,
                Direction.ASC);
    }

    @Override
    public AdeScoredRecord readLastScoredRecord(
            TimeRange timeRange,
            String adeEventType,
            MultiKeyFeature contextFieldNameToValueMap,
            MultiKeyFeature additionalFieldNameToValueMap,
            int scoreThreshold) {

        return readScoredRecord(
                timeRange,
                adeEventType,
                contextFieldNameToValueMap,
                additionalFieldNameToValueMap,
                scoreThreshold,
                Direction.DESC);
    }

    private AdeScoredRecord readScoredRecord(
            TimeRange timeRange,
            String adeEventType,
            MultiKeyFeature contextFieldNameToValueMap,
            MultiKeyFeature additionalFieldNameToValueMap,
            int scoreThreshold,
            Direction direction) {

        Query query = new Query();
        addTimeRangeCriterion(query, START_INSTANT_FIELD, timeRange, additionalFieldNameToValueMap);
        String collectionName = translator.toCollectionName(adeEventType);
        addContextFieldCriteria(query, CONTEXT_FIELD_NAME + ".", contextFieldNameToValueMap);
        addFieldCriteria(query, START_INSTANT_FIELD, additionalFieldNameToValueMap);
        addScoreThresholdCriterion(query, SCORE_FIELD_NAME, scoreThreshold);
        query.with(new Sort(direction, START_INSTANT_FIELD));
        return mongoTemplate.findOne(query, AdeScoredEnrichedRecord.class, collectionName);
    }
}
