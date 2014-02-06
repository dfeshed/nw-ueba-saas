package fortscale.utils.hdfs.partition;

class PartitionsUtils {

	static String normalizePath(String path) {
		String normalized = path.replace('\\', '/');
		if (normalized.endsWith("/"))
			return normalized;
		else
			return normalized + '/';
	}
	
	
	static long normalizeTimestamp(long timestamp) {
		// convert timestamp in seconds to timestamp in milli-seconds
		// 100000000000L is 3/3/1973, assume we won't get data before that....
		if (timestamp<100000000000L)
			timestamp = timestamp * 1000;
		return timestamp;
	}
}
