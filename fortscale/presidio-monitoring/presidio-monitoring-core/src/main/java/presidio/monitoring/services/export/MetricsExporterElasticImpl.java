package presidio.monitoring.services.export;


import fortscale.utils.logging.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import presidio.monitoring.elastic.services.PresidioMetricPersistencyService;
import presidio.monitoring.endPoint.PresidioMetricBucket;


public class MetricsExporterElasticImpl extends MetricsExporter {

    private final Logger logger = Logger.getLogger(MetricsExporterElasticImpl.class);

    private PresidioMetricPersistencyService presidioMetricPersistencyService;

    public MetricsExporterElasticImpl(PresidioMetricBucket presidioMetricBucket, PresidioMetricPersistencyService presidioMetricPersistencyService, ThreadPoolTaskScheduler scheduler) {
        super(presidioMetricBucket, scheduler);
        this.presidioMetricPersistencyService = presidioMetricPersistencyService;
    }

    @Scheduled(fixedRateString = "${monitoring.fixed.rate}")
    public void export() {
        logger.debug("Exporting metrics to elastic");
        presidioMetricPersistencyService.save(getMetricsForExport(lastExport));
        logger.debug("Ended Exporting metrics to elastic");
    }

}
