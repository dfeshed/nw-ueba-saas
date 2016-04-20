package fortscale.monitoring.external.stats.linux.collector.collectors;

import fortscale.monitoring.external.stats.linux.collector.parsers.ExternalStatsProcFileSingleValueParser;
import fortscale.utils.logging.Logger;

/**
 * Created by galiar on 14/04/2016.
 */
public abstract class AbstractExternalStatsCollector {

    private static Logger logger = Logger.getLogger(AbstractExternalStatsCollector.class);
    private static double MB_PER_PAGE = 0.00390625; //4096 (page size assumed 4KB)/ 1048576 (bytes in MB)

    public abstract void collect();

    //public abstract void collect(ExternalStatsProcFileParser parser);

    protected Long getValueFromParserFormatted(String fieldName, ExternalStatsProcFileSingleValueParser memInfoParser, ProcFieldConvertionEnum howToConvert) {
        Long value = memInfoParser.getValue(fieldName);
        if(value == null){
            logger.error("No key : {} exist in file {}!! ",fieldName ,memInfoParser.getFilename());
        }else {
            switch (howToConvert) {
                case KB_TO_MB: {
                    return convertKBToMB(value);
                }
                case PAGES_TO_MB: {
                    return convertPagesToMB(value);
                }
                default:{break;}
            }
        }
        return value;
    }

    protected Long convertKBToMB(Long numberInKB){
        return numberInKB/1024;
    }

    protected Long convertPagesToMB(Long numberOfPages){
        //remove the decimal point, then add 1, since the number of pages is inclusive
        return (long)(numberOfPages * MB_PER_PAGE) +1 ;
    }

}
