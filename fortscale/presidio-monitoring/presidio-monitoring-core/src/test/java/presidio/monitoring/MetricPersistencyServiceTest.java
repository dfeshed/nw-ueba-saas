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
import presidio.monitoring.enums.MetricEnums;
import presidio.monitoring.generator.MetricGeneratorService;
import presidio.monitoring.records.MetricDocument;
import presidio.monitoring.spring.MetricGenerateServiceTestConfig;

import java.time.Instant;
import java.util.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = MetricGenerateServiceTestConfig.class)
public class MetricPersistencyServiceTest {

    @Autowired
    private MetricGeneratorService metricGeneratorService;

    @Autowired
    private PresidioMetricPersistencyService presidioMetricPersistencyService;

    @Autowired
    public MetricRepository metricRepository;

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
        Map<MetricEnums.MetricTagKeysEnum, String> tags = new HashMap<>();
        List<MetricDocument> metricList = metricGeneratorService.generateMetrics(100, from, to, "test", values, tags);
        presidioMetricPersistencyService.save(metricList);

        Assert.assertEquals(100, metricRepository.count());
        //verify first metric only:
        MetricDocument metric = metricRepository.findAll().iterator().next();
        Assert.assertFalse(metric.getValue().isEmpty());
        Assert.assertEquals("test", metric.getName());
    }
}
