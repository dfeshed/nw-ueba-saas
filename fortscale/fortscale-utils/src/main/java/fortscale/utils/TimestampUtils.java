package fortscale.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

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
		DateTime when = new DateTime(convertToMilliSeconds(timestamp));
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
	
}
