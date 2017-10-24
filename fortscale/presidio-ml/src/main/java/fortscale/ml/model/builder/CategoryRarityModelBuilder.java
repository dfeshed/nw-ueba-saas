package fortscale.ml.model.builder;

import fortscale.common.feature.CategoricalFeatureValue;
import fortscale.ml.model.CategoryRarityModel;
import fortscale.ml.model.Model;
import javafx.util.Pair;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class CategoryRarityModelBuilder implements IModelBuilder {
    private static final String MODEL_BUILDER_DATA_TYPE_ERROR_MSG = String.format(
            "Model builder data must be of type %s.", CategoricalFeatureValue.class.getSimpleName());

    private int numOfBuckets;
    private int entriesToSaveInModel;
    private long partitionsResolutionInSeconds;

    public CategoryRarityModelBuilder(CategoryRarityModelBuilderConf config) {
        numOfBuckets = config.getNumOfBuckets();
        entriesToSaveInModel = config.getEntriesToSaveInModel();
        partitionsResolutionInSeconds = config.getPartitionsResolutionInSeconds();
    }

    @Override
    public Model build(Object modelBuilderData) {
        CategoricalFeatureValue categoricalFeatureValue = getCategoricalFeatureValue(modelBuilderData);
        Map<Pair<String, Long/*name,partitionId*/>, Double/*sum of occurrences*/> sequenceReduction = calcSequenceReduceData(categoricalFeatureValue);
        Map<String, Long> featureValueToCountMap = castModelBuilderData(sequenceReduction);
        CategoryRarityModel categoryRarityModel = new CategoryRarityModel();
        long numOfPartitions = sequenceReduction.keySet().stream().map(Pair::getValue).distinct().count();
        categoryRarityModel.init(calcOccurrencesToNumOfFeatures(sequenceReduction), numOfBuckets, numOfPartitions);
        saveTopEntriesInModel(featureValueToCountMap, categoryRarityModel);
        return categoryRarityModel;
    }

    /**
     *
     * @param categoricalFeatureValue
     * @return sequence reduced map with number of at most one per partition
     */
    Map<Pair<String, Long>, Double> calcSequenceReduceData(CategoricalFeatureValue categoricalFeatureValue) {
        long categoricalFeatureValueStrategy = categoricalFeatureValue.getStrategy().toDuration().getSeconds();
        String assertionMessage = String.format("sequencing resolution=%d must be multiplication of categoricalFeatureValue strategy=%d", partitionsResolutionInSeconds, categoricalFeatureValueStrategy);
        Assert.isTrue( partitionsResolutionInSeconds % categoricalFeatureValueStrategy == 0,
                assertionMessage);
        Map<Pair<String, Long>, Double> result = new HashMap<>();

        for (Map.Entry<Pair<String, Instant>, Double> entry : categoricalFeatureValue.getHistogram().entrySet()) {
            long partitionEpochSecond = (entry.getKey().getValue().getEpochSecond() / partitionsResolutionInSeconds) * partitionsResolutionInSeconds;
            Pair<String, Long> resultKey = new Pair(entry.getKey().getKey(), partitionEpochSecond);
            result.put(resultKey, 1D);
        }

        return result;
    }

    Map<Long, Double> calcOccurrencesToNumOfFeatures(Map<Pair<String, Long>, Double> sequenceReducedData) {

        Map<String, Long> nameToNumOfOccurrences = new HashMap<>();
        for (Pair<String, Long> entry : sequenceReducedData.keySet()) {
            String name = entry.getKey();
            Long namOfOccurrences = nameToNumOfOccurrences.get(name);
            if (namOfOccurrences == null) {
                namOfOccurrences = 0L;
            }
            namOfOccurrences++;
            nameToNumOfOccurrences.put(name, namOfOccurrences);
        }

        Map<Long, Double> occurrencesToNumOfFeatures = new HashMap<>();
        for (Long numOfOccurrences : nameToNumOfOccurrences.values()) {
            Double numOfFeatures = occurrencesToNumOfFeatures.get(numOfOccurrences);
            if (numOfFeatures == null) {
                numOfFeatures = 0D;
            }
            numOfFeatures++;
            occurrencesToNumOfFeatures.put(numOfOccurrences,numOfFeatures);

        }
        return occurrencesToNumOfFeatures;
    }

    Map<String, Long> castModelBuilderData(Map<Pair<String, Long>, Double> modelBuilderData) {
        Map<String, Long> map = new HashMap<>();
        modelBuilderData.forEach((key, value) -> {
                    String name = key.getKey();
                    if (map.get(name) != null) {
                        map.put(name, map.get(name) + value.longValue());
                    } else {
                        map.put(name, value.longValue());
                    }
                }
        );
        return map;
    }

    private CategoricalFeatureValue getCategoricalFeatureValue(Object modelBuilderData) {
        Assert.isInstanceOf(CategoricalFeatureValue.class, modelBuilderData, MODEL_BUILDER_DATA_TYPE_ERROR_MSG);
        return (CategoricalFeatureValue) modelBuilderData;
    }

    private void saveTopEntriesInModel(Map<String, Long> countMap, CategoryRarityModel model) {
        countMap.entrySet().stream()
                .sorted((entry1, entry2) -> Long.compare(entry2.getValue(), entry1.getValue()))
                .limit(entriesToSaveInModel)
                .forEach(entry -> model.setFeatureCount(entry.getKey(), entry.getValue()));
        model.setNumberOfEntriesToSaveInModel(entriesToSaveInModel);
    }
}
