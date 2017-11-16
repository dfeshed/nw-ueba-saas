package presidio.data.generators.common.time;

import org.apache.commons.lang3.tuple.Pair;
import presidio.data.generators.common.GeneratorException;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * This {@link ITimeGenerator} returns {@link Instant}s from
 * a multiple time range, with a fixed intervals between them specified per range.
 *
 */
public class MultiRangeTimeGenerator implements ITimeGenerator {
    private final Instant startInstant;
    private final Instant endInstant;
    private List<Pair<Pair<LocalTime, LocalTime>, Duration>> activityRanges;
    private final Duration defaultInterval;

    private Instant nextInstant;

    /**
     * C'tor.
     * @param startInstant   the start instant of the fixed time range (inclusive)
     * @param endInstant     the end instant of the fixed time range (exclusive)
     * @param activityRanges  list of start-end times of activity during a day and duration between events
     * @param defaultInterval for events frequency outside and between specified activityRanges
     *
     */
    public MultiRangeTimeGenerator(Instant startInstant, Instant endInstant, List<Pair<Pair<LocalTime, LocalTime>, Duration>> activityRanges, Duration defaultInterval) {

        // validate provided parameters: start/end time pairs can't overlap
        // duration for each start/end time pair should be positive
        // TODO: verify that time ranges do not overlap
        // TODO: make activityRanges a class (inner)
        if (!startInstant.isBefore(endInstant)) {
            throw new IllegalArgumentException(String.format("startInstant must be before endInstant. " +
                    "startInstant = %s, endInstant = %s.", startInstant.toString(), endInstant.toString()));
        }

        if (defaultInterval.compareTo(Duration.ZERO) <= 0) {
            throw new IllegalArgumentException(String.format("interval " +
                    "must be positive. interval = %s.", defaultInterval.toString()));
        }

        for ( Pair<Pair<LocalTime, LocalTime>, Duration> activityRange : activityRanges ) {
            if (activityRange.getRight().compareTo(Duration.ZERO) <= 0) {
                throw new IllegalArgumentException(String.format("interval " +
                        "must be positive. interval = %s.", activityRange.getRight().toString()));
            }
        }

        this.startInstant = startInstant;
        this.endInstant = endInstant;
        this.activityRanges = activityRanges;
        this.defaultInterval = defaultInterval;

        this.activityRanges.sort(Comparator.comparing(o -> o.getLeft().getLeft()));
        // check partition ?
        this.nextInstant = startInstant;
    }

    @Override
    public boolean hasNext() {
        return nextInstant.isBefore(endInstant);
    }

    @Override
    public Instant getNext() throws GeneratorException {
        if (hasNext()) {
            Instant returnedInstant = nextInstant;
            // TODO: find partition and get duration, if not found - use default
            Duration activityRangeInterval = defaultInterval;

            nextInstant = nextInstant.plus(activityRangeInterval);
            return returnedInstant;
        } else {
            throw new NoSuchElementException("There are no more instants.");
        }
    }

    @Override
    public Instant getFirst() throws GeneratorException {
        return startInstant;
    }

    @Override
    public Instant getLast() throws GeneratorException {
        //return lastInstant;
        throw new UnsupportedOperationException();
    }

    @Override
    public void reset() {
        nextInstant = startInstant;
    }
}
