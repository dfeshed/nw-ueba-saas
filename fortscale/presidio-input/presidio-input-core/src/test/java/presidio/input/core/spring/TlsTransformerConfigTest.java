package presidio.input.core.spring;

import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;

import java.time.Duration;
import java.util.Properties;

@Configuration
@EnableSpringConfigured
public class TlsTransformerConfigTest {
    @Bean
    public static TestPropertiesPlaceholderConfigurer inputCoreTestConfigurer() {
        Properties properties = new Properties();
        properties.put("dataPipeline.startTime", "2019-01-01T00:00:00Z");
        properties.put("presidio.input.core.transformation.waiting.duration", Duration.ZERO.toString());
        return new TestPropertiesPlaceholderConfigurer(properties);
    }
}
