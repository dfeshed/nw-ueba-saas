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

    private static final String DEFAULT_TIME_ZONE = "UTC";
    private static final String DEFAULT_LOCALE = "";

    private static final String OUTPUT_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"; // e.g. 2007-04-26T08:05:04.789Z

    private static final SimpleDateFormat FORTSCALE_TIME_IN_SECONDS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String UTC_TIMEZONE = "UTC";

    private static LinkedList<SimpleDateFormat> availableInputFormats = new LinkedList<>();

    private static LinkedList<String> DEFAULT_INPUT_TIME_FORMATS = new LinkedList<>();

    private static final SimpleDateFormat UNIX_TIME_IN_MILLIS_DATE_FORMAT = new SimpleDateFormat("'unixTimeInMillis'");
    private static final SimpleDateFormat UNIX_TIME_IN_SECONDS_DATE_FORMAT = new SimpleDateFormat("'unixTimeInSeconds'");

    static {
        DEFAULT_INPUT_TIME_FORMATS.add("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        DEFAULT_INPUT_TIME_FORMATS.add("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        DEFAULT_INPUT_TIME_FORMATS.add("yyyyMMddHHmmss'.0Z'");
        DEFAULT_INPUT_TIME_FORMATS.add("MM/dd/yyyy HH:mm:ss z");
        DEFAULT_INPUT_TIME_FORMATS.add("MM/dd/yyyy HH:mm:ss");
        DEFAULT_INPUT_TIME_FORMATS.add("yyyy/MM/dd HH:mm:ss");
        DEFAULT_INPUT_TIME_FORMATS.add("EEE MMM d HH:mm:ss z yyyy");
        DEFAULT_INPUT_TIME_FORMATS.add("yyyy-MM-dd'T'HH:mm:ssXXX");
        DEFAULT_INPUT_TIME_FORMATS.add("MMM dd yyyy HH:mm:ss");
        DEFAULT_INPUT_TIME_FORMATS.add("MM/dd/yy HH:mm:ss");
        DEFAULT_INPUT_TIME_FORMATS.add("MMM  dd HH:mm:ss yyyy");
        DEFAULT_INPUT_TIME_FORMATS.add("MMM dd HH:mm:ss yyyy");
        DEFAULT_INPUT_TIME_FORMATS.add("yyyy MMM  dd HH:mm:ss");
        DEFAULT_INPUT_TIME_FORMATS.add("MM/dd/yyyy:HH:mm:ss");
        DEFAULT_INPUT_TIME_FORMATS.add("MMM dd yyyy  HH:mm:ss");
        DEFAULT_INPUT_TIME_FORMATS.add("yyyy-MM-dd'T'HH:mm:ss.SSS");
        DEFAULT_INPUT_TIME_FORMATS.add("yyyy-MM-dd'T'HH:mm:ss");
        DEFAULT_INPUT_TIME_FORMATS.add("MM/dd/yyyy h:mm a");
        DEFAULT_INPUT_TIME_FORMATS.add("MM/d/yyyy H:mm");
        DEFAULT_INPUT_TIME_FORMATS.add("yyyy-MM-dd HH:mm:ss");
        DEFAULT_INPUT_TIME_FORMATS.add("yyyy MMM dd HH:mm:ss");
        DEFAULT_INPUT_TIME_FORMATS.add("EEE MMM dd HH:mm:ss yyyy");
        DEFAULT_INPUT_TIME_FORMATS.add("MM/d/yyyy h:mm a");
        DEFAULT_INPUT_TIME_FORMATS.add("yyyy MMM d HH:mm:ss");
        DEFAULT_INPUT_TIME_FORMATS.add("unixTimeInSeconds");
        DEFAULT_INPUT_TIME_FORMATS.add("unixTimeInMillis");
    }

    public static String convertTimestampToFortscaleFormat(String timestampToConvert, TimeConversionParamsWrapper timeConversionParamsWrapper) {

        SimpleDateFormat outputFormat = createDateFormat(timeConversionParamsWrapper.getOutputFormat(), timeConversionParamsWrapper.getOutputTimezone(), timeConversionParamsWrapper.getOutputLocale());

        String matchedInputFormatStr = null;

        String fortscaleTimeFormat = null;

        for (String inputFormatStr : DEFAULT_INPUT_TIME_FORMATS) {
            DateTime date = null;
            SimpleDateFormat inputFormat = createDateFormat(inputFormatStr, timeConversionParamsWrapper.getInputTimezone(), getLocale(DEFAULT_LOCALE));
            if (inputFormat == UNIX_TIME_IN_MILLIS_DATE_FORMAT || inputFormat == UNIX_TIME_IN_SECONDS_DATE_FORMAT) {
                date = parseUnixTime(timestampToConvert, DateTimeZone.forTimeZone(timeConversionParamsWrapper.getInputTimezone()));
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
                date = date.withZone(DateTimeZone.forTimeZone(timeConversionParamsWrapper.getOutputTimezone()));
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

        if (matchedInputFormatStr != null && !DEFAULT_INPUT_TIME_FORMATS.getFirst().equals(matchedInputFormatStr)) {
            pushInputFormatTohead(matchedInputFormatStr);
        }

        return fortscaleTimeFormat;
    }

    private static void pushInputFormatTohead(String matchedInputFormat) {
        DEFAULT_INPUT_TIME_FORMATS.remove(matchedInputFormat);
        DEFAULT_INPUT_TIME_FORMATS.addFirst(matchedInputFormat);
    }

//
//    public static String convertTimestampToFortscaleFormat(String timestamp, String outputFormatStr, String inputTimeZoneStr, String outputTimeZoneStr) {
//
//        TimeZone inputTimeZone = getTimeZone(inputTimeZoneStr == null ? DEFAULT_TIME_ZONE : inputTimeZoneStr);
//        TimeZone outputTimeZone = getTimeZone(outputTimeZoneStr == null ? DEFAULT_TIME_ZONE : outputTimeZoneStr);
//
//        SimpleDateFormat outputFormat = createDateFormat(outputFormatStr, outputTimeZone);
//
//        List<SimpleDateFormat> inputFormats = new ArrayList<>();
//        for (String inputFormat : DEFAULT_INPUT_TIME_FORMATS) {
//            SimpleDateFormat dateFormat = createDateFormat(inputFormat, inputTimeZone);
//            if (dateFormat == null) {
//                dateFormat = new SimpleDateFormat(inputFormat, getLocale(DEFAULT_LOCALE));
//                dateFormat.setTimeZone(inputTimeZone);
//                dateFormat.set2DigitYearStart(DateUtil.DEFAULT_TWO_DIGIT_YEAR_START);
//            }
//            inputFormats.add(dateFormat);
//        }
//
//
//        boolean foundMatchingFormat = false;
//        for (SimpleDateFormat inputFormat : inputFormats) {
//            DateTime date = null;
//            if (inputFormat == UNIX_TIME_IN_MILLIS_DATE_FORMAT || inputFormat == UNIX_TIME_IN_SECONDS_DATE_FORMAT) {
//                date = parseUnixTime(timestamp, DateTimeZone.forTimeZone(inputTimeZone));
//            } else {
//                Date parsed = null;
//                try {
//                    parsed = inputFormat.parse(timestamp);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                if (parsed!=null)
//                    date = new DateTime(parsed);
//            }
//            if (date != null) {
//                // change the time zone to the output time zone
//                date = date.withZone(DateTimeZone.forTimeZone(outputTimeZone));
//                String result;
//                if (outputFormat == UNIX_TIME_IN_MILLIS_DATE_FORMAT) {
//                    result = String.valueOf(date.getMillis());
//                } else if (outputFormat == UNIX_TIME_IN_SECONDS_DATE_FORMAT) {
//                    result = String.valueOf(date.getMillis() / 1000);
//                } else {
//                    result = outputFormat.format(date.toDate());
//                }
//
//                return result;
//            }
//        }
//
//        return null;
//    }

    private static SimpleDateFormat createDateFormat(String formatStr, TimeZone timeZone, Locale locale) {
        //TODO need to check what to do here
        validateTimeZone(formatStr, timeZone);

        if ("unixTimeInSeconds".equals(formatStr)) {
            return UNIX_TIME_IN_SECONDS_DATE_FORMAT;
        }
        else if ("unixTimeInMillis".equals(formatStr)){
            return UNIX_TIME_IN_MILLIS_DATE_FORMAT;
        }
        else {
            SimpleDateFormat dateFormat = new SimpleDateFormat(formatStr, locale);
            dateFormat.setTimeZone(timeZone);
            dateFormat.set2DigitYearStart(DateUtil.DEFAULT_TWO_DIGIT_YEAR_START);

            if (!formatStr.equals("yyyy-MM-dd'T'HH:mm:ss.SSS")) {
                dateFormat.setLenient(false);
            }

            return dateFormat;
        }
    }

    //TODO need to check what to do here
    private static void validateTimeZone(String formatStr, TimeZone timeZone) {
//        case "unixTimeInMillis":
//        if (!UTC_TIMEZONE.equals(timeZone.getID())) {
//            //throw new MorphlineCompilationException("timeZone must be UTC for date formatStr 'unixTimeInMillis'", getConfig());
//        }
//        format = UNIX_TIME_IN_MILLIS_DATE_FORMAT;
//        break;
//        case "unixTimeInSeconds":
//        if (!UTC_TIMEZONE.equals(timeZone.getID())) {
//            //throw new MorphlineCompilationException("timeZone must be UTC for date formatStr 'unixTimeInSeconds'", getConfig());
//        }
//        format = UNIX_TIME_IN_SECONDS_DATE_FORMAT;
//        break;
//        case "fortscaleDateFormat":
//        format = FORTSCALE_TIME_IN_SECONDS;
//        break;
    }

    private static TimeZone getTimeZone(String timeZoneID) {
        TimeZone zone = TimeZone.getTimeZone(timeZoneID);
        // check if the zone is GMT and the timeZoneID is not GMT than it means that the
        // TimeZone.getTimeZone did not recieve a valid id
        if (!zone.getID().equalsIgnoreCase(timeZoneID)) {
            //throw new MorphlineCompilationException("Unknown timezone: " + timeZoneID, getConfig());
        } else {
            return zone;
        }

        return null;
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
        //throw new MorphlineCompilationException("Unknown locale: " + name, getConfig());
    }

    // work around the fact that SimpleDateFormat doesn't understand Unix time format
    private static DateTime parseUnixTime(String timestamp, DateTimeZone tz) {
        try {
            return new DateTime(TimestampUtils.convertToMilliSeconds(Long.parseLong(timestamp)), tz);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static final class DateUtil {
        //start HttpClient
        /**
         * Date format pattern used to parse HTTP date headers in RFC 1123 format.
         */
        public static final String PATTERN_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";

        /**
         * Date format pattern used to parse HTTP date headers in RFC 1036 format.
         */
        public static final String PATTERN_RFC1036 = "EEEE, dd-MMM-yy HH:mm:ss zzz";

        /**
         * Date format pattern used to parse HTTP date headers in ANSI C
         * <code>asctime()</code> format.
         */
        public static final String PATTERN_ASCTIME = "EEE MMM d HH:mm:ss yyyy";
        //These are included for back compat
        private static final Collection<String> DEFAULT_HTTP_CLIENT_PATTERNS = Arrays.asList(
                PATTERN_ASCTIME, PATTERN_RFC1036, PATTERN_RFC1123);

        private static final Date DEFAULT_TWO_DIGIT_YEAR_START;

        static {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.ROOT);
            calendar.set(2000, Calendar.JANUARY, 1, 0, 0);
            DEFAULT_TWO_DIGIT_YEAR_START = calendar.getTime();
        }

//      private static final TimeZone GMT = TimeZone.getTimeZone("GMT");

        //end HttpClient

        //---------------------------------------------------------------------------------------

        /**
         * A suite of default date formats that can be parsed, and thus transformed to the Solr specific format
         */
        public static final List<String> DEFAULT_DATE_FORMATS = new ArrayList<String>();

        static {
            DEFAULT_DATE_FORMATS.add("yyyy-MM-dd'T'HH:mm:ss'Z'");
            DEFAULT_DATE_FORMATS.add("yyyy-MM-dd'T'HH:mm:ss");
            DEFAULT_DATE_FORMATS.add("yyyy-MM-dd");
            DEFAULT_DATE_FORMATS.add("yyyy-MM-dd hh:mm:ss");
            DEFAULT_DATE_FORMATS.add("yyyy-MM-dd HH:mm:ss");
            DEFAULT_DATE_FORMATS.add("EEE MMM d hh:mm:ss z yyyy");
            DEFAULT_DATE_FORMATS.addAll(DateUtil.DEFAULT_HTTP_CLIENT_PATTERNS);
        }

    }
}
