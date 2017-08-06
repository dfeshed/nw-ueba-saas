package presidio.ade.modeling.config;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Lior Govrin
 */
@Configuration
public class ModelingServiceFeatureAggregationEventConfServiceConfig {
	@Value("${presidio.ade.modeling.event.type.field.value.aggr.event}")
	private String eventTypeFieldValueAggrEvent;
	@Value("${presidio.ade.modeling.context.field.key}")
	private String contextFieldKey;
	@Value("${presidio.ade.modeling.feature.aggregation.event.confs.base.path}")
	private String featureAggregationEventConfsBasePath;
	@Value("${presidio.ade.modeling.feature.aggregation.event.confs.overriding.path:#{null}}")
	private String featureAggregationEventConfsOverridingPath;
	@Value("${presidio.ade.modeling.feature.aggregation.event.confs.additional.path:#{null}}")
	private String featureAggregationEventConfsAdditionalPath;

	@Autowired
	private BucketConfigurationService bucketConfigurationService;
	@Autowired
	private AggregatedFeatureEventsConfUtilService aggregatedFeatureEventsConfUtilService;

	@Bean
	public AggregatedFeatureEventsConfUtilService aggregatedFeatureEventsConfUtilService() {
		return new AggregatedFeatureEventsConfUtilService(eventTypeFieldValueAggrEvent, contextFieldKey);
	}

	@Bean
	public AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService() {
		return new AggregatedFeatureEventsConfService(
				featureAggregationEventConfsBasePath,
				featureAggregationEventConfsOverridingPath,
				featureAggregationEventConfsAdditionalPath,
				bucketConfigurationService,
				aggregatedFeatureEventsConfUtilService);
	}
}
