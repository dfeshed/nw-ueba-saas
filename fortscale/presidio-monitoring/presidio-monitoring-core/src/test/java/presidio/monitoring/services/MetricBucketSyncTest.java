package presidio.monitoring.services;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
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

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestConfig.class, MetricPersistencyServiceTestConfig.class})
@Ignore
// This test is for manually both adding and exporting metrics. Cannot run as part of the unit tests
public class MetricBucketSyncTest {

    private final String APPLICATION_NAME = "metricGeneratorSyncTest";
    private PresidioSystemMetricsFactory systemMetricFactory = new PresidioSystemMetricsFactory(APPLICATION_NAME);
    private MetricConventionApplyer metricConventionApplyer = new PresidioMetricConventionApplyer(APPLICATION_NAME);
    public PresidioMetricBucket presidioMetricBucket = new PresidioMetricBucket(systemMetricFactory, metricConventionApplyer);

    @Autowired
    private PresidioMetricPersistencyService presidioMetricPersistencyService;

    @Test
    @Ignore
    public void testSynchronization() throws InterruptedException {
        Map<MetricEnums.MetricTagKeysEnum, String> tags = new HashMap<>();
        tags.put(MetricEnums.MetricTagKeysEnum.FEATURE_NAME, "feature1");
        Instant logicalTime = Instant.now();

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("start");
                MetricsExporter metricsExporter = new MetricsExporterElasticImpl(presidioMetricBucket, presidioMetricPersistencyService, taskScheduler());

                while (true) {
                    try {
                        System.out.println("loop");
                        metricsExporter.export();
                        System.out.println("end loop");
                        Thread.sleep(50);
                    } catch (Exception e) {
                        System.out.println(e);
                    }

                }
            }
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    try {
                        System.out.println("add");
                        presidioMetricBucket.addMetric(new Metric.MetricBuilder().
                                setMetricName("testValue").
                                setMetricValue(1).
                                setMetricTags(tags).
                                setMetricLogicTime(logicalTime).
                                build());
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

    }

    private ThreadPoolTaskScheduler taskScheduler() {
        org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler ts = new ThreadPoolTaskScheduler();
        ts.setWaitForTasksToCompleteOnShutdown(true);
        ts.setAwaitTerminationSeconds(5);
        return ts;
    }
}
