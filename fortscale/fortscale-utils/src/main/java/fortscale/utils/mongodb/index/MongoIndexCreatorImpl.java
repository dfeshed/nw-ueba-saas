package fortscale.utils.mongodb.index;

import fortscale.utils.logging.Logger;
import fortscale.utils.mongodb.util.MongoDbUtilService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * flow:
 * 1. if indexes of collection already exists in cache, don't do a thing
 * 2. if not: add to cache and to db
 * Created by barak_schuster on 5/21/17.
 */
public class MongoIndexCreatorImpl implements MongoIndexCreator {
    private static final Logger logger = Logger.getLogger(MongoIndexCreatorImpl.class);

    private Map<String/*mongodb collection name*/, Set<Index>> collectionToIndexCache;
    private MongoTemplate mongoTemplate;
    private MongoDbUtilService mongoDbUtilService;

    /**
     * @param mongoTemplate      you know...
     * @param mongoDbUtilService handles collection creation and caching of existing collections
     */
    public MongoIndexCreatorImpl(MongoTemplate mongoTemplate, MongoDbUtilService mongoDbUtilService) {
        this.collectionToIndexCache = new HashMap<>();
        this.mongoTemplate = mongoTemplate;
        this.mongoDbUtilService = mongoDbUtilService;
    }

    public void ensureIndexes(String collectionName, MongoIndexedStore store) {

        if (!collectionToIndexCache.containsKey(collectionName)) {
            Set<Index> collectionIndexes = store.getIndexes();
            collectionToIndexCache.put(collectionName, collectionIndexes);
            addIndexesToDb(collectionName, collectionIndexes);
        } else {
            Set<Index> storeIndexes = store.getIndexes();
            Set<Index> cacheIndexes = collectionToIndexCache.get(collectionName);
            if (!cacheIndexes.containsAll(storeIndexes)) {
                cacheIndexes.addAll(storeIndexes);
                addIndexesToDb(collectionName, cacheIndexes);
                return;
            }
            logger.debug("not adding any new indexes for collectionName={}", collectionName);
        }
    }

    /**
     * @param collectionName collection to ensure indexes on
     * @param indexes        to be added
     */
    private void addIndexesToDb(String collectionName, Set<Index> indexes) {
        for (Index index : indexes) {
            if (index == null) {
                continue;
            }
            if (!mongoDbUtilService.collectionExists(collectionName)) {
                logger.info("creating collection={}", collectionName);
                mongoDbUtilService.createCollection(collectionName);
            }
            logger.info("ensuring index={} for collectionName={}", index, collectionName);
            mongoTemplate.indexOps(collectionName).ensureIndex(index);
        }
    }
}
