package presidio.monitoring.services;

import fortscale.utils.logging.Logger;
import presidio.monitoring.endPoint.PresidioMetricEndPoint;
import presidio.monitoring.records.Metric;

public class MetricCollectingServiceImpl implements MetricCollectingService {

    private static final Logger logger = Logger.getLogger(MetricCollectingServiceImpl.class);

    private PresidioMetricEndPoint presidioCustomMetrics;

    public MetricCollectingServiceImpl(PresidioMetricEndPoint presidioCustomMetrics) {
        this.presidioCustomMetrics = presidioCustomMetrics;
    }

    @Override
    public void addMetric(Metric metric) {
        logger.debug("Adding metric name {} , value {} , tags {} , unit {}", metric.getName(), metric.getValue(), metric.getTags());
        presidioCustomMetrics.addMetric(metric);
    }

}
