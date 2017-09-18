package presidio.output.domain.repositories;

import fortscale.utils.time.TimeRange;
import presidio.output.domain.records.alerts.CountAggregation;

public interface CountAggregationRepository {

    CountAggregation findByAggregatedByAndContextAndTime(String aggregatedBy, String contextId, TimeRange timeRange);
}
