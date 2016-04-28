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
    private static final double MB_PER_PAGE = 0.00390625; //4096 (page size assumed 4KB)/ 1048576 (bytes in MB)

    public abstract void collect(Map<String,ExternalStatsProcFileParser> parsers);

    protected Long getValueFromParserFormatted(String fieldName, ExternalStatsProcFileKeyValueParser parser, ProcFieldConvertionEnum howToConvert) {
        Long value = parser.getValue(fieldName);
        if(value == null){
            logger.error("No key : {} exist in file {}!! ",fieldName ,parser.getFilename());
        }else {
            switch (howToConvert) {
                case NOPE:{
                    break;
                }
                case KB_TO_MB: {
                    return convertKBToMB(value);
                }
                case PAGES_TO_MB: {
                    return convertPagesToMB(value);
                }
                default:{
                    logger.error("conversion method: {} doesn't exist. file name: {} field name: {}",howToConvert, parser.getName(), fieldName);
                }
            }
        }
        return value;
    }

    protected Long convertKBToMB(Long numberInKB){
        return numberInKB/1024;
    }

    protected Long convertPagesToMB(Long numberOfPages){
        return (long)(numberOfPages * MB_PER_PAGE) ;
    }
    protected Long convertPagesToBytes(Long numberOfPages){
        return (long)(numberOfPages * MB_PER_PAGE * 1024) ;
    }

}
