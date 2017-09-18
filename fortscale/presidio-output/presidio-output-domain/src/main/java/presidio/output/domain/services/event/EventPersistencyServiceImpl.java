package presidio.output.domain.services.event;

import fortscale.common.general.Schema;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimeRange;
import org.springframework.beans.factory.annotation.Autowired;
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.output.domain.repositories.EventRepository;
import presidio.output.domain.translator.OutputToCollectionNameTranslator;

import java.util.List;
import java.util.Map;

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
    public List<? extends EnrichedEvent> findEvents(Schema schema, String userId, TimeRange timeRange, Map<String, Object> features) throws Exception {
        String collectionName = toCollectionNameTranslator.toCollectionName(schema);
        List<? extends EnrichedEvent> events;
        try {
             events = eventRepository.findEvents(collectionName,userId,timeRange,features);
        } catch (Exception e) {
            String errorMsg = String.format("Failed to findEvents events by schema %s, user %s, time range %s, features %s", schema, userId, timeRange, features);
            logger.error(errorMsg, e);
            throw e;
        }
        return events;
    }

    @Override
    public EnrichedEvent findLatestEventForUser(String userId) {
        return eventRepository.findLatestEventForUser(userId);
    }
}
