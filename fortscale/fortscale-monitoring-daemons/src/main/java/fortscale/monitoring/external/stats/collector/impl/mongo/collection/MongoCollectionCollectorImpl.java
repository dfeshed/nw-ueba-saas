package fortscale.monitoring.external.stats.collector.impl.mongo.collection;

import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static fortscale.monitoring.external.stats.Util.CollectorsUtil.entryValueToLong;

/**
 * Collects data from mongodb collection.stats() and update relevant MongoCollectionMetrics metrics accordingly
 */
public class MongoCollectionCollectorImpl {
    private StatsService statsService;
    private MongoTemplate mongoTemplate;
    private Set<String> collectionNames;
    private Map<String, MongoCollectionMetrics> collectionMetricsMap;
    private String db;
    private MongoCollectionCollectorImplMetrics selfMetrics;
    private static final Logger logger = Logger.getLogger(MongoCollectionCollectorImpl.class);


    /**
     * ctor
     *
     * @param mongoTemplate
     * @param statsService
     */
    public MongoCollectionCollectorImpl(MongoTemplate mongoTemplate, StatsService statsService) {
        this.mongoTemplate = mongoTemplate;
        this.statsService = statsService;
        this.collectionMetricsMap = new HashMap<>();
        this.db = mongoTemplate.getDb().getName();
        this.selfMetrics = new MongoCollectionCollectorImplMetrics(this.statsService);

    }

    /**
     * update current existing collections from mongodb
     */
    private void updateCollectionNames() {
        try {
            this.collectionNames = mongoTemplate.getCollectionNames();
        } catch (Exception e) {
            logger.error("error getting collection list", e);
            selfMetrics.updateCollectionNameFailures++;
        }
    }

    /**
     * collect stats from mongodb collection
     *
     * @param epochTime metric update time
     */
    public void collect(long epochTime) {
        updateCollectionNames();
        selfMetrics.updatedCollections = 0;
        // update all collection stats
        for (String collection : collectionNames) {
            try {
                MongoCollectionMetrics mongoCollectionMetrics;

                // create collection metric if there isn't one
                if (!collectionMetricsMap.containsKey(collection)) {
                    mongoCollectionMetrics = new MongoCollectionMetrics(statsService, collection, db);
                    collectionMetricsMap.put(collection, mongoCollectionMetrics);
                }

                // get collection metric
                mongoCollectionMetrics = collectionMetricsMap.get(collection);

                // get collection stats
                HashMap stats = mongoTemplate.getCollection(collection).getStats();

                // update metrics
                mongoCollectionMetrics.size = entryValueToLong(stats.get("size"));
                mongoCollectionMetrics.docStorageSize = entryValueToLong(stats.get("storageSize"));
                mongoCollectionMetrics.avgObjectSize = entryValueToLong(stats.get("avgObjSize"));
                mongoCollectionMetrics.objectCount = entryValueToLong(stats.get("count"));
                mongoCollectionMetrics.indexesSize = entryValueToLong(stats.get("totalIndexSize"));

                HashMap cacheStats = (HashMap) ((HashMap) stats.get("wiredTiger")).get("cache");
                mongoCollectionMetrics.bytesReadIntoCache = entryValueToLong(cacheStats.get("bytes read into cache"));
                mongoCollectionMetrics.bytesReadIntoCache = entryValueToLong(cacheStats.get("bytes written from cache"));

                mongoCollectionMetrics.manualUpdate(epochTime);

                selfMetrics.updatedCollections++;
            } catch (Exception e) {
                String msg = String.format("error while collecting stats from collection %s", collection);
                logger.error(msg, e);
                selfMetrics.collectionUpdateFailures++;
            }
        }
        selfMetrics.manualUpdate(epochTime);
    }

    /**
     * getter
     *
     * @return list of collection metrics
     */
    public Map<String, MongoCollectionMetrics> getCollectionMetricsMap() {
        return collectionMetricsMap;
    }
}

