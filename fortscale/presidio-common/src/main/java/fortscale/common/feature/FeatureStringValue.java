package fortscale.common.feature;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(FeatureStringValue.FEATURE_VALUE_TYPE)
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY, getterVisibility= JsonAutoDetect.Visibility.NONE, setterVisibility= JsonAutoDetect.Visibility.NONE)
public class FeatureStringValue implements FeatureValue {
    public static final String FEATURE_VALUE_TYPE = "feature_string_value";
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public FeatureStringValue(String value) {
        this.value = value;
    }

    public FeatureStringValue() {
    }

    @Override
    public String toString() {
        return value;
    }
}
