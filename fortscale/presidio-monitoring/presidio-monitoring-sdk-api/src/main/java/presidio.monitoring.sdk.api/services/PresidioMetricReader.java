package presidio.monitoring.sdk.api.services;

import presidio.monitoring.sdk.api.services.model.Metric;

import java.time.Instant;

public interface PresidioMetricReader {

    Metric getMetric(String metricName, Instant metricTime);
}
