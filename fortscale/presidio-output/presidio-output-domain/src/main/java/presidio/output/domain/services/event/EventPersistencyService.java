package presidio.output.domain.services.event;


import fortscale.common.general.Schema;
import presidio.output.domain.records.events.EnrichedEvent;

import java.util.List;

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

    EnrichedEvent findLatestEventForUser(String userId);
}
