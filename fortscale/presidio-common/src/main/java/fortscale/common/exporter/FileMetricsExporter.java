package fortscale.common.exporter;



import fortscale.utils.logging.Logger;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import org.springframework.scheduling.annotation.Scheduled;


import java.util.*;

public class FileMetricsExporter extends MetricsExporter{

    private final Logger logger = Logger.getLogger(FileMetricsExporter.class);
    private MetricsEndpoint metricsEndpoint;

    public FileMetricsExporter(MetricsEndpoint metricsEndpoint) {
        super();
        this.metricsEndpoint=metricsEndpoint;
    }


    @Scheduled(fixedRate = 5000)
    public void export(){
        try {
            logger.debug("Exporting");
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
                logger.info("Metric Name : {} Metric Value : {}", metric, value);
            }
        }
        catch (Exception ex){
            logger.error("NOT GOOD");
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
