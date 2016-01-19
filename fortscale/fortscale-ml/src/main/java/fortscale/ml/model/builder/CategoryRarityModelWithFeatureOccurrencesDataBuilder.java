package fortscale.ml.model.builder;

import fortscale.ml.model.CategoryRarityModelWithFeatureOccurrencesData;
import fortscale.ml.model.Model;
import fortscale.utils.logging.Logger;

import java.util.Map;

/**
 * Created by amira on 18/01/2016.
 */
public class CategoryRarityModelWithFeatureOccurrencesDataBuilder extends CategoryRarityModelBuilder{
    private static final Logger logger = Logger.getLogger(CategoryRarityModelWithFeatureOccurrencesDataBuilder.class);
    public static final String MODEL_BUILDER_TYPE = "category_rarity_with_feature_occurrences_data";

    @Override
    public Model build(Object modelBuilderData) {
        Map<String, Long> featureValueToCountMap = castModelBuilderData(modelBuilderData);
        return new CategoryRarityModelWithFeatureOccurrencesData(getOccurrencesToNumOfFeatures(featureValueToCountMap));
    }
}
