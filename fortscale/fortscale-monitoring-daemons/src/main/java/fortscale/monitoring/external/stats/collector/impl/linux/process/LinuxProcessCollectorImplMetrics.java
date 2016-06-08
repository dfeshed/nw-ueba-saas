package fortscale.monitoring.external.stats.collector.impl.linux.process;

import fortscale.monitoring.external.stats.collector.impl.linux.memory.LinuxMemoryCollectorImpl;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;
import fortscale.utils.monitoring.stats.annotations.StatsStringMetricParams;

/**
 * A container class for linux process collector metrics
 *
 * Created by galiar & gaashh on 18/04/2016.
 */
@StatsMetricsGroupParams(name = "linux.process")
public class LinuxProcessCollectorImplMetrics extends StatsMetricsGroup {

    public LinuxProcessCollectorImplMetrics(StatsService statsService, String processName, String processGroupName) {
        // Call parent ctor
        super(statsService, LinuxMemoryCollectorImpl.class,
                // Create anonymous attribute class with initializer block since it does not have ctor
                new StatsMetricsGroupAttributes() {
                    {
                        // We collect the data for on another process, use its name and group
                        overrideProcessName(processName, processGroupName);

                        // Set manual update mode
                        setManualUpdateMode(true);
                    }
                }
        );
    }

    @StatsLongMetricParams
    long pid;

    @StatsLongMetricParams
    long memoryRSS;

    @StatsLongMetricParams
    long memoryVSize;

    @StatsLongMetricParams
    long threads;

    @StatsDoubleMetricParams(name = "utilKernelPercent",   rateSeconds = 1,factor = 100.0 / 1000)
    long kernelTimeMiliSec;

    @StatsDoubleMetricParams(name = "utilUserPercent",     rateSeconds = 1,factor = 100.0 / 1000)
    long userTimeMiliSec;

    @StatsDoubleMetricParams(name = "childrenWaitPercent", rateSeconds = 1,factor = 100.0 / 1000)
    long childrenWaitTimeMiliSec;

    @StatsStringMetricParams
    String commandLine;

}
