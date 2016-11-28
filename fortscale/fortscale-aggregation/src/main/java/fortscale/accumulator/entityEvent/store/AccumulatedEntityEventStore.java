package fortscale.accumulator.entityEvent.store;

import fortscale.accumulator.entityEvent.event.AccumulatedEntityEvent;
import fortscale.entity.event.EntityEventConf;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

/**
 * Created by barak_schuster on 10/6/16.
 */
public interface AccumulatedEntityEventStore {

    void insert(Collection<AccumulatedEntityEvent> accumulatedEvents, String featureName);

    Instant getLastAccumulatedEventStartTime(String featureName);

    List<AccumulatedEntityEvent> findAccumulatedEventsByContextIdAndStartTimeRange(EntityEventConf entityEventConf,
																				   String contextId,
																				   Instant startTimeFrom,
																				   Instant startTimeTo);
}
