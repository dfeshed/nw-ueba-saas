package fortscale.monitoring.external.stats.collector.impl.linux.parsers;

import fortscale.monitoring.external.stats.collector.impl.linux.parsers.exceptions.ProcFileReadFailureException;
import fortscale.monitoring.external.stats.collector.impl.linux.parsers.exceptions.ProcFileParserException;
import fortscale.utils.logging.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * parse proc files which contain only single value, means - no key.
 * example: /proc/pid/cmdline
 * Created by galiar on 27/04/2016.
 */
public class LinuxProcFileSingleValueParser { //extends LinuxProcFileParser {

//    private static Logger logger = Logger.getLogger(LinuxProcFileSingleValueParser.class);
//
//    private String data;
//    private String oldSeparator;
//
//    public LinuxProcFileSingleValueParser(String filename, String  oldSeparator, String  newSeparator, String name)throws ProcFileParserException {
//        super(filename, newSeparator, name);
//        this.oldSeparator = oldSeparator;
//        data = initData();
//    }
//
//    private String initData() throws ProcFileReadFailureException {
//        //convert filename to file
//        String tempData;
//        // in order to deal with some weird separators, such as '\0', we read the file as is, and manually replace the separators
//        //currently, it doesn't matter, since it's very difficult reading null chars. maybe in the future...
//        try {
//            byte[] encoded = Files.readAllBytes(Paths.get(filename));
//            tempData =  new String(encoded);
//        }
//        catch ( IOException e) {
//            String errorMessage = String.format(" proc file %s cannot be generated! ", filename);
//            logger.error( errorMessage, e);
//            throw new ProcFileReadFailureException(errorMessage,e);
//        }
//
//        tempData.replace(oldSeparator,separator);
//        return tempData;
//    }
//
//    public String getData() {
//        return data;
//    }
//
}
