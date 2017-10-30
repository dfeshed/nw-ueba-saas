package presidio.monitoring.export;


import fortscale.utils.logging.Logger;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import presidio.monitoring.elastic.services.PresidioMetricPersistencyService;


public class MetricsExporterElasticImpl extends MetricsExporter {

    private final Logger logger = Logger.getLogger(MetricsExporterElasticImpl.class);

    private PresidioMetricPersistencyService presidioMetricPersistencyService;

    public MetricsExporterElasticImpl(MetricsEndpoint metricsEndpoint, String applicationName, PresidioMetricPersistencyService presidioMetricPersistencyService, ThreadPoolTaskScheduler scheduler) {
        super(metricsEndpoint, applicationName, scheduler);
        this.presidioMetricPersistencyService = presidioMetricPersistencyService;
    }

    @Scheduled(fixedRateString = "${monitoring.fixed.rate}")
    public void export() {
        logger.debug("Exporting metrics to elastic");
        presidioMetricPersistencyService.save(metricsForExport());
        logger.debug("Ended Exporting metrics to elastic");
    }

}
