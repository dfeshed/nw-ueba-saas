package fortscale.monitoring.external.stats.collector.impl.linux.disk;

import fortscale.monitoring.external.stats.collector.impl.linux.memory.LinuxMemoryCollectorImpl;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;


/**
 * linux disk external collector self metrics
 */
@StatsMetricsGroupParams(name = "external.stats.collector.linux.disk")
public class LinuxDiskCollectorSelfMetrics extends StatsMetricsGroup {

    public LinuxDiskCollectorSelfMetrics(StatsService statsService) {
        // Call parent ctor
        super(statsService, LinuxMemoryCollectorImpl.class,
                // Create anonymous attribute class with initializer block since it does not have ctor
                new StatsMetricsGroupAttributes() {
                    {
                        // Set manual update mode
                        setManualUpdateMode(true);
                    }
                }
        );
    }

    @StatsLongMetricParams
    public long updatedDisks; // amount of disks with up-to-date stats

    @StatsLongMetricParams
    public long totalDisks; // total amount of disk we are trying to collect info about

    @StatsLongMetricParams (rateSeconds = 1)
    public long diskUpdateFailures; // collect failures

}
