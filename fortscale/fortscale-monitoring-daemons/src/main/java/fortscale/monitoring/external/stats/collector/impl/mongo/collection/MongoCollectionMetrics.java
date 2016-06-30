package fortscale.monitoring.external.stats.collector.impl.mongo.collection;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

@StatsMetricsGroupParams(name = "mongo.collection")
public class MongoCollectionMetrics extends StatsMetricsGroup {

    // db.collection.stats().count
    @StatsLongMetricParams
    public long objectCount;

    // db.collection.stats().size
    @StatsLongMetricParams
    public long size;

    // db.collection.stats().avgObjSize
    @StatsLongMetricParams
    public long avgObjectSize;

    // db.collection.stats().storageSize
    @StatsLongMetricParams
    public long docStorageSize;

    // db.collection.stats().totalIndexSize
    @StatsLongMetricParams
    public long indexesSize;

    // db.collection.stats().wiretiger.cache.bytes read into cache
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long bytesReadIntoCache;

    // db.collection.stats().wiretiger.cache.bytes written from cache
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long bytesWrittenFromCache;

    /**
     * c'tor
     *
     * @param statsService stats service
     * @param collection   collection name, used as a stat tag
     * @param db           database name
     */
    public MongoCollectionMetrics(StatsService statsService, String collection, String db) {
        super(statsService, MongoCollectionMetrics.class, new StatsMetricsGroupAttributes() {{
            addTag("db", db);
            addTag("collection", collection);
            setManualUpdateMode(true);
        }});

    }

}
