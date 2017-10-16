package presidio.output.domain.services.event;


import fortscale.common.general.Schema;
import fortscale.utils.time.TimeRange;
import presidio.output.domain.records.events.EnrichedEvent;

import java.util.List;
import java.util.Map;

/**
 * Created by efratn on 02/08/2017.
 */
public interface EventPersistencyService {

    /**
     * persist given records into db
     *
     * @param schema storing is done according to events schema
     * @param records  data to be stored
     */
    void store(Schema schema, List<? extends EnrichedEvent> records) throws Exception;

    /**
     *
     * @param schema
     * @param userId
     * @param timeRange
     * @param features
     * @return
     */
    List<? extends EnrichedEvent> findEvents(Schema schema, String userId, TimeRange timeRange, Map<String, Object> features);

    EnrichedEvent findLatestEventForUser(String userId);

    Class findFeatureType(Schema schema, String feature);

}
