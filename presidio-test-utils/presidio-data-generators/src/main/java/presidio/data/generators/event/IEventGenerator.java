package presidio.data.generators.event;

import presidio.data.domain.event.Event;
import presidio.data.generators.common.GeneratorException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

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

    default Stream<T> generateToStream() {
        Stream.Builder<T> builder = Stream.builder();

        while (hasNext() != null) {
            try {
                builder.add(generateNext());
            } catch (GeneratorException e) {
                e.printStackTrace();
            }
        }
        return builder.build();
    }
    T generateNext() throws GeneratorException;
    Instant hasNext();
}
