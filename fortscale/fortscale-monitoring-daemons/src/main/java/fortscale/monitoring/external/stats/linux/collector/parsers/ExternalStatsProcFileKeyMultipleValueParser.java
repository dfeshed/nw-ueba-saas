package fortscale.monitoring.external.stats.linux.collector.parsers;

import fortscale.monitoring.external.stats.linux.collector.parsers.exceptions.ProcFileBadFormatException;
import fortscale.monitoring.external.stats.linux.collector.parsers.exceptions.ProcFileBadNumberFormatException;
import fortscale.monitoring.external.stats.linux.collector.parsers.exceptions.ProcFileParserException;
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
public class ExternalStatsProcFileKeyMultipleValueParser extends ExternalStatsProcFileParser {

    private static Logger logger = Logger.getLogger(ExternalStatsProcFileKeyMultipleValueParser.class);
    private Map<String,ArrayList<Long>> data;

    public ExternalStatsProcFileKeyMultipleValueParser(String filename, String separator , String name, int indexOfKeyInLine) throws ProcFileParserException {
        this(filename, separator,name,indexOfKeyInLine,new ArrayList<>());
    }

    public ExternalStatsProcFileKeyMultipleValueParser(String filename, String separator , String name, int indexOfKeyInLine, List<Integer> indicesToIgnore) throws ProcFileParserException {

        super(filename, separator,name);
        data = initData(indexOfKeyInLine,indicesToIgnore);
    }

    /**
     * inits the data of parser from file. uses the element in index indexOfKeyInLine as key in the data map.
     * @param indexOfKeyInLine
     * @return
     * @throws ProcFileParserException
     */
    private Map initData(int indexOfKeyInLine,List<Integer> indicesToIgnore) throws ProcFileParserException{

        List<String> lines = parseFileToLines();

        Map<String,ArrayList<Long>> dataMap = new HashMap<>();
        for(String line: lines) {
            //line should be in form of <key><separator><whitespace?><value><whitespace?><value><whitespace?><value>....
            String[] parsedString = line.split(String.format("\\s*%s\\s*", separator));
            if (parsedString.length <2){
                String errorMessage = String.format("error in reading line: %s in proc file: %s. maybe you used wrong separator '%s' ?",line, filename,separator);
                logger.error(errorMessage);
                throw new ProcFileBadFormatException(errorMessage);
            }
            ArrayList<Long> longValues = new ArrayList<>();
            try {
                for (int i = 0; i < parsedString.length; i++) {
                    if(!indicesToIgnore.contains(i)) {
                        longValues.add(convertToLong(parsedString[i]));
                    }
                }
            }
            catch (ProcFileBadNumberFormatException e ){
                String errorMessage = String.format("error converting string '%s' to number in line: %s in proc file: %s.",e.getNumberTryingToConvert(),line,filename);
                logger.error(errorMessage);
                throw new ProcFileBadFormatException(errorMessage,e);
            }
            dataMap.put(parsedString[indexOfKeyInLine],longValues);
        }
        return dataMap;
    }

    public ArrayList<Long> getValue(String key){
        return data.get(key);
    }


}
