package fortscale.common.feature;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document
public class MultiKeyFeature {

    private Map<String, FeatureValue> featureNameToValue;


    public MultiKeyFeature(Map<String, FeatureValue> featureNameToValue) {
        this.featureNameToValue = featureNameToValue;
    }

    public Map<String, FeatureValue> getFeatureNameToValue() {
        return featureNameToValue;
    }


    public boolean contains(Map<String, FeatureValue> featureNameToFeatureValue) {
        return this.featureNameToValue.entrySet().containsAll(featureNameToFeatureValue.entrySet());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MultiKeyFeature)) return false;

        MultiKeyFeature that = (MultiKeyFeature) o;

        if (featureNameToValue.size() != that.getFeatureNameToValue().size()) return false;

        for (Map.Entry<String, FeatureValue> entry : featureNameToValue.entrySet()) {
            if (!that.getFeatureNameToValue().get(entry.getKey()).equals(entry.getValue())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (Map.Entry<String, FeatureValue> entry : featureNameToValue.entrySet()) {
            result = 31 * result + entry.getKey().hashCode() + entry.getValue().hashCode();
        }
        return result;
    }


    @Override
    public String toString() {
        return "MultiKeyFeature{" +
                "featureNameToValue=" + featureNameToValue +
                '}';
    }
}
