package presidio.monitoring.export;


import fortscale.utils.logging.Logger;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import presidio.monitoring.aspect.metrics.CustomMetricEndpoint;
import presidio.monitoring.elastic.records.PresidioMetric;

import java.util.*;

public abstract class MetricsExporter implements ApplicationListener<ContextClosedEvent> {

    private final Logger logger = Logger.getLogger(MetricsExporter.class);

    private MetricsEndpoint metricsEndpoint;
    private Set<String> tags;
    private ThreadPoolTaskScheduler scheduler;
    private String applicationName;


    MetricsExporter(MetricsEndpoint metricsEndpoint, String applicationName, ThreadPoolTaskScheduler scheduler) {
        this.metricsEndpoint = metricsEndpoint;
        this.scheduler = scheduler;
        this.tags = new HashSet<>();
        this.applicationName = applicationName;
        tags.add(this.applicationName);
    }

    List<PresidioMetric> metricsForExport() {
        List<PresidioMetric> metricsForExport = new ArrayList<>();
        PresidioMetric value;
        Map<String, Object> map = metricsEndpoint.invoke();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            value = (PresidioMetric) entry.getValue();
            value.setTags(tags);
            metricsForExport.add(value);


        }
        return metricsForExport;
    }

    public abstract void export();

    public void flush() {
        ((CustomMetricEndpoint) metricsEndpoint).setFlush(true);
        export();
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        // flush the metrics
        this.flush();
        // shutdown the scheduler
        scheduler.shutdown();
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }
}
