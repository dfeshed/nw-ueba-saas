package fortscale.aggregation.feature.bucket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeatureBucketReaderConfig {
	@Bean
	public FeatureBucketReader featureBucketReader() {
		// TODO
		return new FeatureBucketStoreMongoImpl(null, null);
	}
}
