package presidio.data.generators.common.time;

import org.apache.commons.lang3.tuple.Pair;
import presidio.data.generators.common.GeneratorException;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import static java.lang.Math.max;

/**
 * This {@link ITimeGenerator} returns {@link Instant}s from
 * a multiple time range, with a fixed intervals between them specified per range.
 *
 */
public class MultiRangeTimeGenerator implements ITimeGenerator {
    private final Instant startInstant;
    private final Instant endInstant;
    private List<ActivityRange> activityRanges;
    private final Duration defaultInterval;

    private Instant nextInstant;
    private int currentRangeIdx = -1;
    private int previousRangeIdx = -1;


    /**
     * C'tor.
     * @param startInstant   the start instant of the fixed time range (inclusive)
     * @param endInstant     the end instant of the fixed time range (exclusive)
     * @param activityRanges  list of start-end times of activity during a day and duration between events
     * @param defaultInterval for events frequency outside and between specified activityRanges
     *
     */
    public MultiRangeTimeGenerator(Instant startInstant, Instant endInstant, List<ActivityRange> activityRanges, Duration defaultInterval) {

        // validate provided parameters: start/end time pairs can't overlap
        // duration for each start/end time pair should be positive
        if (!startInstant.isBefore(endInstant)) {
            throw new IllegalArgumentException(String.format("startInstant must be before endInstant. " +
                    "startInstant = %s, endInstant = %s.", startInstant.toString(), endInstant.toString()));
        }

        if (defaultInterval.isNegative()) {
            throw new IllegalArgumentException(String.format("interval " +
                    "can't be negative. interval = %s.", defaultInterval.toString()));
        }

        this.startInstant = startInstant;
        this.endInstant = endInstant;
        this.defaultInterval = defaultInterval;

        this.activityRanges = activityRanges;
        this.activityRanges.sort(Comparator.comparing(o -> o.startNanoOfADay));
        // TODO: verify that time ranges do not overlap

        if (defaultInterval.isZero()) {
            this.nextInstant = startInstant.truncatedTo(ChronoUnit.DAYS).plus(this.activityRanges.get(0).duration);
            this.currentRangeIdx = 0;
            this.previousRangeIdx = 0;
        } else {
            this.nextInstant = startInstant;
        }
    }

    @Override
    public boolean hasNext() {
        return nextInstant.isBefore(endInstant);
    }

    @Override
    public Instant getNext() throws GeneratorException {
        if (hasNext()) {
            Instant returnedInstant = nextInstant;
            int nextRangeIdx = -1;
            Duration activityRangeInterval = defaultInterval;

            for (int rangeIdx = max(0,currentRangeIdx); rangeIdx < activityRanges.size(); rangeIdx++) {
                ActivityRange currentRange = activityRanges.get(rangeIdx);
                Instant rangeStart = nextInstant.truncatedTo(ChronoUnit.DAYS).plus(currentRange.startNanoOfADay,ChronoUnit.NANOS);
                Instant rangeEnd = nextInstant.truncatedTo(ChronoUnit.DAYS).plus(currentRange.endNanoOfADay,ChronoUnit.NANOS);
                if (nextInstant.isAfter(rangeStart) && nextInstant.isBefore(rangeEnd))
                {
                    activityRangeInterval = currentRange.duration;
                    nextRangeIdx = rangeIdx;
                    break;
                }
            }
            /** if in this iteration moved out of current range, need to set nextInstant to the end of currentInterval and advance the currentRange idx
             *  if were between ranges, or stay in the same range - just increment by current interval
            **/
            if (nextRangeIdx == -1 && currentRangeIdx > previousRangeIdx && previousRangeIdx >= 0) // just stepped out of a range - set nextInstant to previous range end
            {
                nextInstant = startInstant.truncatedTo(ChronoUnit.DAYS).plus(activityRanges.get(currentRangeIdx).endNanoOfADay, ChronoUnit.NANOS);
                previousRangeIdx = currentRangeIdx;
            }
            else if (nextRangeIdx > currentRangeIdx) // just moved to new range - set nextInstant to range start
            {
                nextInstant = startInstant.truncatedTo(ChronoUnit.DAYS).plus(activityRanges.get(nextRangeIdx).startNanoOfADay, ChronoUnit.NANOS);
                previousRangeIdx = currentRangeIdx;
                currentRangeIdx = nextRangeIdx;

            }
            else {
                nextInstant = nextInstant.plus(activityRangeInterval);
            }

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

    public static class ActivityRange {

        long startNanoOfADay;
        long endNanoOfADay;
        Duration duration;

        public ActivityRange(LocalTime start, LocalTime end, Duration duration ) {

            startNanoOfADay = start.toNanoOfDay();
            endNanoOfADay = end.toNanoOfDay();
            this.duration = duration;

            if (duration.compareTo(Duration.ZERO) <= 0) {
                throw new IllegalArgumentException(String.format("interval " +
                        "must be positive. interval = %s.", duration));
            }
        }
    }
}
