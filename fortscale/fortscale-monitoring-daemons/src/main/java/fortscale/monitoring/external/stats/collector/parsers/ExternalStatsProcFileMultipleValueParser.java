package fortscale.monitoring.external.stats.collector.parsers;

import fortscale.monitoring.external.stats.collector.parsers.exceptions.ProcFileBadFormatException;
import fortscale.monitoring.external.stats.collector.parsers.exceptions.ProcFileBadNumberFormatException;
import fortscale.monitoring.external.stats.collector.parsers.exceptions.ProcFileParserException;
import fortscale.utils.logging.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * used to parse proc files from the format of list of lines, where each line consist of pair of key, multiple values.
 * example input (out of /proc/stat): cpu  32439515 1137 10153191 264045277 88082 2474 961370 0 0
 *
 * Created by galiar on 18/04/2016.
 */
public class ExternalStatsProcFileMultipleValueParser extends ExternalStatsProcFileParser {

    private static Logger logger = Logger.getLogger(ExternalStatsProcFileMultipleValueParser.class);
    private Map<String,ArrayList<Long>> data;

    public ExternalStatsProcFileMultipleValueParser(String filename, String separator) throws ProcFileParserException {
        super(filename, separator);
        data = initData();
    }

    private Map initData() throws ProcFileParserException{

        List<String> lines = parseFileToLines();

        Map<String,ArrayList<Long>> dataMap = new HashMap<>();
        for(String line: lines) {
            //line should be in form of <key><separator><whitespace?><value>
            String[] parsedString = line.split(String.format("\\s*%s\\s*", separator));
            if (parsedString.length <2){
                String errorMessage = String.format("error in reading line: {} in proc file: {}. maybe you used wrong separator '{}' ?",line, filename,separator);
                logger.error(errorMessage);
                throw new ProcFileBadFormatException(errorMessage);
            }
            ArrayList<Long> longValues = new ArrayList<>();
            try {
                for (int i = 1; i < parsedString.length; i++) {
                    longValues.add(convertToLong(parsedString[i]));
                }
            }
            catch (ProcFileBadNumberFormatException e ){
                String errorMessage = String.format("error converting string '{}' to number in line: {} in proc file: {}.",e.getNumberTryingToConvert(),line,filename);
                throw new ProcFileBadFormatException(errorMessage,e);
            }
            dataMap.put(parsedString[0],longValues);
        }
        return dataMap;
    }
}
