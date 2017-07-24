package presidio.monitoring.export;



import fortscale.utils.logging.Logger;
import org.json.JSONObject;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;


import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static presidio.monitoring.DefaultPublicMetricsNames.*;


public abstract class MetricsExporter implements AutoCloseable{

    private final Logger logger = Logger.getLogger(MetricsExporter.class);

     private MetricsEndpoint metricsEndpoint;
     private Map<String,Object> customMetrics;
     private Set<String> defaultInfraMetrics;
     private Set<String> tags;




    MetricsExporter(MetricsEndpoint metricsEndpoint,String applicationName){
        this.metricsEndpoint=metricsEndpoint;
        this.customMetrics = new HashMap<>();
        this.defaultInfraMetrics =listOfPresidioFixedSystemMetric();
        this.tags=new HashSet<>();
        tags.add(applicationName);
    }

     Map<String,Object> filterRepitMetrics(){
        Map<String, Object> metricsForExport=new HashMap<>();
        String metric;
        Object value;
        Map<String, Object> map = metricsEndpoint.invoke();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            metric = entry.getKey();
            value = entry.getValue();
            if (!defaultInfraMetrics.contains(metric)) {
                if (!customMetrics.containsKey(metric))
                    customMetrics.put(metric, value);
                else {
                    JSONObject obj1= (JSONObject) customMetrics.get(metric);
                    JSONObject obj2= (JSONObject) value;
                    if (!obj1.get("value").equals(obj2.get("value")))
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
