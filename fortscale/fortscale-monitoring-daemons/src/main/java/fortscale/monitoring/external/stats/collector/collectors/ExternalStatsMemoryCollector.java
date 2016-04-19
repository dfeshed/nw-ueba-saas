package fortscale.monitoring.external.stats.collector.collectors;

import fortscale.monitoring.external.stats.collector.metrics.ExternalStatsMemoryCollectorMetrics;
import fortscale.monitoring.external.stats.collector.parsers.ExternalStatsProcFileSingleValueParser;
import fortscale.monitoring.external.stats.collector.parsers.exceptions.ProcFileParserException;
import org.springframework.beans.factory.annotation.Autowired;


/**
 *
 * This collector collects memory info from proc files.
 * memory info is: total, used, free, real-free, buffers, cache, shared, dirty, swap-in,
 * swap-out, buffer-in, buffer-out -- All in MBytes
 *
 * Created by galiar on 14/04/2016.
 */
public class ExternalStatsMemoryCollector extends AbstractExternalStatsCollector {

    @Autowired
    private ExternalStatsMemoryCollectorMetrics memoryCollectorMetrics;

    private static final String TOTAL_MEMORY_MB = "total";

    private static final String memInfoFilePath = "/proc/meminfo";
    private static final String vmstatFilePath = "/proc/vmstat";

    private static final String memInfoSeparator = ":";
    private static final String vmstatSeparator = " ";


    public void collect(){
        ExternalStatsProcFileSingleValueParser memInfoParser = null;
        ExternalStatsProcFileSingleValueParser vmstatParser = null;
        try {
            memInfoParser = new ExternalStatsProcFileSingleValueParser(memInfoFilePath, memInfoSeparator);
            vmstatParser = new ExternalStatsProcFileSingleValueParser(vmstatFilePath, vmstatSeparator);
        }
        catch (ProcFileParserException e ){
                //TODO something
        }


        Long totalMemory;
        //TODO surround with try catch
            totalMemory = convertToMB(memInfoParser.getValue(TOTAL_MEMORY_MB)) ;



       //     Map<String,Long> vmstatData = vmstatParser.parseFileAsMapOfSingleValue();
        memoryCollectorMetrics.setTotalMemoryMB(totalMemory);


        //finally, update manually, since the data is being updated only while checking - no point letting the stats
        //service to get these metrics in a random time
        memoryCollectorMetrics.manualUpdate();
    }



    //relevant filenames: meminfo vmstat

    // relevant parse functions: parse single value for both (meminfo and vmstat)

    //total - long - meminfo

    //used - long - meminfo

    //free - long - meminfo

    //buffers - long - meminfo

    //cache - long - meminfo

    //shared - long - meminfo

    //dirty - long - meminfo

    //swap-in - long - vmstat

    //swap-out - long - vmstat

    //buffer-in - long - vmstat

    //buffer-out - long - vmstat


    //check for exceptions

    //get relevant data

    //save it to memory metrics

    private Long convertToMB(Long numberInKB){
        return numberInKB/1000;
    }


}
