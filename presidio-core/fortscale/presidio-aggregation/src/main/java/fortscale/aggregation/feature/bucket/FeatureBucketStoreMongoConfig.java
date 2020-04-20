package fortscale.aggregation.feature.bucket;

import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtilConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Created by YaronDL on 7/3/2017.
 */
@Configuration
@Import(MongoDbBulkOpUtilConfig.class)
public class FeatureBucketStoreMongoConfig {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MongoDbBulkOpUtil mongoDbBulkOpUtil;
    @Value("${model.selector.contextId.page.size:50000}")
    private long selectorPageSize;

    @Bean
    public FeatureBucketStore featureBucketStore() {
        return new FeatureBucketStoreMongoImpl(mongoTemplate, mongoDbBulkOpUtil, selectorPageSize);
    }
}
