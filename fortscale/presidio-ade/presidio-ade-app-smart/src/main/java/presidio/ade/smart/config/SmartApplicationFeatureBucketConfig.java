package presidio.ade.smart.config;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Lior Govrin
 */
@Configuration
public class SmartApplicationFeatureBucketConfig {
	@Value("${presidio.ade.feature.bucket.base.configurations.path}")
	private String featureBucketBaseConfigurationsPath;
	@Value("${presidio.ade.feature.bucket.overriding.configurations.path:#{null}}")
	private String featureBucketOverridingConfigurationsPath;
	@Value("${presidio.ade.feature.bucket.additional.configurations.path:#{null}}")
	private String featureBucketAdditionalConfigurationsPath;

	@Bean
	public BucketConfigurationService bucketConfigurationService() {
		return new BucketConfigurationService(
				featureBucketBaseConfigurationsPath,
				featureBucketOverridingConfigurationsPath,
				featureBucketAdditionalConfigurationsPath);
	}
}
