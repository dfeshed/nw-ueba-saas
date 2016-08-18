package fortscale.monitoring.external.stats.collector.impl.linux.memory;

import fortscale.monitoring.external.stats.collector.impl.AbstractExternalStatsCollectorServiceImpl;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;

/**
 *
 * A service that collects linux memory information from /proc files and writes them to stats metrics group
 *
 * Created by gaashh on 6/5/16.
 */
public class LinuxMemoryCollectorImplService extends AbstractExternalStatsCollectorServiceImpl {

    private static final Logger logger = Logger.getLogger(LinuxMemoryCollectorImplService.class);

    // Collector service name. Used for logging
    final static String COLLECTOR_SERVICE_NAME = "linuxMemory";

    // The collector.
    protected LinuxMemoryCollectorImpl collector;

    /**
     * @param statsService         - The stats service. might be null
     * @param procBasePath         - "/proc" base path
     * @param isTickThreadEnabled  - Enable tick thread. Typically true
     * @param tickPeriodSeconds    - Tick thread period
     * @param tickSlipWarnSeconds  - ick period warning threshold*/

    public LinuxMemoryCollectorImplService(StatsService statsService, String procBasePath,
                                           boolean isTickThreadEnabled,
                                           long tickPeriodSeconds, long tickSlipWarnSeconds) {
        // Call parent ctor
        super(COLLECTOR_SERVICE_NAME, statsService, isTickThreadEnabled, tickPeriodSeconds, tickSlipWarnSeconds);

        // Create our one and only collector :-)
        collector = new LinuxMemoryCollectorImpl(collectorServiceName, this.statsService, procBasePath,selfMetrics);

        // Start doing the real work
        start();

    }


    /**
     * collect the data by calling our collector.
     *
     * This function is typically called from the parent class at the tick
     *
     *
     * @param epoch - the measurement time
     */
    public void collect(long epoch) {

        collector.collect(epoch);
    }

    /**
     * Get the collector metrics object. Used for testing
     *
     *
     * @return - the collector metrics object
     */
    public LinuxMemoryCollectorImplMetrics getMetrics() {
        LinuxMemoryCollectorImplMetrics metrics = collector.getMetrics();
        return metrics;
    }

}
