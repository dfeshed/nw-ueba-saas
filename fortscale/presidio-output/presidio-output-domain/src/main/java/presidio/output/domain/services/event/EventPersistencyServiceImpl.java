package presidio.output.domain.services.event;

import fortscale.domain.core.EnrichedRecordsMetadata;
import presidio.output.domain.records.events.EnrichedEvent;

import java.util.List;

/**
 * Created by efratn on 02/08/2017.
 */
public class EventPersistencyServiceImpl implements EventPersistencyService {

    @Override
    public void store(EnrichedRecordsMetadata metaData, List<? extends EnrichedEvent> records) {
        //TODO
    }
}
