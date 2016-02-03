package fortscale.ml.model.builder;

import fortscale.ml.model.CategoryRarityModel;
import fortscale.ml.model.CategoryRarityModelWithFeatureOccurrencesData;
import fortscale.ml.model.Model;
import java.util.Map;

public class CategoryRarityModelWithFeatureOccurrencesDataBuilder extends CategoryRarityModelBuilder {
    @Override
    public Model build(Object modelBuilderData) {
        Map<String, Long> featureValueToCountMap = castModelBuilderData(modelBuilderData);
        CategoryRarityModelWithFeatureOccurrencesData categoryRarityModel = new CategoryRarityModelWithFeatureOccurrencesData();
        categoryRarityModel.init(getOccurrencesToNumOfFeatures(featureValueToCountMap));
        return categoryRarityModel;

    }
}
