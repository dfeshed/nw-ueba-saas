package fortscale.utils.hdfs.partition;

import static fortscale.utils.hdfs.partition.PartitionsUtils.*;

import java.util.LinkedList;
import java.util.List;

public class RuntimePartitionStrategy implements PartitionStrategy {

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
		return "runtime=" + timestamp;
	}

	/**
	 * Gets the partition name extracted from the hdfs path given
	 */
	public String getImpalaPartitionNameFromPath(String path) {
		if (path==null || path.isEmpty())
			throw new IllegalArgumentException("path cannot be null");
		
		// normalize and strip last slash
		String normalized = normalizePath(path);
		normalized = normalized.substring(0, normalized.length()-1);
		
		String partitionPart = normalized.substring(normalized.lastIndexOf("/")+1);
		
		if (isPathInPartitionFormat(partitionPart))
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
		
		String normalized = normalizePath(path);
		return normalized.matches("^/.*/runtime=[\\d]+/$");
	}

	@Override
	public int comparePartitionTo(String partitionPath, long ts) {
		if (partitionPath==null || partitionPath.isEmpty() || !isPartitionPath(partitionPath))
			return 0;
		
		// get the runtime part from the path
		String normalized = normalizePath(partitionPath);
		normalized = normalized.substring(0, normalized.length()-1);
		String partitionPart = normalized.substring(normalized.lastIndexOf("/")+1);
		
		long runtime = Long.parseLong(partitionPart.substring(8));
		runtime = normalizeTimestamp(runtime);
		ts = normalizeTimestamp(ts);
		
		if (ts > runtime)
			return 1;
		if (ts < runtime)
			return -1;
		return 0;
	}

	
	private boolean isPathInPartitionFormat(String path) {
		return path.matches("^runtime=[\\d]+$");
	}
}
