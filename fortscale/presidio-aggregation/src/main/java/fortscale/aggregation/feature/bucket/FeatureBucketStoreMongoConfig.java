package fortscale.aggregation.feature.bucket;

import fortscale.utils.mongodb.config.MongoConfig;
import fortscale.utils.mongodb.util.MongoDbUtilService;
import fortscale.utils.mongodb.util.MongoDbUtilServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Created by YaronDL on 7/3/2017.
 */
@Configuration
@Import({MongoConfig.class,MongoDbUtilServiceConfig.class})
public class FeatureBucketStoreMongoConfig {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MongoDbUtilService mongoDbUtilService;

    @Bean
    public FeatureBucketStore getFeatureBucketStore(){
        return new FeatureBucketStoreMongoImpl(mongoTemplate,mongoDbUtilService);
    }
}
