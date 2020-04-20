package com.rsa.netwitness.presidio.automation.utils.ade.config;

import fortscale.utils.mongodb.index.DynamicIndexingApplicationListenerConfig;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import presidio.ade.domain.record.RecordReaderFactoryServiceConfig;
import presidio.ade.sdk.common.AdeManagerSdk;
import presidio.ade.sdk.common.AdeManagerSdkConfig;
import com.rsa.netwitness.presidio.automation.data.processing.mongo_core.ADETestManager;
import com.rsa.netwitness.presidio.automation.utils.ade.inserter.AdeInserterFactory;
import presidio.monitoring.elastic.allindexrepo.MetricsAllIndexesRepository;
import presidio.monitoring.elastic.repositories.MetricRepository;

import java.util.Properties;


@Configuration
@Import({AdeManagerSdkConfig.class, AdeInserterFactoryConfig.class,DynamicIndexingApplicationListenerConfig.class,
        RecordReaderFactoryServiceConfig.class})
public class ADETestManagerConfig {
    @Autowired
    private AdeManagerSdk adeManagerSDK;
    @Autowired
    private AdeInserterFactory adeInserterFactory;

    @MockBean
    private MetricRepository metricRepository;
    @MockBean
    private ElasticsearchOperations elasticsearchOperations;
    @Mock
    private MetricsAllIndexesRepository metricsAllIndexesRepository;

    @Bean
    public ADETestManager getAdeTestManager(){
        return new ADETestManager(adeManagerSDK,adeInserterFactory);
    }

    @Bean
    public static TestPropertiesPlaceholderConfigurer ademanagerTestConfigurer()
    {
        Properties properties = new Properties();
        properties.put("fortscale.ademanager.aggregation.bucket.conf.json.file.name","file:///var/lib/netwitness/presidio/asl/feature-buckets/**/**/*.json");
        properties.put("streaming.event.field.type.aggr_event","aggr_event");
        properties.put("streaming.aggr_event.field.context","context");
        properties.put("fortscale.ademanager.aggregation.feature.event.conf.json.file.name","file:///var/lib/netwitness/presidio/asl/aggregation-records/**/*.json");
        properties.put("enable.metrics.export",false);
        properties.put("spring.application.name","score-aggregation");
        return new TestPropertiesPlaceholderConfigurer(properties);
    }
}
