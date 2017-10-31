package presidio.monitoring.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import presidio.monitoring.aspect.MonitoringAspects;
import presidio.monitoring.elastic.repositories.MetricRepository;
import presidio.monitoring.elastic.services.PresidioMetricPersistencyService;
import presidio.monitoring.elastic.services.PresidioMetricPersistencyServiceImpl;
import presidio.monitoring.endPoint.PresidioMetricEndPoint;
import presidio.monitoring.endPoint.PresidioSystemMetrics;
import presidio.monitoring.factory.PresidioMetricFactory;
import presidio.monitoring.services.MetricCollectingService;
import presidio.monitoring.services.MetricCollectingServiceImpl;

@Configuration
@EnableElasticsearchRepositories(basePackages = "presidio.monitoring.elastic.repositories")
@Import({fortscale.utils.elasticsearch.config.ElasticsearchConfig.class, MonitoringConfiguration.class})
public class PresidioMonitoringConfiguration {

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public MetricCollectingService metricCollectingService() {
        return new MetricCollectingServiceImpl(presidioMetricEndPoint());
    }

    @Bean
    public PresidioMetricEndPoint presidioMetricEndPoint() {
        return new PresidioMetricEndPoint(new PresidioSystemMetrics(applicationName));
    }

    @Bean
    public PresidioMetricFactory presidioMetricFactory() {
        return new PresidioMetricFactory(applicationName);
    }

    @Autowired
    private MetricRepository metricRepository;

    @Bean
    public PresidioMetricPersistencyService presidioMetricPersistencyService() {
        return new PresidioMetricPersistencyServiceImpl(metricRepository);
    }

    @Bean
    public MonitoringAspects monitoringAspects() {
        return new MonitoringAspects(presidioMetricEndPoint(), presidioMetricFactory());
    }

}
