package fortscale.utils.hdfs.partition;


class PartitionsUtils {

	static String normalizePath(String path) {
		String normalized = path.replace('\\', '/');
		if (normalized.endsWith("/"))
			return normalized;
		else
			return normalized + '/';
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
