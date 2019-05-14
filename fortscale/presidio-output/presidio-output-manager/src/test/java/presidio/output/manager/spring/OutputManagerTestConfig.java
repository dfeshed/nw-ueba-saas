package presidio.output.manager.spring;

import fortscale.utils.mongodb.util.MongoDbBulkOpUtilConfig;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.output.manager.config.OutputManagerBaseConfig;

import java.util.Properties;

@Configuration
@Import({
        MongodbTestConfig.class,
        MongoDbBulkOpUtilConfig.class
})
public class OutputManagerTestConfig extends OutputManagerBaseConfig {

    @Bean
    public static TestPropertiesPlaceholderConfigurer testPropertiesPlaceholderConfigurer() {
        Properties properties = new Properties();
        properties.put("output.enriched.events.retention.in.days", 2);
        return new TestPropertiesPlaceholderConfigurer(properties);
    }
}
