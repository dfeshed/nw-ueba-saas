package fortscale.utils.hdfs.partition;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.LinkedList;
import java.util.List;

import static fortscale.utils.TimestampUtils.normalizeTimestamp;
import static fortscale.utils.hdfs.partition.PartitionsUtils.getPartitionPartFromPath;
import static fortscale.utils.hdfs.partition.PartitionsUtils.normalizePath;

/**
 * Created by idanp on 8/18/2014.
 * Daily partition strategy creates partitions for each day.
 * The path will contain a directory for a year,month and a directory for a day hierarchy.
 */
public class DailyPartitionStrategy implements PartitionStrategy {

    private static final String MONTHLY_DAILY_PARTITION_FIELD_NAME="yearmonthday";

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
        return String.format("%s=%s",MONTHLY_DAILY_PARTITION_FIELD_NAME, getImpalaPartitionValue(timestamp));
    }


    /**
     * Gets the partition field name definition
     */
    @Override
    public String getImpalaPartitionFieldName() {
        return MONTHLY_DAILY_PARTITION_FIELD_NAME;
    }



    @Override
    public String getTablePartitionDefinition() {
        return String.format("%s INT",MONTHLY_DAILY_PARTITION_FIELD_NAME);
    }


    /**
     * Gets the partition value in impala table column for a given timestamp
     */
    public String getImpalaPartitionValue(long timestamp) {
        // get the month and year from the timestamp
        DateTime when = getDateForTimestamp(timestamp);
        int year = when.getYear();
        int month = when.getMonthOfYear();
        int day = when.getDayOfMonth();
        return String.format("%s%02d%02d", year, month,day);
    }

    /**
     * Gets the partition name extracted from the hdfs path given
     */
    public String getImpalaPartitionNameFromPath(String path) {
        if (path==null || path.isEmpty())
            throw new IllegalArgumentException("path cannot be null or empty");

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
        while ( (startDate.getYear() < finishDate.getYear()) || (startDate.getYear() == finishDate.getYear() && startDate.getMonthOfYear() < finishDate.getMonthOfYear()) || (startDate.getYear() == finishDate.getYear() && startDate.getMonthOfYear() == finishDate.getMonthOfYear() && startDate.getDayOfMonth() <= finishDate.getDayOfMonth()) ) {
            partitions.add(getPartitionPathForDate(basePath, startDate));
            startDate = startDate.plusDays(1);
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

        int year = Integer.parseInt(partitionPart.substring(13, 17));
        int month = Integer.parseInt(partitionPart.substring(17,19));
        int day = Integer.parseInt(partitionPart.substring(19));

        DateTime startPeriod = new DateTime(year, month, day, 0, 0, DateTimeZone.UTC);
        DateTime endPeriod = (new DateTime(year, month, day, 0, 0, DateTimeZone.UTC)).plusDays(1).minusSeconds(1);

        long timestamp = normalizeTimestamp(ts);

        if (endPeriod.isBefore(timestamp))
            return 1;
        if (startPeriod.isAfter(timestamp))
            return -1;
        return 0;
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

    private boolean isPathInPartitionFormat(String path) {
        return path.matches(String.format("%s=\\d{8}", MONTHLY_DAILY_PARTITION_FIELD_NAME));
    }



}
