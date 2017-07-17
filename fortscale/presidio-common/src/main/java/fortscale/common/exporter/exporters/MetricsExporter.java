package fortscale.common.exporter.exporters;



import org.springframework.boot.actuate.endpoint.MetricsEndpoint;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static fortscale.common.general.CommonStrings.MEM;
import static fortscale.common.general.CommonStrings.MEM_FREE;
import static fortscale.common.general.CommonStrings.PROCESSORS;
import static fortscale.common.general.CommonStrings.UPTIME;
import static fortscale.common.general.CommonStrings.SYSTEMLOAD_AVERAGE;
import static fortscale.common.general.CommonStrings.HEAP_COMMITTED;
import static fortscale.common.general.CommonStrings.HEAP_INIT;
import static fortscale.common.general.CommonStrings.HEAP_USED;
import static fortscale.common.general.CommonStrings.HEAP;
import static fortscale.common.general.CommonStrings.NONHEAP_COMMITTED;
import static fortscale.common.general.CommonStrings.NONHEAP_INIT;
import static fortscale.common.general.CommonStrings.NONHEAP_USED;
import static fortscale.common.general.CommonStrings.NONHEAP;
import static fortscale.common.general.CommonStrings.THREADS_PEAK;
import static fortscale.common.general.CommonStrings.THREADS_DAEMON;
import static fortscale.common.general.CommonStrings.THREADS_TOTAL_STARTED;
import static fortscale.common.general.CommonStrings.THREADS;
import static fortscale.common.general.CommonStrings.GC_PS_SCAVENGE_COUNT;
import static fortscale.common.general.CommonStrings.GC_PS_SCAVENGE_TIME;
import static fortscale.common.general.CommonStrings.GC_PS_MARKSWEEP_COUNT;
import static fortscale.common.general.CommonStrings.GC_PS_MARKSWEEP_TIME;

public abstract class MetricsExporter implements AutoCloseable{

     private MetricsEndpoint metricsEndpoint;
     private Map<String,String> customMetrics;
     private Set<String> fixedMetrics;

     MetricsExporter(MetricsEndpoint metricsEndpoint){
        this.metricsEndpoint=metricsEndpoint;
        this.customMetrics = new HashMap<>();
        this.fixedMetrics=listOfPresidioFixedSystemMetric();
    }

     Map<String,String> readyMetricsToExporter(){
        Map<String, String> metricsForExport=new HashMap<>();
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
                        break;
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
