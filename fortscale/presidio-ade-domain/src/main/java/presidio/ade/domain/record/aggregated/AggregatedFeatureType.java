package presidio.ade.domain.record.aggregated;

/**
 * {@link AggregatedFeatureType#FEATURE_AGGREGATION} An Aggregated Feature Event of type F, which means it holds a feature that can be scored.
 * {@link AggregatedFeatureType#SCORE_AGGREGATION} A parameter that was calculated from an enriched score. These features are not scored.
 * Created by barak_schuster on 7/9/17.
 */
public enum AggregatedFeatureType {
    SCORE_AGGREGATION, //AKA P
    FEATURE_AGGREGATION; // AKA F


    public static final String FEATURE_AGGREGATION_CODE_REPRESENTATION = "F";
    public static final String SCORE_AGGREGATION_CODE_REPRESENTATION = "P";

    public static AggregatedFeatureType fromCodeRepresentation(String type) {
        if(type.equals(SCORE_AGGREGATION_CODE_REPRESENTATION))
        {
            return SCORE_AGGREGATION;
        }
        else if (type.equals(FEATURE_AGGREGATION_CODE_REPRESENTATION))
        {
            return FEATURE_AGGREGATION;
        }
        else
        {
            throw new RuntimeException(String.format("Unsupported AggregatedFeatureType: %s",type));
        }
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
