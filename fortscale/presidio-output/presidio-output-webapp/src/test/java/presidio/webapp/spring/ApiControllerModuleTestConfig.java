package presidio.webapp.spring;

import fortscale.utils.RestTemplateConfig;
import fortscale.utils.elasticsearch.config.ElasticsearchTestUtils;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Properties;

@Configuration
@Import({OutputWebappConfiguration.class, RestTemplateConfig.class})
public class ApiControllerModuleTestConfig {

    @Bean
    public static TestPropertiesPlaceholderConfigurer configurationApiControllerSpringTestPlaceholder() {
        Properties properties = new Properties();
        properties.put("default.page.size.for.rest.user", "1000");
        properties.put("default.page.number.for.rest.user", "1000");
        properties.put("default.page.size.for.rest.alert", "1000");
        properties.put("default.page.number.for.rest.alert", "1000");
        properties.put("elasticsearch.port", ElasticsearchTestUtils.EL_TEST_PORT);
        properties.put("elasticsearch.clustername", ElasticsearchTestUtils.EL_TEST_CLUSTER);
        properties.put("elasticsearch.host", "localhost");
        return new TestPropertiesPlaceholderConfigurer(properties);
    }
}
