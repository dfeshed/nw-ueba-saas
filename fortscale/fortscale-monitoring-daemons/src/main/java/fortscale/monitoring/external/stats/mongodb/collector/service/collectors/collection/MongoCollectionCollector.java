package fortscale.monitoring.external.stats.mongodb.collector.service.collectors.collection;

import fortscale.monitoring.external.stats.mongodb.collector.service.collectors.collection.metrics.MongoCollectionMetrics;
import fortscale.utils.monitoring.stats.StatsService;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static fortscale.monitoring.external.stats.Util.CollectorsUtil.entryValueToLong;

/**
 * Collects data from mongodb collection.stats() and update relevant MongoCollectionMetrics metrics accordingly
 */
public class MongoCollectionCollector {
    private StatsService statsService;
    private MongoTemplate mongoTemplate;
    private Set<String> collectionNames;
    Map<String,MongoCollectionMetrics> collectionMetricsMap;

    public MongoCollectionCollector(MongoTemplate mongoTemplate, StatsService statsService)
    {
        this.mongoTemplate=mongoTemplate;
        this.statsService = statsService;
        this.collectionMetricsMap = new HashMap<>();

        updateCollectionNames();
        collect(1);
    }

    /**
     * get current existing collections from mongodb
     */
    private void updateCollectionNames()
    {
        this.collectionNames=mongoTemplate.getCollectionNames();
    }

    /**
     * collect stats from mongodb collection
     */
    public void collect(long epochTime)
    {
        for (String collection : collectionNames)
        {
            MongoCollectionMetrics mongoCollectionMetrics;
            if (!collectionMetricsMap.containsKey(collection))
            {
                mongoCollectionMetrics = new MongoCollectionMetrics(statsService,collection);
                collectionMetricsMap.put(collection,mongoCollectionMetrics);
            }
            mongoCollectionMetrics = collectionMetricsMap.get(collection);

            HashMap stats = mongoTemplate.getCollection(collection).getStats();
            mongoCollectionMetrics.size = entryValueToLong(stats.get("size"));
            mongoCollectionMetrics.docStorageSize =entryValueToLong(stats.get("storageSize"));
            mongoCollectionMetrics.avgObjectSize=entryValueToLong(stats.get("avgObjSize")); //// TODO: 6/5/16 object is missing
            mongoCollectionMetrics.objectCount=entryValueToLong(stats.get("count"));
            mongoCollectionMetrics.indexesSize =entryValueToLong(stats.get("totalIndexSize"));

            HashMap cacheStats = (HashMap) ((HashMap)stats.get("wiredTiger")).get("cache");
            mongoCollectionMetrics.bytesReadIntoCache = entryValueToLong(cacheStats.get("bytes read into cache"));
            mongoCollectionMetrics.bytesReadIntoCache = entryValueToLong(cacheStats.get("bytes written from cache"));

            mongoCollectionMetrics.manualUpdate(epochTime);
        }
    }
}

