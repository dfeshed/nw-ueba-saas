package presidio.monitoring.spring;

import fortscale.utils.elasticsearch.config.ElasticsearchTestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import presidio.monitoring.datadog.PresidioMetricDataDogService;
import presidio.monitoring.elastic.allindexrepo.MetricsAllIndexesRepository;
import presidio.monitoring.elastic.allindexrepo.MetricsAllIndexesRepositoryConfig;
import presidio.monitoring.elastic.repositories.MetricRepository;
import presidio.monitoring.elastic.services.PresidioMetricPersistencyService;
import presidio.monitoring.elastic.services.PresidioMetricPersistencyServiceImpl;
import presidio.monitoring.endPoint.PresidioMetricBucket;
import presidio.monitoring.endPoint.PresidioSystemMetricsFactory;
import presidio.monitoring.generator.MetricGeneratorService;
import presidio.monitoring.services.MetricConventionApplyer;
import presidio.monitoring.services.PresidioMetricConventionApplyer;

import java.util.List;

@Configuration
@EnableElasticsearchRepositories(basePackages = {"presidio.monitoring.elastic.repositories","presidio.monitoring.repository"})
@Import({ElasticsearchTestConfig.class,MetricsAllIndexesRepositoryConfig.class, TestConfig.class})
public class MetricPersistencyServiceTestConfig {

    @Value("${datadog.port}")
    private int dataDogPort;

    @Value("${datadog.host}")
    private String dataDogHostName;

    @Value("${datadog.metrics}")
    private List<String> dataDogMetricNames;

    @Bean
    public MetricGeneratorService metricGeneratorService() {
        return new MetricGeneratorService();
    }

    @Autowired
    public MetricRepository metricRepository;

    @Autowired
    public MetricsAllIndexesRepository metricsAllIndexesRepository;

    @Bean
    public MetricConventionApplyer metricConventionApplyer() {
        return new PresidioMetricConventionApplyer(applicationName);
    }

    @Bean
    public PresidioMetricPersistencyService presidioMetricPersistencyService() {
        return new PresidioMetricPersistencyServiceImpl(metricRepository, metricsAllIndexesRepository);
    }

    @Bean
    public PresidioMetricDataDogService presidioMetricDataDogService() {
        return new PresidioMetricDataDogService(dataDogHostName, dataDogPort , dataDogMetricNames);
    }

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public PresidioMetricBucket presidioMetricBucket() {
        return new PresidioMetricBucket(new PresidioSystemMetricsFactory(applicationName), metricConventionApplyer());
    }


}
