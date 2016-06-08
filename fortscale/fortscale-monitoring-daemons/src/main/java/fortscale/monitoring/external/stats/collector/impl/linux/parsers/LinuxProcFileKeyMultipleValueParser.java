package fortscale.monitoring.external.stats.collector.impl.linux.parsers;

import fortscale.monitoring.external.stats.collector.impl.linux.parsers.exceptions.ProcFileBadFormatException;
import fortscale.monitoring.external.stats.collector.impl.linux.parsers.exceptions.ProcFileParserBadNumberFormatException;
import fortscale.monitoring.external.stats.collector.impl.linux.parsers.exceptions.ProcFileParserException;
import fortscale.utils.logging.Logger;

import java.util.*;

/**
 * used to parse proc files from the format of list of lines, where each line consist of pair of key, multiple values.
 * example input (out of /proc/stat): cpu  32439515 1137 10153191 264045277 88082 2474 961370 0 0
 *
 * Created by galiar & gaashh on 18/04/2016.
 */
public class LinuxProcFileKeyMultipleValueParser extends LinuxProcFileParser {

    private static Logger logger = Logger.getLogger(LinuxProcFileKeyMultipleValueParser.class);

    final static protected String DUMMY_KEY = "DUMMY-KEY";

    // data map: line key -> list of fields (strings)
    private Map<String,List<String>> data;

    /**
     *
     * init the parser from the file.
     *
     * This ctor supports only one line files, without line keys
     *
     * @param filename
     * @param separator
     */
    public LinuxProcFileKeyMultipleValueParser(String filename, String separator) {
        super(filename, separator);
        int keyIndex = -1;
        initData(keyIndex);
    }

    /**
     *
     * init the parser from the file.
     *
     * This ctor supports multiple line files having with line keys
     *
     * @param filename
     * @param separator
     * @param keyIndex
     */
    public LinuxProcFileKeyMultipleValueParser(String filename, String separator, int keyIndex) {
        super(filename, separator);
        initData(keyIndex);
    }

    /**
     * inits the data of parser from file. uses the element in index indexOfKeyInLine as key in the data map.
     *
     * @param keyIndex
     * @return
     */
    private void initData(int keyIndex) {

        // Read the file into list of line
        List<String> lines = parseFileToLines();

        // Create an empty map
        data = new HashMap<>();

        // Process all the lines
        long lineNumber = 0;
        for(String line: lines) {

            lineNumber++;

            // Split the line into keys and values
            String[] fields = line.split(String.format("\\s*%s\\s*", separator));

            // Get the key if we have index for it, other wise use dummy key
            String key;
            if (keyIndex >= 0) {
                // Verify we have the key
                if (keyIndex >= fields.length) {
                    String errorMessage = String.format("Error parsing %s:%s  - key is missing. fields=%d line='%s' ?",
                            filename, lineNumber, fields.length, line);
                    logger.error(errorMessage);
                    throw new ProcFileBadFormatException(errorMessage);
                }

                // Get the line key
                key = fields[keyIndex];
            }
            else {
                key = DUMMY_KEY;
            }

            // Create fields list
            List<String> fieldsAsList = Arrays.asList(fields);

            // Add line to data
            data.put(key, fieldsAsList);
        }
    }

    /**
     * @return a set of all the keys parsed
     */
    public Set<String> getKeys() {

        Set<String> keys = data.keySet();

        return keys;
    }

    /**
     *
     * Get a specific field by index from a line containing the 'key' as string
     *
     * Use with ctor having keyIndex
     *
     * @param key
     * @param fieldIndex
     * @return
     */
    public String getStringValue(String key, int fieldIndex) {

        // Get the fields
        List<String> fields;
        fields = data.get(key);
        if (fields == null) {
            String errorMessage = String.format("Error parsing %s - key is missing. key=%s fieldIndex=%d",
                                                filename, key, fieldIndex);
            logger.error(errorMessage);
            throw new ProcFileBadFormatException(errorMessage);
        }

        // Get the field value as string
        if ( fieldIndex < fields.size()) {
            String value = fields.get(fieldIndex);
            return value;
        }
        else {
            String errorMessage = String.format("Error parsing %s - field is missing. key=%s fieldIndex=%d fields.size=%d",
                    filename, key, fieldIndex, fields.size());
            logger.error(errorMessage);
            throw new ProcFileBadFormatException(errorMessage);
        }

    }

    /**
     *
     * Get a specific field by index from a line containing the 'key' as long
     *
     * Use with ctor having keyIndex
     *
     * @param key
     * @param fieldIndex
     * @return
     */
    public long getLongValue(String key, int fieldIndex) {

        // Get the field as string
        String valueAsString = getStringValue(key, fieldIndex);

        // Convert field to long
        try {
            long value = convertToLong(valueAsString);
            return value;
        }
        catch (Exception e) {
            String errorMessage = String.format("Error parsing %s - field '%s' is not a number. key=%s fieldIndex=%d",
                                                 filename, valueAsString, key, fieldIndex);
            logger.error(errorMessage, e);
            throw new ProcFileBadFormatException(errorMessage);
        }

    }

    /**
     *
     * Get a specific field by index from a line as String
     *
     * Use with ctor having not keyIndex
     *
     * @param fieldIndex
     * @return
     */

    public String getStringValue(int fieldIndex) {
        String key = DUMMY_KEY;
        return getStringValue(key, fieldIndex);
    }

    /**
     *
     * Get a specific field by index from a line as long
     *
     * Use with ctor having not keyIndex
     *
     * @param fieldIndex
     * @return
     */

    public long getLongValue(int fieldIndex) {
        String key = DUMMY_KEY;
        return getLongValue(key, fieldIndex);
    }


}
