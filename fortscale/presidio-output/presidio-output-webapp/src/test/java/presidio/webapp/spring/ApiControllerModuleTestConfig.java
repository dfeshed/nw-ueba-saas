package presidio.webapp.spring;

import fortscale.utils.RestTemplateConfig;
import fortscale.utils.elasticsearch.config.ElasticsearchTestConfig;
import fortscale.utils.elasticsearch.config.EmbeddedElasticsearchInitialiser;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtilConfig;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Properties;

@Configuration
@Import({OutputWebappTestConfiguration.class, RestTemplateConfig.class, ElasticsearchTestConfig.class, MongodbTestConfig.class, MongoDbBulkOpUtilConfig.class})
public class ApiControllerModuleTestConfig {

    @Bean
    public static TestPropertiesPlaceholderConfigurer configurationApiControllerSpringTestPlaceholder() {
        Properties properties = new Properties();
        properties.put("default.page.size.for.rest.user", "1000");
        properties.put("default.page.number.for.rest.user", "1000");
        properties.put("default.page.size.for.rest.alert", "1000");
        properties.put("default.page.number.for.rest.alert", "1000");
        properties.put("elasticsearch.port", EmbeddedElasticsearchInitialiser.EL_TEST_PORT);
        properties.put("elasticsearch.clustername", EmbeddedElasticsearchInitialiser.EL_TEST_CLUSTER);
        properties.put("elasticsearch.host", "localhost");
        properties.put("severity.critical", 95);
        properties.put("severity.high", 90);
        properties.put("severity.mid", 80);
        properties.put("indicators.store.page.size", 80);
        properties.put("events.store.page.size", 80);
        return new TestPropertiesPlaceholderConfigurer(properties);
    }
}
