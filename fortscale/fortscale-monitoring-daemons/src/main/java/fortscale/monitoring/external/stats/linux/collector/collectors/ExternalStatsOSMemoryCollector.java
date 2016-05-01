package fortscale.monitoring.external.stats.linux.collector.collectors;

import fortscale.monitoring.external.stats.linux.collector.metrics.ExternalStatsOSMemoryCollectorMetrics;
import fortscale.monitoring.external.stats.linux.collector.parsers.ExternalStatsProcFileKeyValueParser;
import fortscale.monitoring.external.stats.linux.collector.parsers.ExternalStatsProcFileParser;

import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;

import java.util.Map;


/**
 *
 * This collector collects memory info from proc files.
 * memory info is: total, used, free, real-free, buffers, cache, shared, dirty, swap-in,
 * swap-out, buffer-in, buffer-out -- All in MBytes
 *
 * Created by galiar on 14/04/2016.
 */
public class ExternalStatsOSMemoryCollector extends AbstractExternalStatsCollector {

    private static Logger logger = Logger.getLogger(ExternalStatsOSMemoryCollector.class);
    private ExternalStatsOSMemoryCollectorMetrics memoryCollectorMetrics = new ExternalStatsOSMemoryCollectorMetrics(new StatsMetricsGroupAttributes()); //TODO real attributes

    private static final String TOTAL_MEMORY_MB = "MemTotal";
    private static final String FREE_MEMORY_MB = "MemFree";
    private static final String SHARED_MEMORY_MB = "Shmem";
    private static final String REAL_FREE_MEMORY_MB = "Active";
    private static final String BUFFERS_MEMORY_MB = "Buffers";
    private static final String CACHE_MEMORY_MB = "Cached";
    private static final String DIRTY_MEMORY_MB = "Dirty";
    private static final String SWAP_IN_MEMORY_MB = "pswpin";
    private static final String SWAP_OUT_MEMORY_MB = "pswpout";
    private static final String BUFFER_IN_MEMORY_MB = "pgpgin";
    private static final String BUFFER_OUT_MEMORY_MB = "pgpgout";


    @Override
    public void collect(Map<String,ExternalStatsProcFileParser> parsers) {

        ExternalStatsProcFileKeyValueParser memInfoParser = (ExternalStatsProcFileKeyValueParser) parsers.get("meminfo");
        ExternalStatsProcFileKeyValueParser vmstatParser = (ExternalStatsProcFileKeyValueParser) parsers.get("vmstat");

        Long totalMemory =  convertKBToMB(memInfoParser.getValue(TOTAL_MEMORY_MB));
        memoryCollectorMetrics.setTotalMemoryMB(totalMemory);

        Long freeMemory =  convertKBToMB(memInfoParser.getValue(FREE_MEMORY_MB));
        memoryCollectorMetrics.setFreeMemoryMB(freeMemory);

        memoryCollectorMetrics.setUsedMemoryMB(totalMemory-freeMemory);

        Long sharedMemory = convertKBToMB(memInfoParser.getValue(SHARED_MEMORY_MB));
        memoryCollectorMetrics.setSharedMemoryMB(sharedMemory);

        Long realFreeMemory = convertKBToMB(memInfoParser.getValue(REAL_FREE_MEMORY_MB));
        memoryCollectorMetrics.setRealFreeMemoryMB(realFreeMemory);

        Long buffersMemory = convertKBToMB(memInfoParser.getValue(BUFFERS_MEMORY_MB));
        memoryCollectorMetrics.setBuffersMemoryMB(buffersMemory);

        Long cacheMemory = convertKBToMB(memInfoParser.getValue(CACHE_MEMORY_MB));
        memoryCollectorMetrics.setCacheMemoryMB(cacheMemory);

        Long dirtyMemory = convertKBToMB(memInfoParser.getValue(DIRTY_MEMORY_MB));
        memoryCollectorMetrics.setDirtyMemoryMB(dirtyMemory);

        Long swapInMemory = convertPagesToMB(vmstatParser.getValue(SWAP_IN_MEMORY_MB));
        memoryCollectorMetrics.setSwapInMemoryMB(swapInMemory);

        Long swapOutMemory = convertPagesToMB(vmstatParser.getValue(SWAP_OUT_MEMORY_MB));
        memoryCollectorMetrics.setSwapOutMemoryMB(swapOutMemory);

        Long bufferInMemory = convertPagesToMB(vmstatParser.getValue(BUFFER_IN_MEMORY_MB));
        memoryCollectorMetrics.setBufferInMemoryMB(bufferInMemory);

        Long bufferOutMemory = convertPagesToMB(vmstatParser.getValue(BUFFER_OUT_MEMORY_MB));
        memoryCollectorMetrics.setBufferOutMemoryMB(bufferOutMemory);


        //finally, update manually, since the data is being updated only while checking - no point letting the stats
        //service to get these metrics in a random time
        // memoryCollectorMetrics.manualUpdate(); //TODO uncomment when GroupStatsMetrics is ready
    }

    //for testing only
    public ExternalStatsOSMemoryCollectorMetrics getMemoryCollectorMetrics() {
        return memoryCollectorMetrics;
    }

}
