package presidio.monitoring.export;



import fortscale.utils.logging.Logger;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import org.springframework.stereotype.Component;
import presidio.monitoring.elastic.records.PresidioMetric;


import java.util.*;

@Component
public class MetricsExporterFileImpl extends MetricsExporter {

    private final Logger logger = Logger.getLogger(MetricsExporterFileImpl.class);

    public MetricsExporterFileImpl(MetricsEndpoint metricsEndpoint, String applicationName) {
        super(metricsEndpoint,applicationName);
        logger.info("************************  application name :   {}   **************",applicationName);
    }


    //@Scheduled(fixedDelay = 10000)
    public void export(){
        try {
            logger.debug("Exporting");
            for (PresidioMetric metric :filterRepitMetrics()) {
                logger.info("Metric Name : {} Metric Value : {}", metric.getName(),  metric.getValue());
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
