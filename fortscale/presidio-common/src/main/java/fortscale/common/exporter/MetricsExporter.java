package fortscale.common.exporter;


import org.springframework.boot.actuate.endpoint.MetricsEndpoint;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class MetricsExporter implements AutoCloseable{

     MetricsEndpoint metricsEndpoint;
     Map<String,String> customMetrics;
     Set<String> fixedMetrics;

    public MetricsExporter(){
        this.customMetrics = new HashMap<>();
        this.fixedMetrics=initFixedMetrics();
    }

    private Set<String> initFixedMetrics(){
        Map<String, Object> map = metricsEndpoint.invoke();
        Set<String> names= new HashSet<>();
        for (Map.Entry<String, Object> entry :map.entrySet()) {
            names.add(entry.getKey());
        }
        return names;
    }

    @Override
    public void close() throws Exception {
        System.out.print("closing");
    }
}
