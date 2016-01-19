package fortscale.ml.model.builder;

import fortscale.ml.model.Model;
import fortscale.ml.model.CategoryRarityModel;
import fortscale.utils.logging.Logger;


import java.util.*;

public class CategoryRarityModelBuilder implements IModelBuilder {
    private static final Logger logger = Logger.getLogger(CategoryRarityModelBuilder.class);
    public static final String MODEL_BUILDER_TYPE = "category_rarity";


    @Override
    public Model build(Object modelBuilderData) {
        Map<String, Long> featureValueToCountMap = castModelBuilderData(modelBuilderData);
        return new CategoryRarityModel(getOccurrencesToNumOfFeatures(featureValueToCountMap));
    }


    protected Map<Long, Double> getOccurrencesToNumOfFeatures(Map<String, Long> featureValueToCountMap) {
        Map<Long, Double> occurrencesToNumOfFeatures = new HashMap<>();
        for (Map.Entry<String, Long> entry : featureValueToCountMap.entrySet()) {
            long count = entry.getValue();
            Double numOfFeatures = occurrencesToNumOfFeatures.get(count);
            if (numOfFeatures == null) {
                numOfFeatures = 0D;
            }
            occurrencesToNumOfFeatures.put(count, numOfFeatures + 1);
        }
        return occurrencesToNumOfFeatures;
    }


    protected Map<String, Long> castModelBuilderData(Object modelBuilderData) {
        if (modelBuilderData == null) {
            throw new IllegalArgumentException();
        }
        if (!(modelBuilderData instanceof Map)) {
            String errorMsg = "got illegal modelBuilderData type - probably bad ASL";
            logger.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
        return (Map<String, Long>) modelBuilderData;
    }
}
