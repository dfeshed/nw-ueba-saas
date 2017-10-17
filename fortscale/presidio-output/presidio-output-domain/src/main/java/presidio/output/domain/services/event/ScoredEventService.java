package presidio.output.domain.services.event;

import fortscale.common.general.Schema;
import fortscale.utils.time.TimeRange;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.Map;

public interface ScoredEventService {

    List<Object> findDistinctScoredFeatureValue(Schema schema, String adeEventType, Pair<String,String> contextFieldAndValue, TimeRange timeRange, String distinctFieldName, Double scoreThreshold, Map<String, Object> featuresFilters);

}
