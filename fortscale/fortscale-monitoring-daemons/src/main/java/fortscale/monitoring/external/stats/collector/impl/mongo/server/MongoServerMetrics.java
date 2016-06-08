package fortscale.monitoring.external.stats.collector.impl.mongo.server;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

@StatsMetricsGroupParams(name = "mongo.server")
public class MongoServerMetrics extends StatsMetricsGroup {

    @StatsLongMetricParams
    public long currentConnections;
    @StatsLongMetricParams
    public long totalConnectionsCreated;
    @StatsLongMetricParams
    public long dbHeapUsage;
    @StatsLongMetricParams
    public long networkInBytes;
    @StatsLongMetricParams
    public long networkOutBytes;
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long inserts;
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long queries;
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long updates;
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long deletes;
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long getMore;
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long commands;
    @StatsLongMetricParams
    public long cacheMemorySize;
    @StatsLongMetricParams
    public long cacheMemorySizeCfg;
    @StatsLongMetricParams
    public long bytesReadIntoCache;
    @StatsLongMetricParams
    public long bytesWrittenFromCache;
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long docsDeleted;
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long docsInserted;
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long docsReturned;
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long docsUpdated;
    @StatsLongMetricParams
    public long ttlPasses;
    @StatsLongMetricParams
    public long ttlDeletedDocs;

    /**
     * The ctor, in addition to initializing the class, registers the metrics group to the stats service.
     *
     * @param statsService - The stats service to register to. Typically it is obtained via @Autowired
     *                     of the specific service configuration class. If stats service is unavailable,
     *                     as in most unit tests, pass a null.
     * @param db           - db name
     */
    public MongoServerMetrics(StatsService statsService, String db) {
        super(statsService, MongoServerMetrics.class, new StatsMetricsGroupAttributes() {{
            addTag("db", db);
            setManualUpdateMode(true);
        }});
    }
}
