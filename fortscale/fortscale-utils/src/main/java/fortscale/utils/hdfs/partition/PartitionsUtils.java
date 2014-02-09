package fortscale.utils.hdfs.partition;

import static fortscale.utils.hdfs.partition.PartitionsUtils.normalizePath;

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
	
	static String getPartitionPartFromPath(String path) {
		if (path==null)
			return null;
		
		String normalized = normalizePath(path);
		normalized = normalized.substring(0, normalized.length()-1);
		
		if (normalized.contains("/"))
			return normalized.substring(normalized.lastIndexOf("/")+1);
		else
			return null;
	}
}
