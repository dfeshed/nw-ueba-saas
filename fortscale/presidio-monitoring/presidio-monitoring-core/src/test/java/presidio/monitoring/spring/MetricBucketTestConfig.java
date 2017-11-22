package presidio.monitoring.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import presidio.monitoring.endPoint.PresidioMetricBucket;
import presidio.monitoring.endPoint.PresidioSystemMetricsFactory;
import presidio.monitoring.services.MetricConventionApplyer;
import presidio.monitoring.services.PresidioMetricConventionApplyer;

@Configuration
@EnableElasticsearchRepositories(basePackages = "presidio.monitoring.elastic.repositories")
public class MetricBucketTestConfig {

    private final String APPLICATION_NAME = "metric-generator-test";

    @Bean
    public MetricConventionApplyer metricNameTransformer() {
        return new PresidioMetricConventionApplyer(APPLICATION_NAME);
    }

    @Bean
    public PresidioMetricBucket presidioMetricBucket() {
        return new PresidioMetricBucket(new PresidioSystemMetricsFactory(APPLICATION_NAME), metricNameTransformer());
    }


}
