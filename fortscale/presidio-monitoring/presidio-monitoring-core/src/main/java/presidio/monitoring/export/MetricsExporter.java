package presidio.monitoring.export;


import fortscale.utils.logging.Logger;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import presidio.monitoring.elastic.records.PresidioMetric;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class MetricsExporter implements ApplicationListener<ContextClosedEvent> {

    private final Logger logger = Logger.getLogger(MetricsExporter.class);

    private MetricsEndpoint metricsEndpoint;
    private Set<String> tags;
    private ThreadPoolTaskScheduler scheduler;


    MetricsExporter(MetricsEndpoint metricsEndpoint, String applicationName, ThreadPoolTaskScheduler scheduler) {
        this.metricsEndpoint = metricsEndpoint;
        this.scheduler = scheduler;
        this.tags = new HashSet<>();
        tags.add(applicationName);
    }

    List<PresidioMetric> filterRepeatMetrics(boolean isFlush) {
        List<PresidioMetric> metricsForExport = new ArrayList<PresidioMetric>(Arrays.asList());
        PresidioMetric value;
        Map<String, Object> map = metricsEndpoint.invoke();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            value = (PresidioMetric) entry.getValue();
            value.setTags(tags);
            if (!value.getExportOnlyOnFlush()) {
                metricsForExport.add(value);
            } else {
                if (isFlush) {
                    metricsForExport.add(value);
                }
            }

        }
        return metricsForExport;
    }

    public abstract void export(boolean isFlush);

    public void flush() {
        export(true);
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        // flush the metrics
        this.flush();
        // shutdown the scheduler
        scheduler.shutdown();
    }
}
