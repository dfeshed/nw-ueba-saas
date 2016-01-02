package fortscale.aggregation.feature.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketsService;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategy;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyService;
import fortscale.utils.logging.Logger;

public class AggrFeatureEventService implements IAggrFeatureEventService{
    private static final Logger logger = Logger.getLogger(AggrFeatureEventService.class);
    private static final String INFO_MSG_NO_EVENT_CONFS = "No aggregated feature event definitions were received.";

    private FeatureBucketStrategyService featureBucketStrategyService;
    private FeatureBucketsService featureBucketsService;

    private List<AggregatedFeatureEventConf> aggrFeatureEventConfs;
    private Map<String, List<AggrFeatureEventBuilder>> bucketConfName2eventBuildersListMap = new HashMap<>();

    public AggrFeatureEventService(AggregatedFeatureEventsConfService aggrFeatureEventsConfService, FeatureBucketStrategyService featureBucketStrategyService, FeatureBucketsService featureBucketsService) {
        this.featureBucketStrategyService = featureBucketStrategyService;
        this.featureBucketsService = featureBucketsService;
        Assert.notNull(aggrFeatureEventsConfService);
        Assert.notNull(featureBucketStrategyService);
        Assert.notNull(featureBucketsService);
        aggrFeatureEventConfs = aggrFeatureEventsConfService.getAggregatedFeatureEventConfList();
        if(aggrFeatureEventConfs==null || aggrFeatureEventConfs.isEmpty()) {
            logger.info(INFO_MSG_NO_EVENT_CONFS);
        } else {
            createAggrFeatureEventBuilders();
        }
    }

    private void createAggrFeatureEventBuilders() {
        Assert.notNull(featureBucketStrategyService);
        Assert.notNull(featureBucketsService);

        for(AggregatedFeatureEventConf eventConf : aggrFeatureEventConfs) {
            if (eventConf.getBucketConf() == null) {
                throw new RuntimeException("Could not find bucket configuration with name " + eventConf.getBucketConfName() + " for aggregation event config " + eventConf.getName());
            }
            FeatureBucketStrategy strategy = featureBucketStrategyService.getFeatureBucketStrategiesFactory().getFeatureBucketStrategy(eventConf.getBucketConf().getStrategyName());
            AggrFeatureEventBuilder eventBuilder = new AggrFeatureEventBuilder(eventConf, strategy, featureBucketsService);
            addAggrFeatureEventBuilder(eventConf.getBucketConf().getName(), eventBuilder);
        }
    }

    private void addAggrFeatureEventBuilder(String bucketConfName, AggrFeatureEventBuilder eventBuilder) {
        Assert.notNull(eventBuilder);
        Assert.notNull(bucketConfName);
        Assert.isTrue(StringUtils.isNotEmpty(bucketConfName));
        List<AggrFeatureEventBuilder> builders = bucketConfName2eventBuildersListMap.get(bucketConfName);
        if(builders==null) {
            builders = new ArrayList<>();
            bucketConfName2eventBuildersListMap.put(bucketConfName, builders);
        }
        if(!builders.contains(eventBuilder)) {
            builders.add(eventBuilder);
        }
    }

    private List<AggrFeatureEventBuilder> getRelatedAggrFeatureEventBuildersByBucketConfName(String bucketConfName) {
        if(bucketConfName==null) return null;

        List<AggrFeatureEventBuilder> builders =  bucketConfName2eventBuildersListMap.get(bucketConfName);
        if(builders==null) return null;

        List<AggrFeatureEventBuilder> clone = new ArrayList<>(builders.size());
        for(AggrFeatureEventBuilder builder: builders) {
            clone.add(builder);
        }
        return clone;
    }


    /**
     * Handling new feature buckets.
     * The assumption is that new buckets are coming in order.
     */
    @Override
    public void newFeatureBuckets(List<FeatureBucket> buckets) {
        if(buckets!=null) {
            for(FeatureBucket bucket : buckets) {
                List<AggrFeatureEventBuilder> builders = bucketConfName2eventBuildersListMap.get(bucket.getFeatureBucketConfName());
                if(builders!=null) {
                    for(AggrFeatureEventBuilder builder: builders) {
                        builder.updateAggrFeatureEventData(bucket.getBucketId(), bucket.getStrategyId(), bucket.getContextFieldNameToValueMap(), bucket.getStartTime(), bucket.getEndTime());
                    }
                }
            }
        }
    }


    @Override
    public void featureBucketsEndTimeUpdate(List<FeatureBucket> updatedFeatureBucketsWithNewEndTime) {
        if(updatedFeatureBucketsWithNewEndTime==null) {
            return;
        }
        for(FeatureBucket featureBucket : updatedFeatureBucketsWithNewEndTime) {
            List<AggrFeatureEventBuilder> builders = getRelatedAggrFeatureEventBuildersByBucketConfName(featureBucket.getFeatureBucketConfName());
            if(builders==null) continue;
            for(AggrFeatureEventBuilder builder : builders) {
                builder.updateFeatureBucketEndTime(featureBucket.getBucketId(), featureBucket.getEndTime());
            }
        }
    }

    @Override public void sendEvents(long curEventTime) {
        // do nothing
    }
}
