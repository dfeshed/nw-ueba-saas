package presidio.output.domain.repositories;

import fortscale.utils.time.TimeRange;
import org.springframework.data.util.Pair;
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.output.domain.records.events.EnrichedUserEvent;

import java.time.Instant;
import java.util.List;

/**
 * Manage events persistency to Mongo
 * Created by efratn on 02/08/2017.
 */
public interface EventRepository {

    /**
     * Store events into the specified collection
     *
     * @param collectionName
     * @param events         documents to be stored
     */
    void saveEvents(String collectionName, List<? extends EnrichedEvent> events) throws Exception;

    List<? extends EnrichedUserEvent> findEntityEvents(String collectionName, String entityId, TimeRange timeRange, List<Pair<String, Object>> features, int limitEvents, String entityType) throws Exception;

    long countEvents(String collectionName, String entityId, TimeRange timeRange, List<Pair<String, Object>> features, String entityType);

    List<? extends EnrichedUserEvent> findEntityEvents(String collectionName, String entityId, TimeRange timeRange, List<Pair<String, Object>> features, int numOfItemsToSkip, int pageSize, String entityType);

    EnrichedUserEvent findLatestEventForEntity(String entityId, List<String> collectionNames, String entityType);

    void remove(String collectionName, Instant startDate, Instant endDate);

}
