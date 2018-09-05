package presidio.ade.test.utils.generators.feature_buckets;

import fortscale.common.feature.*;
import fortscale.common.util.GenericHistogram;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class FeatureBucketEpochtimeToHighestDoubleMapGenerator extends FeatureBucketEpochtimeMapGenerator {
    public FeatureBucketEpochtimeToHighestDoubleMapGenerator(
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
        Map<MultiKeyFeature, Double> map = new HashMap<>();

        for (Entry<String, Double> entry : ((GenericHistogram)feature.getValue()).getHistogramMap().entrySet()) {
            Map<String, FeatureValue> featureNameToValue = new HashMap<>();
            featureNameToValue.put(featureName, new FeatureStringValue( entry.getKey()) );
            MultiKeyFeature multiKeyFeature = new MultiKeyFeature(featureNameToValue);
            map.put(multiKeyFeature, entry.getValue());
        }

        MultiKeyHistogram multiKeyHistogram1 = new MultiKeyHistogram(map, 0L);
        feature.setValue(multiKeyHistogram1);
        return next;
    }
}
