package fortscale.monitoring.external.stats.collector.impl.mongo.db;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

@StatsMetricsGroupParams(name = "mongo.db")
public class MongoDBMetrics extends StatsMetricsGroup {

    @StatsLongMetricParams
    public long collections;
    @StatsLongMetricParams
    public long objects;
    @StatsLongMetricParams
    public long avgObjectSize;
    @StatsLongMetricParams
    public long docSize;
    @StatsLongMetricParams
    public long storageSize;
    @StatsLongMetricParams
    public long indexSize;


    /**
     * The ctor, in addition to initializing the class, registers the metrics group to the stats service.
     *
     * @param statsService - The stats service to register to. Typically it is obtained via @Autowired
     *                     of the specific service configuration class. If stats service is unavailable,
     *                     as in most unit tests, pass a null.
     * @param db           - db name
     */
    public MongoDBMetrics(StatsService statsService, String db) {
        super(statsService, MongoDBMetrics.class, new StatsMetricsGroupAttributes() {{
            addTag("db", db);
            setManualUpdateMode(true);
        }});
    }
}
