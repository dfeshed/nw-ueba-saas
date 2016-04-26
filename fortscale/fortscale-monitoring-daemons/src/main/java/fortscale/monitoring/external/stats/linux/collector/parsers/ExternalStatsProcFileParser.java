package fortscale.monitoring.external.stats.linux.collector.parsers;
import fortscale.monitoring.external.stats.linux.collector.parsers.exceptions.ProcFileBadNumberFormatException;
import fortscale.monitoring.external.stats.linux.collector.parsers.exceptions.ProcFileBadReadingException;
import fortscale.monitoring.external.stats.linux.collector.parsers.exceptions.ProcFileNotGeneratedException;
import fortscale.monitoring.external.stats.linux.collector.parsers.exceptions.ProcFileParserException;
import fortscale.utils.logging.Logger;

import java.io.*;
import java.util.*;


/**
 * abstract parser for text files, commonly in the context of proc files.
 * (proc files are generated files by unix which contains in-time data about the system)
 * Created by galiar on 14/04/2016.
 */
public abstract class ExternalStatsProcFileParser {

    private static Logger logger = Logger.getLogger(ExternalStatsProcFileParser.class);

    protected String filename;
    protected String separator;
    protected String name;

    public ExternalStatsProcFileParser(String filename, String separator, String name){
        this.filename = filename;
        this.separator = separator;
        this.name =  name;
    }

    public String getFilename() {
        return filename;
    }

    public String getName() {
        return name;
    }


    protected List<String> parseFileToLines() throws ProcFileParserException{

        List<String> lines = new ArrayList<>();
        //convert filename to file
        BufferedReader br;
        try {
             br = new BufferedReader(new FileReader(filename));
        }
        catch (FileNotFoundException e) {
            String errorMessage = String.format(" proc file {} cannot be generated! ", filename);
            logger.error( errorMessage, e);
            throw new ProcFileNotGeneratedException(errorMessage,e);
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
            String errorMessage = String.format("unable to complete read of file {} ", filename);
            logger.error(errorMessage,e);
            throw new ProcFileBadReadingException(errorMessage,e);
        }
        return lines;
    }

    protected Long convertToLong(String str) throws ProcFileParserException{
        Long longValue;
        str = str.replaceAll("\\D",""); // remove all the non digits

        try {
            longValue = Long.parseLong(str);
        }
        catch (NumberFormatException e){
            String errorMessage = String.format("Couldn't parse the string {} to valid number!",str);
            logger.error(errorMessage);
            throw new ProcFileBadNumberFormatException(errorMessage,e,str);
        }
        return longValue;
    }
}
