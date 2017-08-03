package presidio.monitoring.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import presidio.monitoring.export.MetricsExporter;


public class ContextClosedMonitoringHandler implements ApplicationListener<ContextClosedEvent> {

    @Autowired
    ThreadPoolTaskScheduler scheduler;

    @Autowired
    MetricsExporter metricsExporter;

    public ContextClosedMonitoringHandler() {
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        // flush the metrics
        metricsExporter.flush();
        // shutdown the scheduler
        scheduler.shutdown();
    }
}
