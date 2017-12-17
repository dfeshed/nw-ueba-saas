package presidio.monitoring.sdk.impl;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import presidio.monitoring.elastic.services.PresidioMetricPersistencyService;
import presidio.monitoring.sdk.api.services.PresidioExternalMonitoringService;
import presidio.monitoring.sdk.impl.factory.PresidioExternalMonitoringServiceFactory;
import presidio.monitoring.sdk.impl.spring.ExternalMonitoringTestConfiguration;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ExternalMonitoringTestConfiguration.class)
public class ExternalMonitoringServiceTest {

    @Autowired
    PresidioMetricPersistencyService metricExportService;

    @Test
    public void testConfig() {
        Assert.notNull(metricExportService, "External monitoring context cannot be null");
    }

    @Test
    public void createFactory() {
        PresidioExternalMonitoringServiceFactory presidioExternalMonitoringServiceFactory = new PresidioExternalMonitoringServiceFactory();
        try {
            PresidioExternalMonitoringService presidioExternalMonitoringService = presidioExternalMonitoringServiceFactory.createPresidioExternalMonitoringService("test");
        } catch (Exception ex) {
            Assert.notNull(null, " fail to create PresidioExternalMonitoringServiceFactory");
        }
    }
}
