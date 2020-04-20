package presidio.data.generators.common.time;

import presidio.data.generators.common.GeneratorException;

import java.time.Instant;

public class SingleSampleTimeGenerator implements ITimeGenerator {
    Instant instant;

    public SingleSampleTimeGenerator(Instant instant) {
        this.instant = instant;
    }

    @Override
    public Instant hasNext() {
        return instant;
    }

    @Override
    public Instant getNext() throws GeneratorException {
        return instant;
    }

    @Override
    public Instant getFirst() throws GeneratorException {
        return instant;
    }

    @Override
    public Instant getLast() throws GeneratorException {
        return instant;
    }

    @Override
    public void reset() {

    }
}
