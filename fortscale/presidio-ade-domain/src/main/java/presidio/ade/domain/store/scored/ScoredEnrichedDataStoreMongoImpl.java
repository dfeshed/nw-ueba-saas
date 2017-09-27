package presidio.ade.domain.store.scored;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import fortscale.utils.logging.Logger;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import fortscale.utils.time.TimeRange;
import fortscale.utils.ttl.TtlService;
import fortscale.utils.ttl.TtlServiceAware;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.util.Pair;
import presidio.ade.domain.record.AdeRecord;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.domain.store.AdeDataStoreCleanupParams;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord.CONTEXT_FIELD_NAME;
import static presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord.SCORE_FIELD_NAME;
import static presidio.ade.domain.record.enriched.BaseEnrichedContext.EVENT_ID_FIELD_NAME;
import static presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord.START_INSTANT_FIELD;

/**
 * Created by YaronDL on 6/13/2017.
 */
public class ScoredEnrichedDataStoreMongoImpl implements ScoredEnrichedDataStore, TtlServiceAware {

    private static final Logger logger = Logger.getLogger(ScoredEnrichedDataStoreMongoImpl.class);

    private final MongoTemplate mongoTemplate;
    private final AdeScoredEnrichedRecordToCollectionNameTranslator translator;
    private final MongoDbBulkOpUtil mongoDbBulkOpUtil;
    private TtlService ttlService;

    public ScoredEnrichedDataStoreMongoImpl(MongoTemplate mongoTemplate, AdeScoredEnrichedRecordToCollectionNameTranslator translator, MongoDbBulkOpUtil mongoDbBulkOpUtil) {
        this.mongoTemplate = mongoTemplate;
        this.translator = translator;
        this.mongoDbBulkOpUtil = mongoDbBulkOpUtil;
    }

    @Override
    public void store(List<? extends AdeScoredEnrichedRecord> recordList) {
        Map<String, List<AdeScoredEnrichedRecord>> collectionNameToRecordList = new HashMap<>();
        for(AdeScoredEnrichedRecord record: recordList){
            String collectionName = translator.toCollectionName(record);
            List<AdeScoredEnrichedRecord> collectionRecordList = collectionNameToRecordList.get(collectionName);
            if(collectionRecordList == null){
                collectionRecordList = new ArrayList<>();
                collectionNameToRecordList.put(collectionName, collectionRecordList);
            }
            collectionRecordList.add(record);
        }

        for(Map.Entry<String, List<AdeScoredEnrichedRecord>> entry: collectionNameToRecordList.entrySet()){
            List<AdeScoredEnrichedRecord> batchToSave = entry.getValue();
            String collectionName = entry.getKey();
            mongoDbBulkOpUtil.insertUnordered(batchToSave,collectionName);
            ttlService.save(getStoreName(), collectionName);
        }
    }

    @Override
    public void cleanup(AdeDataStoreCleanupParams cleanupParams) {
        //TODO
    }

    @Override
    public List<AdeScoredEnrichedRecord> findScoredEnrichedRecords(List<String> eventIds, String adeEventType, Double scoreThreshold) {
        String collectionName = translator.toCollectionName(adeEventType);
        Criteria scoreFilter = Criteria.where(SCORE_FIELD_NAME).gte(scoreThreshold);
        Criteria eventIdsFilter = Criteria.where(String.format("%s.%s", CONTEXT_FIELD_NAME, EVENT_ID_FIELD_NAME)).in(eventIds);
        Query query = Query.query(eventIdsFilter).addCriteria(scoreFilter);
        return mongoTemplate.find(query,AdeScoredEnrichedRecord.class,collectionName);
    }

    @Override
    public List<String> findScoredEnrichedRecordsDistinctFeatureValues(String adeEventType, Pair<String, String> contextFieldAndValue, TimeRange timeRange, String distinctFieldName, Double scoreThreshold) {
        String collectionName = translator.toCollectionName(adeEventType);
        Criteria contextFilter = Criteria.where(String.format("%s.%s", CONTEXT_FIELD_NAME, contextFieldAndValue.getFirst())).is(contextFieldAndValue.getSecond());
        String contextualDistinctField = distinctFieldName;
        if(!START_INSTANT_FIELD.equals(distinctFieldName)){
            contextualDistinctField = String.format("%s.%s", CONTEXT_FIELD_NAME, distinctFieldName);
        }

        Criteria timeRangeFilter = Criteria.where(START_INSTANT_FIELD).gte(timeRange.getStartAsDate()).lt(timeRange.getEndAsDate());
        Criteria scoreFilter = Criteria.where(SCORE_FIELD_NAME).gte(scoreThreshold);
        Query query = Query.query(contextFilter).addCriteria(timeRangeFilter).addCriteria(scoreFilter);
        DBCollection collection = mongoTemplate.getCollection(collectionName);
        DBObject queryObject = query.getQueryObject();
        return collection.distinct(contextualDistinctField, queryObject);
    }

    @Override
    public void setTtlService(TtlService ttlService) {
        this.ttlService = ttlService;
    }

    @Override
    public void remove(String collectionName, Instant until) {
        Query query = new Query()
                .addCriteria(where(START_INSTANT_FIELD).lt(until));
        mongoTemplate.remove(query, collectionName);
    }

}
