package presidio.output.sdk.impl.services;

import org.springframework.beans.factory.annotation.Autowired;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.sdk.api.OutputDataServiceSDK;
import java.util.List;
import fortscale.common.general.Schema;
import presidio.output.domain.records.events.EnrichedEvent;

/**
 * Created by efratn on 19/07/2017.
 */
public class OutputDataServiceImpl implements OutputDataServiceSDK {

    @Autowired
    private EventPersistencyService eventPersistencyService;

    @Override
    public void store(Schema schema, List<? extends EnrichedEvent> events) {
        eventPersistencyService.store(schema, events);
    }
}
