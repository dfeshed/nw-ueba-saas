package fortscale.ml.scorer.factory;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketReader;
import fortscale.global.configuration.GlobalConfiguration;
import fortscale.ml.model.cache.EventModelsCacheService;
import fortscale.ml.model.cache.ModelsCacheService;
import fortscale.ml.scorer.factory.config.ScorersFactoryConfig;
import fortscale.utils.mongodb.util.MongoDbUtilService;
import fortscale.utils.monitoring.stats.config.NullStatsServiceConfig;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Properties;

@Configuration
@Import({ScorersFactoryConfig.class, GlobalConfiguration.class, NullStatsServiceConfig.class})
public class ScorerFactoriesTestConfig {
    @MockBean
    @Qualifier("modelBucketConfigService")
    private BucketConfigurationService modelBucketConfigService;
    @MockBean
    private FeatureBucketReader featureBucketReader;
    @MockBean
    private EventModelsCacheService eventModelsCacheService;
    @MockBean
    private MongoTemplate mongoTemplate;
    @MockBean
    private MongoDbUtilService mongoDbUtilService;
    @MockBean
    private ModelsCacheService modelCacheService;
    @Bean
    public static TestPropertiesPlaceholderConfigurer scorerFactoriesTestProperties() {
        Properties properties = new Properties();
        properties.put("fortscale.model.aggregation.bucket.conf.json.file.name", "classpath:fortscale/config/asl/model/buckets/model_buckets_test.json");
        return new TestPropertiesPlaceholderConfigurer(properties);
    }
}
