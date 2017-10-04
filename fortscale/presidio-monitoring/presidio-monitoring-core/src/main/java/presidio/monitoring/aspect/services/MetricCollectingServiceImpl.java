package presidio.monitoring.aspect.services;

import fortscale.utils.logging.Logger;
import presidio.monitoring.aspect.metrics.PresidioCustomMetrics;

import java.util.Set;

public class MetricCollectingServiceImpl implements MetricCollectingService {

    private static final Logger logger = Logger.getLogger(MetricCollectingServiceImpl.class);


    @Override
    public void addMetric(String metricName, long metricValue, Set tags, String unit) {
        logger.debug("Adding metric name {} , value {} , tags {} , unit {}", metricName, metricValue, tags, unit);
        PresidioCustomMetrics.addInMethodMetric(metricName, metricValue, tags, unit);
    }

}
