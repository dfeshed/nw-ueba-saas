package presidio.monitoring.export;


import fortscale.utils.logging.Logger;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import presidio.monitoring.elastic.services.MetricExportService;


public class MetricsExporterElasticImpl extends MetricsExporter {

    private final Logger logger=Logger.getLogger(MetricsExporterElasticImpl.class);

    private MetricExportService metricExportService;


    public MetricsExporterElasticImpl(MetricsEndpoint metricsEndpoint, String applicationName,MetricExportService metricExportService,ThreadPoolTaskScheduler scheduler) {
        super(metricsEndpoint,applicationName,scheduler);
        this.metricExportService=metricExportService;
    }


    @Scheduled(fixedRateString = "${monitoring.fixed.rate}")
    public void export() {
        logger.debug("Exporting metrics to elastic");
        metricExportService.save(filterRepeatMetrics());
        logger.debug("Ended Exporting metrics to elastic");
    }

}
