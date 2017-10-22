package presidio.output.proccesor.spring;

import fortscale.utils.elasticsearch.config.ElasticsearchTestUtils;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;

import java.util.Properties;

@Configuration
@EnableSpringConfigured
public class TestConfig {
    @Bean
    public static TestPropertiesPlaceholderConfigurer testPropertiesPlaceholderConfigurer() {
        Properties properties = new Properties();
        properties.put("severity.critical", 95);
        properties.put("severity.high", 85);
        properties.put("severity.mid", 70);
        properties.put("severity.low", 50);
        properties.put("smart.threshold.score", 0);
        properties.put("smart.page.size", 50);
        properties.put("elasticsearch.clustername", ElasticsearchTestUtils.EL_TEST_CLUSTER);
        properties.put("elasticsearch.host", "localhost");
        properties.put("elasticsearch.port", ElasticsearchTestUtils.EL_TEST_PORT);
        properties.put("number.of.classifications", 19);
        return new TestPropertiesPlaceholderConfigurer(properties);
    }
}
