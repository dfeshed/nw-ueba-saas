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
    private Instant nextInstant;
    private Instant lastInstant;

    /**
     * C'tor.
     *
     * @param startInstant the start instant of the fixed time range (inclusive)
     * @param endInstant   the end instant of the fixed time range (exclusive)
     * @param interval     the duration of each interval in the time range
     */
    public FixedRangeTimeGenerator(Instant startInstant, Instant endInstant, Duration interval) {
        this.startInstant = startInstant;
        this.endInstant = endInstant;
        this.interval = interval;
        this.nextInstant = startInstant;
        // The last instant is calculated lazily
        this.lastInstant = null;
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
        if (startInstant.isBefore(endInstant)) {
            return startInstant;
        } else {
            throw new NoSuchElementException("The time range is empty - There is no first instant.");
        }
    }

    @Override
    public Instant getLast() throws GeneratorException {
        if (startInstant.isBefore(endInstant)) {
            if (lastInstant == null) {
                lastInstant = startInstant;

                while (lastInstant.plus(interval).isBefore(endInstant)) {
                    lastInstant = lastInstant.plus(interval);
                }
            }

            return lastInstant;
        } else {
            throw new NoSuchElementException("The time range is empty - There is no last instant.");
        }
    }

    @Override
    public void reset() {
        nextInstant = startInstant;
    }
}
