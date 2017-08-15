package presidio.monitoring.elastic.services;


import presidio.monitoring.elastic.records.PresidioMetric;

import java.util.List;

public interface MetricExportService {

    PresidioMetric save(PresidioMetric presidioMetric);

    Iterable<PresidioMetric> save(List<PresidioMetric> presidioMetric);
}
