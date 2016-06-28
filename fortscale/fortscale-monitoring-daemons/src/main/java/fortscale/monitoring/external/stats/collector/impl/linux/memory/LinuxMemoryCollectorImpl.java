package fortscale.monitoring.external.stats.collector.impl.linux.memory;

import fortscale.monitoring.external.stats.collector.impl.ExternalStatsCollectorMetrics;
import fortscale.monitoring.external.stats.collector.impl.linux.parsers.LinuxProcFileKeyValueParser;

import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;

import java.io.File;


/**
 *
 * This collector collects memory info from proc files.
 * memory info is: total, used, free, real-free, buffers, cache, shared, dirty, swap-in,
 * swap-out, buffer-in, buffer-out -- All in MBytes
 *
 * Created by galiar & gaashh on 14/04/2016.
 */
public class LinuxMemoryCollectorImpl {

    private static Logger logger = Logger.getLogger(LinuxMemoryCollectorImpl.class);

    protected static final String NUMA_NAME = "all";

    private static final String TOTAL_MEMORY_MB = "MemTotal";
    private static final String FREE_MEMORY_MB = "MemFree";
    private static final String SHARED_MEMORY_MB = "Shmem";
    private static final String ACTIVE_MEMORY_MB = "Active";
    private static final String INACTIVE_MEMORY_MB = "Inactive";
    private static final String BUFFERS_MEMORY_MB = "Buffers";
    private static final String CACHE_MEMORY_MB = "Cached";
    private static final String DIRTY_MEMORY_MB = "Dirty";
    private static final String SWAP_IN_MEMORY_MB = "pswpin";
    private static final String SWAP_OUT_MEMORY_MB = "pswpout";
    private static final String BUFFER_IN_MEMORY_MB = "pgpgin";
    private static final String BUFFER_OUT_MEMORY_MB = "pgpgout";

    private static final long KB_TO_BYTES = 1024;
    private static final long PAGES_TO_BYTES = 4 * KB_TO_BYTES;

    // self stats
    private ExternalStatsCollectorMetrics selfMetrics;

    // Collector name - mainly used for logging
    String collectorName;

    // Stats service metrics
    protected LinuxMemoryCollectorImplMetrics metrics;

    // "proc" file system base path.
    String procBasePath;

    /**
     * ctor
     *
     * Creates the metrics group
     *
     * @param collectorServiceName
     * @param statsService
     * @param procBasePath
     */
    public LinuxMemoryCollectorImpl(String collectorServiceName, StatsService statsService, String procBasePath,ExternalStatsCollectorMetrics selfMetrics) {

        // Save params
        this.collectorName = String.format("%s[%s]", collectorServiceName, NUMA_NAME);
        this.procBasePath  = procBasePath;

        logger.debug("Creating Linux memory collector instance {}. procBasePath={}", collectorName, procBasePath);

        // Create metrics
        metrics = new LinuxMemoryCollectorImplMetrics(statsService, NUMA_NAME);
        this.selfMetrics = selfMetrics;

    }


    /**
     * Collect the data from the /proc files and updates the metrics
     *
     * @param epoch
     */
    public void collect(long epoch) {

        logger.debug("Collecting {} at {}", collectorName, epoch);

        try {

            // Create the parses
            String memInfoFilename = new File(procBasePath, "meminfo").toString();
            LinuxProcFileKeyValueParser memInfoParser = new LinuxProcFileKeyValueParser(memInfoFilename, ":");

            String vmstatFilename = new File(procBasePath, "vmstat").toString();
            LinuxProcFileKeyValueParser vmstatParser = new LinuxProcFileKeyValueParser(vmstatFilename , " ");

            // Get the values from parsers to metric class
            metrics.totalMemory = memInfoParser.getValue(TOTAL_MEMORY_MB) * KB_TO_BYTES;
            metrics.freeMemory  = memInfoParser.getValue(FREE_MEMORY_MB)  * KB_TO_BYTES;
            metrics.usedMemory  = metrics.totalMemory - metrics.freeMemory;

            metrics.activeMemory   = memInfoParser.getValue(ACTIVE_MEMORY_MB)   * KB_TO_BYTES;
            metrics.inactiveMemory = memInfoParser.getValue(INACTIVE_MEMORY_MB) * KB_TO_BYTES;
            metrics.sharedMemory   = memInfoParser.getValue(SHARED_MEMORY_MB)   * KB_TO_BYTES;
            metrics.buffersMemory  = memInfoParser.getValue(BUFFERS_MEMORY_MB)  * KB_TO_BYTES;
            metrics.cacheMemory    = memInfoParser.getValue(CACHE_MEMORY_MB)    * KB_TO_BYTES;
            metrics.dirtyMemory    = memInfoParser.getValue(DIRTY_MEMORY_MB)    * KB_TO_BYTES;
            metrics.realFreeMemory = metrics.freeMemory + metrics.buffersMemory + metrics.cacheMemory;

            metrics.swapInMemory    = vmstatParser.getValue(SWAP_IN_MEMORY_MB)    * PAGES_TO_BYTES;
            metrics.swapOutMemory   = vmstatParser.getValue(SWAP_OUT_MEMORY_MB)   * PAGES_TO_BYTES;
            metrics.bufferInMemory  = vmstatParser.getValue(BUFFER_IN_MEMORY_MB)  * PAGES_TO_BYTES;
            metrics.bufferOutMemory = vmstatParser.getValue(BUFFER_OUT_MEMORY_MB) * PAGES_TO_BYTES;

            metrics.manualUpdate(epoch);
        }
        catch (Exception e) {
            selfMetrics.collectFailures++;
            String msg = String.format("Error collecting %s at %d. Ignored", collectorName, epoch);
            logger.error(msg, e);
        }
    }

    // --- getters / setters


    public LinuxMemoryCollectorImplMetrics getMetrics() {
        return metrics;
    }

}

