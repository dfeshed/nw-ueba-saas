package fortscale.ml.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import fortscale.common.feature.Feature;
import fortscale.ml.model.store.ModelWithFeatureOccurencesData;
import org.springframework.data.annotation.PersistenceConstructor;

import java.util.HashMap;
import java.util.Map;
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class CategoryRarityModelWithFeatureOccurrencesData extends CategoryRarityModel implements ModelWithFeatureOccurencesData {
    private Map<Feature, Double> featureOccurrences = new HashMap<>();

    public void init(Map<Long, Double> occurrencesToNumOfFeatures) {
        super.init(occurrencesToNumOfFeatures);
    }

    @Override
    public Double getFeatureCount(Feature feature) {
        return featureOccurrences ==null?null:feature.getValue()==null?null: featureOccurrences.get(feature);
    }

    @Override
    public void setFeatureCount(Feature feature, double counter) {
        if(feature==null || feature.getValue()==null) {
            return;
        }
        if(featureOccurrences ==null) {
            featureOccurrences = new HashMap<>();
        }
        featureOccurrences.put(feature, counter);
    }

}
