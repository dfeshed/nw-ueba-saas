package fortscale.utils.influxdb.metrics;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * monitors influxdbService CRUD activities
 */
@StatsMetricsGroupParams(name = "influxdb")
public class InfluxdbMetrics extends StatsMetricsGroup {
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long queries;
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long queryFailures;
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long pointsRead;
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long writes;
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long writeFailures;
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long batchWrites;
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long batchWriteFailures;
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long pointsWritten;
    //    @StatsLongMetricParams(rateSeconds = 1)
    public long createDb;
    //    @StatsLongMetricParams(rateSeconds = 1)
    public long deleteDb;
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long networkFailures;

    /**
     * The ctor, in addition to initializing the class, registers the metrics group to the stats service.
     *
     * @param statsService - The stats service to register to. Typically it is obtained via @Autowired
     *                     of the specific service configuration class. If stats service is unavailable,
     *                     as in most unit tests, pass a null.
     * @param clientId     identifies influxdbService  instance
     */
    public InfluxdbMetrics(StatsService statsService, String clientId) {
        super(statsService, InfluxdbMetrics.class, new StatsMetricsGroupAttributes() {{
            addTag("clientId", clientId);
        }});
    }
}
