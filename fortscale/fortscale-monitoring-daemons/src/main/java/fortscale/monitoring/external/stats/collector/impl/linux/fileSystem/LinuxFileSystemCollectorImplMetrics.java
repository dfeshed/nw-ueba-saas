package fortscale.monitoring.external.stats.collector.impl.linux.fileSystem;

import fortscale.monitoring.external.stats.collector.impl.linux.memory.LinuxMemoryCollectorImpl;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;


/**
 * A container class for linux file system collector metrics
 */
@StatsMetricsGroupParams(name = "linux.fileSystem")
public class LinuxFileSystemCollectorImplMetrics extends StatsMetricsGroup {

    public LinuxFileSystemCollectorImplMetrics(StatsService statsService, String paths) {
        // Call parent ctor
        super(statsService, LinuxMemoryCollectorImpl.class,
                // Create anonymous attribute class with initializer block since it does not have ctor
                new StatsMetricsGroupAttributes() {
                    {
                        // add tags
                        addTag("paths", paths);

                        // Set manual update mode
                        setManualUpdateMode(true);
                    }
                }
        );
    }

    @StatsLongMetricParams
    public long totalSize;

    @StatsLongMetricParams
    public long freeSpace;

    @StatsDoubleMetricParams
    public double freeSpacePercent;

    @StatsLongMetricParams
    public long usedSpace;

}
