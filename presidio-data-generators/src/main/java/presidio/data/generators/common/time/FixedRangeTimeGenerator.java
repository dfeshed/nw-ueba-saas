package presidio.data.generators.common.time;

import presidio.data.generators.common.GeneratorException;

import java.time.Duration;
import java.time.Instant;
import java.util.NoSuchElementException;

/**
 * This {@link ITimeGenerator} returns {@link Instant}s from
 * a fixed time range, with a fixed interval between them.
 *
 * @author Lior Govrin
 */
public class FixedRangeTimeGenerator implements ITimeGenerator {
    private final Instant startInstant;
    private final Instant endInstant;
    private final Duration interval;
    private final Instant lastInstant;
    private Instant nextInstant;

    /**
     * C'tor.
     *
     * @param startInstant the start instant of the fixed time range (inclusive)
     * @param endInstant   the end instant of the fixed time range (exclusive)
     * @param interval     the duration of each interval in the time range
     */
    public FixedRangeTimeGenerator(Instant startInstant, Instant endInstant, Duration interval) {
        if (!startInstant.isBefore(endInstant)) {
            throw new IllegalArgumentException(String.format("startInstant must be before endInstant. " +
                    "startInstant = %s, endInstant = %s.", startInstant.toString(), endInstant.toString()));
        }

        if (interval.compareTo(Duration.ZERO) <= 0) {
            throw new IllegalArgumentException(String.format("interval " +
                    "must be positive. interval = %s.", interval.toString()));
        }

        this.startInstant = startInstant;
        this.endInstant = endInstant;
        this.interval = interval;

        long numberOfIntervals = Duration.between(startInstant, endInstant).toNanos() / interval.toNanos();
        this.lastInstant = startInstant.plus(interval.multipliedBy(numberOfIntervals));
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
            nextInstant = nextInstant.plus(interval);
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
        return lastInstant;
    }

    @Override
    public void reset() {
        nextInstant = startInstant;
    }
}
