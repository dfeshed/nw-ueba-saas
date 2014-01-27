package fortscale.utils.hdfs.split;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class WeeklyFileSplitStrategy implements FileSplitStrategy {

	@Override
	public String getFilePath(String basePath, String filename, long timestamp) {
		if (basePath==null)
			throw new IllegalArgumentException("base path is required");
		if (filename==null)
			throw new IllegalArgumentException("file name is required");
		if (!filename.contains("."))
			throw new IllegalArgumentException("file name must contain suffix");
		
		// convert timestamp in seconds to timestamp in milli-seconds
		if (timestamp<100000000000L)
			timestamp = timestamp * 1000;
		
		DateTime when = new DateTime(timestamp, DateTimeZone.UTC);
		int year = when.getYear();
		int month = when.getMonthOfYear();
		int week = (when.getDayOfMonth() / 7)+1;
		
		// normalize path
		String normalize = basePath.replace('\\', '/');
		StringBuilder sb = new StringBuilder();
		sb.append(normalize);
		if (!normalize.endsWith("/"))
			sb.append("/");
		sb.append(filename.substring(0, filename.lastIndexOf('.')));
		sb.append('_');
		sb.append(year);
		if (month<10)
			sb.append("0");
		sb.append(month);
		sb.append(week);
		sb.append(filename.substring(filename.lastIndexOf('.')));
		
		return sb.toString();
	}

}
