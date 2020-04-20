package fortscale.aggregation.feature.bucket;

import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class FeatureBucketReaderConfig {

	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private MongoDbBulkOpUtil mongoDbBulkOpUtil;
	@Value("${model.selector.contextId.page.size:50000}")
	private long selectorPageSize;

	@Bean
	public FeatureBucketReader featureBucketReader() {
		return new FeatureBucketStoreMongoImpl(mongoTemplate, mongoDbBulkOpUtil, selectorPageSize);
	}
}
