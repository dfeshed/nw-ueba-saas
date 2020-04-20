package presidio.ade.manager;

import fortscale.utils.elasticsearch.PresidioElasticsearchTemplate;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.manager.config.AdeManagerApplicationConfig;
import presidio.monitoring.elastic.allindexrepo.MetricsAllIndexesRepository;
import presidio.monitoring.elastic.repositories.MetricRepository;
import presidio.monitoring.spring.PresidioMonitoringConfiguration;

import java.util.Collections;
import java.util.Properties;


@Configuration
@Import({MongodbTestConfig.class,PresidioMonitoringConfiguration.class})
public class AdeManagerApplicationConfigurationTest extends AdeManagerApplicationConfig {

    @MockBean
    private MetricRepository metricRepository;
    @MockBean
    private MetricsAllIndexesRepository metricsAllIndexesRepository;
    @MockBean
    private PresidioElasticsearchTemplate elasticsearchTemplate;

    @Bean
    public static TestPropertiesPlaceholderConfigurer managerApplicationTestProperties() {
        Properties properties = new Properties();
        properties.put("spring.application.name", "ade-manager");
        properties.put("presidio.enriched.ttl.duration", "PT24H");
        properties.put("presidio.enriched.cleanup.interval", "PT24H");
        properties.put("enable.metrics.export", false);
        properties.put("monitoring.fixed.rate","60000");
        properties.put("datadog.host", "localhost");
        properties.put("datadog.port", 8125);
        properties.put("datadog.metrics", Collections.emptyList());

        return new TestPropertiesPlaceholderConfigurer(properties);
    }


}