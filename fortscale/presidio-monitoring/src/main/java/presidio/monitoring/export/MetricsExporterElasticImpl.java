package presidio.monitoring.export;


import fortscale.utils.logging.Logger;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import org.springframework.scheduling.annotation.Scheduled;
import presidio.monitoring.elastic.services.MetricExportService;




public class MetricsExporterElasticImpl extends MetricsExporter {

    private final Logger logger=Logger.getLogger(MetricsExporterElasticImpl.class);

    private MetricExportService metricExportService;


    public MetricsExporterElasticImpl(MetricsEndpoint metricsEndpoint, String applicationName,MetricExportService metricExportService) {
        super(metricsEndpoint,applicationName);
        this.metricExportService=metricExportService;
    }


    @Scheduled(fixedRate = 5000)
    public void export() {
        logger.info("Exporting metrics to elastic");
        metricExportService.save(filterRepitMetrics());
        logger.info("Ended Exporting metrics to elastic");
    }


    @Override
    public void close() throws Exception {
        export();
    }
}
