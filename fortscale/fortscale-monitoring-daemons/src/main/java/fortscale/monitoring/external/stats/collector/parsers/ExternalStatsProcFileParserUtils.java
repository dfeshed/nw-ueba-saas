package fortscale.monitoring.external.stats.collector.parsers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
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
    public static Map parseFileAsMapOfSingleValue(String filename, String separator){

        //if lines is null or empty  - return null
        List<String> lines = parseFileToLines(filename);
        if(lines == null || lines.isEmpty()){
            logger.error("file doesn't exist or empty: {} ",filename);
            return null;
        }

        Map<String,String> dataMap = new HashMap<>();

        for(String line: lines){
            //line should be in form of <key><separator><whitespace?><value>
            String[] parsedString = line.split(String.format("\\s*%s\\s*",separator));
            if (parsedString.length != 2){
                logger.error("error in reading proc file: {}. should be in format <key><whitespace?><separator><whitespace?><value>",filename);
                return null;
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
    public static Map parseFileAsMapOfMultipleValues(String filename, String separator){

        //if lines is null or empty  - return null
        List<String> lines = parseFileToLines(filename);
        if(lines == null || lines.isEmpty()){
            logger.error("file doesn't exist or empty: {} ",filename);
            return null;
        }


        Map<String,ArrayList<String>> dataMap = new HashMap<>();
        for(String line: lines) {
            //line should be in form of <key><separator><whitespace?><value>
            String[] parsedString = line.split(String.format("\\s*%s\\s*", separator));
            if (parsedString.length <2){
                logger.error("error in reading proc file: {}. maybe you used wrong separator '[}' ?",filename,separator);
                return null;
            }
            dataMap.put(parsedString[0], new ArrayList<>(Arrays.asList(Arrays.copyOfRange(parsedString,1,parsedString.length))));
        }
        return dataMap;

    }

    private static List<String> parseFileToLines(String filename){

        List<String> lines = new ArrayList<>();
        //convert filename to file
        BufferedReader br;
        try {
             br = new BufferedReader(new FileReader(filename));
        }
        catch (FileNotFoundException e) {
            logger.error(" proc file {} cannot be generated! ", filename);
            return null;
        }

        //convert file to list of lines
        try {
            String line  = br.readLine();
            while (line != null){
                lines.add(line);
                line = br.readLine();
            }
        }
        catch (IOException e){
            logger.error(" unable to complete read of file {}, due to IO Exception: {} ", filename,e.getMessage());
            return null;
        }

        return lines;
    }
}
