package presidio.data.generators.event;

import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.ITimeGenerator;

import java.util.List;

public interface IEventGenerator<T> {
    List<T> generate() throws GeneratorException;
    List<T> generate(int size) throws GeneratorException;
    T generateNext() throws GeneratorException;
    boolean hasNext();
}
