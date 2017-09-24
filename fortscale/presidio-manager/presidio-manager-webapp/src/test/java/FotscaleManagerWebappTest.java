import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import presidio.webapp.controller.configuration.ConfigurationApiController;
import presidio.webapp.service.ConfigurationManagerService;
import presidio.webapp.spring.ManagerWebappConfiguration;

import java.util.Properties;

/**
 * Created by efratn on 10/08/2017.
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {FotscaleManagerWebappTest.ContextConfigurationInternal.class,ManagerWebappConfiguration.class})
public class FotscaleManagerWebappTest {

    @Autowired
    private ConfigurationManagerService configurationManagerService;

    @Autowired
    private ConfigurationApiController configurationApiController;
    @Test
    public void contextLoads() {
        Assert.notNull(configurationManagerService, "client service on sprint context cannot be null");
        configurationApiController.configurationGet();

    }


    @Configuration
    public static class ContextConfigurationInternal {
        @Bean
        public TestPropertiesPlaceholderConfigurer modelConfServiceTestPropertiesPlaceholderConfigurer() {
            Properties properties = new Properties();
            properties.put("keytab.file.path", "/path/file.postfix");
            properties.put("manager.dags.dag_id.fullFlow.prefix","full_flow");
            properties.put("manager.dags.state.buildingBaselineDuration","P30D");
            return new TestPropertiesPlaceholderConfigurer(properties);
        }
    }
}