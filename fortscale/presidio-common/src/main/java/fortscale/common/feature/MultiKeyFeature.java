package fortscale.common.feature;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

    public boolean containsAtLeastOneValue(Set<FeatureStringValue> featureValues) {
        for(FeatureStringValue featureValue : featureValues){
            if(featureNameToValue.containsValue(featureValue)){
                return true;
            }
        }
        return false;
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
