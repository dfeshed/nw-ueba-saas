package presidio.monitoring.exporter.exporters;



import fortscale.utils.logging.Logger;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import presidio.monitoring.aspect.CustomMetricEndpoint;


import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static presidio.monitoring.DefaultPublicMetricsNames.*;


public abstract class MetricsExporter implements AutoCloseable{

    private final Logger logger = Logger.getLogger(MetricsExporter.class);

     private MetricsEndpoint metricsEndpoint;
     private Map<String,String> customMetrics;
     private Set<String> fixedMetrics;
     private Set<String> tags;




    MetricsExporter(MetricsEndpoint metricsEndpoint,String applicationName){
        this.metricsEndpoint=metricsEndpoint;
        this.customMetrics = new HashMap<>();
        this.fixedMetrics=listOfPresidioFixedSystemMetric();
        this.tags=new HashSet<>();
        tags.add(applicationName);
    }

     Map<String,Object> filterRepitMetrics(){
        Map<String, Object> metricsForExport=new HashMap<>();
        String metric;
        String value;
        Map<String, Object> map = metricsEndpoint.invoke();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            metric = entry.getKey();
            value = entry.getValue().toString();
            if (!fixedMetrics.contains(metric)) {
                if (!customMetrics.containsKey(metric))
                    customMetrics.put(metric, value);
                else {
                    if (!customMetrics.get(metric).equals(value))
                        customMetrics.replace(metric, value);
                    else {
                        logger.info("****** Metric is not exported, name : {}  value: {}  ********* ",metric,value);
                        continue;
                    }
                }
            }
            metricsForExport.put(metric,value);
        }
        return metricsForExport;
    }



    private Set<String> listOfPresidioFixedSystemMetric(){
        return new HashSet<>(Arrays.asList(MEM, MEM_FREE,PROCESSORS,UPTIME,SYSTEMLOAD_AVERAGE,HEAP_COMMITTED,
                HEAP_INIT,HEAP_USED,HEAP,NONHEAP_COMMITTED,NONHEAP_INIT,NONHEAP,NONHEAP_USED,THREADS_PEAK,THREADS_DAEMON,
                THREADS_TOTAL_STARTED,THREADS,GC_PS_SCAVENGE_COUNT,GC_PS_SCAVENGE_TIME,GC_PS_MARKSWEEP_COUNT,GC_PS_MARKSWEEP_TIME));
    }

    @Override
    public void close() throws Exception {
        System.out.print("closing");
    }
}
