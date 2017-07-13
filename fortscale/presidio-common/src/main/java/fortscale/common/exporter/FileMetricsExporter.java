package fortscale.common.exporter;



import fortscale.utils.logging.Logger;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Map;

public class FileMetricsExporter implements MetricsExporter {

    private final Logger logger = Logger.getLogger(FileMetricsExporter.class);
    private MetricsEndpoint metricsEndpoint;

    public FileMetricsExporter(MetricsEndpoint metricsEndpoint) {
        this.metricsEndpoint=metricsEndpoint;

    }

    @Scheduled(fixedRate = 5000)
    public void export(){
        logger.debug("Exporting");
        Map<String, Object> map = metricsEndpoint.invoke();
        for (Map.Entry<String, Object> entry :map.entrySet()) {
            logger.info("Metric Name : {} Metric Value : {}",entry.getKey(),entry.getValue());
//            System.out.println(entry.getKey() + entry.getValue());
        }
    }

}
