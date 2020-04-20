package presidio.ade.smart.config;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Lior Govrin
 */
@Configuration
@Import(SmartApplicationFeatureBucketConfig.class)
public class SmartApplicationAggregationRecordConfig {
	@Value("${presidio.ade.aggregation.record.base.configurations.path}")
	private String aggregationRecordBaseConfigurationsPath;
	@Value("${presidio.ade.aggregation.record.overriding.configurations.path:#{null}}")
	private String aggregationRecordOverridingConfigurationsPath;
	@Value("${presidio.ade.aggregation.record.additional.configurations.path:#{null}}")
	private String aggregationRecordAdditionalConfigurationsPath;

	@Autowired
	private BucketConfigurationService bucketConfigurationService;

	@Bean
	public AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService() {
		return new AggregatedFeatureEventsConfService(
				aggregationRecordBaseConfigurationsPath,
				aggregationRecordOverridingConfigurationsPath,
				aggregationRecordAdditionalConfigurationsPath,
				bucketConfigurationService);
	}
}
