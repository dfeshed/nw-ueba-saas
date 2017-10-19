package fortscale.ml.model;

import java.time.Duration;
import java.time.Instant;
import java.util.TreeMap;

/**
 * AggregatedFeatureValuesData contains strategy of AggregatedFeatureValues buckets (e.g: 3600) and instantToAggregatedFeatureValues map.
 */
public class AggregatedFeatureValuesData {

    private Duration instantStep;
    private TreeMap<Instant, Double> instantToAggregatedFeatureValues;

    public AggregatedFeatureValuesData(Duration instantStep, TreeMap<Instant, Double> instantToAggregatedFeatureValues){
        this.instantStep = instantStep;
        this.instantToAggregatedFeatureValues = instantToAggregatedFeatureValues;
    }

    public Duration getInstantStep() {
        return instantStep;
    }

    public TreeMap<Instant, Double> getInstantToAggregatedFeatureValues() {
        return instantToAggregatedFeatureValues;
    }
}
