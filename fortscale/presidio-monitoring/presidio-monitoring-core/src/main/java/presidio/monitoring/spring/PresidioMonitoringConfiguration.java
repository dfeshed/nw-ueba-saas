package presidio.monitoring.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.monitoring.endPoint.PresidioMetricEndPoint;
import presidio.monitoring.endPoint.PresidioSystemMetrics;
import presidio.monitoring.services.MetricCollectingService;
import presidio.monitoring.services.MetricCollectingServiceImpl;

@Configuration
@Import(MonitoringConfiguration.class)
public class PresidioMonitoringConfiguration {

    @Bean
    public MetricCollectingService metricCollectingService() {
        return new MetricCollectingServiceImpl(presidioMetricEndPoint());
    }

    @Bean
    public PresidioMetricEndPoint presidioMetricEndPoint() {
        return new PresidioMetricEndPoint(new PresidioSystemMetrics());
    }
}
