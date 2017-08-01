package fortscale.aggregation.feature.functions;

import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.feature.Feature;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Aggregate one or more buckets containing a feature containing a mapping from features group to max value.
 * Such a mapping (of type Map<List<String>, Integer>) is created by AggrFeatureFeatureToMaxMapFunc.
 * If configured to aggregate more than one bucket, first the buckets' mappings will be aggregated into one mapping
 * such that every features group will be mapped to the maximal value among all the values of the instances of this
 * features group among the buckets' mappings.
 *
 * The final step should be performed by an extending class - which is to take
 * the aggregated mapping and compute some value out of it (e.g. - sum).
 *
 * Parameters this class gets from the ASL:
 * 1. pick: the name of the feature stored in the buckets which its value should be picked and used by this class.
 */
public abstract class AbstractAggrFeatureEventFeatureToMaxMapFunc extends AbstractAggrFeatureEvent {
    /**
     * The name of the feature generated by the buckets.json step which its value should be picked and used in this (aggregated_feature_events.json) step
     */
    public final static String PICK_FIELD_NAME = "pick";

    @Override
    protected AggrFeatureValue calculateAggrFeatureValue(AggregatedFeatureEventConf aggrFeatureEventConf, List<Map<String, Feature>> multipleBucketsAggrFeaturesMapList) {
        AggrFeatureValue featuresGroupToMax = calculateFeaturesGroupToMaxFromBucketAggrFeature(aggrFeatureEventConf, multipleBucketsAggrFeaturesMapList);
        return featuresGroupToMax == null ? null : calculateFeaturesGroupToMaxValue(featuresGroupToMax);
    }

    protected abstract AggrFeatureValue calculateFeaturesGroupToMaxValue(AggrFeatureValue aggrFeatureValue);

    private AggrFeatureValue calculateFeaturesGroupToMaxFromBucketAggrFeature(AggregatedFeatureEventConf aggrFeatureEventConf, List<Map<String, Feature>> multipleBucketsAggrFeaturesMapList) {
        String featureToPick = getFeatureToPick(aggrFeatureEventConf);
        Map<String, Integer> featuresGroupToMax = new HashMap<>();
        long total = 0;
        for (Map<String, Feature> aggrFeatures : multipleBucketsAggrFeaturesMapList) {
            Feature aggrFeature = aggrFeatures.get(featureToPick);
            if (aggrFeature == null) {
                continue;
            }
            if (!(aggrFeature.getValue() instanceof AggrFeatureValue) || !(((AggrFeatureValue) aggrFeature.getValue()).getValue() instanceof Map)) {
                throw new IllegalArgumentException(String.format("Missing aggregated feature named %s of type %s containing %s",
                        featureToPick, AggrFeatureValue.class.getSimpleName(), Map.class.getSimpleName()));
            }
            for (Map.Entry<String, Integer> featuresGroupAndMax : ((Map<String, Integer>) ((AggrFeatureValue) aggrFeature.getValue()).getValue()).entrySet()) {
                Integer max = featuresGroupToMax.get(featuresGroupAndMax.getKey());
                if (max == null) {
                    max = Integer.MIN_VALUE;
                }
                featuresGroupToMax.put(featuresGroupAndMax.getKey(), Math.max(max, featuresGroupAndMax.getValue()));
            }
            total += ((AggrFeatureValue) aggrFeature.getValue()).getTotal();
        }

        return featuresGroupToMax.isEmpty()? null : new AggrFeatureValue(featuresGroupToMax, total);
    }

    private String getFeatureToPick(AggregatedFeatureEventConf aggrFeatureEventConf) {
        List<String> featuresToPick = aggrFeatureEventConf.getAggregatedFeatureNamesMap().get(PICK_FIELD_NAME);
        Assert.notNull(featuresToPick);
        Assert.isTrue(featuresToPick.size() == 1);
        return featuresToPick.get(0);
    }
}
