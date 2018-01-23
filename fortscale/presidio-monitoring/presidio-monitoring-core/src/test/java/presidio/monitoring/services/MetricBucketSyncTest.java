package presidio.monitoring.services;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ContextConfiguration;
import presidio.monitoring.elastic.services.PresidioMetricPersistencyService;
import presidio.monitoring.endPoint.PresidioMetricBucket;
import presidio.monitoring.endPoint.PresidioSystemMetricsFactory;
import presidio.monitoring.records.Metric;
import presidio.monitoring.sdk.api.services.enums.MetricEnums;
import presidio.monitoring.services.export.MetricsExporter;
import presidio.monitoring.services.export.MetricsExporterElasticImpl;
import presidio.monitoring.spring.MetricPersistencyServiceTestConfig;
import presidio.monitoring.spring.TestConfig;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@ContextConfiguration(classes = {TestConfig.class, MetricPersistencyServiceTestConfig.class})
@Ignore
public class MetricBucketSyncTest {

    private final String APPLICATION_NAME = "metricGeneratorSyncTest";
    private PresidioSystemMetricsFactory systemMetricFactory = new PresidioSystemMetricsFactory(APPLICATION_NAME);
    private MetricConventionApplyer metricConventionApplyer = new PresidioMetricConventionApplyer(APPLICATION_NAME);
    public PresidioMetricBucket presidioMetricBucket = new PresidioMetricBucket(systemMetricFactory, metricConventionApplyer);

    @Autowired
    private PresidioMetricPersistencyService presidioMetricPersistencyService;

    @Test
    @Ignore
    public void testSynchronization() {
        Map<MetricEnums.MetricTagKeysEnum, String> tags = new HashMap<>();
        tags.put(MetricEnums.MetricTagKeysEnum.FEATURE_NAME, "feature1");
        Instant logicalTime = Instant.now();

        new Thread(new Runnable() {
            @Override
            public void run() {
                MetricsExporter metricsExporter = new MetricsExporterElasticImpl(presidioMetricBucket, presidioMetricPersistencyService, taskScheduler());

                while (true) {
                    metricsExporter.export();
                }
            }
        }).start();

        MetricsExporter metricsExporter = new MetricsExporterElasticImpl(presidioMetricBucket, presidioMetricPersistencyService, taskScheduler());
        metricsExporter.export();

        try {
            while (true)
                presidioMetricBucket.addMetric(new Metric.MetricBuilder().
                        setMetricName("testValue").
                        setMetricValue(1).
                        setMetricTags(tags).
                        setMetricLogicTime(logicalTime).
                        build());
        } catch (Exception e){
            Assert.fail();
            throw e;
        }
    }

    private ThreadPoolTaskScheduler taskScheduler() {
        org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler ts = new ThreadPoolTaskScheduler();
        ts.setWaitForTasksToCompleteOnShutdown(true);
        ts.setAwaitTerminationSeconds(5);
        return ts;
    }
}
