package fortscale.utils.hdfs.partition;


/**
 * Default partition strategy places all files in the same flat directory
 */
public class DefaultPartitionStrategy implements PartitionStrategy {

	@Override
	public String getPartitionPath(long timestamp, String basePath) {
		if (basePath==null)
			throw new IllegalArgumentException("base path is required");
		
		// normalize path
		return normalizePath(basePath);
	}
	
	/**
	 * Gets the partition name for impala. 
	 * Returns null if there is no relevant partition name for impala.
	 */
	@Override
	public String getImpalaPartitionName(long timestamp) {
		return null;
	}

	
	/**
	 * Gets the partitions names that contains data for the date range given
	 */
	public String[] getPartitionsForDateRange(String basePath, long start, long finish) {
		if (basePath==null)
			throw new IllegalArgumentException("base path is required");
		
		// normalize path
		return new String[] { normalizePath(basePath) };
	}
	
	private String normalizePath(String path) {
		String normalized = path.replace('\\', '/');
		if (normalized.endsWith("/"))
			return normalized;
		else
			return normalized + '/';
	}
}
