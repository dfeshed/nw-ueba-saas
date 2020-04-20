package presidio.ade.modeling.config;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Lior Govrin
 */
@Configuration
public class ModelingServiceFeatureBucketConfServiceConfig {
	@Value("${presidio.ade.modeling.feature.bucket.confs.base.path}")
	private String featureBucketConfsBasePath;
	@Value("${presidio.ade.modeling.feature.bucket.confs.overriding.path:#{null}}")
	private String featureBucketConfsOverridingPath;
	@Value("${presidio.ade.modeling.feature.bucket.confs.additional.path:#{null}}")
	private String featureBucketConfsAdditionalPath;

	@Bean
	public BucketConfigurationService bucketConfigurationService() {
		return new BucketConfigurationService(
				featureBucketConfsBasePath,
				featureBucketConfsOverridingPath,
				featureBucketConfsAdditionalPath);
	}
}
