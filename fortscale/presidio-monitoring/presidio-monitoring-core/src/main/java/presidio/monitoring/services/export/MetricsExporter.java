package presidio.monitoring.services.export;


import fortscale.utils.logging.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import presidio.monitoring.endPoint.PresidioMetricBucket;
import presidio.monitoring.records.MetricDocument;

import java.util.List;

public abstract class MetricsExporter implements ApplicationListener<ContextClosedEvent> {

    private final Logger logger = Logger.getLogger(MetricsExporter.class);

    private PresidioMetricBucket presidioMetricBucket;
    private ThreadPoolTaskScheduler scheduler;
    protected boolean lastExport;


    MetricsExporter(PresidioMetricBucket presidioMetricBucket, ThreadPoolTaskScheduler scheduler) {
        this.presidioMetricBucket = presidioMetricBucket;
        this.scheduler = scheduler;
        this.lastExport = false;
    }

    public List<MetricDocument> getMetricsForExport(boolean isLastExport) {
        logger.debug("Getting metrics from metric bucket.");
        return presidioMetricBucket.getAllMetrics(isLastExport);
    }

    public List<MetricDocument> getApplicationMetricsForExport() {
        logger.debug("Getting all application metrics from metric bucket.");
        return presidioMetricBucket.getApplicationMetricsAndResetApplicationMetrics();
    }

    public List<MetricDocument> getSystemMetricsForExport() {
        logger.debug("Getting all system metrics from metric bucket.");
        return presidioMetricBucket.getSystemMetrics();
    }

    public abstract void export();

    public abstract void manualExportMetrics(MetricBucketEnum metricBucketEnum);

    public void flush() {
        logger.debug("Closing application and exporting metrics last time.");
        lastExport = true;
        export();
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        // flush the metrics
        this.flush();
        // shutdown the scheduler
        if(scheduler!=null) {
            scheduler.shutdown();
        }
    }

    public enum MetricBucketEnum {
        APPLICATION, SYSTEM, ALL;
    }
}
