package presidio.ade.test.utils.generators.feature_buckets;

import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.feature.Feature;
import fortscale.common.util.GenericHistogram;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static fortscale.aggregation.feature.functions.AggrFeatureFeatureToMaxMapFunc.FEATURE_SEPARATOR_KEY;

public class FeatureBucketEpochtimeToHighestIntegerMapGenerator extends FeatureBucketEpochtimeMapGenerator {
    public FeatureBucketEpochtimeToHighestIntegerMapGenerator(
            Instant startInstant,
            Duration strategy,
            Map<Duration, Long> deltaToCountMap,
            String featureName) {

        super(startInstant, strategy, deltaToCountMap, featureName);
    }

    @Override
    public Map<String, Feature> getNext() {
        Map<String, Feature> next = super.getNext();
        Feature feature = next.get(featureName);
        Map<String, Integer> map = new HashMap<>();

        for (Entry<String, Double> entry : ((GenericHistogram)feature.getValue()).getHistogramMap().entrySet()) {
            String key = String.format("%s%s%s", featureName, FEATURE_SEPARATOR_KEY, entry.getKey());
            map.put(key, entry.getValue().intValue());
        }

        AggrFeatureValue aggrFeatureValue = new AggrFeatureValue(map, 0L);
        feature.setValue(aggrFeatureValue);
        return next;
    }
}
