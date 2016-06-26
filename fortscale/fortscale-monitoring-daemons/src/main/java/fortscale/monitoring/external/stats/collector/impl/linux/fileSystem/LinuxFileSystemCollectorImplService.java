package fortscale.monitoring.external.stats.collector.impl.linux.fileSystem;

import fortscale.monitoring.external.stats.collector.impl.AbstractExternalStatsCollectorServiceImpl;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;

/**
 * A service that collects linux file system information and writes them to stats metrics group

 */
public class LinuxFileSystemCollectorImplService extends AbstractExternalStatsCollectorServiceImpl {

    private static final Logger logger = Logger.getLogger(LinuxFileSystemCollectorImplService.class);

    // Collector service name. Used for logging
    final static String COLLECTOR_SERVICE_NAME = "linuxFileSystem";

    // The collector.
    protected LinuxFileSystemCollectorImpl collector;

    /**
     * @param statsService        - The stats service. might be null
     * @param paths               - file system paths
     * @param isTickThreadEnabled - Enable tick thread. Typically true
     * @param tickPeriodSeconds   - Tick thread period
     * @param tickSlipWarnSeconds - ick period warning threshold
     */

    public LinuxFileSystemCollectorImplService(StatsService statsService, String[] paths,
                                               boolean isTickThreadEnabled,
                                               long tickPeriodSeconds, long tickSlipWarnSeconds) {
        // Call parent ctor
        super(COLLECTOR_SERVICE_NAME, statsService, isTickThreadEnabled, tickPeriodSeconds, tickSlipWarnSeconds);

        // Create our one and only collector :-)
        collector = new LinuxFileSystemCollectorImpl(this.statsService, paths,selfMetrics);

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
