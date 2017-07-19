package fortscale.common.exporter.exporters;



import fortscale.utils.logging.Logger;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.util.*;

@Component
public class FileMetricsExporter extends MetricsExporter {

    private final Logger logger = Logger.getLogger(FileMetricsExporter.class);

    public FileMetricsExporter(MetricsEndpoint metricsEndpoint,String applicationName) {
        super(metricsEndpoint,applicationName);
        logger.info("************************  application name :   {}   **************",applicationName);
    }


    @Scheduled(fixedDelay = 10000)
    public void export(){
        try {
            logger.debug("Exporting");
            for (Map.Entry<String, String> entry : readyMetricsToExporter().entrySet()) {
                logger.info("Metric Name : {} Metric Value : {}", entry.getKey(),  entry.getValue());
            }
        }
        catch (Exception ex){
            logger.error("Error accrued when exporting metrics {}",ex.getMessage());
        }
    }


    @Override
    public void close() throws Exception {
        logger.info("********************************************");
        logger.info("************* Last Time Metrics ************");
        logger.info("********************************************");
        export();

    }
}
