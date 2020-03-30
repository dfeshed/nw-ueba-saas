package presidio.output.sdk.impl.services.spring;

import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;

import java.util.Collections;
import java.util.Properties;

@Configuration
@EnableSpringConfigured
public class TestConfig {
    @Bean
    public static TestPropertiesPlaceholderConfigurer testPropertiesPlaceholderConfigurer() {
        Properties properties = new Properties();
        properties.put("enable.metrics.export",false);
        properties.put("datadog.host", "localhost");
        properties.put("datadog.port", 8125);
        properties.put("datadog.metrics", Collections.emptyList());
        return new TestPropertiesPlaceholderConfigurer(properties);
    }
}
