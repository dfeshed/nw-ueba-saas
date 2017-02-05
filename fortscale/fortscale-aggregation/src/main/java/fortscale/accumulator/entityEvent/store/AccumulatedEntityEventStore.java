package fortscale.accumulator.entityEvent.store;

import fortscale.accumulator.entityEvent.event.AccumulatedEntityEvent;
import fortscale.entity.event.EntityEventConf;

import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

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

    default Set<String> findDistinctContextsByTimeRange(EntityEventConf entityEventConf, Date startTime, Date endTime)
    {
        return findDistinctContextsByTimeRange(entityEventConf,startTime.toInstant(),endTime.toInstant());
    }

    Set<String> findDistinctContextsByTimeRange(EntityEventConf entityEventConf, Instant startTime, Instant endTime);
}
