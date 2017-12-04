package presidio.monitoring.services.export;


import fortscale.utils.logging.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import presidio.monitoring.elastic.services.PresidioMetricPersistencyService;
import presidio.monitoring.endPoint.PresidioMetricBucket;


public class MetricsExporterElasticImpl extends MetricsExporter {

    private final Logger logger = Logger.getLogger(MetricsExporterElasticImpl.class);

    private final boolean REPORT_ONCE = true;
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
        switch (metricBucketEnum) {
            case APPLICATION:
                presidioMetricPersistencyService.save(getApplicationMetricsForExport());
                break;
            case SYSTEM:
                presidioMetricPersistencyService.save(getSystemMetricsForExport());
                break;
            case ALL:
                presidioMetricPersistencyService.save(getMetricsForExport(REPORT_ONCE));
                break;
            default:
                logger.info("Bad metricBucketEnum was given");
        }
        logger.debug("Ended Exporting metrics to elastic");
    }
}
