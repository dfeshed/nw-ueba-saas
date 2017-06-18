package fortscale.utils.time;

import java.time.Instant;

/**
 * An object that represents a specific range between a start point and an end point on the time line.
 *
 * @author Lior Govrin
 */
public class TimeRange implements Comparable<TimeRange> {
    private final Instant start;
    private final Instant end;

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
     * @return the start point of this range on the time line
     */
    public Instant getStart() {
        return start;
    }

    /**
     * @return the end point of this range on the time line
     */
    public Instant getEnd() {
        return end;
    }

    @Override
    public int compareTo(TimeRange other) {
        if(other == null) throw new NullPointerException("The time range cannot be null.");
        int startComparisonResult = start.compareTo(other.start);
        if (startComparisonResult != 0) return startComparisonResult;
        return end.compareTo(other.end);
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