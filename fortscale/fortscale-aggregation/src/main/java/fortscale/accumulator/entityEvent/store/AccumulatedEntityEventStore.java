package fortscale.accumulator.entityEvent.store;

import fortscale.accumulator.entityEvent.event.AccumulatedEntityEvent;

import java.time.Instant;
import java.util.Collection;

/**
 * Created by barak_schuster on 10/6/16.
 */
public interface AccumulatedEntityEventStore {

    void insert(Collection<AccumulatedEntityEvent> accumulatedEvents, String featureName);

    Instant getLastAccumulatedEventStartTime(String featureName);
}
