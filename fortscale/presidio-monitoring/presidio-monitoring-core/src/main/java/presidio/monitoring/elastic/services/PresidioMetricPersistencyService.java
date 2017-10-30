package presidio.monitoring.elastic.services;


import presidio.monitoring.records.PresidioMetric;

import java.util.List;

public interface PresidioMetricPersistencyService {

    PresidioMetric save(PresidioMetric presidioMetric);

    Iterable<PresidioMetric> save(List<PresidioMetric> presidioMetric);
}
