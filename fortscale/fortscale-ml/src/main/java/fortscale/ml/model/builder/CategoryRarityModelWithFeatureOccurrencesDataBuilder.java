package fortscale.ml.model.builder;

import fortscale.ml.model.CategoryRarityModelWithFeatureOccurrencesData;
import fortscale.ml.model.Model;
import java.util.Map;

public class CategoryRarityModelWithFeatureOccurrencesDataBuilder extends CategoryRarityModelBuilder {
    @Override
    public Model build(Object modelBuilderData) {
        Map<String, Integer> featureValueToCountMap = castModelBuilderData(modelBuilderData);
        return new CategoryRarityModelWithFeatureOccurrencesData(getOccurrencesToNumOfFeatures(featureValueToCountMap));
    }
}
