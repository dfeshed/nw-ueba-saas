package presidio.ade.domain.store.scored;

import fortscale.utils.logging.Logger;
import org.springframework.data.mongodb.core.MongoTemplate;
import presidio.ade.domain.record.scored.AdeScoredRecord;
import presidio.ade.domain.store.AdeDataStoreCleanupParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by YaronDL on 6/13/2017.
 */
public class ScoredDataStoreMongoImpl implements ScoredDataStore{

    private static final Logger logger = Logger.getLogger(ScoredDataStoreMongoImpl.class);

    private final MongoTemplate mongoTemplate;
    private final ScoredDataToCollectionNameTranslator translator;

    public ScoredDataStoreMongoImpl(MongoTemplate mongoTemplate, ScoredDataToCollectionNameTranslator translator) {
        this.mongoTemplate = mongoTemplate;
        this.translator = translator;
    }

    @Override
    public void store(List<? extends AdeScoredRecord> recordList) {
        Map<String, List<AdeScoredRecord>> collectionNameToRecordList = new HashMap<>();
        for(AdeScoredRecord record: recordList){
            String collectionName = translator.toCollectionName(record);
            List<AdeScoredRecord> collectionRecordList = collectionNameToRecordList.get(collectionName);
            if(collectionRecordList == null){
                collectionRecordList = new ArrayList<>();
                collectionNameToRecordList.put(collectionName, collectionRecordList);
            }
            collectionRecordList.add(record);
        }

        for(Map.Entry<String, List<AdeScoredRecord>> entry: collectionNameToRecordList.entrySet()){
            mongoTemplate.insert(entry.getValue(),entry.getKey());
        }
    }

    @Override
    public void cleanup(AdeDataStoreCleanupParams cleanupParams) {
        //TODO
    }
}
