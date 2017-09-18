package fortscale.utils.time;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Date;

/**
 * An object that represents a specific range between a start point and an end point on the time line.
 *
 * @author Lior Govrin
 */
public class TimeRange implements Comparable<TimeRange> {
    private Instant start;
    private Instant end;

    public TimeRange() {
    }

    /**
     * {@link Instant} based c'tor.
     *
     * @param start the start point of this range on the time line
     * @param end   the end point of this range on the time line
     */
    public TimeRange(Instant start, Instant end) {
        if (start == null) throw new NullPointerException("Start point cannot be null.");
        if (end == null) throw new NullPointerException("End point cannot be null.");
        this.start = start;
        this.end = end;
    }

    /**
     * Epochtime based c'tor.
     *
     * @param startInSeconds the start epochtime of this range, in seconds
     * @param endInSeconds   the end epochtime of this range, in seconds
     */
    public TimeRange(long startInSeconds, long endInSeconds) {
        this(Instant.ofEpochSecond(startInSeconds), Instant.ofEpochSecond(endInSeconds));
    }

    /**
     * {@link Date} based c'tor.
     *
     * @param start the start point of this range on the time line
     * @param end   the end point of this range on the time line
     */
    public TimeRange(Date start, Date end) {
        this(Instant.ofEpochMilli(start.getTime()), Instant.ofEpochMilli(end.getTime()));
    }

    /**
     * @return the start point of this range on the time line
     */
    public Instant getStart() {
        return start;
    }

    public Date getStartAsDate()
    {
        return Date.from(start);
    }

    /**
     * @return the end point of this range on the time line
     */
    public Instant getEnd() {
        return end;
    }

    public Date getEndAsDate()
    {
        return Date.from(end);
    }

    public void setStart(Instant start) {
        this.start = start;
    }

    public void setEnd(Instant end) {
        this.end = end;
    }

    @Override
    public int compareTo(@NotNull TimeRange other) {
        int startComparisonResult = start.compareTo(other.start);
        if (startComparisonResult != 0) return startComparisonResult;
        return end.compareTo(other.end);
    }


    public static int compareTimeRange(TimeRange other1, TimeRange other2) {
        return other1.compareTo(other2);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        TimeRange timeRange = (TimeRange)other;
        return start.equals(timeRange.start) && end.equals(timeRange.end);
    }

    @Override
    public int hashCode() {
        int result = start.hashCode();
        result = 31 * result + end.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s{start = %s, end = %s}", getClass().getSimpleName(), start.toString(), end.toString());
    }
}
