package fortscale.utils.hdfs.partition;

import static fortscale.utils.hdfs.partition.PartitionsUtils.*;
import static fortscale.utils.TimestampUtils.*;

import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Monthly partition strategy creates partitions for each calendar month.
 * The path will contain a directory for a year and a directory for a month hierarcy.
 */
public class MonthlyPartitionStrategy implements PartitionStrategy {
	private static final String MONTHLY_PARTITION_FIELD_NAME="yearmonth";

	@Override
	public String getPartitionPath(long timestamp, String basePath) {
		if (basePath==null)
			throw new IllegalArgumentException("base path cannot be null");
				
		// get the current year and month and calculate the partition path based on it
		DateTime when = getDateForTimestamp(timestamp);
		return getPartitionPathForDate(basePath, when);
	}

	
	/**
	 * Gets the partition name for impala. 
	 * Returns null if there is no relevant partition name for impala.
	 */
	@Override
	public String getImpalaPartitionName(long timestamp) {	
		return String.format("%s=%s",MONTHLY_PARTITION_FIELD_NAME, getImpalaPartitionValue(timestamp));
	}
	
	/**
	 * Gets the partition value in impala table column for a given timestamp
	 */
	public String getImpalaPartitionValue(long timestamp) {
		// get the month and year from the timestamp
		DateTime when = getDateForTimestamp(timestamp);
		int year = when.getYear();
		int month = when.getMonthOfYear();
		return String.format("%s%02d", year, month);
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
	
	/**
	 * Gets the partitions names that contains data for the date range given
	 */
	public String[] getPartitionsForDateRange(String basePath, long start, long finish) {
		if (basePath==null)
			throw new IllegalArgumentException("base path cannot be null");
		if (start > finish)
			throw new IllegalArgumentException("start cannot be after finish");
		
		DateTime startDate = getDateForTimestamp(start);
		DateTime finishDate = getDateForTimestamp(finish);
		
		List<String> partitions = new LinkedList<String>();
		while ( (startDate.getYear() < finishDate.getYear()) || (startDate.getYear() == finishDate.getYear() && startDate.getMonthOfYear() <= finishDate.getMonthOfYear()) ) {
			partitions.add(getPartitionPathForDate(basePath, startDate));
			startDate = startDate.plusMonths(1);			
		}
		return partitions.toArray(new String[0]);
	}
		
	/**
	 * Determine if the path given is according to the partition naming strategy
	 */
	public boolean isPartitionPath(String path) {
		if (path==null || path.isEmpty())
			return false;
		
		String partitionPart = getPartitionPartFromPath(path);
		return partitionPart!=null && isPathInPartitionFormat(partitionPart);
	}
	
	/**
	 * compares the given timestamp to the partition period. Return 0 in case the 
	 * timestamp is within the partition, 1 if the timestamp is newer than the partition
	 * or -1 if the timestamp is older than the partition. 
	 */
	public int comparePartitionTo(String partitionPath, long ts) {
		if (!isPartitionPath(partitionPath))
			return 0;
		
		String partitionPart = getPartitionPartFromPath(partitionPath);
		
		int year = Integer.parseInt(partitionPart.substring(10, 14));
		int month = Integer.parseInt(partitionPart.substring(14));
		
		DateTime startPeriod = new DateTime(year, month, 1, 0, 0, DateTimeZone.UTC);
		DateTime endPeriod = (new DateTime(year, month, 1, 0, 0, DateTimeZone.UTC)).plusMonths(1).minusDays(1);
		
		long timestamp = normalizeTimestamp(ts);
		
		if (endPeriod.isBefore(timestamp))
			return 1;
		if (startPeriod.isAfter(timestamp))
			return -1;
		return 0;
	}
	
	private boolean isPathInPartitionFormat(String path) {
		return path.matches(String.format("%s=\\d{6}", MONTHLY_PARTITION_FIELD_NAME));
	}
	
	private DateTime getDateForTimestamp(long timestamp) {		
		return new DateTime(normalizeTimestamp(timestamp), DateTimeZone.UTC);
	}
	
	private String getPartitionPathForDate(String basePath, DateTime when) {
		StringBuilder sb = new StringBuilder();
		sb.append(normalizePath(basePath));
		sb.append(getImpalaPartitionName(when.getMillis()));
		sb.append("/");
		
		return sb.toString();
	}


	@Override
	public String getTablePartitionDefinition() {
		return String.format("%s INT",MONTHLY_PARTITION_FIELD_NAME);
	}
	
}
