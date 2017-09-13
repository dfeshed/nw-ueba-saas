package presidio.ade.manager;

import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.manager.config.AdeManagerApplicationConfig;

import java.util.Properties;


@Configuration
@Import({MongodbTestConfig.class})
public class AdeManagerApplicationConfigurationTest extends AdeManagerApplicationConfig {
    @Bean
    public static TestPropertiesPlaceholderConfigurer managerApplicationTestProperties() {
        Properties properties = new Properties();
        properties.put("spring.application.name", "ade-manager");
        properties.put("presidio.enriched.ttl.duration", "PT5H");
        properties.put("presidio.enriched.cleanup.interval", "PT24H");
        return new TestPropertiesPlaceholderConfigurer(properties);
    }


}