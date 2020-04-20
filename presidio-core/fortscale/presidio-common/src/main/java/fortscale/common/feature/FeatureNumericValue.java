package fortscale.common.feature;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY, getterVisibility= JsonAutoDetect.Visibility.NONE, setterVisibility= JsonAutoDetect.Visibility.NONE)
public class FeatureNumericValue implements FeatureValue {

    public static final String FEATURE_VALUE_TYPE = "feature_numeric_value";
    private Number value;

    public Number getValue() {
        return value;
    }

    public void setValue(Number value) {
        this.value = value;
    }

    public FeatureNumericValue() {
    }

    public FeatureNumericValue(Number value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value!=null?value.toString():null;
    }
}
