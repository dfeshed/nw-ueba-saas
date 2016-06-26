package fortscale.monitoring.external.stats.collector.impl.mongo.db;

import fortscale.monitoring.external.stats.collector.impl.ExternalStatsCollectorMetrics;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.HashMap;

import static fortscale.monitoring.external.stats.Util.CollectorsUtil.entryValueToLong;

/**
 * Collects data from mongodb db.stats() and update relevant MongoDBMetrics metrics accordingly
 */
public class MongoDBCollectorImpl {
    private StatsService statsService;
    private MongoTemplate mongoTemplate;
    private ExternalStatsCollectorMetrics selfMetrics;
    private MongoDBMetrics metrics;
    private static final Logger logger = Logger.getLogger(MongoDBCollectorImpl.class);

    /**
     * ctor
     *
     * @param mongoTemplate
     * @param statsService
     */
    public MongoDBCollectorImpl(MongoTemplate mongoTemplate, StatsService statsService,ExternalStatsCollectorMetrics selfMetrics) {
        this.mongoTemplate = mongoTemplate;
        this.statsService = statsService;
        this.metrics = new MongoDBMetrics(statsService, mongoTemplate.getDb().getName());
        this.selfMetrics = selfMetrics;
    }


    /**
     * collect stats from mongodb db stats
     *
     * @param epochTime metric update time
     */
    public void collect(long epochTime) {
        //get db stats
        try {
            HashMap stats = mongoTemplate.getDb().getStats();
            metrics.collections = entryValueToLong(stats.get("collections"));
            metrics.avgObjectSize = entryValueToLong(stats.get("avgObjSize"));
            metrics.objects = entryValueToLong(stats.get("objects"));
            metrics.docSize = entryValueToLong(stats.get("dataSize"));
            metrics.indexSize = entryValueToLong(stats.get("indexSize"));
            metrics.storageSize = entryValueToLong(stats.get("storageSize"));

            metrics.manualUpdate(epochTime);
        } catch (Exception e) {
            logger.error("error while collecting db stats from mongodb", e);
            selfMetrics.collectFailures++;
        }
        selfMetrics.manualUpdate(epochTime);

    }

    /**
     * getter
     *
     * @return metrics stats
     */
    public MongoDBMetrics getMetrics() {
        return metrics;
    }
}

