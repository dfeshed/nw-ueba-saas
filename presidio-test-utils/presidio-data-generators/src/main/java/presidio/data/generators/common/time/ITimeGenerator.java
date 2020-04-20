package presidio.data.generators.common.time;

import presidio.data.generators.common.GeneratorException;

import java.time.Instant;

public interface ITimeGenerator {

    Instant hasNext();
    Instant getNext() throws GeneratorException;
    Instant getFirst() throws GeneratorException;
    Instant getLast() throws GeneratorException;
    void reset();
}
