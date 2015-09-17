package fortscale.aggregation.feature.event;

import java.util.List;

/**
 * Service to provide basic query functionality of aggregated events
 *
 * @author gils
 * Date: 16/09/2015
 */
public interface AggregatedEventQueryService {
    List<AggrEvent> getAggregatedEventsByContextAndTimeRange(String featureName, String contextType, String ContextName, Long startTime, Long endTime);
}
