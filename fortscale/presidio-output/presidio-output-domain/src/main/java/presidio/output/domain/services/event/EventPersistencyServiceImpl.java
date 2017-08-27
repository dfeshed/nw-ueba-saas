package presidio.output.domain.services.event;

import fortscale.common.general.Schema;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.output.domain.repositories.EventRepository;
import presidio.output.domain.translator.OutputToCollectionNameTranslator;

import java.util.List;

/**
 * Created by efratn on 02/08/2017.
 */
public class EventPersistencyServiceImpl implements EventPersistencyService {

    private static final Logger logger = Logger.getLogger(EventPersistencyServiceImpl.class);

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private OutputToCollectionNameTranslator toCollectionNameTranslator;

    public EventPersistencyServiceImpl(EventRepository eventRepository,
                                       OutputToCollectionNameTranslator outputToCollectionNameTranslator) {
        this.toCollectionNameTranslator = outputToCollectionNameTranslator;
        this.eventRepository = eventRepository;
    }

    @Override
    public void store(Schema schema, List<? extends EnrichedEvent> events) throws Exception {
        logger.info("storing events by schema={}", schema);
        String collectionName = toCollectionNameTranslator.toCollectionName(schema);
        try {
            eventRepository.saveEvents(collectionName, events);
        } catch (Exception e) {
            String errorMsg = String.format("Failed to store events by schema %s", schema);
            logger.error(errorMsg, e);
            throw e;
        }
    }

    @Override
    public EnrichedEvent findLatestEventForUser(String userId) {
        return eventRepository.findLatestEventForUser(userId);
    }
}
