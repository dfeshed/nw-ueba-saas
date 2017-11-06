package presidio.monitoring.sdk.impl;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import presidio.monitoring.sdk.impl.spring.ExternalMonitoringConfiguration;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ExternalMonitoringConfiguration.class)
public class ExternalMonitoringServiceTest {


    @Test
    public void testConfig() {
        ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(ExternalMonitoringConfiguration.class);
        Assert.notNull(context, "");
    }
}
