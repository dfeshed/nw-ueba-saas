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

    private int numOfBuckets;
    private int entriesToSaveInModel;

    public CategoryRarityModelBuilder(CategoryRarityModelBuilderConf config) {
        numOfBuckets = config.getNumOfBuckets();
        entriesToSaveInModel = config.getEntriesToSaveInModel();
    }

    @Override
    public Model build(Object modelBuilderData) {
        GenericHistogram genericHistogram = getGenericHistogram(modelBuilderData);
        Map<String, Long> featureValueToCountMap = castModelBuilderData(genericHistogram);
        CategoryRarityModel categoryRarityModel = new CategoryRarityModel();
        long numOfPartitions = genericHistogram.getNumberOfPartitions();
        categoryRarityModel.init(getOccurrencesToNumOfFeatures(featureValueToCountMap), numOfBuckets, numOfPartitions);
        saveTopEntriesInModel(featureValueToCountMap, categoryRarityModel);
        return categoryRarityModel;
    }

    private Map<Long, Double> getOccurrencesToNumOfFeatures(Map<String, Long> featureValueToCountMap) {
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

    private Map<String, Long> castModelBuilderData(GenericHistogram modelBuilderData) {
        Map<String, Long> map = new HashMap<>();

        modelBuilderData.getHistogramMap().forEach((key, value) -> map.put(key, value.longValue()));
        return map;
    }

    private GenericHistogram getGenericHistogram(Object modelBuilderData) {
        Assert.isInstanceOf(GenericHistogram.class, modelBuilderData, MODEL_BUILDER_DATA_TYPE_ERROR_MSG);
        return (GenericHistogram) modelBuilderData;
    }

    private void saveTopEntriesInModel(Map<String, Long> countMap, CategoryRarityModel model) {
        countMap.entrySet().stream()
                .sorted((entry1, entry2) -> Long.compare(entry2.getValue(), entry1.getValue()))
                .limit(entriesToSaveInModel)
                .forEach(entry -> model.setFeatureCount(entry.getKey(), entry.getValue()));
        model.setNumberOfEntriesToSaveInModel(entriesToSaveInModel);
    }
}
