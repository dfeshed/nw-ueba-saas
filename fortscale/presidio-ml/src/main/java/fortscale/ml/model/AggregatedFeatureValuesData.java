package fortscale.ml.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.Duration;
import java.time.Instant;
import java.util.TreeMap;

/**
 * AggregatedFeatureValuesData contains strategy of AggregatedFeatureValues buckets and instantToAggregatedFeatureValues map.
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

    /**
     * @return ToString you know...
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
