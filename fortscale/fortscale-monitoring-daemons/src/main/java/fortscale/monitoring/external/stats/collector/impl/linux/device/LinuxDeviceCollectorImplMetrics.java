package fortscale.monitoring.external.stats.collector.impl.linux.device;

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
@StatsMetricsGroupParams(name = "linux.device")
public class LinuxDeviceCollectorImplMetrics extends StatsMetricsGroup {

    public LinuxDeviceCollectorImplMetrics(StatsService statsService, String deviceName) {
        // Call parent ctor
        super(statsService, LinuxMemoryCollectorImpl.class,
                // Create anonymous attribute class with initializer block since it does not have ctor
                new StatsMetricsGroupAttributes() {
                    {
                        // add tags
                        addTag("device", deviceName);

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
    long sectorsRead;
    @StatsDoubleMetricParams(rateSeconds = 1)
    long timeSpentReading;
    @StatsDoubleMetricParams(rateSeconds = 1)
    long writesCompleted;
    @StatsDoubleMetricParams(rateSeconds = 1)
    long writesMerged;
    @StatsDoubleMetricParams(rateSeconds = 1)
    long sectorsWritten;
    @StatsDoubleMetricParams(rateSeconds = 1)
    long timeSpentWriting;
    @StatsLongMetricParams
    long IOCurrentlyInProgress;
    @StatsDoubleMetricParams(rateSeconds = 1)
    long timeSpentDoingIO;
    @StatsLongMetricParams
    long weightedTimeSpentDoingIO;
}
