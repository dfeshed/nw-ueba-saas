package fortscale.streaming.service.aggregation;


import java.util.List;

import org.apache.samza.config.Config;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskCoordinator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import fortscale.aggregation.DataSourcesSyncTimer;
import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.bucket.FeatureBucketsService;
import fortscale.aggregation.feature.bucket.FeatureBucketsStore;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyData;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyService;
import fortscale.aggregation.feature.event.AggrFeatureEventService;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.streaming.ExtendedSamzaTaskContext;
import fortscale.streaming.service.FortscaleStringValueResolver;
import fortscale.streaming.service.aggregation.feature.bucket.FeatureBucketsServiceSamza;
import fortscale.streaming.service.aggregation.feature.bucket.strategy.FeatureBucketStrategyServiceSamza;
import fortscale.streaming.service.aggregation.feature.event.AggrInternalAndKafkaEventTopologyService;
import fortscale.utils.ConversionUtils;
import net.minidev.json.JSONObject;

@Configurable(preConstruction = true)
public class AggregatorManager {
	private static final Logger logger = LoggerFactory.getLogger(AggregatorManager.class);
	public static final String SAMZA_TASK_FORTSCALE_TIMESTAMP_FIELD_CONFIG_PATH = "fortscale.timestamp.field";

	private String timestampFieldName;
	private FeatureBucketStrategyService featureBucketStrategyService;
	private FeatureBucketsService featureBucketsService;
	private AggrFeatureEventService featureEventService;


	@Autowired
	private FortscaleStringValueResolver fortscaleStringValueResolver;
	@Autowired
	private BucketConfigurationService bucketConfigurationService;
	@Autowired
	private FeatureBucketsStore featureBucketsStore;
	@Autowired
	private DataSourcesSyncTimer dataSourcesSyncTimer;
	@Autowired
	private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
	@Autowired
	private AggrInternalAndKafkaEventTopologyService aggrEventTopologyService;


	public AggregatorManager(Config config, ExtendedSamzaTaskContext context) {
		timestampFieldName = fortscaleStringValueResolver.resolveStringValue(config, SAMZA_TASK_FORTSCALE_TIMESTAMP_FIELD_CONFIG_PATH);
		featureBucketStrategyService = new FeatureBucketStrategyServiceSamza(context, featureBucketsStore);
		featureBucketsService = new FeatureBucketsServiceSamza(context, featureBucketsStore, featureBucketStrategyService);
		featureEventService = new AggrFeatureEventService(aggregatedFeatureEventsConfService, featureBucketStrategyService, featureBucketsService);
	}

	public void processEvent(JSONObject event, MessageCollector collector) throws Exception {
		Long timestamp = ConversionUtils.convertToLong(event.get(timestampFieldName));
		if (timestamp == null) {
			logger.warn("Event message {} contains no timestamp in field {}", event.toJSONString(), timestampFieldName);
			return;
		}
		aggrEventTopologyService.setMessageCollector(collector);
		aggrEventTopologyService.setAggregatorManager(this);

		processEvent(event);
	}
	
	public void processEvent(JSONObject event) throws Exception {
		dataSourcesSyncTimer.process(event);
		List<FeatureBucketStrategyData> updatedFeatureBucketStrategyDataList = featureBucketStrategyService.updateStrategies(event);
		List<FeatureBucketConf> featureBucketConfs = bucketConfigurationService.getRelatedBucketConfs(event);
		if (featureBucketConfs != null && !featureBucketConfs.isEmpty()) {
			//TODO: routeEventsToOtherContexts
			List<FeatureBucket> updatedFeatureBucketsWithNewEndTime = featureBucketsService.updateFeatureBucketsWithNewBucketEndTime(featureBucketConfs, updatedFeatureBucketStrategyDataList);
			featureEventService.featureBucketsEndTimeUpdate(updatedFeatureBucketsWithNewEndTime);
			//TODO: Update AggregationEventsManager with updatedFeatureBucketsWithNewEndTime
			List<FeatureBucket> newFeatureBuckets = featureBucketsService.updateFeatureBucketsWithNewEvent(event, featureBucketConfs);
			if(newFeatureBuckets.size()>0) {
				featureEventService.newFeatureBuckets(newFeatureBuckets);
			}
		}
	}

	public void window(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		aggrEventTopologyService.setMessageCollector(collector);
		dataSourcesSyncTimer.timeCheck(System.currentTimeMillis());
	}

	public void close() throws Exception {
		// TODO implement
	}
}
