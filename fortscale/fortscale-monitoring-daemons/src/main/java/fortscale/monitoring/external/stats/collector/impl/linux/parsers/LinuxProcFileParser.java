package fortscale.monitoring.external.stats.collector.impl.linux.parsers;
import fortscale.monitoring.external.stats.collector.impl.linux.parsers.exceptions.ProcFileParserBadNumberFormatException;
import fortscale.monitoring.external.stats.collector.impl.linux.parsers.exceptions.ProcFileReadLineFailureException;
import fortscale.monitoring.external.stats.collector.impl.linux.parsers.exceptions.ProcFileReadFailureException;
import fortscale.monitoring.external.stats.collector.impl.linux.parsers.exceptions.ProcFileParserException;
import fortscale.utils.logging.Logger;

import java.io.*;
import java.util.*;


/**
 * abstract parser for text files, commonly in the context of proc files.
 * (proc files are generated files by unix which contains in-time data about the system)
 * Created by galiar & gaashh on 14/04/2016.
 */
public abstract class LinuxProcFileParser {

    private static Logger logger = Logger.getLogger(LinuxProcFileParser.class);

    protected String filename;
    protected String separator;
    protected String name;

    public LinuxProcFileParser(String filename, String separator){
        this.filename  = filename;
        this.separator = separator;
    }

    protected List<String> parseFileToLines() {

        List<String> lines = new LinkedList<>();

        // Build buffer read for file
        BufferedReader br;
        try {
             br = new BufferedReader(new FileReader(filename));
        }
        catch (Exception e) {
            String errorMessage = String.format("Proc file %s read failure", filename);
            logger.error( errorMessage, e);
            throw new ProcFileReadFailureException(errorMessage, e);
        }

        // Read file into a list of lines
        long lineNumber = 0;
        try {
            String line;
            while (true) {
                lineNumber++;
                line  = br.readLine();
                if (line == null) {
                    break;
                }
                lines.add(line);
            }
        }
        catch (Exception e){
            String errorMessage = String.format("Failed to read from %s at line %d", filename, lineNumber);
            logger.error(errorMessage,e);
            throw new ProcFileReadLineFailureException(errorMessage,e);
        }
        finally {
            try {
                br.close();
            } catch (IOException e) {
                logger.error("failed to close buffer reader for file={}",filename,e);
            }
        }
        return lines;
    }

    protected long convertToLong(String text) {

        long value;

        String str = text.replaceAll("\\D",""); // remove all the non digits

        try {
            value = Long.parseLong(str);
            return value;
        }
        catch (Exception e){
            String errorMessage = String.format("Couldn't parse the string '%s' to valid number!. text='%s' filename=%s",
                                                 str, text, filename);
            logger.error(errorMessage, e);
            throw new ProcFileParserBadNumberFormatException(errorMessage, e);
        }

    }

}
