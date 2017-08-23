package presidio.ade.domain.store.accumulator.smart;


import presidio.ade.domain.record.accumulator.AccumulatedSmartRecord;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface SmartAccumulationDataReader {

    /**
     * Find accumulated records by contextId and startTimeRange
     *
     * @param configurationName smart configuration name
     * @param contextId         context id
     * @param startTimeFrom     start time from
     * @param startTimeTo       start time to
     * @return list of AccumulatedSmartRecord
     */
    List<AccumulatedSmartRecord> findAccumulatedEventsByContextIdAndStartTimeRange(String configurationName,
                                                                                   String contextId,
                                                                                   Instant startTimeFrom,
                                                                                   Instant startTimeTo);

    /**
     * @param configurationName smart configuration name
     * @param startInstant
     * @param endInstant
     * @return set of distinct context ids
     */
    Set<String> findDistinctContextsByTimeRange(String configurationName, Instant startInstant, Instant endInstant);

}
