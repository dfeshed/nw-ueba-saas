package fortscale.streaming.service.aggregation.feature.event;

import fortscale.streaming.aggregation.feature.Feature;
import fortscale.streaming.service.aggregation.FeatureBucket;
import fortscale.streaming.service.aggregation.FeatureBucketConf;
import fortscale.streaming.service.aggregation.bucket.strategy.FeatureBucketStrategy;
import fortscale.streaming.service.aggregation.bucket.strategy.FeatureBucketStrategyService;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by amira on 08/07/2015.
 */
@Service
public class AggrFeatureEventService implements InitializingBean {
    private static final Logger logger = Logger.getLogger(AggrFeatureEventService.class);
    private static final String INFO_MSG_NO_EVENT_CONFS = "No aggregated feature event definitions were received.";
    private static final String ERROR_MSG_REMOVE_BUCKET_ID_MAPPING_INVALID_PARAMS = "Null or empty params for removeBucketID2builderMapping()";

    @Autowired
    private AggrFeatureEventsConfService aggrFeatureEventsConfService;

    @Autowired
    private FeatureBucketStrategyService featureBucketStrategyService;

    private List<AggrFeatureEventConf> aggrFeatureEventConfs;
    private Map<String, List<AggrFeatureEventBuilder>> bucketConfName2eventBuildersListMap = new HashMap<>();
    private Map<String, List<AggrFeatureEventBuilder>> bucketID2eventBuildersListMap = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(aggrFeatureEventsConfService);
        Assert.notNull(featureBucketStrategyService);
        aggrFeatureEventConfs = aggrFeatureEventsConfService.getAggrFeatuerEventsDefinitions();
        if(aggrFeatureEventConfs==null || aggrFeatureEventConfs.isEmpty()) {
            logger.info(INFO_MSG_NO_EVENT_CONFS);
        } else {
            createAggrFeatureEventBuilders();
        }
    }

    private void createAggrFeatureEventBuilders() {
        for(AggrFeatureEventConf eventConf : aggrFeatureEventConfs) {

            FeatureBucketStrategy strategy = featureBucketStrategyService.getFeatureBucketStrategiesFactory().getFeatureBucketStrategy(eventConf.getFeatureBucketConf().getStrategyName());
            AggrFeatureEventBuilder eventBuilder = new AggrFeatureEventBuilder(eventConf, strategy, this);
            addAggrFeatureEventBuilder(eventConf.getFeatureBucketConf().getName(), eventBuilder);
        }
    }

    private void addAggrFeatureEventBuilder(String bucketConfName, AggrFeatureEventBuilder eventBuilder) {
        Assert.notNull(eventBuilder);
        Assert.notNull(bucketConfName);
        Assert.isTrue(StringUtils.isNotEmpty(bucketConfName));
        List<AggrFeatureEventBuilder> builders = bucketConfName2eventBuildersListMap.get(bucketConfName);
        if(builders==null) {
            builders = new ArrayList<>();
        }
        if(!builders.contains(eventBuilder)) {
            builders.add(eventBuilder);
        }
    }

    private List<AggrFeatureEventBuilder> getRelatedAggrFeatureEventBuilders(FeatureBucketConf bucketConf) {
        if(bucketConf==null) return null;

        List<AggrFeatureEventBuilder> builders =  bucketConfName2eventBuildersListMap.get(bucketConf);
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
                        builder.updateAggrFeatureEvent(bucket.getBucketId(), bucket.getContextFieldNameToValueMap(), bucket.getStartTime(), bucket.getEndTime());
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

}
