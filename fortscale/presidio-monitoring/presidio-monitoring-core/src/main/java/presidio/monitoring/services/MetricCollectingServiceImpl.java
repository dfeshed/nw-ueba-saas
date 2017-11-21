package presidio.monitoring.services;

import fortscale.utils.logging.Logger;
import presidio.monitoring.endPoint.PresidioMetricBucket;
import presidio.monitoring.records.Metric;

public class MetricCollectingServiceImpl implements MetricCollectingService {

    private static final Logger logger = Logger.getLogger(MetricCollectingServiceImpl.class);

    private PresidioMetricBucket presidioMetricBucket;

    public MetricCollectingServiceImpl(PresidioMetricBucket presidioMetricBucket) {
        this.presidioMetricBucket = presidioMetricBucket;
    }

    /**
     * This method adds metric to presidioMetricBucket that store all the metrics before all will be exported.
     */
    @Override
    public void addMetric(Metric metric) {
        logger.debug("Adding metric name {} , value {} , tags {} , unit {}", metric.getName(), metric.getValue(), metric.getTags());
        presidioMetricBucket.addMetric(metric);
    }

}
