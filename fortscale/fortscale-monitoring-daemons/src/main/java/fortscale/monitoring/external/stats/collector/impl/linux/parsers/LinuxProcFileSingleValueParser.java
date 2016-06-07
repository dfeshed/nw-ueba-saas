package fortscale.monitoring.external.stats.collector.impl.linux.parsers;

import fortscale.monitoring.external.stats.collector.impl.linux.parsers.exceptions.ProcFileReadFailureException;
import fortscale.monitoring.external.stats.collector.impl.linux.parsers.exceptions.ProcFileParserException;
import fortscale.utils.logging.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

/**
 * parse proc files which contain only single value, means - no key.
 * example: /proc/pid/cmdline
 * Created by galiar on 27/04/2016.
 */
public class LinuxProcFileSingleValueParser extends LinuxProcFileParser {

    private static Logger logger = Logger.getLogger(LinuxProcFileSingleValueParser.class);

    private String data;

    public LinuxProcFileSingleValueParser(String filename) {
        super(filename, "not-used");

        // Read the lines
        List<String> lines = parseFileToLines();

        // Get the first line
        String line = lines.get(0);

        // Save the line
        data = line;
    }

    public String getData() {
        return data;
    }

}
