package fortscale.monitoring.external.stats.samza.collector.service.stats;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * stats monitoring counters
 */
@StatsMetricsGroupParams(name = "samza.metrics.collector")
public class SamzaMetricCollectorMetrics extends StatsMetricsGroup {

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long readSamzaMetrics;
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long unresolvedMetricMessages;
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long convertedMessages;
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long fullMessageConversionFailures;
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long convertedEntries;
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long entriesConversionFailures;

    /**
     * The ctor, in addition to initializing the class, registers the metrics group to the stats service.
     *
     * @param statsService - The stats service to register to. Typically it is obtained via @Autowired
     *                     of the specific service configuration class. If stats service is unavailable,
     *                     as in most unit tests, pass a null.
     */
    public SamzaMetricCollectorMetrics(StatsService statsService) {
        super(statsService, SamzaMetricCollectorMetrics.class, null);
    }


}
