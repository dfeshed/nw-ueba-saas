package fortscale.aggregation.feature.bucket;

import fortscale.utils.mongodb.util.MongoDbUtilService;
import fortscale.utils.mongodb.util.MongoDbUtilServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.concurrent.TimeUnit;

/**
 * Created by YaronDL on 7/3/2017.
 */
@Configuration
@Import({MongoDbUtilServiceConfig.class})
public class FeatureBucketStoreMongoConfig {
    private static final long DEFAULT_EXPIRE_AFTER_SECONDS = TimeUnit.DAYS.toSeconds(90);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MongoDbUtilService mongoDbUtilService;

    @Bean
    public FeatureBucketStore featureBucketStore() {
        return new FeatureBucketStoreMongoImpl(mongoTemplate, mongoDbUtilService, DEFAULT_EXPIRE_AFTER_SECONDS);
    }
}
