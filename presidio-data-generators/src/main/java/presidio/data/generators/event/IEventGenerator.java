package presidio.data.generators.event;

import presidio.data.generators.common.GeneratorException;

import java.util.List;

public interface IEventGenerator<T> {
    List<T> generate() throws GeneratorException;
}
