package fortscale.utils.hdfs.partition;

import static fortscale.utils.TimestampUtils.convertToSeconds;
import static fortscale.utils.hdfs.partition.PartitionsUtils.getPartitionPartFromPath;
import static fortscale.utils.hdfs.partition.PartitionsUtils.normalizePath;

import java.util.LinkedList;
import java.util.List;

public class RuntimePartitionStrategy implements PartitionStrategy {
	private static final String RUNTIME_PARTITION_FIELD_NAME="runtime";

	@Override
	public String getPartitionPath(long timestamp, String basePath) {
		if (basePath==null)
			throw new IllegalArgumentException("base path is required");
		
		StringBuilder sb = new StringBuilder();
		sb.append(normalizePath(basePath));
		sb.append(getImpalaPartitionName(timestamp));
		sb.append("/");
		
		return sb.toString();
	}

	@Override
	public String getImpalaPartitionName(long timestamp) {
		return String.format("%s=%d",RUNTIME_PARTITION_FIELD_NAME, convertToSeconds(timestamp));
	}
	
	/**
	 * Gets the partition field name definition
	 */
	@Override
	public String getImpalaPartitionFieldName() {
		return RUNTIME_PARTITION_FIELD_NAME;
	}
	
	/**
	 * Gets the partition value in impala table column for a given timestamp
	 */
	public String getImpalaPartitionValue(long timestamp) {
		return String.format("%d", convertToSeconds(timestamp));
	}

	/**
	 * Gets the partition name extracted from the hdfs path given
	 */
	public String getImpalaPartitionNameFromPath(String path) {
		if (path==null || path.isEmpty())
			throw new IllegalArgumentException("path cannot be null");
			
		String partitionPart = getPartitionPartFromPath(path);
		if (partitionPart!=null && isPathInPartitionFormat(partitionPart))
			return partitionPart;
		else
			return null;
	}
	
	@Override
	public String[] getPartitionsForDateRange(String basePath, long start, long finish) {
		if (basePath==null)
			throw new IllegalArgumentException("base path cannot be null");
		if (start > finish)
			throw new IllegalArgumentException("start cannot be after finish");
		
		List<String> partitions = new LinkedList<String>();	
		for (long i=start;i<=finish;i++) {
			partitions.add(getPartitionPath(i, basePath));
		}
		return partitions.toArray(new String[0]);
	}

	@Override
	public boolean isPartitionPath(String path) {
		if (path==null || path.isEmpty())
			return false;
			
		String partitionPart = getPartitionPartFromPath(path);
		String regex = String.format("^%s=[\\d]+$", RUNTIME_PARTITION_FIELD_NAME);
		return partitionPart!=null && partitionPart.matches(regex);

	}

	@Override
	public int comparePartitionTo(String partitionPath, long ts) {
		if (partitionPath==null || partitionPath.isEmpty() || !isPartitionPath(partitionPath))
			return 0;
		
		// get the runtime part from the path
		String partitionPart = getPartitionPartFromPath(partitionPath);
		long runtime = Long.parseLong(partitionPart.substring(8));
		runtime = convertToSeconds(runtime);
		ts = convertToSeconds(ts);
		
		if (ts > runtime)
			return 1;
		if (ts < runtime)
			return -1;
		return 0;
	}

	
	private boolean isPathInPartitionFormat(String path) {
		if (path==null)
			return false;
		else
			return path.matches("^runtime=[\\d]+$");
	}

	@Override
	public String getTablePartitionDefinition() {
		return String.format("%s INT",RUNTIME_PARTITION_FIELD_NAME);
	}
}
