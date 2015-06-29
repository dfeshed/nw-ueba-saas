package fortscale.streaming.service.aggregation;

import static fortscale.utils.ConversionUtils.convertToLong;

import java.util.List;

import net.minidev.json.JSONObject;

import org.apache.samza.config.Config;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskCoordinator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import fortscale.streaming.ExtendedSamzaTaskContext;
import fortscale.streaming.service.FortscaleStringValueResolver;
import fortscale.streaming.service.aggregation.bucket.strategy.FeatureBucketStrategyData;
import fortscale.streaming.service.aggregation.bucket.strategy.FeatureBucketStrategyService;
import fortscale.streaming.service.aggregation.bucket.strategy.samza.FeatureBucketStrategyServiceSamza;
import fortscale.streaming.service.aggregation.samza.FeatureBucketsServiceSamza;

@Configurable(preConstruction=true)
public class AggregatorManager {
	private static final Logger logger = LoggerFactory.getLogger(AggregatorManager.class);
	
	public static final String SAMZA_TASK_FORTSCALE_TIMESTAMP_FIELD_CONFIG_PATH = "fortscale.timestamp.field";

	private String timestampFieldName;
	private FeatureBucketStrategyService featureBucketStrategyService;
	private FeatureBucketsService featureBucketsService;
	
	@Autowired
	private FortscaleStringValueResolver fortscaleStringValueResolver;
	
	@Autowired
	private BucketConfigurationService bucketConfigurationService;
	
	@Autowired
	private FeatureBucketsStore featureBucketsStore;

	public AggregatorManager(Config config, ExtendedSamzaTaskContext context) {
		timestampFieldName = fortscaleStringValueResolver.resolveStringValue(config, SAMZA_TASK_FORTSCALE_TIMESTAMP_FIELD_CONFIG_PATH);
		
		featureBucketStrategyService = new FeatureBucketStrategyServiceSamza(context, featureBucketsStore);
		featureBucketsService = new FeatureBucketsServiceSamza(context, featureBucketsStore, featureBucketStrategyService);
	}

	public void processEvent(JSONObject event) throws Exception {
		Long timestamp = convertToLong(event.get(timestampFieldName));
		if (timestamp == null) {
			logger.warn("Event message {} contains no timestamp in field {}", event.toJSONString(), timestampFieldName);
			return;
		}

		List<FeatureBucketStrategyData> updatedFeatureBucketStrategyDatas = featureBucketStrategyService.updateStrategies(event);
		List<FeatureBucketConf> featureBucketConfs = bucketConfigurationService.getRelatedBucketConfs(event);
		if(featureBucketConfs != null && !featureBucketConfs.isEmpty()){
			//TODO:routeEventsToOtherContexts
			List<FeatureBucket> updatedFeatureBucketsWithNewEndTime = featureBucketsService.updateFeatureBucketsWithNewBucketEndTime(featureBucketConfs, updatedFeatureBucketStrategyDatas);
			//TODO: Update AggregationEventsManager with updatedFeatureBucketsWithNewEndTime
			List<FeatureBucket> newFeatureBuckets = featureBucketsService.updateFeatureBucketsWithNewEvent(event, featureBucketConfs);
			//TODO: Update AggregationEventsManager with newFeatureBuckets
		}
	}

	public void window(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		// TODO implement
	}

	public void close() throws Exception {
		// TODO implement
	}
}
