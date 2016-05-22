package fortscale.monitoring.external.stats.samza.collector.metrics;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * stats monitoring counters
 */
@StatsMetricsGroupParams(name = "samza.metrics.collector")
public class SamzaMetricCollectorMetrics extends StatsMetricsGroup {


    @StatsLongMetricParams
    public long numberOfReadSamzaMetrics = 0;
    @StatsLongMetricParams
    public long numberOfUnresolvedMetricMessages=0;

    /**
     * The ctor, in addition to initializing the class, registers the metrics group to the stats service.
     *
     * @param statsService                - The stats service to register to. Typically it is obtained via @Autowired
     *                                    of the specific service configuration class. If stats service is unavailable,
     *                                    as in most unit tests, pass a null.
     * @param statsMetricsGroupAttributes - metrics group attributes (e.g. tag list). Might be null.
     */
    public SamzaMetricCollectorMetrics(StatsService statsService, StatsMetricsGroupAttributes statsMetricsGroupAttributes) {
        super(statsService, SamzaMetricCollectorMetrics.class, statsMetricsGroupAttributes);
    }




}
