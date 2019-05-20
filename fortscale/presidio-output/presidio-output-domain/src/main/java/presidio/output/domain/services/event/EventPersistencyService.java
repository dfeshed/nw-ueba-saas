package presidio.output.domain.services.event;


import fortscale.common.general.Schema;
import fortscale.utils.time.TimeRange;
import org.springframework.data.util.Pair;
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.output.domain.records.events.EnrichedUserEvent;

import java.time.Instant;
import java.util.List;

/**
 * Created by efratn on 02/08/2017.
 */
public interface EventPersistencyService {

    /**
     * persist given records into db
     *
     * @param schema  storing is done according to events schema
     * @param records data to be stored
     */
    void store(Schema schema, List<? extends EnrichedEvent> records) throws Exception;

    /**
     * @param schema
     * @param userId
     * @param timeRange
     * @param features
     * @param eventsLimit
     * @return
     */
    List<? extends EnrichedUserEvent> findUserEvents(Schema schema, String userId, TimeRange timeRange, List<Pair<String, Object>> features, int eventsLimit);

    List<? extends EnrichedUserEvent> findUserEvents(Schema schema, String userId, TimeRange timeRange, List<Pair<String, Object>> features, int numOfItemsToSkip, int pageSize);

    public Long countUserEvents(Schema schema, String userId, TimeRange timeRange, List<Pair<String, Object>> features);

    EnrichedUserEvent findLatestEventForUser(String userId, List<String> collectionNames);

    /**
     * Determine the feature property type
     *
     * @param schema  the schema of the event
     * @param feature the feature name (i.e: operationType
     * @return the property type, or {@code Object.class} as fallback
     */
    Class findFeatureType(Schema schema, String feature);

    void remove(Schema schema, Instant startDate, Instant endDate);


}
