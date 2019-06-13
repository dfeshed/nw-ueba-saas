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
     * @param entityId
     * @param timeRange
     * @param features
     * @param eventsLimit
     * @return
     */
    List<? extends EnrichedUserEvent> findEvents(Schema schema, String entityId, TimeRange timeRange, List<Pair<String, Object>> features, int eventsLimit, String entityType);

    List<? extends EnrichedUserEvent> readRecords(Schema schema, String entityId, TimeRange timeRange, List<Pair<String, Object>> features, int numOfItemsToSkip, int pageSize, String entityType);

    public Long countEvents(Schema schema, String entityId, TimeRange timeRange, List<Pair<String, Object>> features, String entityType);

    EnrichedEvent findLatestEventForEntity(String entityId, List<String> collectionNames, String entityType);

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
