package presidio.monitoring.spring;


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
        properties.put("elasticsearch.clustername", "fortscale");
        properties.put("elasticsearch.host", "localhost");
        properties.put("elasticsearch.port", 9300);
        properties.put("enable.metrics.export", false);

        return new TestPropertiesPlaceholderConfigurer(properties);
    }

}