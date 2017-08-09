package presidio.ade.domain.store.scored;

import fortscale.utils.logging.Logger;
import org.springframework.data.mongodb.core.MongoTemplate;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.domain.store.AdeDataStoreCleanupParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by YaronDL on 6/13/2017.
 */
public class ScoredEnrichedDataStoreMongoImpl implements ScoredEnrichedDataStore {

    private static final Logger logger = Logger.getLogger(ScoredEnrichedDataStoreMongoImpl.class);

    private final MongoTemplate mongoTemplate;
    private final AdeScoredEnrichedRecordToCollectionNameTranslator translator;

    public ScoredEnrichedDataStoreMongoImpl(MongoTemplate mongoTemplate, AdeScoredEnrichedRecordToCollectionNameTranslator translator) {
        this.mongoTemplate = mongoTemplate;
        this.translator = translator;
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
            mongoTemplate.insert(entry.getValue(),entry.getKey());
        }
    }

    @Override
    public void cleanup(AdeDataStoreCleanupParams cleanupParams) {
        //TODO
    }
}
