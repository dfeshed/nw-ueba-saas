package fortscale.collection.services.time;

import fortscale.domain.core.ApplicationConfiguration;
import fortscale.services.ApplicationConfigurationService;
import fortscale.utils.time.TimestampUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author gils
 * 03/03/2016
 */
@Component
public class FortscaleDateFormatServiceImpl implements FortscaleDateFormatService, InitializingBean{

    private static Logger logger = LoggerFactory.getLogger(FortscaleDateFormatServiceImpl.class);

    private static final String DATE_FORMATS_KEY = "fortscale.date.formats";

    private static final String DATE_FORMAT_DELIMITER = "###";

    private static final int MILLIS_IN_SECOND = 1000;

    private static final String UTC_TIME_ZONE = "UTC";

    private static final String UNIX_TIME_IN_SECONDS = "unixTimeInSeconds";
    private static final String UNIX_TIME_IN_MILLIS = "unixTimeInMillis";

    private static final SimpleDateFormat UNIX_TIME_IN_MILLIS_DATE_FORMAT = new SimpleDateFormat("'unixTimeInMillis'");
    private static final SimpleDateFormat UNIX_TIME_IN_SECONDS_DATE_FORMAT = new SimpleDateFormat("'unixTimeInSeconds'");

    private static final Date DEFAULT_TWO_DIGIT_YEAR_START;

    private List<String> availableDateFormats = FortscaleDateTimeFormats.getAvailableInputFormats();

//    @Autowired
    ApplicationConfigurationService applicationConfigurationService;

