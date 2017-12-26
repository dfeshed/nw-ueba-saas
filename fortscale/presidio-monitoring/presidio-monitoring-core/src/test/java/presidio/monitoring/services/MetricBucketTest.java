package presidio.monitoring.services;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.monitoring.endPoint.PresidioMetricBucket;
import presidio.monitoring.endPoint.PresidioSystemMetricsFactory;
import presidio.monitoring.records.Metric;
import presidio.monitoring.records.MetricDocument;
import presidio.monitoring.sdk.api.services.enums.MetricEnums;
import presidio.monitoring.spring.TestConfig;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class MetricBucketTest {


    private final String APPLICATION_NAME = "metricGeneratorTest";

    private PresidioSystemMetricsFactory systemMetricFactory = new PresidioSystemMetricsFactory(APPLICATION_NAME);
    private MetricConventionApplyer metricConventionApplyer = new PresidioMetricConventionApplyer(APPLICATION_NAME);
    public PresidioMetricBucket presidioMetricBucket = new PresidioMetricBucket(systemMetricFactory, metricConventionApplyer);

    @Test
    public void addingMetricToMetricBucketTest() {
        Instant logicalTime = Instant.EPOCH;
        Instant logicalTime2 = logicalTime.plusMillis(10000);
        Map<MetricEnums.MetricTagKeysEnum, String> tags1 = new HashMap<>();
        tags1.put(MetricEnums.MetricTagKeysEnum.FEATURE_NAME, "feature1");
        Map<MetricEnums.MetricTagKeysEnum, String> tags2 = new HashMap<>();
        tags2.put(MetricEnums.MetricTagKeysEnum.FEATURE_NAME, "feature2");
        presidioMetricBucket.addMetric(new Metric.MetricBuilder().
                setMetricName("testValue").
                setMetricValue(1).
                setMetricTags(tags1).
                setMetricLogicTime(logicalTime).
                build());
        presidioMetricBucket.addMetric(new Metric.MetricBuilder().
                setMetricName("testValue").
                setMetricValue(1).
                setMetricTags(tags1).
                setMetricLogicTime(logicalTime).
                build());
        presidioMetricBucket.addMetric(new Metric.MetricBuilder().
                setMetricName("test1").
                setMetricValue(1).
                setMetricTags(tags1).
                setMetricLogicTime(logicalTime).
                build());
        presidioMetricBucket.addMetric(new Metric.MetricBuilder().
                setMetricName("test1").
                setMetricValue(1).
                setMetricTags(tags1).
                setMetricLogicTime(logicalTime).
                build());
        presidioMetricBucket.addMetric(new Metric.MetricBuilder().
                setMetricName("test1").
                setMetricValue(1).
                setMetricTags(tags2).
                setMetricLogicTime(logicalTime).
                build());
        presidioMetricBucket.addMetric(new Metric.MetricBuilder().
                setMetricName("test2").
                setMetricValue(1).
                setMetricTags(tags1).
                setMetricLogicTime(logicalTime).
                build());
        presidioMetricBucket.addMetric(new Metric.MetricBuilder().
                setMetricName("test1").
                setMetricValue(1).
                setMetricTags(tags1).
                setMetricLogicTime(logicalTime2).
                build());
        List<MetricDocument> metricList = presidioMetricBucket.getApplicationMetricsAndResetApplicationMetrics();
        metricList.size();
        Assert.assertEquals(5, metricList.size());
        metricList.forEach(metric -> {
            if (metric.getName().equals("metricGeneratorTest.testValue") && metric.getLogicTime().equals(logicalTime)) {
                Assert.assertEquals(2, metric.getValue().get(MetricEnums.MetricValues.DEFAULT_METRIC_VALUE));
            }
        });
        metricList = presidioMetricBucket.getSystemMetrics();
        Assert.assertEquals(21, metricList.size());
    }

    @Test
    public void addingMetricWithMultipleValuesToMetricBucketTest() {
        Map<MetricEnums.MetricValues, Number> metricValues = new HashMap<>();
        metricValues.put(MetricEnums.MetricValues.fromValue("sum"), 3);
        metricValues.put(MetricEnums.MetricValues.fromValue("max"), 4);
        Map<MetricEnums.MetricValues, Number> metricValues2 = new HashMap<>();
        metricValues2.put(MetricEnums.MetricValues.fromValue("avg"), 4);
        metricValues2.put(MetricEnums.MetricValues.fromValue("max"), 5);
        Instant logicalTime = Instant.EPOCH;
        presidioMetricBucket.addMetric(new Metric.MetricBuilder().
                setMetricName("test1").
                setMetricMultipleValues(metricValues).
                setMetricLogicTime(logicalTime).
                build());
        presidioMetricBucket.addMetric(new Metric.MetricBuilder().
                setMetricName("test1").
                setMetricMultipleValues(metricValues2).
                setMetricLogicTime(logicalTime).
                build());

        List<MetricDocument> metricList = presidioMetricBucket.getApplicationMetricsAndResetApplicationMetrics();
        metricList.size();
        Assert.assertEquals(1, metricList.size());
        metricList.forEach(metric -> {
            if (metric.getName().equals("metricGeneratorTest.test1")) {
                Assert.assertEquals(3, metric.getValue().get(MetricEnums.MetricValues.fromValue("sum")));
                Assert.assertEquals(9, metric.getValue().get(MetricEnums.MetricValues.fromValue("max")));
                Assert.assertEquals(4, metric.getValue().get(MetricEnums.MetricValues.fromValue("avg")));
                Assert.assertEquals(3, metric.getValue().size());
            }
        });
    }

    @Test
    public void addingMetricWithLogicalTimeToMetricBucketTest() {
        Map<MetricEnums.MetricValues, Number> metricValues = new HashMap<>();
        metricValues.put(MetricEnums.MetricValues.fromValue("max"), 4);
        Map<MetricEnums.MetricValues, Number> metricValues2 = new HashMap<>();
        metricValues2.put(MetricEnums.MetricValues.fromValue("max"), 5);
        Instant logicalTime = Instant.EPOCH;
        Instant logicalTime2 = logicalTime.plusMillis(1);
        presidioMetricBucket.addMetric(new Metric.MetricBuilder().
                setMetricName("test1").
                setMetricMultipleValues(metricValues).
                setMetricLogicTime(logicalTime).
                build());
        presidioMetricBucket.addMetric(new Metric.MetricBuilder().
                setMetricName("test1").
                setMetricMultipleValues(metricValues2).
                setMetricLogicTime(logicalTime).
                build());
        presidioMetricBucket.addMetric(new Metric.MetricBuilder().
                setMetricName("test1").
                setMetricMultipleValues(metricValues2).
                setMetricLogicTime(logicalTime2).
                build());

        List<MetricDocument> metricList = presidioMetricBucket.getApplicationMetricsAndResetApplicationMetrics();
        metricList.size();
        Assert.assertEquals(2, metricList.size());
        metricList.forEach(metric -> {
            if (metric.getName().equals("metricGeneratorTest.test1") && metric.getLogicTime().toInstant().equals(logicalTime)) {
                Assert.assertEquals(9, metric.getValue().get(MetricEnums.MetricValues.fromValue("max")));
            }
        });
    }
}
