package presidio.monitoring.aspect.services;

import fortscale.utils.logging.Logger;
import presidio.monitoring.aspect.metrics.PresidioCustomMetrics;
import presidio.monitoring.elastic.records.PresidioMetric;

public class MetricCollectingServiceImpl implements MetricCollectingService {

    private static final Logger logger = Logger.getLogger(MetricCollectingServiceImpl.class);

    private PresidioCustomMetrics presidioCustomMetrics;

    public MetricCollectingServiceImpl(PresidioCustomMetrics presidioCustomMetrics) {
        this.presidioCustomMetrics = presidioCustomMetrics;
    }

    @Override
    public void addMetric(PresidioMetric presidioMetric) {
        logger.debug("Adding metric name {} , value {} , tags {} , unit {}", presidioMetric.getName(), presidioMetric.getValue(), presidioMetric.getTags(), presidioMetric.getUnit());
        presidioCustomMetrics.addMetric(presidioMetric);
    }

    @Override
    public void addMetricReportOnce(PresidioMetric presidioMetric) {
        logger.debug("Adding metric name {} , value {} , tags {} , unit {}", presidioMetric.getName(), presidioMetric.getValue(), presidioMetric.getTags(), presidioMetric.getUnit());
        presidioCustomMetrics.addMetricReportOnce(presidioMetric);
    }

}
