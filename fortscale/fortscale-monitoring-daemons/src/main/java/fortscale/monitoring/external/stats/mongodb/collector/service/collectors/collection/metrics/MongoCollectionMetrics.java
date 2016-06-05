package fortscale.monitoring.external.stats.mongodb.collector.service.collectors.collection.metrics;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
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
    @StatsLongMetricParams
    public long bytesReadIntoCache;

    // db.collection.stats().wiretiger.cache.bytes written from cache
    @StatsLongMetricParams
    public long bytesWrittenFromCache;

    /**
     * c'tor
     * @param statsService stats service
     * @param collection collection name, used as a stat tag
     */
    public MongoCollectionMetrics(StatsService statsService, String collection) {
        super(statsService, MongoCollectionMetrics.class, new StatsMetricsGroupAttributes() {{
            addTag("collection", collection);
            setManualUpdateMode(true);
        }});

    }

}
