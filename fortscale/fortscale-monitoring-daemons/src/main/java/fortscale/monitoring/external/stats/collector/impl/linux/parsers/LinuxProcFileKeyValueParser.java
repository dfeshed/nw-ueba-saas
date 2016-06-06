package fortscale.monitoring.external.stats.collector.impl.linux.parsers;

import fortscale.monitoring.external.stats.collector.impl.linux.parsers.exceptions.ProcFileBadFormatException;
import fortscale.monitoring.external.stats.collector.impl.linux.parsers.exceptions.ProcFileParserBadNumberFormatException;
import fortscale.monitoring.external.stats.collector.impl.linux.parsers.exceptions.ProcFileParserException;
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
public class LinuxProcFileKeyValueParser extends LinuxProcFileParser {

    private static Logger logger = Logger.getLogger(LinuxProcFileKeyValueParser.class);

    private Map<String, Long> data = new HashMap<>();

    public LinuxProcFileKeyValueParser(String procBasePath, String filename, String separator) {
        super(procBasePath, filename, separator);
        data = initData();
    }

    public Long getValue(String key){
        return data.get(key);
    }


    private Map initData() throws ProcFileParserException{

        List<String> lines = parseFileToLines();

        Map<String,Long> dataMap = new HashMap<>();

        long lineNumber = 0;
        for(String line: lines){

            //line should be in form of <key><separator><whitespace?><value>
            lineNumber++;
            String[] parsedString = line.split(String.format("\\s*%s\\s*",separator));
            if (parsedString.length != 2){
                String errorMessage = String.format("Error in reading line: %s in proc file: %s at line %d",
                                       line,filename, lineNumber);
                logger.error(errorMessage);
                throw new ProcFileBadFormatException(errorMessage);
            }

            // Convert the value to long
            Long value = convertToLong(parsedString[1]);

            // Add value to map
            dataMap.put(parsedString[0], value);
        }

        return dataMap;
    }

}
