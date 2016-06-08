package fortscale.monitoring.external.stats.collector.impl.mongo.server;

import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.HashMap;

import static fortscale.monitoring.external.stats.Util.CollectorsUtil.entryValueToLong;

/**
 * Collects data from mongodb db.serverStatus() and update relevant metrics accordingly
 */
public class MongoServerCollectorImpl {
    private StatsService statsService;
    private MongoTemplate mongoTemplate;

    private final static String COMMAND = "{serverStatus: 1}";
    MongoServerCollectorImplMetrics metrics;
    private static final Logger logger = Logger.getLogger(MongoServerCollectorImpl.class);

    /**
     * ctor
     *
     * @param mongoTemplate mongo template
     * @param statsService stats service
     */
    public MongoServerCollectorImpl(MongoTemplate mongoTemplate, StatsService statsService) {
        this.mongoTemplate = mongoTemplate;
        this.statsService = statsService;
        this.metrics = new MongoServerCollectorImplMetrics(statsService, mongoTemplate.getDb().getName());
    }


    /**
     * collect stats from mongodb db stats
     *
     * @param epochTime metric update time
     */
    public void collect(long epochTime) {
        //get server stats
        try {
            HashMap stats = mongoTemplate.executeCommand(COMMAND);

            // connection stats
            HashMap connectionStats = (HashMap) stats.get("connections");
            metrics.currentConnections = entryValueToLong(connectionStats.get("current"));
            metrics.totalConnectionsCreated = entryValueToLong(connectionStats.get("totalCreated"));

            // extra info stats
            metrics.dbHeapUsage = entryValueToLong(((HashMap) stats.get("extra_info")).get("heap_usage_bytes"));

            // network stats
            HashMap networkStats = (HashMap) stats.get("network");
            metrics.networkInBytes = entryValueToLong(networkStats.get("bytesIn"));
            metrics.networkOutBytes = entryValueToLong(networkStats.get("bytesOut"));

            // ops stats
            HashMap opCountersStats = (HashMap) stats.get("opcounters");
            metrics.inserts = entryValueToLong(opCountersStats.get("insert"));
            metrics.queries = entryValueToLong(opCountersStats.get("query"));
            metrics.updates = entryValueToLong(opCountersStats.get("update"));
            metrics.deletes = entryValueToLong(opCountersStats.get("delete"));
            metrics.getMore = entryValueToLong(opCountersStats.get("getmore"));
            metrics.commands = entryValueToLong(opCountersStats.get("command"));

            // wiredTiger cache stats
            HashMap wiredTigerCacheStats = (HashMap) ((HashMap) stats.get("wiredTiger")).get("cache");
            metrics.cacheMemorySize = entryValueToLong(wiredTigerCacheStats.get("bytes currently in the cache"));
            metrics.cacheMemorySizeCfg = entryValueToLong(wiredTigerCacheStats.get("maximum bytes configured"));
            metrics.bytesReadIntoCache = entryValueToLong(wiredTigerCacheStats.get("bytes read into cache"));
            metrics.bytesWrittenFromCache = entryValueToLong(wiredTigerCacheStats.get("bytes written from cache"));

            HashMap metricsStats = (HashMap) stats.get("metrics");

            // docs stats
            HashMap documentStats = (HashMap) metricsStats.get("document");
            metrics.docsDeleted = entryValueToLong(documentStats.get("deleted"));
            metrics.docsInserted = entryValueToLong(documentStats.get("inserted"));
            metrics.docsReturned = entryValueToLong(documentStats.get("returned"));
            metrics.updates = entryValueToLong(documentStats.get("updated"));

            //ttl stats
            HashMap ttlStats = (HashMap)metricsStats.get("ttl");
            metrics.ttlDeletedDocs=entryValueToLong(ttlStats.get("deletedDocuments"));
            metrics.ttlPasses=entryValueToLong(ttlStats.get("passes"));

            // update metrics
            metrics.manualUpdate(epochTime);
        } catch (Exception e) {
            logger.error("error while collecting server stats from mongodb", e);
        }
    }

    /**
     * getter
     *
     * @return metrics stats
     */
    public MongoServerCollectorImplMetrics getMetrics() {
        return metrics;
    }
}

