package fortscale.utils;

import org.joda.time.DateTime;

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
	
}
