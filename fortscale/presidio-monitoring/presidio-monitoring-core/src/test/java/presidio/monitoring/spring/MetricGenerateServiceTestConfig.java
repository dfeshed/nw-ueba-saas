package presidio.monitoring.spring;

import fortscale.utils.elasticsearch.config.ElasticsearchTestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import presidio.monitoring.elastic.repositories.MetricRepository;
import presidio.monitoring.elastic.services.PresidioMetricPersistencyService;
import presidio.monitoring.elastic.services.PresidioMetricPersistencyServiceImpl;
import presidio.monitoring.endPoint.PresidioMetricBucket;
import presidio.monitoring.endPoint.PresidioSystemMetricsFactory;
import presidio.monitoring.generator.MetricGeneratorService;

@Configuration
@EnableElasticsearchRepositories(basePackages = "presidio.monitoring.elastic.repositories")
@Import({ElasticsearchTestConfig.class, TestConfig.class})
public class MetricGenerateServiceTestConfig {

    @Bean
    public MetricGeneratorService metricGeneratorService() {
        return new MetricGeneratorService();
    }

    @Autowired
    public MetricRepository metricRepository;

    @Bean
    public PresidioMetricPersistencyService metricExportService() {
        return new PresidioMetricPersistencyServiceImpl(metricRepository);
    }

    private String applicationName = "metricGeneratorTest";

    @Bean
    public PresidioMetricBucket presidioMetricBucket() {
        return new PresidioMetricBucket(new PresidioSystemMetricsFactory(applicationName), applicationName);
    }


}
