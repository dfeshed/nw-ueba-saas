package presidio.ade.domain.store.input.store;

import fortscale.utils.mongodb.index.MongoIndexedStore;
import fortscale.utils.mongodb.index.MongoIndexCreator;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Query;
import presidio.ade.domain.store.input.ADEInputCleanupParams;
import presidio.ade.domain.store.input.ADEInputRecord;
import presidio.ade.domain.store.input.ADEInputRecordsMetaData;
import presidio.ade.domain.store.translators.ADEInputDataToCollectionNameTranslator;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fortscale.utils.logging.Logger;

public class ADEInputDataStoreImplMongo implements ADEInputDataStore ,MongoIndexedStore {
    private static final Logger logger = Logger.getLogger(ADEInputDataStoreImplMongo.class);

    private final MongoTemplate mongoTemplate;
    private final ADEInputDataToCollectionNameTranslator translator;
    private final MongoIndexCreator mongoIndexCreator;

    public ADEInputDataStoreImplMongo(MongoTemplate mongoTemplate, MongoIndexCreator mongoIndexCreator, ADEInputDataToCollectionNameTranslator translator) {
        this.mongoTemplate = mongoTemplate;
        this.translator = translator;
        this.mongoIndexCreator = mongoIndexCreator;
    }

    public void store(ADEInputRecordsMetaData recordsMetaData, List<? extends ADEInputRecord> records) {
        logger.info("storing by recordsMetaData={}", recordsMetaData);
        String collectionName = translator.toCollectionName(recordsMetaData);
        mongoIndexCreator.ensureIndexes(collectionName,this);
        mongoTemplate.insert(records, collectionName);
    }

    @Override
    public void cleanup(ADEInputCleanupParams cleanupParams) {
        logger.info("cleanup by cleanupParams={}", cleanupParams);

        Collection<String> collectionNames = translator.toCollectionNames(cleanupParams);
        Query cleanupQuery = toCleanupQuery(cleanupParams);
        for (String collectionName : collectionNames) {
            mongoTemplate.remove(cleanupQuery, collectionName);
        }
    }

    /**
     * @param cleanupParams
     * @return cleanup query by cleanup params
     */
    private Query toCleanupQuery(ADEInputCleanupParams cleanupParams) {
        return null;
    }


    public Set<Index> getIndexes() {
        Set<Index> indexSet = new HashSet<>();
        indexSet.add(new Index().on(ADEInputRecord.EVENT_TIME_FIELD, Sort.Direction.ASC));
        return indexSet;
    }
}
