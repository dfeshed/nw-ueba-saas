package presidio.output.domain.services.event;

import fortscale.common.general.Schema;
import fortscale.utils.recordreader.RecordReader;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import fortscale.utils.time.TimeRange;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.util.Pair;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.sdk.common.AdeManagerSdk;
import presidio.output.domain.records.events.EnrichedUserEvent;
import presidio.output.domain.records.events.ScoredEnrichedUserEvent;
import presidio.output.domain.repositories.EventMongoPageIterator;

import java.util.*;
import java.util.stream.Collectors;

public class ScoredEventServiceImpl implements ScoredEventService {

    private EventPersistencyService eventPersistencyService;

    private AdeManagerSdk adeManagerSdk;

    private RecordReaderFactoryService recordReaderFactoryService;

    public ScoredEventServiceImpl(EventPersistencyService eventPersistencyService, AdeManagerSdk adeManagerSdk, RecordReaderFactoryService recordReaderFactoryService) {
        this.eventPersistencyService = eventPersistencyService;
        this.adeManagerSdk = adeManagerSdk;
        this.recordReaderFactoryService = recordReaderFactoryService;
    }

    @Override
    public Collection<ScoredEnrichedUserEvent> findDistinctScoredEnrichedUserEvent(Schema schema, String adeEventType, Pair<String, String> contextFieldAndValue, TimeRange timeRange, Set<String> distinctFieldNames, Double scoreThreshold, List<Pair<String, Object>> featuresFilters, int eventsLimit, int eventsPageSize, String entityType) {

        Map<Object, ScoredEnrichedUserEvent> scoredEnrichedEvent = new HashMap<Object, ScoredEnrichedUserEvent>();
        int totalEvents = eventPersistencyService.countEvents(schema,  contextFieldAndValue.getSecond(), timeRange, featuresFilters, entityType).intValue();
        EventMongoPageIterator eventMongoPageIterator = new EventMongoPageIterator(eventPersistencyService, eventsPageSize, schema, contextFieldAndValue.getSecond(), timeRange, featuresFilters, totalEvents, entityType);

        while (eventMongoPageIterator.hasNext()) {
            List<? extends EnrichedUserEvent> events = eventMongoPageIterator.next();

            // retrieve events score (change to join within mongo once the performance is stable)
            List<String> eventsIds = events.stream().map(e -> e.getEventId()).collect(Collectors.toList());
            List<AdeScoredEnrichedRecord> adeScoredEnrichedRecords = adeManagerSdk.findScoredEnrichedRecords(eventsIds, adeEventType, 0d);
            Map<String, Double> scoredEvents = adeScoredEnrichedRecords.stream().collect(Collectors.toMap(e -> e.getContext().getEventId(), e -> e.getScore(),  (p1, p2) -> p1));

            // get distinct scored enriched
            for (EnrichedUserEvent e: events) {

                if (!scoredEvents.containsKey(e.getEventId())) {// skip events with zero score
                    continue;
                }

                RecordReader recordReader = recordReaderFactoryService.getRecordReader(e);
                Map features = Collections.unmodifiableMap(recordReader.get(distinctFieldNames));

                if (scoredEnrichedEvent.containsKey(features)) {
                    continue;
                }

                scoredEnrichedEvent.put(features, new ScoredEnrichedUserEvent(e, scoredEvents.get(e.getEventId())));
            }
        }

        return scoredEnrichedEvent.values();
    }


    public List<ScoredEnrichedUserEvent> findUserEventsAndScores(Schema schema, String adeEventType, String entityId, TimeRange timeRange, List<Pair<String, Object>> featuresFilters, int eventsLimit, int eventsPageSize, String entityType) {

        List<ScoredEnrichedUserEvent> scoredEnrichedUserEvents = new ArrayList<>();
        EventMongoPageIterator eventMongoPageIterator = new EventMongoPageIterator(eventPersistencyService, eventsPageSize, schema, entityId, timeRange, featuresFilters, eventsLimit, entityType);

        while (eventMongoPageIterator.hasNext()) {
            // get raw events from output_ collections
            List<? extends EnrichedUserEvent> rawEvents = eventMongoPageIterator.next();

            if (CollectionUtils.isNotEmpty(rawEvents)) {
                // get scored raw events from ade
                List<String> eventsIds = rawEvents.stream().map(e -> e.getEventId()).collect(Collectors.toList());
                List<AdeScoredEnrichedRecord> adeScoredEnrichedRecords = adeManagerSdk.findScoredEnrichedRecords(eventsIds, adeEventType, 0d);

                // create ScoredEnrichedEvents
                for (EnrichedUserEvent rawEvent : rawEvents) {

                    Optional<Double> enrichedRecordScore = adeScoredEnrichedRecords.stream()
                            .filter(e -> e.getContext().getEventId().equals(rawEvent.getEventId()))
                            .findFirst()
                            .map(e -> e.getScore());
                    ScoredEnrichedUserEvent scoredEnrichedUserEvent = new ScoredEnrichedUserEvent(rawEvent, enrichedRecordScore.orElse(0d));
                    scoredEnrichedUserEvents.add(scoredEnrichedUserEvent);
                }

            }
        }
        return scoredEnrichedUserEvents;
    }
}
