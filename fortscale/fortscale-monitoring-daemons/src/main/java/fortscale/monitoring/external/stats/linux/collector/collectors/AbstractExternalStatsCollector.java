package fortscale.monitoring.external.stats.linux.collector.collectors;

import fortscale.monitoring.external.stats.collector.impl.linux.parsers.LinuxProcFileParser;

import java.util.Map;

/**
 * Created by galiar on 14/04/2016.
 */
public abstract class AbstractExternalStatsCollector {

    private static final double PAGE_SIZE = 4096.0;
    private static final double MB_PER_PAGE = PAGE_SIZE/1048576; // 1048576  = (bytes in MB)
    private static final int KILO = 1024;

    public abstract void collect(Map<String,LinuxProcFileParser> parsers);

    protected Long convertKBToMB(Long numberInKB){
        return numberInKB/KILO;
    }

    protected Long convertPagesToMB(Long numberOfPages){
        return (long)(numberOfPages * MB_PER_PAGE) ;
    }
    protected Long convertPagesToBytes(Long numberOfPages){
        return (long)(numberOfPages * PAGE_SIZE) ;
    }

    protected Long convertBytesToMB(Long bytes){
        return bytes/KILO/KILO;
    }


}
