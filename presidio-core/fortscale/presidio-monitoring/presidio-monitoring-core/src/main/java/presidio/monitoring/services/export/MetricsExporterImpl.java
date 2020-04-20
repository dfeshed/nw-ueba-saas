package presidio.monitoring.services.export;


import fortscale.utils.logging.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import presidio.monitoring.datadog.PresidioMetricDataDogService;
import presidio.monitoring.elastic.services.PresidioMetricPersistencyService;
import presidio.monitoring.endPoint.PresidioMetricBucket;
import presidio.monitoring.records.MetricDocument;

import java.util.List;


public class MetricsExporterImpl extends MetricsExporter {

    private final Logger logger = Logger.getLogger(MetricsExporterImpl.class);

    private final boolean REPORT_ONCE = true;
    private PresidioMetricPersistencyService presidioMetricPersistencyService;
    private PresidioMetricDataDogService presidioMetricDataDogService;

    public MetricsExporterImpl(PresidioMetricBucket presidioMetricBucket, PresidioMetricPersistencyService presidioMetricPersistencyService,
                               PresidioMetricDataDogService presidioMetricDataDogService, ThreadPoolTaskScheduler scheduler) {
        super(presidioMetricBucket, scheduler);
        this.presidioMetricPersistencyService = presidioMetricPersistencyService;
        this.presidioMetricDataDogService = presidioMetricDataDogService;
    }

    @Scheduled(fixedRateString = "${monitoring.fixed.rate}")
    public void export() {
        List<MetricDocument> metrics = getMetricsForExport(lastExport);

        logger.debug("Exporting metrics to elastic");
        presidioMetricPersistencyService.save(metrics);
        logger.debug("Ended Exporting metrics to elastic");

        logger.debug("Exporting metrics to DataDog");
        int numberMetrics = presidioMetricDataDogService.saveCount(metrics);
        logger.debug("Ended Exporting metrics to DataDog. Export {} metrics to DataDog", numberMetrics);
    }


    /**
     * manualExportMetrics will export system , application or all depends on the value of metricBucketEnum.
     * if metricBucketEnum = APPLICATION , we will export all application metrics even if they are report once,
     * application metric bucket will be empty after this.
     * if metricBucketEnum = SYSTEM , we will export system metrics updated to this time.
     * if metricBucketEnum = ALL , we will export system metrics updated to this time ,
     * we will export all application metrics even if they are report once,
     * application metric bucket will be empty after this.
     *
     * @param metricBucketEnum can be or APPLICATION , SYSTEM or ALL
     */
    @Override
    public void manualExportMetrics(MetricBucketEnum metricBucketEnum) {
        logger.debug("Manual exporting metrics to elastic");
        List<MetricDocument> metrics;
        switch (metricBucketEnum) {
            case APPLICATION:
                metrics = getApplicationMetricsForExport();
                presidioMetricPersistencyService.save(metrics);
                presidioMetricDataDogService.saveCount(metrics);
                break;
            case SYSTEM:
                metrics = getSystemMetricsForExport();
                presidioMetricPersistencyService.save(metrics);
                presidioMetricDataDogService.saveCount(metrics);
                break;
            case ALL:
                metrics = getMetricsForExport(REPORT_ONCE);
                presidioMetricPersistencyService.save(metrics);
                presidioMetricDataDogService.saveCount(metrics);
                break;
            default:
                logger.info("Bad metricBucketEnum was given");
        }
        logger.debug("Ended Exporting metrics to elastic");
    }
}
