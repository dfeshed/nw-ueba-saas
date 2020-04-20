package fortscale.common.feature;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.List;

@JsonTypeName(FeatureListValue.FEATURE_VALUE_TYPE)
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY, getterVisibility= JsonAutoDetect.Visibility.NONE, setterVisibility= JsonAutoDetect.Visibility.NONE)
public class FeatureListValue implements FeatureValue {
    public static final String FEATURE_VALUE_TYPE = "feature_list_value";
    private List<String> value;

    public List<String> getValue() {
        return value;
    }

    public void setValue(List<String> value) {
        this.value = value;
    }

    public FeatureListValue(List<String> value) {
        this.value = value;
    }

    public FeatureListValue() {
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
