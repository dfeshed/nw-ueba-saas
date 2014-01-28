package fortscale.utils.hdfs.partition;

import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Monthly partition strategy creates partitions for each calendar month.
 * The path will contain a directory for a year and a directory for a month hierarcy.
 */
public class MonthlyPartitionStrategy implements PartitionStrategy {

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
		// get the month and year from the timestamp
		DateTime when = getDateForTimestamp(timestamp);
		int year = when.getYear();
		int month = when.getMonthOfYear();
		
		return String.format("yearmonth=%s%02d", year, month);
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
		while (startDate.getYear() <= finishDate.getYear() && startDate.getMonthOfYear() <= finishDate.getMonthOfYear() ) {
			partitions.add(getPartitionPathForDate(basePath, startDate));
			startDate = startDate.plusMonths(1);			
		}
		return partitions.toArray(new String[0]);
	}
	
	private DateTime getDateForTimestamp(long timestamp) {
		// convert timestamp in seconds to timestamp in milli-seconds
		// 100000000000L is 3/3/1973, assume we won't get data before that....
		if (timestamp<100000000000L)
			timestamp = timestamp * 1000;
		
		return new DateTime(timestamp, DateTimeZone.UTC);
	}
	
	private String getPartitionPathForDate(String basePath, DateTime when) {
		String normalizedBasePath = basePath.replace('\\', '/');
		
		StringBuilder sb = new StringBuilder();
		sb.append(normalizedBasePath);
		if (!normalizedBasePath.endsWith("/"))
			sb.append('/');
		sb.append(getImpalaPartitionName(when.getMillis()));
		sb.append("/");

		
		return sb.toString();
	}
}
