package presidio.output.domain.services.event;

import fortscale.common.general.Schema;
import fortscale.utils.recordreader.ReflectionRecordReader;
import fortscale.utils.time.TimeRange;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.util.Pair;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.sdk.common.AdeManagerSdk;
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.output.domain.records.events.ScoredEnrichedEvent;
import presidio.output.domain.repositories.EventMongoPageIterator;

import java.util.*;
import java.util.stream.Collectors;

public class ScoredEventServiceImpl implements ScoredEventService {

    private EventPersistencyService eventPersistencyService;

    private AdeManagerSdk adeManagerSdk;

    public ScoredEventServiceImpl(EventPersistencyService eventPersistencyService, AdeManagerSdk adeManagerSdk) {
        this.eventPersistencyService = eventPersistencyService;
        this.adeManagerSdk = adeManagerSdk;
    }

    @Override
    public Collection<ScoredEnrichedEvent> findDistinctScoredEnrichedEvent(Schema schema, String adeEventType, Pair<String, String> contextFieldAndValue, TimeRange timeRange, String distinctFieldName, Double scoreThreshold, List<Pair<String, Object>> featuresFilters, int eventsLimit, int eventsPageSize) {

        Map<Object, ScoredEnrichedEvent> scoredEnrichedEvent = new HashMap<Object, ScoredEnrichedEvent>();
        int totalEvents = eventPersistencyService.countEvents(schema,  contextFieldAndValue.getSecond(), timeRange, featuresFilters).intValue();
        EventMongoPageIterator eventMongoPageIterator = new EventMongoPageIterator(eventPersistencyService, eventsPageSize, schema, contextFieldAndValue.getSecond(), timeRange, featuresFilters, totalEvents);

        while (eventMongoPageIterator.hasNext()) {
            List<? extends EnrichedEvent> events = eventMongoPageIterator.next();

            // retrieve events score (change to join within mongo once the performance is stable)
            List<String> eventsIds = events.stream().map(e -> e.getEventId()).collect(Collectors.toList());
            List<AdeScoredEnrichedRecord> adeScoredEnrichedRecords = adeManagerSdk.findScoredEnrichedRecords(eventsIds, adeEventType, 0d);
            Map<String, Double> scoredEvents = adeScoredEnrichedRecords.stream().collect(Collectors.toMap(e -> e.getContext().getEventId(), e -> e.getScore(),  (p1, p2) -> p1));

            // get distinct scored enriched
            for (EnrichedEvent e: events) {

                if (!scoredEvents.containsKey(e.getEventId())) {// skip events with zero score
                    continue;
                }

                Object feature = new ReflectionRecordReader(e).get(distinctFieldName);
                if (scoredEnrichedEvent.containsKey(feature)) {// get distinct features
                    continue;
                }

                scoredEnrichedEvent.put(feature, new ScoredEnrichedEvent(e, scoredEvents.get(e.getEventId())));
            }
        }

        return scoredEnrichedEvent.values();
    }


    public List<ScoredEnrichedEvent> findEventsAndScores(Schema schema, String adeEventType, String userId, TimeRange timeRange, List<Pair<String, Object>> featuresFilters, int eventsLimit, int eventsPageSize) {

        List<ScoredEnrichedEvent> scoredEnrichedEvents = new ArrayList<>();
        EventMongoPageIterator eventMongoPageIterator = new EventMongoPageIterator(eventPersistencyService, eventsPageSize, schema, userId, timeRange, featuresFilters, eventsLimit);

        while (eventMongoPageIterator.hasNext()) {
            // get raw events from output_ collections
            List<? extends EnrichedEvent> rawEvents = eventMongoPageIterator.next();

            if (CollectionUtils.isNotEmpty(rawEvents)) {
                // get scored raw events from ade
                List<String> eventsIds = rawEvents.stream().map(e -> e.getEventId()).collect(Collectors.toList());
                List<AdeScoredEnrichedRecord> adeScoredEnrichedRecords = adeManagerSdk.findScoredEnrichedRecords(eventsIds, adeEventType, 0d);

                // create ScoredEnrichedEvents
                for (EnrichedEvent rawEvent : rawEvents) {

                    Optional<Double> enrichedRecordScore = adeScoredEnrichedRecords.stream()
                            .filter(e -> e.getContext().getEventId().equals(rawEvent.getEventId()))
                            .findFirst()
                            .map(e -> e.getScore());
                    ScoredEnrichedEvent scoredEnrichedEvent = new ScoredEnrichedEvent(rawEvent, enrichedRecordScore.orElse(0d));
                    scoredEnrichedEvents.add(scoredEnrichedEvent);
                }

            }
        }
        return scoredEnrichedEvents;
    }
}
