package fortscale.common.feature;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class Feature implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private FeatureValue value;

    public Feature() {}

    public Feature(String name) {
        this.name = name;
    }

    public Feature(String name, FeatureValue value) {
        this.name = name;
        this.value = value;
    }

    public Feature(String name, Number value) {
        this.name = name;
        this.value = new FeatureNumericValue(value);
    }

    public Feature(String name, String value) {
        this.name = name;
        this.value = new FeatureStringValue(value);
    }

    public String getName() {
        return name;
    }

    public FeatureValue getValue() {
        return value;
    }

    public void setValue(FeatureValue value) {
        this.value = value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static Feature toFeature(String name, Object object) {
        FeatureValue featureValue = null;

        if (object instanceof String) {
            featureValue = new FeatureStringValue((String) object);
        } else if (object instanceof Number) {
            featureValue = new FeatureNumericValue((Number) object);
        } else if (object != null && object.getClass().isEnum()) {
            featureValue = new FeatureStringValue(((Enum) object).name());
        }

        return new Feature(name, featureValue);
    }
}
