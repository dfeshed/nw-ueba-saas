package fortscale.ml.model.builder;

import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.CategoryRarityModel;
import fortscale.ml.model.Model;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

public class CategoryRarityModelBuilder implements IModelBuilder {
    private static final String MODEL_BUILDER_DATA_TYPE_ERROR_MSG = String.format(
            "Model builder data must be of type %s.", GenericHistogram.class.getSimpleName());


    @Override
    public Model build(Object modelBuilderData) {
        Map<String, Long> featureValueToCountMap = castModelBuilderData(modelBuilderData);
        CategoryRarityModel categoryRarityModel = new CategoryRarityModel();
        categoryRarityModel.init(getOccurrencesToNumOfFeatures(featureValueToCountMap));
        return categoryRarityModel;
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
        Assert.isInstanceOf(GenericHistogram.class, modelBuilderData, MODEL_BUILDER_DATA_TYPE_ERROR_MSG);
        Map<String, Long> map = new HashMap<>();
        ((GenericHistogram)modelBuilderData).getHistogramMap().entrySet()
                .forEach(entry -> map.put(entry.getKey(), entry.getValue().longValue()));
        return map;
    }
}
