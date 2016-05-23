package fortscale.monitoring.metrics.adapter.stats;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * metric adapter stats monitoring counters
 */
@StatsMetricsGroupParams(name = "monitoringdaemon.metricadapter")
public class MetricAdapterMetrics extends StatsMetricsGroup {
    @StatsLongMetricParams
    public long epochTime = 0;
    @StatsLongMetricParams
    public long numberOfWrittenPoints = 0;
    @StatsLongMetricParams
    public long numberOfWrittenPointsBytes = 0;
    @StatsLongMetricParams
    public long numberOfReadMetricMessages = 0;
    @StatsLongMetricParams
    public long numberOfMessagesFromBadVersion=0;
    @StatsLongMetricParams
    public long numberOfReadEngineDataMessages = 0;
    @StatsLongMetricParams
    public long numberOfReadEngineDataMessagesBytes = 0;
    @StatsLongMetricParams
    public long numberOfUnresolvedMetricMessages=0;

    /**
     * The ctor, in addition to initializing the class, registers the metrics group to the stats service.
     *
     * @param statsService                - The stats service to register to. Typically it is obtained via @Autowired
     *                                    of the specific service configuration class. If stats service is unavailable,
     *                                    as in most unit tests, pass a null.
     */
    public MetricAdapterMetrics(StatsService statsService) {
        super(statsService, MetricAdapterMetrics.class, null);
    }




}
