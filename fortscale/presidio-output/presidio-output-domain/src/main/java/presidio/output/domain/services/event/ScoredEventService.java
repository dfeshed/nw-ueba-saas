package presidio.output.domain.services.event;

import fortscale.common.general.Schema;
import fortscale.utils.time.TimeRange;
import org.springframework.data.util.Pair;
import presidio.output.domain.records.events.ScoredEnrichedEvent;

import java.util.List;
import java.util.Map;

public interface ScoredEventService {

    List<Object> findDistinctScoredFeatureValue(Schema schema, String adeEventType, Pair<String,String> contextFieldAndValue, TimeRange timeRange, String distinctFieldName, Double scoreThreshold, Map<String, Object> featuresFilters);

    List<ScoredEnrichedEvent> findEventsAndScores(Schema schema, String adeEventType, String userId, TimeRange timeRange, Map<String, Object> featuresFilters);

}
