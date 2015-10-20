package fortscale.utils.time;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Utility class to handle timestamp conversions
 */
public final class TimestampUtils {

	public static long normalizeTimestamp(long timestamp) {
		// convert timestamp in seconds to timestamp in milli-seconds
		// 100000000000L is 3/3/1973, assume we won't get data before that....
		if (timestamp<100000000000L)
			timestamp = timestamp * 1000;
		return timestamp;
	}
	
	public static long convertToMilliSeconds(long timestamp) {
		return normalizeTimestamp(timestamp);
	}
	
	public static long convertToSeconds(long timestamp) {
		// 100000000000L is 3/3/1973, assume we won't get data before that....
		if (timestamp>100000000000L)
			timestamp = timestamp / 1000;
		return timestamp;
	}
	
	public static long toStartOfDay(long timestamp) {
		DateTime when = new DateTime(convertToMilliSeconds(timestamp), DateTimeZone.forTimeZone(TimeZone.getDefault()));
		return when.withTimeAtStartOfDay().getMillis();
	}

	public static boolean isFutureTimestamp(long timestamp) {
		return isFutureTimestamp(timestamp, 0);
	}
	
	public static boolean isFutureTimestamp(long timestamp, int gapInHours) {
		DateTime now = new DateTime(DateTimeZone.UTC);
		now = now.plusHours(gapInHours);
		return now.isBefore(convertToMilliSeconds(timestamp));
	}

	/**
	 * Convert splunk time stamp to UNIX time.
	 * Splunk time range reference:
	 * http://docs.splunk.com/Documentation/Splunk/6.2.0/Search/Specifytimemodifiersinyoursearch
	 * @param timestamp
	 * @return
	 */
	public static String convertSplunkTimeToUnix(String timestamp) {
		// If the timestamp contains only number, it's already in unix time
		if (timestamp.matches("[0-9]+")){
			return timestamp;
		}

		// Else, it's in splunk time format
		// Convert it to unix time
		Calendar c = Calendar.getInstance();
		String timeUnit = timestamp.replaceAll("[0-9]+", "");
		// remove the sign
		timeUnit.substring(1);
		int timeValue = Integer.valueOf(timestamp.replaceAll("[A-Za-z]+", ""));

		switch (timeUnit) {
		case "s":
		case "sec":
		case "secs":
		case "second":
		case "seconds":
			c.add(Calendar.SECOND, timeValue);
			break;
		case "m":
		case "min":
		case "minute":
		case "minutes":
			c.add(Calendar.MINUTE, timeValue);
			break;
		case "h":
		case "hr":
		case "hrs":
		case "hour":
		case "hours":
			c.add(Calendar.HOUR, timeValue);
			break;
		case "d":
		case "day":
		case "days":
			c.add(Calendar.DATE, timeValue);
			break;
		case "w":
		case "week":
		case "weeks":
			c.add(Calendar.WEEK_OF_YEAR, timeValue);
			break;
		case "mon":
		case "month":
		case "months":
			c.add(Calendar.MONTH, timeValue);
			break;
		case "y":
		case "yr":
		case "yrs":
		case "year":
		case "years":
			c.add(Calendar.YEAR, timeValue);
			break;
		case "now":
			// do nothing;
			break;
		default:
			// do nothing
			break;
		}

		return String.valueOf(c.getTimeInMillis());
	}
}
