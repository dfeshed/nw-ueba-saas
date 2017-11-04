package presidio.monitoring.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.monitoring.generator.MetricGeneratorService;

@Configuration
@Import({PresidioMonitoringConfiguration.class, TestConfig.class})
public class MetricGenerateServiceTestConfig {

    @Bean
    public MetricGeneratorService metricGeneratorService() {
        return new MetricGeneratorService();
    }

}
