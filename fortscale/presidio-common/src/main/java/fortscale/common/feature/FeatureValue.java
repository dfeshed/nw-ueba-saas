package fortscale.common.feature;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fortscale.common.util.ContinuousValueAvgStdN;
import fortscale.common.util.GenericHistogram;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = AggrFeatureValue.class, name = AggrFeatureValue.FEATURE_VALUE_TYPE),
        @JsonSubTypes.Type(value = GenericHistogram.class, name = GenericHistogram.FEATURE_VALUE_TYPE),
        @JsonSubTypes.Type(value = CategoricalFeatureValue.class, name = CategoricalFeatureValue.FEATURE_VALUE_TYPE),
        @JsonSubTypes.Type(value = ContinuousValueAvgStdN.class, name = ContinuousValueAvgStdN.FEATURE_VALUE_TYPE),
        @JsonSubTypes.Type(value = FeatureNumericValue.class, name = FeatureNumericValue.FEATURE_VALUE_TYPE),
        @JsonSubTypes.Type(value = FeatureStringValue.class, name = FeatureStringValue.FEATURE_VALUE_TYPE),
        @JsonSubTypes.Type(value = FeatureListValue.class, name = FeatureListValue.FEATURE_VALUE_TYPE),
        @JsonSubTypes.Type(value = MultiKeyHistogram.class, name = MultiKeyHistogram.FEATURE_VALUE_TYPE)
})

public interface FeatureValue {
}
