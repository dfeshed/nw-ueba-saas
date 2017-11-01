package presidio.monitoring;


import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.monitoring.elastic.services.PresidioMetricPersistencyService;
import presidio.monitoring.generator.MetricGeneratorService;
import presidio.monitoring.records.MetricDocument;
import presidio.monitoring.spring.MetricGenerateServiceTestConfig;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

@Ignore
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = MetricGenerateServiceTestConfig.class)
public class MetricsModuleTest {

    @Autowired
    private MetricGeneratorService metricGeneratorService;

    @Autowired
    private PresidioMetricPersistencyService presidioMetricPersistencyService;

    @Test
    public void createTestMetrics() {
        Instant from = Instant.now().minusMillis(1000000);
        Instant to = Instant.now().minusMillis(1000);
        List values = new LinkedList();
        values.add(100);
        values.add(50);
        values.add(10);
        List<MetricDocument> metricList = metricGeneratorService.generateMetrics(100, from, to, "test", values, "test", null, false);
    }
}
