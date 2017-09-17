package presidio.data.generators.common.time;

import presidio.data.generators.common.GeneratorException;

import java.time.Instant;

public interface ITimeGenerator {

    boolean hasNext();
    Instant getNext() throws GeneratorException;
    void reset();
}
