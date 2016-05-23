package fortscale.utils.influxdb.metrics;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * monitors influxdbService CRUD activities
 */
@StatsMetricsGroupParams(name = "influxdb")
public class InfluxdbMetrics extends StatsMetricsGroup {
    @StatsLongMetricParams(rateSeconds = 1)
    public long queries;
    @StatsLongMetricParams(rateSeconds = 1)
    public long queryFailures;
    @StatsLongMetricParams(rateSeconds = 1)
    public long pointsRead;
    @StatsLongMetricParams(rateSeconds = 1)
    public long writes;
    @StatsLongMetricParams(rateSeconds = 1)
    public long writeFailures;
    @StatsLongMetricParams(rateSeconds = 1)
    public long batchwrites;
    @StatsLongMetricParams(rateSeconds = 1)
    public long batchWriteFailures;
    @StatsLongMetricParams(rateSeconds = 1)
    public long pointsWritten;
    @StatsLongMetricParams(rateSeconds = 1)
    public long createDb;


    /**
     * The ctor, in addition to initializing the class, registers the metrics group to the stats service.
     *
     * @param statsService - The stats service to register to. Typically it is obtained via @Autowired
     *                     of the specific service configuration class. If stats service is unavailable,
     *                     as in most unit tests, pass a null.
     */
    public InfluxdbMetrics(StatsService statsService) {
        super(statsService, InfluxdbMetrics.class, null);
    }
}
