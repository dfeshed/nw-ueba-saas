package fortscale.monitoring.external.stats.collector.impl.linux.core;

import fortscale.monitoring.external.stats.collector.impl.AbstractExternalStatsCollectorServiceImpl;
import fortscale.monitoring.external.stats.collector.impl.linux.parsers.LinuxProcFileKeyMultipleValueParser;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * A service that collects linux cores information from /proc/stat file and writes them to stats metrics group.
 *
 * Created by gaashh on 6/8/16.
 */
public class LinuxCoreCollectorImplService extends AbstractExternalStatsCollectorServiceImpl {

    private static final Logger logger = Logger.getLogger(LinuxCoreCollectorImplService.class);

    // Collector service name. Used for logging
    final static String COLLECTOR_SERVICE_NAME = "linuxCore";

    // /proc/stats params
    final static int    STAT_KEY_INDEX      = 0;
    final static String STAT_CORE_PREFIX    = "cpu";
    final static String STAT_ALL_CORES_KEY  = "cpu";
    final static String STAT_ALL_CORES_NAME = "ALL";


    // Linux /proc file system base path
    String procBasePath;

    // Map of all collector maps: core name -> collector
    Map<String,LinuxCoreCollectorImpl> collectorsMap = new HashMap<>();


    /**
     * ctor
     *
     * @param statsService              - The stats service. might be null
     * @param procBasePath              - "/proc" base path
     * @param isTickThreadEnabled       - Enable tick thread. Typically true
     * @param tickPeriodSeconds         - Tick thread period
     * @param tickSlipWarnSeconds       - Tick period warning threshold
     *
     */

    public LinuxCoreCollectorImplService(StatsService statsService, String procBasePath,
                                            boolean isTickThreadEnabled,
                                            long tickPeriodSeconds, long tickSlipWarnSeconds) {
        // Call parent ctor
        super(COLLECTOR_SERVICE_NAME, statsService, isTickThreadEnabled, tickPeriodSeconds, tickSlipWarnSeconds);

        // Save vars
        this.procBasePath              = procBasePath;

        // Start doing the real work
        start();

    }


    /**
     * collect the process data for Fortscale processes and external processes. It creates a parser for /proc/stats
     * and process all the cpus within it
     *
     * This function is typically called from the parent class at the tick
     *
     * @param epoch - the measurement time
     */

    public void collect(long epoch) {

        // Calc file name
        String statFilename = new File(procBasePath, "stat").toString();

        String coreKey = "NOT-SET";
        try {
            // Create the parser
            LinuxProcFileKeyMultipleValueParser parser = new LinuxProcFileKeyMultipleValueParser(statFilename, " ", STAT_KEY_INDEX);

            // Get the keys
            Set<String> coreKeysSet = parser.getKeys();

            // loop all the core. process regular cores and count them
            long regularCoreCount = 0;
            for (String coreKeyVar : coreKeysSet) {

                // To help the exception
                coreKey = coreKeyVar;

                // Skip non-core entries
                if (!coreKey.startsWith(STAT_CORE_PREFIX)) {
                    continue;
                }

                // If it is the "ALL-core", skip it
                if (coreKey.equals(STAT_ALL_CORES_KEY)) {
                    continue;
                }

                // Regular core, count it
                regularCoreCount++;

                // Core name is core key
                String coreName = coreKey;

                // Process the regular core
                collectOneCore(epoch, parser, coreName, coreKey, 1.0);
            }

            // Process the "all-core" core
            double allCoreFactor = (regularCoreCount == 0) ? 1.0 : (1.0 / regularCoreCount);
            collectOneCore(epoch, parser, STAT_ALL_CORES_NAME, STAT_ALL_CORES_KEY, allCoreFactor);

        }
        catch (Exception e) {
            logger.warn("Linux core collector service {} - problem parsing proc file {} for key {}. Ignored",
                    collectorServiceName, statFilename, coreKey);
            return;

        }
    }

    /**
     *
     * Collect data for one core. Core data is provided in the parter with key 'coreKey'
     *
     *
     * @param epoch
     * @param parser     - a /proc/stat parser
     * @param coreName   - core logical name (for metrics)
     * @param coreKey    - core key (for parser)
     * @param factor
     */
    public void collectOneCore(long epoch, LinuxProcFileKeyMultipleValueParser parser, String coreName, String coreKey, double factor) {

        // Try to get the collector from the collect map
        LinuxCoreCollectorImpl collector = collectorsMap.get(coreName);
        if (collector == null) {

            // Not found, create a new collector
            collector = new LinuxCoreCollectorImpl(collectorServiceName, statsService, coreName, coreKey);

            // Add the new collector to the collectors map
            collectorsMap.put(coreName, collector);
        }

        // Collect it
        collector.collect(epoch, parser, factor);

    }

    /**
     * Get the collector metrics object. Used for testing
     *
     * @return - the collector metrics object
     */
    public LinuxCoreCollectorImplMetrics getMetrics(String coreName) {

        // Get the collector
        LinuxCoreCollectorImpl collector = collectorsMap.get(coreName);

        // Check not found
        if (collector == null) {
            return null;
        }

        // Get the metrics from the collector
        LinuxCoreCollectorImplMetrics metrics = collector.getMetrics();

        return metrics;
    }

}
