package fortscale.aggregation.feature.event;

import java.util.List;

/**
 * Service to provide basic query functionality of aggregated events
 *
 * @author gils
 * Date: 16/09/2015
 */
public interface AggregatedEventQueryService {
    List<AggrEvent> getAggregatedEventsByContextIdAndTimeRange(String featureName, String contextType, String contextName, Long startTime, Long endTime);
}
