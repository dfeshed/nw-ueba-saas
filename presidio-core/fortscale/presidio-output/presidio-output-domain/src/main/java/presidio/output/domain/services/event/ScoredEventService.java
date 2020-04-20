package presidio.output.domain.services.event;

import fortscale.common.general.Schema;
import fortscale.utils.time.TimeRange;
import org.springframework.data.util.Pair;
import presidio.output.domain.records.events.ScoredEnrichedEvent;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface ScoredEventService {

    Collection<ScoredEnrichedEvent> findDistinctScoredEnrichedEvent(Schema schema, String adeEventType, Pair<String, String> contextFieldAndValue, TimeRange timeRange, Set<String> distinctFieldNames, Double scoreThreshold, List<Pair<String, Object>> featuresFilters, int eventsLimit, int eventsPageSize, String entityType);

    List<ScoredEnrichedEvent> findEventsAndScores(Schema schema, String adeEventType, String entityId, TimeRange timeRange, List<Pair<String, Object>> featuresFilters, int eventsLimit, int eventsPageSize, String entityType);

}
