package fortscale.utils.time;

import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Utility class to handle timestamp conversions
 */
public final class TimestampUtils {
	private static final long MILLIS_IN_SECOND = 1000;

	public static long normalizeTimestamp(long timestamp) {
		// convert timestamp in seconds to timestamp in milli-seconds
		// 100000000000L is 3/3/1973, assume we won't get data before that....
		if (timestamp < 100000000000L)
			timestamp = timestamp * MILLIS_IN_SECOND;
		return timestamp;
	}

	public static boolean isTimestampInSeconds(long timestamp) {
		// convert timestamp in seconds to timestamp in milli-seconds
		// 100000000000L is 3/3/1973, assume we won't get data before that....
		return timestamp<100000000000L;
	}

	public static long convertToMilliSeconds(long timestamp) {
		return normalizeTimestamp(timestamp);
	}
	
	public static long convertToSeconds(long timestamp) {
		// 100000000000L is 3/3/1973, assume we won't get data before that....
		if (timestamp > 100000000000L)
			timestamp = timestamp / MILLIS_IN_SECOND;
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

		// Check if it is a "snap to" timestamp
		// "Snap to" format example: -7d@d
		if (timestamp.contains("@")) {
			return convertSnapToUnix(timestamp);
		}


		// Else, it's in normal splunk time format
		// Convert it to unix time
		String timeUnit = getTimeUnit(timestamp);

		int timeValue = getTimeValue(timestamp);

		Calendar c = convertSplunkTime(timeUnit, timeValue);
		return String.valueOf(c.getTimeInMillis());
	}

	/**
	 * Support 'snap to' time format
	 * @param timestamp
	 * @return
	 */
	private static String convertSnapToUnix(String timestamp) {
		String[] timeModifier = timestamp.split("@");
		if (timeModifier.length != 2) {
			throw new IllegalArgumentException("Illegal snap to time format");
		}
		String timeUnit = getTimeUnit(timeModifier[0]);
		int timeValue = getTimeValue(timeModifier[0]);

		Calendar c = convertSplunkTime(timeUnit, timeValue);

		int ceilingValue;
		switch (timeModifier[1]) {
		case "m":
			ceilingValue = Calendar.MONTH;
			break;
		case "h":
			ceilingValue = Calendar.HOUR;
			break;
		case "d":
			ceilingValue = Calendar.DATE;
			break;
		case "y":
			ceilingValue = Calendar.YEAR;
			break;
		default:
			ceilingValue = Calendar.DATE;
		}

		c = DateUtils.ceiling(c, ceilingValue);

		return String.valueOf(c.getTimeInMillis());
	}

	/**
	 * Create calendar object from time modifiers params
	 * @param timeUnit
	 * @param timeValue
	 * @return
	 */
	private static Calendar convertSplunkTime(String timeUnit, int timeValue){
		Calendar c = Calendar.getInstance();
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

		return c;
	}

	/**
	 * Get the time unit out of the time modifier
	 * @param timestamp
	 * @return
	 */
	private static String getTimeUnit(String timestamp) {
		timestamp = timestamp.replaceAll("[0-9]+", "");
		// remove the sign
		timestamp = timestamp.substring(1);

		return  timestamp;
	}

	/**
	 * Get the time value out of the time modifier
	 * @param timestamp
	 * @return
	 */
	private static int getTimeValue (String timestamp){
		return Integer.valueOf(timestamp.replaceAll("[A-Za-z]+", ""));
	}

	/**
	 * @param date a Date representation of the time.
	 * @return the number of milliseconds since January 1,
	 *         1970, 00:00:00 UTC represented by date.
	 */
	public static long convertToMilliseconds(Date date) {
		if (date == null) {
			throw new IllegalArgumentException("Date cannot be null");
		} else {
			return date.getTime();
		}
	}

	/**
	 * @param date a Date representation of the time.
	 * @return the number of seconds since January 1,
	 *         1970, 00:00:00 UTC represented by date.
	 */
	public static long convertToSeconds(Date date) {
		return convertToMilliseconds(date) / MILLIS_IN_SECOND;
	}

	public static int getHourFromTimeInSeconds(Long timestamp) {
		return getHourFromTimeInMillis(convertToMilliSeconds(timestamp));
	}

	public static int getHourFromTimeInMillis(Long timestamp) {
		DateTime time = new DateTime(timestamp);
		return time.getHourOfDay();
	}
}
