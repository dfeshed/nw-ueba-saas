package fortscale.monitoring.external.stats.collector.impl.linux.core;

import fortscale.monitoring.external.stats.collector.impl.ExternalStatsCollectorMetrics;
import fortscale.monitoring.external.stats.collector.impl.linux.parsers.LinuxProcFileKeyMultipleValueParser;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;

/**
 * collects data of a single Linux process of the system.
 * Created by galiar & gaash on 27/04/2016.
 */
public class LinuxCoreCollectorImpl {

    private static Logger logger = Logger.getLogger(LinuxCoreCollectorImpl.class);

    // Parser field index
    private static final int USER_INDEX = 1;
    private static final int NICE_INDEX = 2;
    private static final int SYSTEM_INDEX = 3;
    private static final int IDLE_INDEX = 4;
    private static final int WAIT_INDEX = 5;
    private static final int HW_INTERRUPTS_INDEX = 6;
    private static final int SW_INTERRUPTS_INDEX = 7;
    private static final int STEAL_INDEX = 8;

    // Kernel tick duration
    private static final long KERNEL_TICK_TO_MSEC = 10;  // Tick is 10mSec


    // Collector name - mainly used for logging
    String collectorName;

    // Core name (for metrics tag)
    String coreName;

    // Core key (for parser)
    String coreKey;

    // Stats service metrics
    protected LinuxCoreCollectorImplMetrics metrics;

    ExternalStatsCollectorMetrics selfMetrics;

    /**
     *
     * ctor
     *
     * Creates the metrics group
     *
     * @param collectorServiceName
     * @param statsService
     * @param coreName
     * @param coreKey
     *
     */
    public LinuxCoreCollectorImpl(String collectorServiceName, StatsService statsService,
                                     String coreName, String coreKey, ExternalStatsCollectorMetrics selfMetrics) {

        // Save params while doing some calculations
        this.coreName         = coreName;
        this.coreKey          = coreKey;
        this.collectorName    = String.format("%s[%s]", collectorServiceName, coreName);

        // self metrics
        this.selfMetrics=selfMetrics;
        logger.debug("Creating Linux core collector instance {} for core {} with key {}", collectorName, coreName, coreKey);

        // Create metrics
        metrics = new LinuxCoreCollectorImplMetrics(statsService, coreName);
    }

    /**
     * Converts given tick to mSec by multiplying by factor and KERNEL_TICK_TO_MSEC
     *
     * @param ticks
     * @param factor
     * @return
     */
    protected long convert(long ticks, double factor) {

        long value = Math.round( (double)(ticks * KERNEL_TICK_TO_MSEC) * factor);

        return value;

    }

    /**
     * Collect the data from the /proc files and updates the metrics.
     *
     * @param epoch
     * @param parser - the parser containing the /proc file. Reference the data with the core name
     * @param factor - multiply all values by this number. Usefull to device the "all-core" by core number
     */

    public void collect(long epoch, LinuxProcFileKeyMultipleValueParser parser, double factor) {

        logger.debug("Collecting {} at {} for core {} with key {}. factor={}",
                     collectorName, epoch, coreName, coreKey, factor);

        try {

            metrics.userMiliSec         = convert(parser.getLongValue(coreKey, USER_INDEX), factor);
            metrics.systemMiliSec       = convert(parser.getLongValue(coreKey, SYSTEM_INDEX),        factor);
            metrics.niceMiliSec         = convert(parser.getLongValue(coreKey, NICE_INDEX),          factor);
            metrics.idleMiliSec         = convert(parser.getLongValue(coreKey, IDLE_INDEX),          factor);
            metrics.waitMiliSec         = convert(parser.getLongValue(coreKey, WAIT_INDEX),          factor);
            metrics.hwInterruptsMiliSec = convert(parser.getLongValue(coreKey, HW_INTERRUPTS_INDEX), factor);
            metrics.swInterruptsMiliSec = convert(parser.getLongValue(coreKey, SW_INTERRUPTS_INDEX), factor);
            metrics.stealMiliSec        = convert(parser.getLongValue(coreKey, STEAL_INDEX),         factor);

            // Update the metrics
            metrics.manualUpdate(epoch);

        }
        catch (Exception e) {
            selfMetrics.collectFailures++;
            String msg = String.format("Error collecting %s at %d for core %s with key %s. Ignored",
                    collectorName, epoch, coreName, coreKey);
            logger.warn(msg, e);
        }


    }

    public LinuxCoreCollectorImplMetrics getMetrics() {
        return metrics;
    }
}
