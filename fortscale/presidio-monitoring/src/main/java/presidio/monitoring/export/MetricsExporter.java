package presidio.monitoring.export;



import fortscale.utils.logging.Logger;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import presidio.monitoring.elastic.records.PresidioMetric;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static presidio.monitoring.DefaultPublicMetricsNames.*;

public abstract class MetricsExporter implements ApplicationListener<ContextClosedEvent> {

    private final Logger logger = Logger.getLogger(MetricsExporter.class);

     private MetricsEndpoint metricsEndpoint;
     private Map<String,PresidioMetric> customMetrics;
     private Set<String> defaultInfraMetrics;
     private Set<String> tags;
     private ThreadPoolTaskScheduler scheduler;




    MetricsExporter(MetricsEndpoint metricsEndpoint,String applicationName,ThreadPoolTaskScheduler scheduler){
        this.metricsEndpoint=metricsEndpoint;
        this.customMetrics = new HashMap<>();
        this.scheduler=scheduler;
        this.defaultInfraMetrics =listOfPresidioFixedSystemMetric();
        this.tags=new HashSet<>();
        tags.add(applicationName);
    }

    List<PresidioMetric> filterRepitMetrics() {
        List<PresidioMetric> metricsForExport = new ArrayList<PresidioMetric>(Arrays.asList());
        String metric;
        PresidioMetric value;
        Map<String, Object> map = metricsEndpoint.invoke();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            metric = entry.getKey();
            value = (PresidioMetric) entry.getValue();
            if (!defaultInfraMetrics.contains(metric)) {
                if (!customMetrics.containsKey(metric))
                    customMetrics.put(metric, value);
                else {
                    PresidioMetric presidioMetric = customMetrics.get(metric);
                    if (!presidioMetric.equals(value))
                        customMetrics.replace(metric, value);
                    else {
                        logger.info("****** Metric is not exported, name : {}  value: {}  ********* ", metric, value);
                        continue;
                    }
                }
            }
            metricsForExport.add(value);
        }
        return metricsForExport;
    }


    private Set<String> listOfPresidioFixedSystemMetric() {
        return new HashSet<>(Arrays.asList(MEM, MEM_FREE, PROCESSORS, UPTIME, SYSTEMLOAD_AVERAGE, HEAP_COMMITTED,
                HEAP_INIT, HEAP_USED, HEAP, NONHEAP_COMMITTED, NONHEAP_INIT, NONHEAP, NONHEAP_USED, THREADS_PEAK, THREADS_DAEMON,
                THREADS_TOTAL_STARTED, THREADS, GC_PS_SCAVENGE_COUNT, GC_PS_SCAVENGE_TIME, GC_PS_MARKSWEEP_COUNT, GC_PS_MARKSWEEP_TIME));
    }

    public abstract void export();

    public void flush() {
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
