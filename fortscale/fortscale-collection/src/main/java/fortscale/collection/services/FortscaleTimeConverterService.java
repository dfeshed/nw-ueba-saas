package fortscale.collection.services;

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
public class FortscaleTimeConverterService {

    private static final String UTC_TIME_ZONE = "UTC";

    private static final SimpleDateFormat UNIX_TIME_IN_MILLIS_DATE_FORMAT = new SimpleDateFormat("'unixTimeInMillis'");
    private static final SimpleDateFormat UNIX_TIME_IN_SECONDS_DATE_FORMAT = new SimpleDateFormat("'unixTimeInSeconds'");

    private static final Date DEFAULT_TWO_DIGIT_YEAR_START;

    private static final String UNIX_TIME_IN_SECONDS = "unixTimeInSeconds";

    private static final String UNIX_TIME_IN_MILLIS = "unixTimeInMillis";

    private static LinkedList<String> availableInputFormatList = new LinkedList<>();

    static {
        // a suite of default date formats
        availableInputFormatList.add("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        availableInputFormatList.add("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        availableInputFormatList.add("yyyyMMddHHmmss'.0Z'");
        availableInputFormatList.add("MM/dd/yyyy HH:mm:ss z");
        availableInputFormatList.add("MM/dd/yyyy HH:mm:ss");
        availableInputFormatList.add("yyyy/MM/dd HH:mm:ss");
        availableInputFormatList.add("EEE MMM d HH:mm:ss z yyyy");
        availableInputFormatList.add("yyyy-MM-dd'T'HH:mm:ssXXX");
        availableInputFormatList.add("MMM dd yyyy HH:mm:ss");
        availableInputFormatList.add("MM/dd/yy HH:mm:ss");
        availableInputFormatList.add("MMM  dd HH:mm:ss yyyy");
        availableInputFormatList.add("MMM dd HH:mm:ss yyyy");
        availableInputFormatList.add("yyyy MMM  dd HH:mm:ss");
        availableInputFormatList.add("MM/dd/yyyy:HH:mm:ss");
        availableInputFormatList.add("MMM dd yyyy  HH:mm:ss");
        availableInputFormatList.add("yyyy-MM-dd'T'HH:mm:ss.SSS");
        availableInputFormatList.add("yyyy-MM-dd'T'HH:mm:ss");
        availableInputFormatList.add("MM/dd/yyyy h:mm a");
        availableInputFormatList.add("MM/d/yyyy H:mm");
        availableInputFormatList.add("yyyy-MM-dd HH:mm:ss");
        availableInputFormatList.add("yyyy MMM dd HH:mm:ss");
        availableInputFormatList.add("EEE MMM dd HH:mm:ss yyyy");
        availableInputFormatList.add("MM/d/yyyy h:mm a");
        availableInputFormatList.add("yyyy MMM d HH:mm:ss");
        availableInputFormatList.add(UNIX_TIME_IN_SECONDS);
        availableInputFormatList.add(UNIX_TIME_IN_MILLIS);

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.ROOT);
        calendar.set(2000, Calendar.JANUARY, 1, 0, 0);
        DEFAULT_TWO_DIGIT_YEAR_START = calendar.getTime();
    }

