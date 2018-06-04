package presidio.webapp.service;

import presidio.webapp.model.Metric;

import java.util.List;

public interface RestMetricsService {

    public List<Metric> getMetricsByName(List<String> metricNames);
}
