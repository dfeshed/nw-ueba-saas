package fortscale.ml.model;

import fortscale.common.feature.Feature;
import fortscale.ml.model.store.ModelWithFeatureOccurencesData;

import java.util.HashMap;
import java.util.Map;

public class CategoryRarityModelWithFeatureOccurrencesData extends CategoryRarityModel implements ModelWithFeatureOccurencesData {
    private Map<Feature, Double> featureOccurrences = new HashMap<>();

    public CategoryRarityModelWithFeatureOccurrencesData(Map<Integer, Double> occurrencesToNumOfFeatures) {
        super(occurrencesToNumOfFeatures);
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
