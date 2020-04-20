package presidio.monitoring.elastic.services;


import fortscale.utils.time.TimeRange;
import presidio.monitoring.records.MetricDocument;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface PresidioMetricPersistencyService {

    MetricDocument save(MetricDocument metricDocument);

    Iterable<MetricDocument> save(List<MetricDocument> metricDocument);

    List<MetricDocument> getMetricsByNamesAndTime(Collection<String> names, TimeRange timeRange, Map<String, String> tags);
}
