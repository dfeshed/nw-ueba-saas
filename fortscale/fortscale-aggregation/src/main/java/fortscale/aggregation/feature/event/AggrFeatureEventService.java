package fortscale.aggregation.feature.event;

import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.bucket.FeatureBucketsService;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategy;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyService;
import fortscale.utils.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by amira on 08/07/2015.
 */
public class AggrFeatureEventService {
    private static final Logger logger = Logger.getLogger(AggrFeatureEventService.class);
    private static final String INFO_MSG_NO_EVENT_CONFS = "No aggregated feature event definitions were received.";
    private static final String ERROR_MSG_REMOVE_BUCKET_ID_MAPPING_INVALID_PARAMS = "Null or empty params for removeBucketID2builderMapping()";

    private AggregatedFeatureEventsConfService aggrFeatureEventsConfService;
    private FeatureBucketStrategyService featureBucketStrategyService;
    private FeatureBucketsService featureBucketsService;

    private List<AggregatedFeatureEventConf> aggrFeatureEventConfs;
    private Map<String, List<AggrFeatureEventBuilder>> bucketConfName2eventBuildersListMap = new HashMap<>();
    private Map<String, List<AggrFeatureEventBuilder>> bucketID2eventBuildersListMap = new HashMap<>();

    public AggrFeatureEventService(AggregatedFeatureEventsConfService aggrFeatureEventsConfService, FeatureBucketStrategyService featureBucketStrategyService, FeatureBucketsService featureBucketsService) {
        this.aggrFeatureEventsConfService = aggrFeatureEventsConfService;
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
            FeatureBucketStrategy strategy = featureBucketStrategyService.getFeatureBucketStrategiesFactory().getFeatureBucketStrategy(eventConf.getBucketConf().getStrategyName());
            AggrFeatureEventBuilder eventBuilder = new AggrFeatureEventBuilder(eventConf, strategy, this, featureBucketsService);
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

    private List<AggrFeatureEventBuilder> getRelatedAggrFeatureEventBuilders(String bucketID) {
        if(bucketID==null || StringUtils.isEmpty(bucketID)) return null;

        List<AggrFeatureEventBuilder> builders =  bucketID2eventBuildersListMap.get(bucketID);
        if(builders==null) return null;

        List<AggrFeatureEventBuilder> clone = new ArrayList<>(builders.size());
        for(AggrFeatureEventBuilder builder: builders) {
            clone.add(builder);
        }
        return clone;
    }

    private void addBucketID2builderMapping(String bucketID, AggrFeatureEventBuilder builder) {
        Assert.notNull(bucketID);
        Assert.isTrue(StringUtils.isNotEmpty(bucketID));
        Assert.notNull(builder);

        List<AggrFeatureEventBuilder> builders = bucketID2eventBuildersListMap.get(bucketID);
        if(builder==null) {
            builders = new ArrayList<>();
        }
        if(!builders.contains(builder)) {
            builders.add(builder);
        }
    }

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


    void removeBucketID2builderMapping(String bucketID, AggrFeatureEventBuilder builder) {
        if(bucketID==null || StringUtils.isEmpty(bucketID) || builder==null) {
            logger.error(ERROR_MSG_REMOVE_BUCKET_ID_MAPPING_INVALID_PARAMS);
            return;
        }
        List<AggrFeatureEventBuilder> builders = bucketID2eventBuildersListMap.get(bucketID);
        if(builders!=null) {
            builders.remove(builder);
        }
    }

    public void featureBucketsEndTimeUpdate(List<FeatureBucket> updatedFeatureBucketsWithNewEndTime) {
        if(updatedFeatureBucketsWithNewEndTime==null) {
            return;
        }
        for(FeatureBucket featureBucket : updatedFeatureBucketsWithNewEndTime) {
            List<AggrFeatureEventBuilder> builders = getRelatedAggrFeatureEventBuildersByBucketConfName(featureBucket.getFeatureBucketConfName());
            if(builders==null) continue;
            for(AggrFeatureEventBuilder builder : builders) {
                builder.updateFeatureBacketEndTime(featureBucket.getBucketId(), featureBucket.getEndTime());
            }
        }
    }
}
