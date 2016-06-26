package fortscale.monitoring.external.stats.collector.impl;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

@StatsMetricsGroupParams(name = "monitoring.external.stats.collector")
public class ExternalStatsCollectorMetrics extends StatsMetricsGroup {
    /**
     * The ctor, in addition to initializing the class, registers the metrics group to the stats service.
     *
     * @param statsService  - The stats service to register to. Typically it is obtained via @Autowired
     *                      of the specific service configuration class. If stats service is unavailable,
     *                      as in most unit tests, pass a null.
     * @param collectorName - collector name
     */
    public ExternalStatsCollectorMetrics(StatsService statsService, String collectorName) {

        // Call parent ctor
        super(statsService, ExternalStatsCollectorMetrics.class,
                // Create anonymous attribute class with initializer block since it does not have ctor
                new StatsMetricsGroupAttributes() {
                    {
                        addTag("collector", collectorName);
                        // Set manual update mode
                        setManualUpdateMode(true);
                    }
                }
        );
    }

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long collects;
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long collectFailures;
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long collectionsDelayed;
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long collectionsTooEarly;
}
