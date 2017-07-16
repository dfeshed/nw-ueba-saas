package fortscale.common.exporter;



import fortscale.utils.logging.Logger;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import org.springframework.scheduling.annotation.Scheduled;


import java.util.*;

public class FileMetricsExporter implements MetricsExporter , AutoCloseable{

    private final Logger logger = Logger.getLogger(FileMetricsExporter.class);
    private MetricsEndpoint metricsEndpoint;
    private Map<String,String> customMetrics;
    private final Set<String> fixedMetrics;

    public FileMetricsExporter(MetricsEndpoint metricsEndpoint) {
        this.metricsEndpoint=metricsEndpoint;
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


    @Scheduled(fixedRate = 5000)
    public void export(){
        logger.debug("Exporting");
        String metric;
        String value;
        Map<String, Object> map = metricsEndpoint.invoke();
        for (Map.Entry<String, Object> entry :map.entrySet()) {
            metric = entry.getKey();
            value = entry.getValue().toString();
            if(!fixedMetrics.contains(metric)){
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
            logger.info("Metric Name : {} Metric Value : {}", metric, value);
        }
    }


    @Override
    public void close() throws Exception {
        logger.info("********************************************");
        logger.info("**************Last Time Metrics ************");
        logger.info("********************************************");
        export();

    }
}
