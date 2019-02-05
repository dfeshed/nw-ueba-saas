package presidio.data.generators.event;

import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.ITimeGenerator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public interface IEventGenerator<T> {
    default List<T> generate() throws GeneratorException {
        List<T> events = new ArrayList<T>();
        // fill list of events
        while (hasNext() != null) {
            events.add(generateNext());
        }
        return events;
    }
    default List<T> generate(int size) throws GeneratorException {
        List<T> events = new ArrayList<T>();
        // fill list of events
        while (hasNext() != null && events.size()<size) {
            events.add(generateNext());
        }
        return events;
    }
    T generateNext() throws GeneratorException;
    Instant hasNext();
}
