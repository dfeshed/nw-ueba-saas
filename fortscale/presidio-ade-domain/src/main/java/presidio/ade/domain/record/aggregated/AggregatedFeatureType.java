package presidio.ade.domain.record.aggregated;

/**
 * {@link AggregatedFeatureType#FEATURE_AGGREGATION} An Aggregated Feature Event of type F, which means it holds a feature bucket that can be scored.
 * {@link AggregatedFeatureType#SCORE_AGGREGATION} A parameter which was calculated from a bucket on scored raw. those events are not scored
 * Created by barak_schuster on 7/9/17.
 */
public enum AggregatedFeatureType {
    SCORE_AGGREGATION, //AKA P
    FEATURE_AGGREGATION; // AKA F

    public static AggregatedFeatureType fromConfStringType(String type) {
        if(type.equals("P"))
        {
            return SCORE_AGGREGATION;
        }
        else
        {
            return FEATURE_AGGREGATION;
        }
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
