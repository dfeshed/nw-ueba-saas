package presidio.output.domain.services.event;


import fortscale.domain.core.EnrichedRecordsMetadata;
import presidio.output.domain.records.events.EnrichedEvent;

import java.util.List;

/**
 * Created by efratn on 02/08/2017.
 */
public interface EventPersistencyService {

    /**
     * persist given records into db
     *
     * @param metaData some metadata considering the data to be stored. i.e. what is the schema, what is the time range etc...
     * @param records  data to be stored
     */
    void store(EnrichedRecordsMetadata metaData, List<? extends EnrichedEvent> records);
}
