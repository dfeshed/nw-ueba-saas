package fortscale.streaming.service.aggregation;

import fortscale.aggregation.DataSourcesSyncTimer;
import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.bucket.FeatureBucketsService;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyData;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyService;
import fortscale.aggregation.feature.event.AggrFeatureEventDummyService;
import fortscale.aggregation.feature.event.AggrFeatureEventImprovedService;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.aggregation.feature.event.IAggrFeatureEventService;
import fortscale.common.event.DataEntitiesConfigWithBlackList;
import fortscale.common.event.Event;
import fortscale.common.event.RawEvent;
import fortscale.common.feature.extraction.AggrEvent;
import fortscale.streaming.ExtendedSamzaTaskContext;
import fortscale.streaming.service.FortscaleValueResolver;
import fortscale.streaming.service.aggregation.feature.bucket.FeatureBucketsServiceSamza;
import fortscale.streaming.service.aggregation.feature.bucket.FeatureBucketsStoreSamza;
import fortscale.streaming.service.aggregation.feature.bucket.strategy.FeatureBucketStrategyServiceSamza;
import fortscale.streaming.service.aggregation.feature.event.AggrInternalAndKafkaEventTopologyService;
import fortscale.streaming.service.aggregation.feature.event.AggregationMetricsService;
import fortscale.utils.ConversionUtils;
import net.minidev.json.JSONObject;
import org.apache.samza.config.Config;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskCoordinator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import java.util.List;

@Configurable(preConstruction = true)
public class AggregatorManager {
	private static final Logger logger = LoggerFactory.getLogger(AggregatorManager.class);
	public static final String SAMZA_TASK_FORTSCALE_TIMESTAMP_FIELD_CONFIG_PATH = "fortscale.timestamp.field";
	

	private String timestampFieldName;
	private FeatureBucketStrategyService featureBucketStrategyService;
	private FeatureBucketsService featureBucketsService;
	private IAggrFeatureEventService featureEventService;


	@Autowired
	private FortscaleValueResolver fortscaleValueResolver;
	@Autowired
	private BucketConfigurationService bucketConfigurationService;

	private FeatureBucketsStoreSamza featureBucketsStore;
	@Autowired
	private DataSourcesSyncTimer dataSourcesSyncTimer;
	@Autowired
	private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
	@Autowired
	private AggrInternalAndKafkaEventTopologyService aggrEventTopologyService;

	@Autowired
	private DataEntitiesConfigWithBlackList dataEntitiesConfigWithBlackList;

	@Value("${streaming.event.datasource.field.name}")
	private String dataSourceFieldName;

	@Value("${streaming.event.field.type}")
    private String eventTypeFieldName;
    @Value("${streaming.event.field.type.aggr_event}")
    private String aggrEventType;

    @Value("${streaming.aggr_event.field.aggregated_feature_name}")
    private String aggrFeatureNameFieldName;
    @Value("${streaming.aggr_event.field.aggregated_feature_value}")
    private String aggrFeatureValueFieldName;
    @Value("${streaming.aggr_event.field.bucket_conf_name}")
    private String bucketConfFieldName;
	@Value("${fortscale.event.context.json.prefix}")
	protected String contextJsonPrefix;


	private AggregationMetricsService aggregationMetricsService;


	public AggregatorManager(Config config, ExtendedSamzaTaskContext context, Boolean skipSendingAggregationEvents) {
		timestampFieldName = fortscaleValueResolver.resolveStringValue(config, SAMZA_TASK_FORTSCALE_TIMESTAMP_FIELD_CONFIG_PATH);
		featureBucketsStore = new FeatureBucketsStoreSamza(context);
		featureBucketStrategyService = new FeatureBucketStrategyServiceSamza(context, featureBucketsStore);
		featureBucketsService = new FeatureBucketsServiceSamza(context, featureBucketsStore, featureBucketStrategyService);
		if (skipSendingAggregationEvents) {
			featureEventService = new AggrFeatureEventDummyService();
		}
		else {
			featureEventService = new AggrFeatureEventImprovedService(aggregatedFeatureEventsConfService, featureBucketsService);
		}
		aggregationMetricsService = new AggregationMetricsService(context);
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

	public void processEvent(JSONObject jsonObject) throws Exception {
		Event event = createEvent(jsonObject);
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

	private Event createEvent(JSONObject eventMessage) {
		String eventType = (String)eventMessage.get(eventTypeFieldName);
		String dataSource = eventMessage.getAsString(dataSourceFieldName);

		if (aggrEventType.equals(eventType)) {
			return new AggrEvent(
					eventMessage, aggrFeatureNameFieldName, aggrFeatureValueFieldName,
					bucketConfFieldName, dataSource, contextJsonPrefix);
		} else {
			return new RawEvent(eventMessage, dataEntitiesConfigWithBlackList, dataSource);
		}
	}

	public void window(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		aggrEventTopologyService.setMessageCollector(collector);
		aggrEventTopologyService.setAggregationMetricsService(aggregationMetricsService);
		aggrEventTopologyService.setAggregatorManager(this);
		
		featureEventService.sendEvents(dataSourcesSyncTimer.getLastEventEpochtime());
		dataSourcesSyncTimer.timeCheck(System.currentTimeMillis());
		featureBucketsStore.cleanup();
	}

	public void advanceTime(long epochtime) {
		dataSourcesSyncTimer.advanceLastEventEpochtime(epochtime);
	}

	public void close() throws Exception {
		// TODO implement
	}
}
