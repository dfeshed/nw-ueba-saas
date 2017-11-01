package presidio.monitoring.elastic.services;


import presidio.monitoring.records.MetricDocument;

import java.util.List;

public interface PresidioMetricPersistencyService {

    MetricDocument save(MetricDocument metricDocument);

    Iterable<MetricDocument> save(List<MetricDocument> metricDocument);
}
