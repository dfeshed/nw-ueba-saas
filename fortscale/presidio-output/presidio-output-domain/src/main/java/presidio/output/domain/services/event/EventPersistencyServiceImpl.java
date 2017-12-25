package presidio.output.domain.services.event;

import fortscale.common.general.Schema;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimeRange;
import org.springframework.beans.BeanUtils;
import org.springframework.data.util.Pair;
import org.springframework.util.ClassUtils;
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.output.domain.repositories.EventRepository;
import presidio.output.domain.translator.OutputToClassNameTranslator;
import presidio.output.domain.translator.OutputToCollectionNameTranslator;

import java.time.Instant;
import java.util.List;

/**
 * Created by efratn on 02/08/2017.
 */
public class EventPersistencyServiceImpl implements EventPersistencyService {

    private static final Logger logger = Logger.getLogger(EventPersistencyServiceImpl.class);

    private EventRepository eventRepository;

    private OutputToCollectionNameTranslator toCollectionNameTranslator;

    private OutputToClassNameTranslator toClassNameTranslator;

    public EventPersistencyServiceImpl(EventRepository eventRepository,
                                       OutputToCollectionNameTranslator outputToCollectionNameTranslator,
                                       OutputToClassNameTranslator outputToClassNameTranslator) {
        this.toCollectionNameTranslator = outputToCollectionNameTranslator;
        this.toClassNameTranslator = outputToClassNameTranslator;
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
    public List<? extends EnrichedEvent> findEvents(Schema schema, String userId, TimeRange timeRange, List<Pair<String, Object>> features, int eventsLimit) {
        String collectionName = toCollectionNameTranslator.toCollectionName(schema);
        List<? extends EnrichedEvent> events;
        try {
            events = eventRepository.findEvents(collectionName, userId, timeRange, features, eventsLimit);
        } catch (Exception e) {
            String errorMsg = String.format("Failed to findEvents events by schema %s, user %s, time range %s, features %s", schema, userId, timeRange, features);
            logger.error(errorMsg, e);
            throw new RuntimeException(e);
        }
        return events;
    }

    @Override
    public EnrichedEvent findLatestEventForUser(String userId) {
        return eventRepository.findLatestEventForUser(userId);
    }

    @Override
    public Class<?> findFeatureType(Schema schema, String feature) {
        String eventClassName = toClassNameTranslator.toClassName(schema);
        Class<?> featureType = Object.class;
        try {
            Class<?> clazz = ClassUtils.forName(eventClassName, this.getClass().getClassLoader());
            featureType = BeanUtils.findPropertyType(feature, clazz);

        } catch (Exception ex) {
            // swallow and continue
            logger.error("Cannot find feature {} in schema {}", feature, schema);
        }

        return featureType;
    }

    @Override
    public void remove(Schema schema, Instant startDate, Instant endDate) {
        eventRepository.remove(toCollectionNameTranslator.toCollectionName(schema), startDate, endDate);
    }

    @Override
    public void retention(Schema schema, Instant endDate) {
        eventRepository.retention(toCollectionNameTranslator.toCollectionName(schema), endDate);

    }
}
