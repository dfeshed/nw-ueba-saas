package presidio.monitoring.services.export;

import fortscale.utils.logging.Logger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import presidio.monitoring.endPoint.PresidioMetricBucket;

/**
 * metrics exporter inheritor that does not do a thing. used for tests or when export is disabled by property ${enable.metrics.export}
 * Created by barak_schuster on 12/4/17.
 */
public class NullMetricsExporter extends MetricsExporter{

    private final Logger logger = Logger.getLogger(NullMetricsExporter.class);

    public NullMetricsExporter(PresidioMetricBucket presidioMetricBucket, ThreadPoolTaskScheduler scheduler) {
        super(presidioMetricBucket, scheduler);
        logger.warn("NullMetricsExporter is used, metrics will not be exported to elastic search. see ${enable.metrics.export} value");
    }

    @Override
    public void export() {

    }

    @Override
    public void manualExportMetrics(MetricBucketEnum metricBucketEnum) {

    }
}
