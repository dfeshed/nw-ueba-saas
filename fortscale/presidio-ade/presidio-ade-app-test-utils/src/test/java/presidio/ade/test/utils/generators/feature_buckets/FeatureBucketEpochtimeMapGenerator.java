package presidio.ade.test.utils.generators.feature_buckets;

import fortscale.common.feature.Feature;
import fortscale.common.util.GenericHistogram;
import org.springframework.util.Assert;
import presidio.data.generators.common.IMapGenerator;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;

/**
 * This generator creates start instant (or date time) histograms for feature buckets. The first histogram created is
 * for a feature bucket whose start instant is {@link #startInstant}, and with every call to {@link #getNext()}, the
 * generator jumps to the next start instant according to a fixed interval ({@link #strategy}). The histograms are
 * returned as maps, and the deltas between the epoch-times and the feature bucket's start instant are fixed and
 * defined by {@link #deltaToCountMap}.
 *
 * @author Lior Govrin
 */
public class FeatureBucketEpochtimeMapGenerator implements IMapGenerator<String, Feature> {
    private final Instant startInstant;
    private final Duration strategy;
    private final Map<Duration, Long> deltaToCountMap;
    private final String featureName;
    private Instant nextInstant;

    public FeatureBucketEpochtimeMapGenerator(
            Instant startInstant,
            Duration strategy,
            Map<Duration, Long> deltaToCountMap,
            String featureName) {

        for (Duration delta : deltaToCountMap.keySet()) {
            Assert.isTrue(delta.compareTo(strategy) < 0, "The deltas from the start " +
                    "instant must be less than the feature bucket strategy duration.");
        }

        this.startInstant = startInstant;
        this.strategy = strategy;
        this.deltaToCountMap = deltaToCountMap;
        this.featureName = featureName;
        this.nextInstant = startInstant;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public Map<String, Feature> getNext() {
        GenericHistogram genericHistogram = new GenericHistogram();
        deltaToCountMap.forEach((delta, count) -> {
            long epochtime = nextInstant.plus(delta).getEpochSecond();
            genericHistogram.add(epochtime, new Double(count));
        });
        nextInstant = nextInstant.plus(strategy);
        return Collections.singletonMap(featureName, new Feature(featureName, genericHistogram));
    }

    @Override
    public void reset() {
        nextInstant = startInstant;
    }
}
