package presidio.output.domain.repositories;

import fortscale.utils.time.TimeRange;
import org.springframework.data.util.Pair;
import presidio.output.domain.records.events.EnrichedEvent;

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

    List<? extends EnrichedEvent> findEvents(String collectionName, String userId, TimeRange timeRange, List<Pair<String, Object>> features, int limitEvents) throws Exception;

    List<EnrichedEvent> findEvents(String collectionName, String userId, TimeRange timeRange, List<Pair<String, Object>> features, int numOfItemsToSkip, int pageSize);

    EnrichedEvent findLatestEventForUser(String userId);

    void remove(String collectionName, Instant startDate, Instant endDate);
}
