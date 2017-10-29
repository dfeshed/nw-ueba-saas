package presidio.monitoring.export;


import fortscale.utils.logging.Logger;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import presidio.monitoring.elastic.records.PresidioMetric;

@Component
public class MetricsExporterFileImpl extends MetricsExporter {

    private final Logger logger = Logger.getLogger(MetricsExporterFileImpl.class);

    public MetricsExporterFileImpl(MetricsEndpoint metricsEndpoint, String applicationName, ThreadPoolTaskScheduler scheduler) {
        super(metricsEndpoint, applicationName, scheduler);
        logger.info("************************  application name :   {}   **************", applicationName);
    }


    //@Scheduled(fixedDelay = 10000)
    public void export() {
        try {
            logger.debug("Exporting");
            for (PresidioMetric metric : getMetricsForExport()) {
                logger.info("Metric Name : {} Metric Value : {}", metric.getName(), metric.getValue());
            }
        } catch (Exception ex) {
            logger.error("Error accrued when exporting metrics {}", ex.getMessage());
        }
    }

}
