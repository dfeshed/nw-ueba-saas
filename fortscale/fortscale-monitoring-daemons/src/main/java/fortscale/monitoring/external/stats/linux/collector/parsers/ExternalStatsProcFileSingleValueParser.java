package fortscale.monitoring.external.stats.linux.collector.parsers;

import fortscale.monitoring.external.stats.linux.collector.parsers.exceptions.ProcFileBadFormatException;
import fortscale.monitoring.external.stats.linux.collector.parsers.exceptions.ProcFileBadNumberFormatException;
import fortscale.monitoring.external.stats.linux.collector.parsers.exceptions.ProcFileParserException;
import fortscale.utils.logging.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * used to parse proc files from the format of list of lines, where each line consist of single pair of key,value
 * example input (out of /proc/meminfo): Mapped:           256632 kB
 *
 * Created by galiar on 18/04/2016.
 */
public class ExternalStatsProcFileSingleValueParser extends ExternalStatsProcFileParser {

    private static Logger logger = Logger.getLogger(ExternalStatsProcFileSingleValueParser.class);

    private Map<String, Long> data = new HashMap<>();

    public ExternalStatsProcFileSingleValueParser(String filename, String separator, String name) throws ProcFileParserException {
        super(filename, separator,name);
        data = initData();
    }

    public Long getValue(String key){
        return data.get(key);
    }


    private Map initData() throws ProcFileParserException{

        List<String> lines = parseFileToLines();

        Map<String,Long> dataMap = new HashMap<>();

        for(String line: lines){
            //line should be in form of <key><separator><whitespace?><value>
            String[] parsedString = line.split(String.format("\\s*%s\\s*",separator));
            if (parsedString.length != 2){
                String errorMessage = String.format("error in reading line: %s in proc file: %s. should be in format <key><whitespace?><separator><whitespace?><value>",line,filename);
                logger.error(errorMessage);
                throw new ProcFileBadFormatException(errorMessage);
            }
            try {
                dataMap.put(parsedString[0], convertToLong(parsedString[1]));
            }
            catch (ProcFileBadNumberFormatException e){
                String errorMessage = String.format("error converting string '%s' to number in line: %s in proc file: %s.",e.getNumberTryingToConvert(),line,filename);
                throw new ProcFileBadFormatException(errorMessage,e);
            }
        }
        return dataMap;
    }



}
