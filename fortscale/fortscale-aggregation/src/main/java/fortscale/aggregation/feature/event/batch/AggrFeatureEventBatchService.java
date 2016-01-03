package fortscale.aggregation.feature.event.batch;

import fortscale.aggregation.feature.Feature;
import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.bucket.FeatureBucketsReaderService;
import fortscale.aggregation.feature.event.*;
import fortscale.aggregation.feature.functions.IAggrFeatureEventFunctionsService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AggrFeatureEventBatchService {

	@Autowired private IAggrFeatureEventFunctionsService aggrFeatureFuncService;

	@Autowired private AggrFeatureEventBuilderService aggrFeatureEventBuilderService;

	@Autowired private FeatureBucketsReaderService featureBucketsReaderService;

	@Autowired private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;

	@Autowired private BucketConfigurationService bucketConfigurationService;

	public void buildAndSave(IAggregationEventSender sender, Long bucketStartTime, Long bucketEndTime) {
		for (FeatureBucketConf featureBucketConf : bucketConfigurationService.getFeatureBucketConfs()) {

			for (FeatureBucket bucket : featureBucketsReaderService.getFeatureBucketsByTimeRange(featureBucketConf, bucketStartTime, bucketEndTime)) {
				List<Map<String, Feature>> bucketAggrFeaturesMapList = new ArrayList<>();
				bucketAggrFeaturesMapList.add(bucket.getAggregatedFeatures());
				for (AggregatedFeatureEventConf conf : aggregatedFeatureEventsConfService.getAggregatedFeatureEventConfList(bucket.getFeatureBucketConfName())) {

					Feature feature = aggrFeatureFuncService.calculateAggrFeature(conf, bucketAggrFeaturesMapList);
					saveEvent(feature);
					sendEvent(sender, conf, bucket.getContextFieldNameToValueMap(), feature, bucket.getStartTime(), bucket.getEndTime());
				}
			}
		}

	}

	private void saveEvent(Feature feature) {
		// TODO: 12/31/2015
	}

	private void sendEvent(IAggregationEventSender sender, AggregatedFeatureEventConf conf, Map<String, String> context,
			Feature feature, Long startTimeSec, Long endTimeSec) {
		if (sender != null) {
			JSONObject event = aggrFeatureEventBuilderService.buildEvent(conf, context, feature, startTimeSec, endTimeSec);

			boolean isOfTypeF = AggrEvent.AGGREGATED_FEATURE_TYPE_F_VALUE.equals(conf.getType());
			sender.send(isOfTypeF, event);
		}
	}

}
