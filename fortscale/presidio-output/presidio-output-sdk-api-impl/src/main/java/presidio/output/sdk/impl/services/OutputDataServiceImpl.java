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

    private static final fortscale.utils.logging.Logger logger = fortscale.utils.logging.Logger.getLogger(OutputDataServiceImpl.class);

    @Autowired
    private EventPersistencyService eventPersistencyService;

    public OutputDataServiceImpl(EventPersistencyService eventPersistencyService) {
        this.eventPersistencyService = eventPersistencyService;
    }

    @Override
    public void store(Schema schema, List<? extends EnrichedEvent> events) throws Exception {
        logger.debug("storing events for schema {} into output persistency", schema);
        eventPersistencyService.store(schema, events);
    }
}
