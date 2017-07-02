package fortscale.ml.scorer.factory;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketsReaderService;
import fortscale.global.configuration.GlobalConfiguration;
import fortscale.ml.model.cache.EventModelsCacheService;
import fortscale.ml.model.cache.ModelsCacheService;
import fortscale.ml.scorer.factory.config.ScorersFactoryConfig;
import fortscale.utils.mongodb.util.MongoDbUtilService;
import fortscale.utils.monitoring.stats.config.NullStatsServiceConfig;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Created by barak_schuster on 6/29/17.
 */
@Configuration
@Import({ScorersFactoryConfig.class, GlobalConfiguration.class, NullStatsServiceConfig.class})
public class ScorerFactoriesTestConfig {
    @MockBean
    private BucketConfigurationService bucketConfigurationService;
    @MockBean
    private FeatureBucketsReaderService featureBucketsReaderService;
    @MockBean
    private EventModelsCacheService eventModelsCacheService;
    @MockBean
    private MongoTemplate mongoTemplate;
    @MockBean
    private MongoDbUtilService mongoDbUtilService;

    @MockBean
    private ModelsCacheService modelCacheService;
}
