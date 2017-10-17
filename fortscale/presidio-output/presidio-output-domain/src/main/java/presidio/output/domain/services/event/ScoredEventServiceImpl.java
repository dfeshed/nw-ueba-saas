package presidio.output.domain.services.event;

import fortscale.common.general.Schema;
import fortscale.utils.recordreader.ReflectionRecordReader;
import fortscale.utils.time.TimeRange;
import org.springframework.data.util.Pair;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.sdk.common.AdeManagerSdk;
import presidio.output.domain.records.events.EnrichedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ScoredEventServiceImpl implements ScoredEventService {

    private EventPersistencyService eventPersistencyService;

    private AdeManagerSdk adeManagerSdk;

    public ScoredEventServiceImpl(EventPersistencyService eventPersistencyService, AdeManagerSdk adeManagerSdk) {
        this.eventPersistencyService = eventPersistencyService;
        this.adeManagerSdk = adeManagerSdk;
    }

    @Override
    public List<Object> findDistinctScoredFeatureValue(Schema schema, String adeEventType, Pair<String, String> contextFieldAndValue, TimeRange timeRange, String distinctFieldName, Double scoreThreshold, Map<String, Object> featuresFilters) {

        List<? extends EnrichedEvent> events = eventPersistencyService.findEvents(schema, contextFieldAndValue.getSecond(), timeRange, featuresFilters);

        // filter by score (change to join within mongo once the performance is stable)
        List<String> eventsIds = events.stream().map(e -> e.getEventId()).collect(Collectors.toList());
        List<AdeScoredEnrichedRecord> adeScoredEnrichedRecords = adeManagerSdk.findScoredEnrichedRecords(eventsIds, adeEventType, 0d);
        Set<String> scoredEventIDs = adeScoredEnrichedRecords.stream().map(e -> e.getContext().getEventId()).collect(Collectors.toSet());

        // get distinct values
        List<Object> distinctValues = new ArrayList<Object>();
        distinctValues.addAll(events
                              .stream()
                              .filter(e-> scoredEventIDs.contains(e.getEventId()))
                              .map(e-> new ReflectionRecordReader(e).get(distinctFieldName))
                              .collect(Collectors.toSet()));

        return distinctValues;
    }
}
