package fortscale.collection.services.time;

import java.util.List;

/**
 * Service to provide date formatting & pattern resolving functionality
 *
 * @author gils
 * 02/03/2016
 */
public interface FortscaleDateFormatService {

    /**
     * Formats a date timestamp by auto-detecting the timestamp format
     *
     * @param dateTimestamp the date timestamp
     * @param inputTimezone the input time zone
     * @param outputFormatStr the output format string
     * @param outputTimezone the output timezone
     *
     * @return the formatted date timestamp
     */
    String formatDateTimestamp(String dateTimestamp, String inputTimezone, String outputFormatStr, String outputTimezone)throws FortscaleDateFormatterException;


    /**
     * Formats a date timestamp with a given timezone and optional input formats to a date timestamp with a given output format and timezone
     *
     * @param dateTimestamp the date timestamp
     * @param optionalInputFormats optional input formats
     * @param outputFormatStr the output format string
     * @param outputTimezone the output timezone
     *
     *
     * @return the formatted date timestamp
     */
    String formatDateTimestamp(String dateTimestamp, List<String> optionalInputFormats, String inputTimezone, String outputFormatStr, String outputTimezone) throws FortscaleDateFormatterException;

    /**
     * Find all date patterns matching to the input date timestamp
     *
     * @param dateTimestamp the date timestamp
     * @param tzInput the time zone input
     *
     * @return list of date patterns which matches the date timestamp input
     */
    List<String> findDateTimestampPatternMatches(String dateTimestamp, String tzInput);
}
