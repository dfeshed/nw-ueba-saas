package fortscale.monitoring.external.stats.collector.parsers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


/**
 * utils class to parse text files, commonly in the context of proc files.
 * (proc files are generated files by unix which contains in-time data about the system)
 * Created by galiar on 14/04/2016.
 */
public class ExternalStatsProcFileParserUtils {

    private static Logger logger = LoggerFactory.getLogger(ExternalStatsProcFileParserUtils.class);

    /**
     * used to parse proc files from the format of list of lines, where each line consist of single pair of key,value
     * @param filename
     * @param separator
     * @return
     */
    public  Map parseFileAsMapOfSingleValue(String filename, String separator) throws ProcFileParserException{

        List<String> lines = parseFileToLines(filename);

        Map<String,String> dataMap = new HashMap<>();

        for(String line: lines){
            //line should be in form of <key><separator><whitespace?><value>
            String[] parsedString = line.split(String.format("\\s*%s\\s*",separator));
            if (parsedString.length != 2){
                logger.error("error in reading line: {} in proc file: {}. should be in format <key><whitespace?><separator><whitespace?><value>",line,filename);
                throw new ProcFileParserException(filename);
            }
            dataMap.put(parsedString[0],parsedString[1]);
        }
        return dataMap;
    }

    /**
     * used to parse proc files from the format of list of lines, where each line consist of pair of key, multiple values
     * @param filename
     * @param separator
     * @return
     */
    public  Map parseFileAsMapOfMultipleValues(String filename, String separator) throws ProcFileParserException{

        List<String> lines = parseFileToLines(filename);

        Map<String,ArrayList<String>> dataMap = new HashMap<>();
        for(String line: lines) {
            //line should be in form of <key><separator><whitespace?><value>
            String[] parsedString = line.split(String.format("\\s*%s\\s*", separator));
            if (parsedString.length <2){
                logger.error("error in reading line: {} in proc file: {}. maybe you used wrong separator '{}' ?",line, filename,separator);
                throw new ProcFileParserException(filename);
            }
            dataMap.put(parsedString[0], new ArrayList<>(Arrays.asList(Arrays.copyOfRange(parsedString,1,parsedString.length))));
        }
        return dataMap;

    }

    private  List<String> parseFileToLines(String filename) throws ProcFileParserException{

        List<String> lines = new ArrayList<>();
        //convert filename to file
        BufferedReader br;
        try {
             br = new BufferedReader(new FileReader(filename));
        }
        catch (FileNotFoundException e) {
            logger.error(" proc file {} cannot be generated! Exception is: {} ", filename , e);
            throw new ProcFileParserException(filename);
        }

        //convert file to list of lines
        String line = "";
        try {
             line  = br.readLine();
            while (line != null){
                lines.add(line);
                line = br.readLine();
            }
        }
        catch (IOException e){
            logger.error("unable to complete read of file {} , because of line: {}, due to IO Exception: {} ", filename,line,e);
            throw new ProcFileParserException(filename);
        }

        return lines;
    }
}
