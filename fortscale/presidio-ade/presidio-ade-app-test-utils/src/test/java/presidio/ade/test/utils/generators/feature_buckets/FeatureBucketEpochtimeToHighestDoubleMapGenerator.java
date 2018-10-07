package presidio.ade.test.utils.generators.feature_buckets;

import fortscale.common.feature.*;
import fortscale.common.util.GenericHistogram;
import fortscale.utils.data.Pair;

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
        MultiKeyHistogram multiKeyHistogram = new MultiKeyHistogram();

        for (Entry<String, Double> entry : ((GenericHistogram)feature.getValue()).getHistogramMap().entrySet()) {
            MultiKeyFeature multiKeyFeature = new MultiKeyFeature();
            multiKeyFeature.add(featureName, new FeatureStringValue( entry.getKey()));
            multiKeyHistogram.add(multiKeyFeature, entry.getValue());
        }

        feature.setValue(multiKeyHistogram);
        return next;
    }
}
