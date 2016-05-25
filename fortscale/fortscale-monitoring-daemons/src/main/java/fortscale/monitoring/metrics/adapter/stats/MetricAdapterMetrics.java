package fortscale.monitoring.metrics.adapter.stats;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * metric adapter stats monitoring counters
 */
@StatsMetricsGroupParams(name = "monitoringdaemon.metric.adapter")
public class MetricAdapterMetrics extends StatsMetricsGroup {

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long writtenPoints = 0;
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long readMetricMessages = 0;
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long messagesFromBadVersion = 0;
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long readEngineDataMessages = 0;
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long unresolvedMetricMessages = 0;

    /**
     * The ctor, in addition to initializing the class, registers the metrics group to the stats service.
     *
     * @param statsService - The stats service to register to. Typically it is obtained via @Autowired
     *                     of the specific service configuration class. If stats service is unavailable,
     *                     as in most unit tests, pass a null.
     */
    public MetricAdapterMetrics(StatsService statsService) {
        super(statsService, MetricAdapterMetrics.class, null);
    }


}
