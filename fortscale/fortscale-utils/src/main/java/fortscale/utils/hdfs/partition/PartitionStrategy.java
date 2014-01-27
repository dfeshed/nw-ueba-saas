package fortscale.utils.hdfs.partition;

/**
 * Strategy for computing the hadoop path for a file according to a partition strategy.
 */
public interface PartitionStrategy {

	/**
	 * Gets the hadoop partition directory path for the given event time
	 */
	String getPartitionPath(long timestamp, String basePath);
	
	/**
	 * Gets the partition name for impala. Returns null if there is no relevant partition for impala
	 */
	String getImpalaPartitionName(long timestamp);
	
	/**
	 * Gets the partitions names that contains data for the date range given
	 */
	String[] getPartitionsForDateRange(String basePath, long start, long finish);
}
