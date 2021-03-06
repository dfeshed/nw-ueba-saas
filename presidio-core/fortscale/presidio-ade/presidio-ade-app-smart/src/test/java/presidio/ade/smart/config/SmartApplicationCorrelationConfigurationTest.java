package presidio.ade.smart.config;

import fortscale.utils.elasticsearch.PresidioElasticsearchTemplate;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.monitoring.elastic.allindexrepo.MetricsAllIndexesRepository;
import presidio.monitoring.elastic.repositories.MetricRepository;
import presidio.monitoring.spring.PresidioMonitoringConfiguration;

import java.util.Collections;
import java.util.Properties;


@Configuration
@Import({MongodbTestConfig.class, PresidioMonitoringConfiguration.class})
public class SmartApplicationCorrelationConfigurationTest extends SmartApplicationConfiguration{

    @MockBean
    private MetricRepository metricRepository;
    @MockBean
    private MetricsAllIndexesRepository metricsAllIndexesRepository;
    @MockBean
    private PresidioElasticsearchTemplate elasticsearchTemplate;
    
    @Bean
    public static TestPropertiesPlaceholderConfigurer smartApplicationConfigurationTestPropertiesPlaceholderConfigurer() {
        Properties properties = new Properties();
        // Feature bucket conf service (for both buckets of Ps and Fs)
        properties.put("presidio.ade.feature.bucket.base.configurations.path", "classpath*:config/asl/feature-buckets/**/*.json");
        // Feature aggregation event conf service (for both Ps and Fs)
        properties.put("presidio.ade.aggregation.record.base.configurations.path", "classpath*:config/asl/aggregation-records/**/*.json");
        // Smart event conf service
        properties.put("presidio.ade.smart.record.base.configurations.path","classpath*:config/asl/smart-records/smart_records_test.json");
        // Smart scorer conf service
        properties.put("presidio.ade.scorer.base.configurations.path","classpath*:config/asl/scorers/smart-records/*.json");
        // Model conf service
        properties.put("presidio.modeling.base.configurations.path", "classpath*:config/asl/models/smart-records/*.json");
        // Additional properties
        properties.put("presidio.ade.aggregation.records.threshold", 10);
        properties.put("presidio.ade.aggregation.data.pagination.service.num.of.context.ids.in.page","1000");
        properties.put("presidio.application.name","smart");
        properties.put("presidio.default.ttl.duration","PT48H");
        properties.put("presidio.default.cleanup.interval","PT24H");
        properties.put("fortscale.model.retriever.smart.oldestAllowedModelDurationDiff","PT48H");
        properties.put("fortscale.model.cache.maxDiffBetweenCachedModelAndEvent", "PT48H");
        properties.put("fortscale.model.cache.size", 100);
        properties.put("presidio.ade.model.smart.weights.score.minimal.cluster.score", 0);

        properties.put("enable.metrics.export", false);
        properties.put("monitoring.fixed.rate","60000");
        properties.put("datadog.host", "localhost");
        properties.put("datadog.port", 8125);
        properties.put("datadog.metrics", Collections.emptyList());
        return new TestPropertiesPlaceholderConfigurer(properties);
    }
}
