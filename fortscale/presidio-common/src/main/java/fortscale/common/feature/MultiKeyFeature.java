package fortscale.common.feature;

import fortscale.utils.data.Pair;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

@Document
public class MultiKeyFeature {

    private Map<String, FeatureValue> featureNameToValue;

    public MultiKeyFeature() {
        this.featureNameToValue = new HashMap<>();
    }

    public void add(String featureName, FeatureValue featureValue) {
        this.featureNameToValue.put(featureName, featureValue);
    }

    public Map<String, FeatureValue> getFeatureNameToValue() {
        return featureNameToValue;
    }

    public boolean contains(MultiKeyFeature multiKeyFeature) {
        return this.featureNameToValue.entrySet().containsAll(multiKeyFeature.getFeatureNameToValue().entrySet());
    }

    public boolean containsValue(FeatureValue featureValue) {
        return featureNameToValue.containsValue(featureValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MultiKeyFeature)) return false;
        MultiKeyFeature that = (MultiKeyFeature) o;
        return featureNameToValue.equals(((MultiKeyFeature) o).featureNameToValue);
    }

    @Override
    public int hashCode() {
        return featureNameToValue.hashCode();
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
