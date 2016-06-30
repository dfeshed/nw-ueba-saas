package fortscale.monitoring.external.stats.collector.impl.linux.blockDevice;

import fortscale.monitoring.external.stats.collector.impl.linux.memory.LinuxMemoryCollectorImpl;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * A container class for linux device IO  metrics
 */
@StatsMetricsGroupParams(name = "linux.blockDevice")
public class LinuxBlockDeviceCollectorImplMetrics extends StatsMetricsGroup {

    public LinuxBlockDeviceCollectorImplMetrics(StatsService statsService, String deviceName) {
        // Call parent ctor
        super(statsService, LinuxMemoryCollectorImpl.class,
                // Create anonymous attribute class with initializer block since it does not have ctor
                new StatsMetricsGroupAttributes() {
                    {
                        // add tags
                        addTag("blockDevice", deviceName);

                        // Set manual update mode
                        setManualUpdateMode(true);
                    }
                }
        );
    }
    @StatsDoubleMetricParams(rateSeconds = 1)
    long readsCompletedSuccessfully;

    @StatsDoubleMetricParams(rateSeconds = 1)
    long readsMerged;

    @StatsDoubleMetricParams(rateSeconds = 1)
    long writesCompleted;

    @StatsDoubleMetricParams(rateSeconds = 1)
    long writesMerged;

    @StatsLongMetricParams
    long IOCurrentlyInProgress;

    @StatsDoubleMetricParams(rateSeconds = 1,factor = 512, name="bytesRead")
    long sectorsRead;

    @StatsDoubleMetricParams(rateSeconds = 1,factor = 512, name="bytesWritten")
    long sectorsWritten;

    @StatsDoubleMetricParams(rateSeconds = 1 , name = "readUtilPercent", factor = 100.0)
    long timeSpentReadingMilli;

    @StatsDoubleMetricParams(rateSeconds = 1, name = "IOUtilPercent", factor = 100.0)
    long timeSpentDoingIOMilli;

    @StatsDoubleMetricParams(rateSeconds = 1, name = "weightedUtilPercent", factor = 100.0)
    long weightedTimeSpentDoingIOMilli;

    @StatsDoubleMetricParams(rateSeconds = 1, name = "writeUtilPercent", factor = 100.0)
    long timeSpentWritingMilli;

}
