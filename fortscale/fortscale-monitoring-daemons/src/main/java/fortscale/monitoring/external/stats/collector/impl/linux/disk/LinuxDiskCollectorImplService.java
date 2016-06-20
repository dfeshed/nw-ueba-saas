package fortscale.monitoring.external.stats.collector.impl.linux.disk;

import fortscale.monitoring.external.stats.collector.impl.AbstractExternalStatsCollectorServiceImpl;
import fortscale.monitoring.external.stats.collector.impl.linux.memory.LinuxMemoryCollectorImpl;
import fortscale.monitoring.external.stats.collector.impl.linux.memory.LinuxMemoryCollectorImplMetrics;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;

/**
 * A service that collects linux file system information and writes them to stats metrics group

 */
public class LinuxDiskCollectorImplService extends AbstractExternalStatsCollectorServiceImpl {

    private static final Logger logger = Logger.getLogger(LinuxDiskCollectorImplService.class);

    // Collector service name. Used for logging
    final static String COLLECTOR_SERVICE_NAME = "linuxDisk";

    // The collector.
    protected LinuxDiskCollectorImpl collector;

    /**
     * @param statsService        - The stats service. might be null
     * @param disks               - file system paths
     * @param isTickThreadEnabled - Enable tick thread. Typically true
     * @param tickPeriodSeconds   - Tick thread period
     * @param tickSlipWarnSeconds - ick period warning threshold
     */

    public LinuxDiskCollectorImplService(StatsService statsService, String[] disks,
                                         boolean isTickThreadEnabled,
                                         long tickPeriodSeconds, long tickSlipWarnSeconds) {
        // Call parent ctor
        super(COLLECTOR_SERVICE_NAME, statsService, isTickThreadEnabled, tickPeriodSeconds, tickSlipWarnSeconds);

        // Create our one and only collector :-)
        collector = new LinuxDiskCollectorImpl(this.statsService, disks);

        // Start doing the real work
        start();

    }


    /**
     * collect the data by calling our collector.
     * <p>
     * This function is typically called from the parent class at the tick
     *
     * @param epoch - the measurement time
     */
    public void collect(long epoch) {

        collector.collect(epoch);
    }


}
