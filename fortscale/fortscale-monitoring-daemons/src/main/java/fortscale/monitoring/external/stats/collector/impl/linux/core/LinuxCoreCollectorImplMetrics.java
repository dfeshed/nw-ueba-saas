package fortscale.monitoring.external.stats.collector.impl.linux.core;

import fortscale.monitoring.external.stats.collector.impl.linux.memory.LinuxMemoryCollectorImpl;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * A container class for linux core collector metrics
 *
 * Created by galiar & gaashh on 18/04/2016.
 */
@StatsMetricsGroupParams(name = "linux.core")
public class LinuxCoreCollectorImplMetrics extends StatsMetricsGroup {

    public LinuxCoreCollectorImplMetrics(StatsService statsService, String coreName) {
        // Call parent ctor
        super(statsService, LinuxMemoryCollectorImpl.class,
                // Create anonymous attribute class with initializer block since it does not have ctor
                new StatsMetricsGroupAttributes() {
                    {

                        // Add tags
                        addTag("core", coreName);

                        // Set manual update mode
                        setManualUpdateMode(true);
                    }
                }
        );
    }

    // Factor converts 1000mSec to 100%
    @StatsDoubleMetricParams(name="userPercent",    rateSeconds = 1, factor = 100.0/1000)
    long userMiliSec;

    @StatsDoubleMetricParams(name="systemPercent",  rateSeconds = 1, factor = 100.0/1000)
    long systemMiliSec;

    @StatsDoubleMetricParams(name="nicePercent",    rateSeconds = 1, factor = 100.0/1000)
    long niceMiliSec;

    @StatsDoubleMetricParams(name="idlePercent",    rateSeconds = 1, factor = 100.0/1000)
    long idleMiliSec;

    @StatsDoubleMetricParams(name="waitPercent",    rateSeconds = 1, factor = 100.0/1000)
    long waitMiliSec;

    @StatsDoubleMetricParams(name="hwInterPercent", rateSeconds = 1, factor = 100.0/1000)
    long hwInterruptsMiliSec;

    @StatsDoubleMetricParams(name="swInterPercent", rateSeconds = 1, factor = 100.0/1000)
    long swInterruptsMiliSec;

    @StatsDoubleMetricParams(name="stealPercent",   rateSeconds = 1, factor = 100.0/1000)
    long stealMiliSec;

}