    static {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(UTC_TIME_ZONE), Locale.ROOT);
        calendar.set(2000, Calendar.JANUARY, 1, 0, 0);
        DEFAULT_TWO_DIGIT_YEAR_START = calendar.getTime();
    }

    @Override
    public List<String> findDateTimestampPatternMatches(String dateTimestamp, String tzInput) {
        TimeZone inputTimezone = getTimeZone(tzInput == null ? UTC_TIME_ZONE : tzInput);

        List<String> matchedInputFormats = new ArrayList<>();

        for (String inputFormatStr : availableDateFormats) {
            DateTime dateTime;
            SimpleDateFormat inputFormat = createDateFormat(inputFormatStr, inputTimezone, false);
            if (isEpochTimeFormat(inputFormatStr)) {
                try {
                    dateTime = parseEpochTime(dateTimestamp, DateTimeZone.forTimeZone(inputTimezone));
                } catch (FortscaleDateFormatterException e) {
                    continue;
                }

                if (dateTime != null) {
                    matchedInputFormats.add(inputFormatStr);
                }
            }
            else {
                Date parsedDate;
                try {
                    parsedDate = inputFormat.parse(dateTimestamp);
                } catch (ParseException e) {
                    continue; // i.e. iterate to the next possible input format
                }
                if (parsedDate != null) {
                    matchedInputFormats.add(inputFormatStr);
                }
            }
        }

        return matchedInputFormats;
    }

    @Override
    public String formatDateTimestamp(String dateTimestamp, List<String> optionalInputFormats, String tzInput, String outputFormatStr, String tzOutput) throws FortscaleDateFormatterException {
        return formatDateTimestamp(dateTimestamp, optionalInputFormats, tzInput, outputFormatStr, tzOutput, false);
    }

    private String formatDateTimestamp(String dateTimestamp, List<String> optionalInputFormats, String tzInput, String outputFormatStr, String tzOutput, boolean autoDetectPatternMode) throws FortscaleDateFormatterException {
        TimeZone inputTimezone = getTimeZone(tzInput == null ? UTC_TIME_ZONE : tzInput);
        TimeZone outputTimezone = getTimeZone(tzOutput == null ? UTC_TIME_ZONE : tzOutput);

        SimpleDateFormat outputFormat = createDateFormat(outputFormatStr, outputTimezone, false);

        for (String inputFormatStr : optionalInputFormats) {
            DateTime dateTime;

            // in case of pattern auto-detection we must set the parser to be non-lenient
            SimpleDateFormat inputFormat = createDateFormat(inputFormatStr, inputTimezone, autoDetectPatternMode);

            if (isEpochTimeFormat(inputFormatStr)) {
                dateTime = parseEpochTime(dateTimestamp, DateTimeZone.forTimeZone(inputTimezone));

                if (dateTime != null) {
                    String formattedDateTimestamp = formatDate(dateTime, outputFormat);

                    logTimestampConversion(dateTimestamp, formattedDateTimestamp, inputFormatStr);

                    return formattedDateTimestamp;
                }
                else {
                    throw new FortscaleDateFormatterException("Could not parse epoch time with value: " + dateTimestamp);
                }
            }
            else {
                Date parsedDate;
                try {
                    parsedDate = inputFormat.parse(dateTimestamp);
                } catch (ParseException e) {
                    continue; // i.e. iterate to the next possible input format
                }
                if (parsedDate != null) {
                    dateTime = new DateTime(parsedDate);

                    String formattedDateTimestamp = formatDate(dateTime, outputFormat);

                    logTimestampConversion(dateTimestamp, formattedDateTimestamp, inputFormatStr);

                    return formattedDateTimestamp;
                }
            }
        }

        throw new FortscaleDateFormatterException("Could not found pattern match for date timestamp: " + dateTimestamp);
    }

    private static boolean isEpochTimeFormat(String inputFormatStr) {
        return inputFormatStr.equals(UNIX_TIME_IN_MILLIS) || inputFormatStr.equals(UNIX_TIME_IN_SECONDS);
    }

    private void logTimestampConversion(String timestamp, String convertedTimestamp, String inputFormatStr) {
        if (logger.isDebugEnabled()) {
            logger.info("Timestamp converted: " + timestamp + " ==> " + convertedTimestamp + ". Input matched pattern: " + inputFormatStr);
        }
    }

    @Override
    public String formatDateTimestamp(String dateTimestamp, String tzInput, String outputFormatStr, String tzOutput) throws FortscaleDateFormatterException {
        return formatDateTimestamp(dateTimestamp, availableDateFormats, tzInput, outputFormatStr, tzOutput, true);
    }

    private String formatDate(DateTime date, SimpleDateFormat outputFormat) {
        String result;
        if (outputFormat.equals(UNIX_TIME_IN_MILLIS_DATE_FORMAT)) {
            result = String.valueOf(date.getMillis());
        } else if (outputFormat.equals(UNIX_TIME_IN_SECONDS_DATE_FORMAT)) {
            result = String.valueOf(date.getMillis() / MILLIS_IN_SECOND);
        } else {
            result = outputFormat.format(date.toDate());
        }

        return result;
    }

    private static SimpleDateFormat createDateFormat(String formatStr, TimeZone timeZone, boolean isStrictParsing) {
        validateTimeZone(formatStr, timeZone);

        if (UNIX_TIME_IN_SECONDS.equals(formatStr)) {
            return UNIX_TIME_IN_SECONDS_DATE_FORMAT;
        }
        else if (UNIX_TIME_IN_MILLIS.equals(formatStr)){
            return UNIX_TIME_IN_MILLIS_DATE_FORMAT;
        }
        else {
            SimpleDateFormat dateFormat = new SimpleDateFormat(formatStr, Locale.ROOT);

            dateFormat.setTimeZone(timeZone);

            dateFormat.set2DigitYearStart(DEFAULT_TWO_DIGIT_YEAR_START);

            dateFormat.setLenient(!isStrictParsing);

            return dateFormat;
        }
    }

    private static void validateTimeZone(String formatStr, TimeZone timeZone) {
        if (isEpochTimeFormat(formatStr)) {
            if (!UTC_TIME_ZONE.equals(timeZone.getID())) {
                throw new IllegalStateException("TimeZone must be UTC for date formatStr 'unixTimeInMillis' or 'unixTimeInSeconds'");
            }
        }
    }

    // work around the fact that SimpleDateFormat doesn't understand Unix time format
    private static DateTime parseEpochTime(String timestamp, DateTimeZone timeZone) throws FortscaleDateFormatterException {
        try {
            return new DateTime(TimestampUtils.convertToMilliSeconds(Long.parseLong(timestamp)), timeZone);
        } catch (Exception e) {
            throw new FortscaleDateFormatterException("Could not parse epoch time with value: " + timestamp, e);
        }
    }

    private TimeZone getTimeZone(String timeZoneID) {
        TimeZone zone = TimeZone.getTimeZone(timeZoneID);
        // check if the zone is GMT and the timeZoneID is not GMT than it means that the
        // TimeZone.getTimeZone did not recieve a valid id
        if (!zone.getID().equalsIgnoreCase(timeZoneID)) {
            throw new IllegalStateException("Unknown timezone: " + timeZoneID);
        } else {
            return zone;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        List<String> availableInputFormats = FortscaleDateTimeFormats.getAvailableInputFormats();

        if (applicationConfigurationService != null) {
            ApplicationConfiguration dateFormatsAppConfig = applicationConfigurationService.getApplicationConfigurationByKey(DATE_FORMATS_KEY);

            if (dateFormatsAppConfig != null && !StringUtils.isEmpty(dateFormatsAppConfig.getValue())) {
                // date formats record already exist in DB ==> populate the date formats list
                String dateFormatsStr = dateFormatsAppConfig.getValue();

                String[] dateFormatsArr = StringUtils.split(dateFormatsStr, DATE_FORMAT_DELIMITER);

                availableDateFormats = Arrays.asList(dateFormatsArr);
            } else {
                // persist the date formats in DB
                applicationConfigurationService.insertConfigItem(DATE_FORMATS_KEY, StringUtils.join(availableInputFormats, DATE_FORMAT_DELIMITER));
            }
        }
        else {
            availableDateFormats = availableInputFormats;
        }
    }
}
