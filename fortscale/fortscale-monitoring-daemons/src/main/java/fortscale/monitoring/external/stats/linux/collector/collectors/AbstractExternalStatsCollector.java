package fortscale.monitoring.external.stats.linux.collector.collectors;

import fortscale.monitoring.external.stats.linux.collector.parsers.ExternalStatsProcFileKeyValueParser;
import fortscale.monitoring.external.stats.linux.collector.parsers.ExternalStatsProcFileParser;
import fortscale.utils.logging.Logger;

import java.util.Map;

/**
 * Created by galiar on 14/04/2016.
 */
public abstract class AbstractExternalStatsCollector {

    private static Logger logger = Logger.getLogger(AbstractExternalStatsCollector.class);
    private static final double PAGE_SIZE = 4096.0;
    private static final double MB_PER_PAGE = PAGE_SIZE/1048576; // 1048576  = (bytes in MB)

    public abstract void collect(Map<String,ExternalStatsProcFileParser> parsers);

    protected Long convertKBToMB(Long numberInKB){
        return numberInKB/1024;
    }

    protected Long convertPagesToMB(Long numberOfPages){
        return (long)(numberOfPages * MB_PER_PAGE) ;
    }
    protected Long convertPagesToBytes(Long numberOfPages){
        return (long)(numberOfPages * PAGE_SIZE) ;
    }

}
