package presidio.monitoring.sdk.impl;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import presidio.monitoring.elastic.services.PresidioMetricPersistencyService;
import presidio.monitoring.sdk.impl.spring.ExternalMonitoringConfiguration;
import presidio.monitoring.sdk.impl.spring.ExternalMonitoringTestConfiguration;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ExternalMonitoringTestConfiguration.class)
@ActiveProfiles("useEmbeddedElastic")
public class ExternalMonitoringServiceTest {

    @Autowired
    PresidioMetricPersistencyService metricExportService;

    @Test
    public void testConfig() {
        Assert.notNull(metricExportService, "External monitoring context cannot be null");
    }
}
