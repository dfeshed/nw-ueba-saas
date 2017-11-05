package presidio.monitoring.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.monitoring.aspect.MonitoringAspects;
import presidio.monitoring.endPoint.PresidioMetricEndPoint;
import presidio.monitoring.endPoint.PresidioSystemMetricsFactory;
import presidio.monitoring.factory.PresidioMetricFactory;
import presidio.monitoring.services.MetricCollectingService;
import presidio.monitoring.services.MetricCollectingServiceImpl;

@Configuration
@Import(MonitoringConfiguration.class)
public class PresidioMonitoringConfiguration {

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public MetricCollectingService metricCollectingService() {
        return new MetricCollectingServiceImpl(presidioMetricEndPoint());
    }

    @Bean
    public PresidioMetricEndPoint presidioMetricEndPoint() {
        return new PresidioMetricEndPoint(new PresidioSystemMetricsFactory(applicationName));
    }

    @Bean
    public PresidioMetricFactory presidioMetricFactory() {
        return new PresidioMetricFactory(applicationName);
    }

    @Bean
    public MonitoringAspects monitoringAspects() {
        return new MonitoringAspects(presidioMetricEndPoint(), presidioMetricFactory());
    }

}