    public static String convertTimestampToFortscaleFormat(String timestampToConvert, List<String> inputFormats, TimeZone inputTimezone, String outputFormatStr, TimeZone outputTimezone) {
        SimpleDateFormat outputFormat = createDateFormat(outputFormatStr, outputTimezone, getLocale(""));

        String fortscaleTimeFormat = null;

        for (String inputFormatStr : inputFormats) {
            DateTime date = null;
            SimpleDateFormat inputFormat = createDateFormat(inputFormatStr, inputTimezone, getLocale(""));
            if (inputFormat == UNIX_TIME_IN_MILLIS_DATE_FORMAT || inputFormat == UNIX_TIME_IN_SECONDS_DATE_FORMAT) {
                date = parseUnixTime(timestampToConvert, DateTimeZone.forTimeZone(inputTimezone));
            } else {
                Date parsed;
                try {
                    parsed = inputFormat.parse(timestampToConvert);
                } catch (ParseException e) {
                    continue;
                }
                if (parsed != null) {
                    date = new DateTime(parsed);
                }
            }
            if (date != null) {
                // change the time zone to the output time zone
                date = date.withZone(DateTimeZone.forTimeZone(inputTimezone));
                String result;
                if (outputFormat.equals(UNIX_TIME_IN_MILLIS_DATE_FORMAT)) {
                    result = String.valueOf(date.getMillis());
                } else if (outputFormat.equals(UNIX_TIME_IN_SECONDS_DATE_FORMAT)) {
                    result = String.valueOf(date.getMillis() / 1000);
                } else {
                    result = outputFormat.format(date.toDate());
                }

                fortscaleTimeFormat = result;

                break;
            }
        }

        return fortscaleTimeFormat;
    }

    public static String convertTimestampToFortscaleFormat(String timestampToConvert, TimeZone inputTimezone, String outputFormatStr, TimeZone outputTimezone) {

        SimpleDateFormat outputFormat = createDateFormat(outputFormatStr, outputTimezone, getLocale(""));

        String matchedInputFormatStr = null;

        String fortscaleTimeFormat = null;

        for (String inputFormatStr : availableInputFormatList) {
            DateTime date = null;
            SimpleDateFormat inputFormat = createDateFormat(inputFormatStr, inputTimezone, getLocale(""));
            if (inputFormat == UNIX_TIME_IN_MILLIS_DATE_FORMAT || inputFormat == UNIX_TIME_IN_SECONDS_DATE_FORMAT) {
                date = parseUnixTime(timestampToConvert, DateTimeZone.forTimeZone(inputTimezone));
            } else {
                Date parsed;
                try {
                    parsed = inputFormat.parse(timestampToConvert);
                } catch (ParseException e) {
                    continue;
                }
                if (parsed != null) {
                    date = new DateTime(parsed);
                }
            }
            if (date != null) {
                // change the time zone to the output time zone
                date = date.withZone(DateTimeZone.forTimeZone(inputTimezone));
                String result;
                if (outputFormat.equals(UNIX_TIME_IN_MILLIS_DATE_FORMAT)) {
                    result = String.valueOf(date.getMillis());
                } else if (outputFormat.equals(UNIX_TIME_IN_SECONDS_DATE_FORMAT)) {
                    result = String.valueOf(date.getMillis() / 1000);
                } else {
                    result = outputFormat.format(date.toDate());
                }

                matchedInputFormatStr = inputFormatStr;
                fortscaleTimeFormat = result;

                break;
            }
        }

        if (matchedInputFormatStr != null && !availableInputFormatList.getFirst().equals(matchedInputFormatStr)) {
            pushInputFormatTohead(matchedInputFormatStr);
        }

        return fortscaleTimeFormat;
    }

    private static void pushInputFormatTohead(String matchedInputFormat) {
        availableInputFormatList.remove(matchedInputFormat);
        availableInputFormatList.addFirst(matchedInputFormat);
    }

    private static SimpleDateFormat createDateFormat(String formatStr, TimeZone timeZone, Locale locale) {
        validateTimeZone(formatStr, timeZone);

        if (UNIX_TIME_IN_SECONDS.equals(formatStr)) {
            return UNIX_TIME_IN_SECONDS_DATE_FORMAT;
        }
        else if (UNIX_TIME_IN_MILLIS.equals(formatStr)){
            return UNIX_TIME_IN_MILLIS_DATE_FORMAT;
        }
        else {
            SimpleDateFormat dateFormat = new SimpleDateFormat(formatStr, locale);
            dateFormat.setTimeZone(timeZone);
            dateFormat.set2DigitYearStart(DEFAULT_TWO_DIGIT_YEAR_START);

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
}
