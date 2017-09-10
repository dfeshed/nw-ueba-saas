package presidio.ade.manager;

import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.manager.config.ManagerApplicationConfig;

import java.util.Properties;


@Configuration
@Import({MongodbTestConfig.class})
public class ManagerApplicationConfigurationTest extends ManagerApplicationConfig {
    @Bean
    public static TestPropertiesPlaceholderConfigurer managerApplicationTestProperties() {
        Properties properties = new Properties();
        properties.put("spring.application.name", "manager-app-name-test");
        properties.put("presidio.enriched.ttl.duration", "PT5H");
        properties.put("presidio.enriched.cleanup.interval", "PT24H");
        return new TestPropertiesPlaceholderConfigurer(properties);
    }


}