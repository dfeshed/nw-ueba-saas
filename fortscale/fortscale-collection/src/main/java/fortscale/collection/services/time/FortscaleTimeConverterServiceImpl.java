package fortscale.collection.services.time;

import fortscale.utils.time.TimestampUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author gils
 * 28/02/2016
 */
public class FortscaleTimeConverterServiceImpl implements FortscaleTimeConverterService {

    private static final String UTC_TIME_ZONE = "UTC";

    private static final String UNIX_TIME_IN_SECONDS = "unixTimeInSeconds";
    private static final String UNIX_TIME_IN_MILLIS = "unixTimeInMillis";

    private static final SimpleDateFormat UNIX_TIME_IN_MILLIS_DATE_FORMAT = new SimpleDateFormat("'unixTimeInMillis'");
    private static final SimpleDateFormat UNIX_TIME_IN_SECONDS_DATE_FORMAT = new SimpleDateFormat("'unixTimeInSeconds'");

    private static final Date DEFAULT_TWO_DIGIT_YEAR_START;
    private static final Locale DEFAULT_LOCALE = getLocale("");

    private static FortscaleTimeConverterServiceImpl instance = null;

    static {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.ROOT);
        calendar.set(2000, Calendar.JANUARY, 1, 0, 0);
        DEFAULT_TWO_DIGIT_YEAR_START = calendar.getTime();
    }

    private FortscaleTimeConverterServiceImpl() {
    }

    public static FortscaleTimeConverterServiceImpl getInstance() {
        if (instance == null) {
            instance = new FortscaleTimeConverterServiceImpl();
        }

        return instance;
    }

    @Override
    public String convertTimestamp(String timestamp, List<String> optionalInputFormats, String tzInput, String outputFormatStr, String tzOutput) {
        TimeZone inputTimezone = getTimeZone(tzInput == null ? UTC_TIME_ZONE : tzInput);
        TimeZone outputTimezone = getTimeZone(tzOutput == null ? UTC_TIME_ZONE : tzOutput);

        SimpleDateFormat outputFormat = createDateFormat(outputFormatStr, outputTimezone);

        for (String inputFormatStr : optionalInputFormats) {
            DateTime date = null;
            SimpleDateFormat inputFormat = createDateFormat(inputFormatStr, inputTimezone);
            if (inputFormat == UNIX_TIME_IN_MILLIS_DATE_FORMAT || inputFormat == UNIX_TIME_IN_SECONDS_DATE_FORMAT) {
                date = parseUnixTime(timestamp, DateTimeZone.forTimeZone(inputTimezone));
            }
            else {
                Date parsedDate;
                try {
                    parsedDate = inputFormat.parse(timestamp);
                } catch (ParseException e) {
                    continue;
                }
                if (parsedDate != null) {
                    return formatDate(parsedDate, outputFormat);
                }
            }
            if (date != null) {
                return formatDateUnix(date, outputFormat);
            }
        }

        return null;
    }

    @Override
    public String convertTimestamp(String timestamp, String tzInput, String outputFormatStr, String tzOutput) {
        return convertTimestamp(timestamp, FortscaleTimeFormats.getAvailableInputFormats(), tzInput, outputFormatStr, tzOutput);
    }

    private String formatDate(Date parsedDate, SimpleDateFormat outputFormat) {
        String fortscaleTimeFormat;
        long dateTimestampInMillis = parsedDate.getTime();

        String result;
        if (outputFormat.equals(UNIX_TIME_IN_MILLIS_DATE_FORMAT)) {
            result = String.valueOf(dateTimestampInMillis);
        } else if (outputFormat.equals(UNIX_TIME_IN_SECONDS_DATE_FORMAT)) {
            result = String.valueOf(dateTimestampInMillis / 1000);
        } else {
            result = outputFormat.format(dateTimestampInMillis);
        }

        fortscaleTimeFormat = result;
        return fortscaleTimeFormat;
    }

    private String formatDateUnix(DateTime date, SimpleDateFormat outputFormat) {
        String result;
        if (outputFormat.equals(UNIX_TIME_IN_MILLIS_DATE_FORMAT)) {
            result = String.valueOf(date.getMillis());
        } else if (outputFormat.equals(UNIX_TIME_IN_SECONDS_DATE_FORMAT)) {
            result = String.valueOf(date.getMillis() / 1000);
        } else {
            result = outputFormat.format(date.toDate());
        }

        return result;
    }

    @Override
    public String convertTimestamp(String timestamp, String outputFormatStr, String outputTimezone) {
        return convertTimestamp(timestamp, null, outputFormatStr, outputTimezone);
    }

    private static SimpleDateFormat createDateFormat(String formatStr, TimeZone timeZone) {
        validateTimeZone(formatStr, timeZone);

        if (UNIX_TIME_IN_SECONDS.equals(formatStr)) {
            return UNIX_TIME_IN_SECONDS_DATE_FORMAT;
        }
        else if (UNIX_TIME_IN_MILLIS.equals(formatStr)){
            return UNIX_TIME_IN_MILLIS_DATE_FORMAT;
        }
        else {
            SimpleDateFormat dateFormat = new SimpleDateFormat(formatStr, DEFAULT_LOCALE);

            dateFormat.setTimeZone(timeZone);

            dateFormat.set2DigitYearStart(DEFAULT_TWO_DIGIT_YEAR_START);

            // TODO workaround
            if (!formatStr.equals("yyyy-MM-dd'T'HH:mm:ss.SSS")) {
                dateFormat.setLenient(false);
            }

            return dateFormat;
        }
    }

    private static void validateTimeZone(String formatStr, TimeZone timeZone) {
        if (formatStr.equals(UNIX_TIME_IN_MILLIS) || formatStr.equals(UNIX_TIME_IN_SECONDS)) {
            if (!UTC_TIME_ZONE.equals(timeZone.getID())) {
                throw new IllegalStateException("TimeZone must be UTC for date formatStr 'unixTimeInMillis' or 'unixTimeInSeconds'");
            }
        }
    }

    // work around the fact that SimpleDateFormat doesn't understand Unix time format
    private static DateTime parseUnixTime(String timestamp, DateTimeZone tz) {
        try {
            return new DateTime(TimestampUtils.convertToMilliSeconds(Long.parseLong(timestamp)), tz);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Locale getLocale(String name) {
        if (name.equals(Locale.ROOT.toString())) {
            return Locale.ROOT;
        } else {
            for (Locale locale : Locale.getAvailableLocales()) {
                if (locale.toString().equals(name)) {
                    return locale;
                }
            }
        }

        return null;
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
}
