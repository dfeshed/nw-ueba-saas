package fortscale.common.exporter;



import org.springframework.boot.actuate.endpoint.MetricsEndpoint;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static fortscale.common.exporter.PresidioFixedSystemMetric.listOfPresidioFixedSystemMetric;

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

    @Override
    public void close() throws Exception {
        System.out.print("closing");
    }
}
