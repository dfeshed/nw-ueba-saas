package presidio.monitoring.services.export;


import fortscale.utils.logging.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import presidio.monitoring.endPoint.PresidioMetricEndPoint;
import presidio.monitoring.records.PresidioMetric;

import java.util.List;

public abstract class MetricsExporter implements ApplicationListener<ContextClosedEvent> {

    private final Logger logger = Logger.getLogger(MetricsExporter.class);

    private PresidioMetricEndPoint presidioMetricEndPoint;
    private ThreadPoolTaskScheduler scheduler;
    protected boolean lastExport;


    MetricsExporter(PresidioMetricEndPoint presidioMetricEndPoint, ThreadPoolTaskScheduler scheduler) {
        this.presidioMetricEndPoint = presidioMetricEndPoint;
        this.scheduler = scheduler;
        this.lastExport = false;
    }

    public List<PresidioMetric> getMetricsForExport(boolean isLastExport) {
        logger.info("Getting metrics from end point.");
        return presidioMetricEndPoint.getAllMetrics(isLastExport);
    }

    public abstract void export();

    public void flush() {
        logger.info("Closing application and exporting metrics last time.");
        lastExport = true;
        export();
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        // flush the metrics
        this.flush();
        // shutdown the scheduler
        scheduler.shutdown();
    }

}
