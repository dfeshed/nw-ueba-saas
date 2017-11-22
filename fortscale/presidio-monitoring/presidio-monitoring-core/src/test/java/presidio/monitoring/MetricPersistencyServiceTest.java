package presidio.monitoring;


import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.monitoring.elastic.repositories.MetricRepository;
import presidio.monitoring.elastic.services.PresidioMetricPersistencyService;
import presidio.monitoring.endPoint.PresidioMetricBucket;
import presidio.monitoring.generator.MetricGeneratorService;
import presidio.monitoring.records.Metric;
import presidio.monitoring.records.MetricDocument;
import presidio.monitoring.sdk.api.services.enums.MetricEnums;
import presidio.monitoring.spring.MetricGenerateServiceTestConfig;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = MetricGenerateServiceTestConfig.class)
public class MetricPersistencyServiceTest {

    @Autowired
    private MetricGeneratorService metricGeneratorService;

    @Autowired
    private PresidioMetricPersistencyService presidioMetricPersistencyService;

    @Autowired
    public MetricRepository metricRepository;

    @Autowired
    public PresidioMetricBucket presidioMetricBucket;

    @After
    public void deleteTestData() {
        metricRepository.delete(metricRepository.findAll());
    }

    @Test
    public void createTestMetrics() {
        Instant from = Instant.now().minusMillis(1000000);
        Instant to = Instant.now().minusMillis(1000);
        List<Number> values = new LinkedList();
        values.add(100);
        values.add(50);
        values.add(10);
        List<MetricDocument> metricList = metricGeneratorService.generateMetrics(100, from, to, values,
                new Metric.MetricBuilder().setMetricName("test")
                        .setMetricTags(new HashMap<>())
                        .build());
        presidioMetricPersistencyService.save(metricList);

        //verify first metric only:
        MetricDocument metric = metricRepository.findAll().iterator().next();
        Assert.assertFalse(metric.getValue().isEmpty());
        Assert.assertEquals("test", metric.getName());
        Assert.assertEquals(100, metricRepository.count());
    }

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
