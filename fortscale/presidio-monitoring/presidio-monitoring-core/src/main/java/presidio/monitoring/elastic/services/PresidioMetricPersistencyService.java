package presidio.monitoring.elastic.services;


import fortscale.utils.time.TimeRange;
import presidio.monitoring.records.MetricDocument;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface PresidioMetricPersistencyService {

    MetricDocument save(MetricDocument metricDocument);

    Iterable<MetricDocument> save(List<MetricDocument> metricDocument);

    List<MetricDocument> getMetricsByNamesAndTime(Collection<String> names, TimeRange timeRange);

    List<MetricDocument> getMetricsByNames(Collection<String> names);
}
