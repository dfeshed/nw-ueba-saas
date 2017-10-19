package presidio.monitoring.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import presidio.monitoring.aspect.services.MetricCollectingService;
import presidio.monitoring.aspect.services.MetricCollectingServiceImpl;

@Import(MonitoringConfiguration.class)
public class PresidioMonitoringConfiguration {

    @Bean
    public MetricCollectingService metricCollectingService() {
        return new MetricCollectingServiceImpl();
    }
}
