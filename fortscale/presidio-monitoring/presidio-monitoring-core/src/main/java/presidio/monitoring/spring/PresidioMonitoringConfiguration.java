package presidio.monitoring.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.monitoring.aspect.MonitoringAspects;
import presidio.monitoring.aspect.MonitroingAspectSetup;
import presidio.monitoring.endPoint.PresidioMetricBucket;
import presidio.monitoring.endPoint.PresidioSystemMetricsFactory;
import presidio.monitoring.services.MetricCollectingService;
import presidio.monitoring.services.MetricCollectingServiceImpl;
import presidio.monitoring.services.MetricConventionApplyer;
import presidio.monitoring.services.PresidioMetricConventionApplyer;

@Configuration
@Import(MonitoringConfiguration.class)
public class PresidioMonitoringConfiguration {

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public MetricCollectingService metricCollectingService() {
        return new MetricCollectingServiceImpl(presidioMetricBucket());
    }

    @Bean
    public MetricConventionApplyer metricNameTransformer() {
        return new PresidioMetricConventionApplyer();
    }

    @Bean
    public PresidioMetricBucket presidioMetricBucket() {
        return new PresidioMetricBucket(new PresidioSystemMetricsFactory(applicationName), metricNameTransformer());
    }

    @Bean
    public MonitoringAspects monitoringAspects() {
        return new MonitoringAspects();
    }

    @Bean
    public MonitroingAspectSetup monitroingAspectSetup() {
        return new MonitroingAspectSetup(presidioMetricBucket());
    }

}
