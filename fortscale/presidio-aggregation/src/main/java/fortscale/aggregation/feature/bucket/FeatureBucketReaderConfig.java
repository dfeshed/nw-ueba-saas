package fortscale.aggregation.feature.bucket;

import fortscale.utils.mongodb.util.MongoDbUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.concurrent.TimeUnit;

@Configuration
public class FeatureBucketReaderConfig {
	private static final long DEFAULT_EXPIRE_AFTER_SECONDS = TimeUnit.DAYS.toSeconds(90);

	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private MongoDbUtilService mongoDbUtilService;

	@Bean
	public FeatureBucketReader featureBucketReader() {
		return new FeatureBucketStoreMongoImpl(mongoTemplate, mongoDbUtilService, DEFAULT_EXPIRE_AFTER_SECONDS);
	}
}
