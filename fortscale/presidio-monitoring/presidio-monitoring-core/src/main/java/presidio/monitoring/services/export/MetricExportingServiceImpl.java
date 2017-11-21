package presidio.monitoring.services.export;


import fortscale.utils.logging.Logger;

public class MetricExportingServiceImpl implements MetricExportingService {

    private final Logger logger = Logger.getLogger(MetricExportingServiceImpl.class);

    private MetricsExporter metricsExporter;

    public MetricExportingServiceImpl(MetricsExporter metricsExporter) {
        this.metricsExporter = metricsExporter;
    }

    @Override
    public void exportApplicationMetrics() {
        logger.info("Exporting application metrics");
        metricsExporter.manualExportMetrics(MetricsExporter.MetricBucketEnum.APPLICATION);
    }

    @Override
    public void exportAllMetrics() {
        logger.info("Exporting all metrics");
        metricsExporter.manualExportMetrics(MetricsExporter.MetricBucketEnum.ALL);
    }

    @Override
    public void exportSystemMetrics() {
        logger.info("Exporting system metrics");
        metricsExporter.manualExportMetrics(MetricsExporter.MetricBucketEnum.SYSTEM);
    }
}
