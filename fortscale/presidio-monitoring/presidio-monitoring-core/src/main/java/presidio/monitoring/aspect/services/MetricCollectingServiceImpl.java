package presidio.monitoring.aspect.services;

import fortscale.utils.logging.Logger;
import presidio.monitoring.aspect.metrics.PresidioCustomMetrics;

import java.util.Set;

public class MetricCollectingServiceImpl implements MetricCollectingService {

    private static final Logger logger = Logger.getLogger(MetricCollectingServiceImpl.class);

    private PresidioCustomMetrics presidioCustomMetrics;

    public MetricCollectingServiceImpl(PresidioCustomMetrics presidioCustomMetrics) {
        this.presidioCustomMetrics = presidioCustomMetrics;
    }

    @Override
    public void addMetric(String metricName, long metricValue, Set tags, String unit) {
        logger.debug("Adding metric name {} , value {} , tags {} , unit {}", metricName, metricValue, tags, unit);
        presidioCustomMetrics.addMetric(metricName, metricValue, tags, unit);
    }

    @Override
    public void addMetricReportOnce(String metricName, long metricValue, Set tags, String unit) {
        logger.debug("Adding metric name {} , value {} , tags {} , unit {}", metricName, metricValue, tags, unit);
        presidioCustomMetrics.addMetricReportOnce(metricName, metricValue, tags, unit);
    }

}
