package fortscale.monitoring.external.stats.collector.impl.linux.memory;



import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * a container class for memory collector metrics
 * inherits from statsMetricsGroup, therefore the class fields that marked with @Stats<Type>MetricParams
 * are being written to fluxDB by Stats service mechanism
 *
 * Created by galiar & gaashh on 18/04/2016.
 */
@StatsMetricsGroupParams(name = "linux.memory")
public class ExternalStatsCollectorLinuxMemoryMetrics extends StatsMetricsGroup {

    public ExternalStatsCollectorLinuxMemoryMetrics(StatsService statsService, String numaName) {
        // Call parent ctor
        super(statsService, ExternalStatsCollectorLinuxMemoryCollector.class,
                // Create anonymous attribute class with initializer block since it does not have ctor
                new StatsMetricsGroupAttributes() {
                    {
                        addTag("numa", numaName);
                    }
                }
        );
    }

    @StatsLongMetricParams
    long totalMemory;

    @StatsLongMetricParams
    long usedMemory;

    @StatsLongMetricParams
    long freeMemory;

    @StatsLongMetricParams
    long realFreeMemory;

    @StatsLongMetricParams
    long buffersMemory;

    @StatsLongMetricParams
    long cacheMemory;

    @StatsLongMetricParams
    long sharedMemory;

    @StatsLongMetricParams
    long activeMemory;

    @StatsLongMetricParams
    long inactiveMemory;

    @StatsLongMetricParams
    long dirtyMemory;

    @StatsDoubleMetricParams(rateSeconds = 1)
    long swapInMemory;

    @StatsDoubleMetricParams(rateSeconds = 1)
    long swapOutMemory;

    @StatsDoubleMetricParams(rateSeconds = 1)
    long bufferInMemory;

    @StatsDoubleMetricParams(rateSeconds = 1)
    long bufferOutMemory;

}
