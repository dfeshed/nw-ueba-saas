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

	/**
	 *
	 * Convert an epoch in seconds, mSec, uSec, nSec, ... to seconds
	 * The function assumes epoch is later then 1973-03-03 and guesses the epoch units
	 *
	 * This is improved version of convertToSeconds() above
	 *
	 * @param epochInAnyUnit in any time unit
	 * @return epoch in seconds
	 */
	public static long convertEpochInAnyUnitToSeconds(long epochInAnyUnit) {

		// While the epoch it too big, scale it down by 1000
		while (epochInAnyUnit > 100000000000L) { // 100000000000L is 1973-03-03 in mSec
			epochInAnyUnit /= 1000; // mSec -> Sec, uSec-> mSec, ...
		}
		
		return epochInAnyUnit;
	}

	/**
	 * 
	 * Convert the epoch to 'date-in-long' format
	 * 
	 * 'date-in-long' format is human readable date held as a long number in the format YYYYMMDDMMHHSS (14 digit number)
	 * 
	 * Might throw an exception if epoch is invalid
	 * 
	 * @param epoch (in seconds)
	 * @return epoch in 'date-in-long' format
	 */
	public static long epochToDateInLong(long epoch) {

		// Create Date object from epoch
		Date date = new Date(epoch * 1000);  // 1000: seconds -> mSec

		// Create UTC calender from date
		Calendar calender = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calender.setTime(date);

		// Split date into elements
		long year    = calender.get(Calendar.YEAR);
		long month   = calender.get(Calendar.MONTH) + 1; // Month is zero based
		long day     = calender.get(Calendar.DAY_OF_MONTH);
		long hour    = calender.get(Calendar.HOUR_OF_DAY);
		long minutes = calender.get(Calendar.MINUTE);
		long seconds = calender.get(Calendar.SECOND);

		// Build the 'date-in-long' number
		long dateInLong =
				(10000L * hour + 100L * minutes + seconds) +             //         HHMMSS
				(10000L * year + 100L * month   + day    )  * 1000000L;  // YYYYMMDD

		// Done
		return dateInLong;
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
}
