package fortscale.monitoring.external.stats.linux.collector.collectors;

import fortscale.monitoring.external.stats.linux.collector.metrics.ExternalStatsOSMemoryCollectorMetrics;
import fortscale.monitoring.external.stats.linux.collector.parsers.ExternalStatsProcFileParser;
import fortscale.monitoring.external.stats.linux.collector.parsers.ExternalStatsProcFileSingleValueParser;
import fortscale.monitoring.external.stats.linux.collector.parsers.exceptions.ProcFileParserException;
import fortscale.services.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.logging.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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

    private static final String MEM_INFO_FILE_PATH = "/proc/meminfo";
    private static final String VMSTAT_FILE_PATH = "/proc/vmstat";

    private static final String MEM_INFO_SEPARATOR = ":";
    private static final String VMSTAT_SEPARATOR = " ";

    @Override
    public void collect(){ //TODO maybe move this part to collector manager
        ExternalStatsProcFileSingleValueParser memInfoParser = null;
        ExternalStatsProcFileSingleValueParser vmstatParser = null;
        try {
            memInfoParser = new ExternalStatsProcFileSingleValueParser(MEM_INFO_FILE_PATH, MEM_INFO_SEPARATOR);
            vmstatParser = new ExternalStatsProcFileSingleValueParser(VMSTAT_FILE_PATH, VMSTAT_SEPARATOR);
        }
        catch (ProcFileParserException e ){
                //TODO something
        }

        collect(new ArrayList<>(Arrays.asList(memInfoParser,vmstatParser)));

    }

    @Override
    public void collect(List<ExternalStatsProcFileParser> parsers) {

        ExternalStatsProcFileSingleValueParser memInfoParser = null;
        ExternalStatsProcFileSingleValueParser vmstatParser = null;

        for (ExternalStatsProcFileParser parser: parsers){
            if(parser.getName().equals("meminfo")){
                memInfoParser = (ExternalStatsProcFileSingleValueParser) parser;
            }
            else if(parser.getName().equals("vmstat")){
                vmstatParser = (ExternalStatsProcFileSingleValueParser) parser;
            }
        }

        Long totalMemory = getValueFromParserFormatted(TOTAL_MEMORY_MB,memInfoParser,ProcFieldConvertionEnum.KB_TO_MB);
        memoryCollectorMetrics.setTotalMemoryMB(totalMemory);

        Long freeMemory = getValueFromParserFormatted(FREE_MEMORY_MB,memInfoParser,ProcFieldConvertionEnum.KB_TO_MB);
        memoryCollectorMetrics.setFreeMemoryMB(freeMemory);

        memoryCollectorMetrics.setUsedMemoryMB(totalMemory-freeMemory);

        Long sharedMemory = getValueFromParserFormatted(SHARED_MEMORY_MB,memInfoParser,ProcFieldConvertionEnum.KB_TO_MB);
        memoryCollectorMetrics.setSharedMemoryMB(sharedMemory);

        Long realFreeMemory = getValueFromParserFormatted(REAL_FREE_MEMORY_MB,memInfoParser,ProcFieldConvertionEnum.KB_TO_MB);
        memoryCollectorMetrics.setRealFreeMemoryMB(realFreeMemory);

        Long buffersMemory = getValueFromParserFormatted(BUFFERS_MEMORY_MB,memInfoParser,ProcFieldConvertionEnum.KB_TO_MB);
        memoryCollectorMetrics.setBuffersMemoryMB(buffersMemory);

        Long cacheMemory = getValueFromParserFormatted(CACHE_MEMORY_MB,memInfoParser,ProcFieldConvertionEnum.KB_TO_MB);
        memoryCollectorMetrics.setCacheMemoryMB(cacheMemory);

        Long dirtyMemory = getValueFromParserFormatted(DIRTY_MEMORY_MB,memInfoParser,ProcFieldConvertionEnum.KB_TO_MB);
        memoryCollectorMetrics.setDirtyMemoryMB(dirtyMemory);

        Long swapInMemory = getValueFromParserFormatted(SWAP_IN_MEMORY_MB,vmstatParser,ProcFieldConvertionEnum.PAGES_TO_MB);
        memoryCollectorMetrics.setSwapInMemoryMB(swapInMemory);

        Long swapOutMemory = getValueFromParserFormatted(SWAP_OUT_MEMORY_MB,vmstatParser,ProcFieldConvertionEnum.PAGES_TO_MB);
        memoryCollectorMetrics.setSwapOutMemoryMB(swapOutMemory);

        Long bufferInMemory = getValueFromParserFormatted(BUFFER_IN_MEMORY_MB,vmstatParser,ProcFieldConvertionEnum.PAGES_TO_MB);
        memoryCollectorMetrics.setBufferInMemoryMB(bufferInMemory);

        Long bufferOutMemory = getValueFromParserFormatted(BUFFER_OUT_MEMORY_MB,vmstatParser,ProcFieldConvertionEnum.PAGES_TO_MB);
        memoryCollectorMetrics.setBufferOutMemoryMB(bufferOutMemory);


        //finally, update manually, since the data is being updated only while checking - no point letting the stats
        //service to get these metrics in a random time
        // memoryCollectorMetrics.manualUpdate(); //TODO uncomment when GroupStatsMetrics is ready
    }
}
