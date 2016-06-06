package fortscale.monitoring.external.stats.collector.impl.linux.memory;

import fortscale.monitoring.external.stats.collector.impl.linux.parsers.LinuxProcFileKeyValueParser;

import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;


/**
 *
 * This collector collects memory info from proc files.
 * memory info is: total, used, free, real-free, buffers, cache, shared, dirty, swap-in,
 * swap-out, buffer-in, buffer-out -- All in MBytes
 *
 * Created by galiar on 14/04/2016.
 */
public class ExternalStatsCollectorLinuxMemoryCollector {

    private static Logger logger = Logger.getLogger(ExternalStatsCollectorLinuxMemoryCollector.class);

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

    String procBasePath;
    String collectorName;
    protected ExternalStatsCollectorLinuxMemoryMetrics metrics;

    public ExternalStatsCollectorLinuxMemoryCollector(String collectorServiceName, StatsService statsService, String procBasePath) {

        // Save params
        this.collectorName = String.format("%s[%s]"), collectorServiceName, NUMA_NAME);
        this.procBasePath  = procBasePath;

        // Create metrics
        metrics = new ExternalStatsCollectorLinuxMemoryMetrics(statsService, NUMA_NAME);
    }


    public void collect(long epoch) {
        logger.debug("Collecting {} at {}", collectorName, epoch);
        try {

            // Create the parses
            LinuxProcFileKeyValueParser memInfoParser = new LinuxProcFileKeyValueParser(procBasePath, "meminfo", ":");
            LinuxProcFileKeyValueParser vmstatParser = new LinuxProcFileKeyValueParser(procBasePath, "vmstat", " ");

            // Fill the values
            metrics.totalMemory = memInfoParser.getValue(TOTAL_MEMORY_MB) * KB_TO_BYTES;
            metrics.freeMemory  = memInfoParser.getValue(FREE_MEMORY_MB) * KB_TO_BYTES;
            metrics.usedMemory  = metrics.totalMemory - metrics.freeMemory;

            metrics.activeMemory   = memInfoParser.getValue(ACTIVE_MEMORY_MB) * KB_TO_BYTES;
            metrics.inactiveMemory = memInfoParser.getValue(INACTIVE_MEMORY_MB) * KB_TO_BYTES;
            metrics.sharedMemory   = memInfoParser.getValue(SHARED_MEMORY_MB) * KB_TO_BYTES;
            metrics.buffersMemory  = memInfoParser.getValue(BUFFERS_MEMORY_MB) * KB_TO_BYTES;
            metrics.cacheMemory    = memInfoParser.getValue(CACHE_MEMORY_MB) * KB_TO_BYTES;
            metrics.dirtyMemory    = memInfoParser.getValue(DIRTY_MEMORY_MB) * KB_TO_BYTES;
            metrics.realFreeMemory = metrics.freeMemory + metrics.buffersMemory + metrics.cacheMemory;

            metrics.swapInMemory    = vmstatParser.getValue(SWAP_IN_MEMORY_MB) * PAGES_TO_BYTES;
            metrics.swapOutMemory   = vmstatParser.getValue(SWAP_OUT_MEMORY_MB) * PAGES_TO_BYTES;
            metrics.bufferInMemory  = vmstatParser.getValue(BUFFER_IN_MEMORY_MB) * PAGES_TO_BYTES;
            metrics.bufferOutMemory = vmstatParser.getValue(BUFFER_OUT_MEMORY_MB) * PAGES_TO_BYTES;

            metrics.manualUpdate(epoch);
        }
        catch (Exception e) {
            String msg = String.format("Error collecting {} at {}. Ignored", collectorName, epoch);
            logger.error(msg,e);
        }


    }

    // --- getters / setters


    public ExternalStatsCollectorLinuxMemoryMetrics getMetrics() {
        return metrics;
    }

}

