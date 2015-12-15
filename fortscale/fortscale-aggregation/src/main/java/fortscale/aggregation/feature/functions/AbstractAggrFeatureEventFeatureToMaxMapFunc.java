package fortscale.aggregation.feature.functions;

import fortscale.aggregation.feature.Feature;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractAggrFeatureEventFeatureToMaxMapFunc extends AbstractAggrFeatureEvent {
    public final static String PLUCK_FIELD_NAME = "pluck";

    @Override
    protected AggrFeatureValue calculateAggrFeatureValue(AggregatedFeatureEventConf aggrFeatureEventConf, List<Map<String, Feature>> multipleBucketsAggrFeaturesMapList) {
    	AggrFeatureValue featuresGroupToMax = calculateFeaturesGroupToMaxFromBucketAggrFeature(aggrFeatureEventConf, multipleBucketsAggrFeaturesMapList);
        return calculateMapAggrFeatureValue(featuresGroupToMax);
    }
    
    protected abstract AggrFeatureValue calculateMapAggrFeatureValue(AggrFeatureValue aggrFeatureValue);

    private AggrFeatureValue calculateFeaturesGroupToMaxFromBucketAggrFeature(AggregatedFeatureEventConf aggrFeatureEventConf, List<Map<String, Feature>> multipleBucketsAggrFeaturesMapList) {
        String featureToPluck = getFeatureToPluck(aggrFeatureEventConf);
        Map<List<String>, Integer> featuresGroupToMax = new HashMap<>();
        for (Map<String, Feature> aggrFeatures : multipleBucketsAggrFeaturesMapList) {
            Feature aggrFeature = aggrFeatures.get(featureToPluck);
            if (aggrFeature == null) {
                continue;
            }
            if (!(aggrFeature.getValue() instanceof AggrFeatureValue) || !(((AggrFeatureValue) aggrFeature.getValue()).getValue() instanceof Map)) {
                throw new IllegalArgumentException(String.format("Missing aggregated feature named %s of type %s containing %s",
                        featureToPluck, AggrFeatureValue.class.getSimpleName(), Map.class.getSimpleName()));
            }
            for (Map.Entry<List<String>, Integer> featuresGroupAndMax : ((Map<List<String>, Integer>) ((AggrFeatureValue) aggrFeature.getValue()).getValue()).entrySet()) {
                Integer max = featuresGroupToMax.get(featuresGroupAndMax.getValue());
                if (max == null) {
                    max = Integer.MIN_VALUE;
                }
                featuresGroupToMax.put(featuresGroupAndMax.getKey(), Math.max(max, featuresGroupAndMax.getValue()));
            }
        }

        return new AggrFeatureValue(featuresGroupToMax, (long) featuresGroupToMax.size());
    }

    private String getFeatureToPluck(AggregatedFeatureEventConf aggrFeatureEventConf) {
        List<String> featuresToPluck = aggrFeatureEventConf.getAggregatedFeatureNamesMap().get(PLUCK_FIELD_NAME);
        Assert.notNull(featuresToPluck);
        Assert.isTrue(featuresToPluck.size() == 1);
        return featuresToPluck.get(0);
    }
}
