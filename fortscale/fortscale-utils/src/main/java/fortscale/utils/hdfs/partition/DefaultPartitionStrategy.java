package fortscale.utils.hdfs.partition;

import static fortscale.utils.hdfs.partition.PartitionsUtils.normalizePath;

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
	 * Gets the partition name extracted from the hdfs path given
	 */
	public String getImpalaPartitionNameFromPath(String path) {
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
	
	/**
	 * Determine if the path given is according to the partition naming strategy
	 */
	public boolean isPartitionPath(String path) {
		return true;
	}
	
	/**
	 * compares the given timestamp to the partition period. Return 0 in case the 
	 * timestamp is within the partition, 1 if the timestamp is newer than the partition
	 * or -1 if the timestamp is older than the partition. 
	 */
	public int comparePartitionTo(String partitionPath, long ts) {
		return 0;
	}

	@Override
	public String getPartitionDefinition() {
		return null;
	}
	
	
}
