package presidio.output.domain.repositories;

import presidio.output.domain.records.events.EnrichedEvent;

import java.util.List;

/**
 * Manage events persistency to Mongo
 * Created by efratn on 02/08/2017.
 */
public interface EventRepository {

    /**
     * Store events into the specified collection
     * @param collectionName
     * @param events documents to be stored
     */
    void saveEvents(String collectionName, List<? extends EnrichedEvent> events) throws Exception;

    EnrichedEvent findLatestEventForUser(String userId);
}
