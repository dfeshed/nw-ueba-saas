package presidio.monitoring.services;


import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.monitoring.elastic.repositories.MetricRepository;
import presidio.monitoring.endPoint.PresidioMetricBucket;
import presidio.monitoring.endPoint.PresidioSystemMetricsFactory;
import presidio.monitoring.records.Metric;
import presidio.monitoring.records.MetricDocument;
import presidio.monitoring.sdk.api.services.enums.MetricEnums;
import presidio.monitoring.spring.MetricBucketTestConfig;
import presidio.monitoring.spring.MetricPersistencyServiceTestConfig;
import presidio.monitoring.spring.TestConfig;

import java.util.List;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class MetricBucketTest {


    @Value("${spring.application.name}")
    private String applicationName;

    private PresidioSystemMetricsFactory systemMetricFactory = new PresidioSystemMetricsFactory(applicationName);
    private MetricConventionApplyer metricConventionApplyer = new PresidioMetricConventionApplyer();
    public PresidioMetricBucket presidioMetricBucket = new PresidioMetricBucket(systemMetricFactory, metricConventionApplyer);

    @Test
    public void addingMetricToMetricBucketTest() {
        presidioMetricBucket.addMetric(new Metric.MetricBuilder().
                setMetricName("test1").
                setMetricValue(1).
                build());
        presidioMetricBucket.addMetric(new Metric.MetricBuilder().
                setMetricName("test1").
                setMetricValue(1).
                build());
        presidioMetricBucket.addMetric(new Metric.MetricBuilder().
                setMetricName("test2").
                setMetricValue(1).
                build());
        List<MetricDocument> metricList = presidioMetricBucket.getApplicationMetrics();
        metricList.size();
        Assert.assertEquals(2, metricList.size());
        metricList.forEach(metric -> {
            if (metric.getName().equals("test1")) {
                Assert.assertEquals(2, metric.getValue().get(MetricEnums.MetricValues.DEFAULT_METRIC_VALUE));
            }
        });
        metricList = presidioMetricBucket.getSystemMetrics();
        Assert.assertEquals(21, metricList.size());
    }
}
