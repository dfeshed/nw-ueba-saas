package presidio.data.generators.event;

import presidio.data.domain.event.Event;
import presidio.data.generators.common.GeneratorException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public interface IEventGenerator<T extends Event> {
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
