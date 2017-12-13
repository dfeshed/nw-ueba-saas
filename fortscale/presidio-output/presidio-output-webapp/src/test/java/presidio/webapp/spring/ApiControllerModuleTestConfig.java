package presidio.webapp.spring;

import fortscale.utils.RestTemplateConfig;
import fortscale.utils.elasticsearch.config.ElasticsearchTestConfig;
import fortscale.utils.elasticsearch.config.EmbeddedElasticsearchInitialiser;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.output.commons.services.spring.UserSeverityServiceConfig;

import java.util.Properties;

@Configuration
@Import({OutputWebappTestConfiguration.class, RestTemplateConfig.class, ElasticsearchTestConfig.class, UserSeverityServiceConfig.class})
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
        return new TestPropertiesPlaceholderConfigurer(properties);
    }
}
