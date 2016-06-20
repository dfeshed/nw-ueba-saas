package fortscale.monitoring.external.stats.collector.impl.linux.disk;

import fortscale.monitoring.external.stats.collector.impl.linux.memory.LinuxMemoryCollectorImpl;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;


/**
 * A container class for linux disk collector metrics
 */
@StatsMetricsGroupParams(name = "linux.disk")
public class LinuxDiskCollectorImplMetrics extends StatsMetricsGroup {

    public LinuxDiskCollectorImplMetrics(StatsService statsService, String diskPath) {
        // Call parent ctor
        super(statsService, LinuxMemoryCollectorImpl.class,
                // Create anonymous attribute class with initializer block since it does not have ctor
                new StatsMetricsGroupAttributes() {
                    {
                        // add tags
                        addTag("diskPath", diskPath);

                        // Set manual update mode
                        setManualUpdateMode(true);
                    }
                }
        );
    }

    @StatsLongMetricParams
    public long totalFileSystemSize;

    @StatsLongMetricParams
    public long freeSpace;

    @StatsLongMetricParams
    public long usedSpace;

}
