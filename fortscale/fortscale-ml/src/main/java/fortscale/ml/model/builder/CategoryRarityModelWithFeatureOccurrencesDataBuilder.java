package fortscale.ml.model.builder;

import fortscale.ml.model.CategoryRarityModelWithFeatureOccurrencesData;
import fortscale.ml.model.Model;
import java.util.Map;

public class CategoryRarityModelWithFeatureOccurrencesDataBuilder extends CategoryRarityModelBuilder {

    public CategoryRarityModelWithFeatureOccurrencesDataBuilder(CategoryRarityModelBuilderConf config) {
        super(config);
    }

    @Override
    public Model build(Object modelBuilderData) {
        Map<String, Long> featureValueToCountMap = castModelBuilderData(modelBuilderData);
        CategoryRarityModelWithFeatureOccurrencesData categoryRarityModel = new CategoryRarityModelWithFeatureOccurrencesData();
        categoryRarityModel.init(getOccurrencesToNumOfFeatures(featureValueToCountMap), numOfBuckets);
        return categoryRarityModel;
    }
}
