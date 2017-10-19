package fortscale.ml.model;

import fortscale.utils.fixedduration.FixedDurationStrategy;

import java.time.Instant;
import java.util.TreeMap;

/**
 * AggregatedFeatureValuesData contains strategy of AggregatedFeatureValues buckets and instantToAggregatedFeatureValues map.
 */
public class AggregatedFeatureValuesData {

    private FixedDurationStrategy fixedDurationStrategy;
    private TreeMap<Instant, Double> instantToAggregatedFeatureValues;

    public AggregatedFeatureValuesData(FixedDurationStrategy fixedDurationStrategy, TreeMap<Instant, Double> instantToAggregatedFeatureValues){
        this.fixedDurationStrategy = fixedDurationStrategy;
        this.instantToAggregatedFeatureValues = instantToAggregatedFeatureValues;
    }

    public FixedDurationStrategy getFixedDurationStrategy() {
        return fixedDurationStrategy;
    }

    public TreeMap<Instant, Double> getInstantToAggregatedFeatureValues() {
        return instantToAggregatedFeatureValues;
    }
}
