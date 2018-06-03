package presidio.monitoring.sdk.impl.services;

import presidio.monitoring.sdk.api.services.PresidioMetricReader;
import presidio.monitoring.sdk.api.services.model.Metric;

import java.time.Instant;

public class MetricReaderImpl implements PresidioMetricReader {

    @Override
    public Metric getMetric(String metricName, Instant metricTime) {
        return null;
    }
}
