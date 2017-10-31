package fortscale.ml.model.builder;

import fortscale.common.feature.CategoricalFeatureValue;
import fortscale.ml.model.CategoryRarityModel;
import fortscale.ml.model.Model;
import javafx.util.Pair;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
        long numDistinctFeatures = featureValueToCountMap.size();
        categoryRarityModel.init(calcOccurrencesToNumOfDistinctPartitions(sequenceReduction), numOfBuckets, numOfPartitions, numDistinctFeatures);
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

    Map<Long, Integer> calcOccurrencesToNumOfDistinctPartitions(Map<Pair<String, Long>, Double> sequenceReducedData) {

        Map<String, Set<Long>> nameToPartitionsSet = new HashMap<>();
        for (Pair<String, Long> entry : sequenceReducedData.keySet()) {
            String name = entry.getKey();
            Set<Long> partitionsSet = nameToPartitionsSet.get(name);
            if (partitionsSet == null) {
                partitionsSet = new HashSet<>();
            }
            partitionsSet.add(entry.getValue());
            nameToPartitionsSet.put(name, partitionsSet);
        }

        Map<Long, Set<Long>> occurrencesToPartitionSet = new HashMap<>();
        Map<Long, Set<String>> occurrencesToNameSet = new HashMap<>();
        for (Map.Entry<String, Set<Long>> nameToPartitionsEntry : nameToPartitionsSet.entrySet()) {
            long numOfOccurrences = nameToPartitionsEntry.getValue().size();
            // filling occurrencesToPartitionSet
            Set<Long> occurrencesPartitionsSet = occurrencesToPartitionSet.get(numOfOccurrences);
            if (occurrencesPartitionsSet == null) {
                occurrencesPartitionsSet = new HashSet<>();
                occurrencesToPartitionSet.put(numOfOccurrences,occurrencesPartitionsSet);
            }
            occurrencesPartitionsSet.addAll(nameToPartitionsEntry.getValue());


            //filling occurrencesToNameSet
            Set<String> occurrencesNameSet = occurrencesToNameSet.get(numOfOccurrences);
            if (occurrencesNameSet == null) {
                occurrencesNameSet = new HashSet<>();
                occurrencesToNameSet.put(numOfOccurrences,occurrencesNameSet);
            }
            occurrencesNameSet.add(nameToPartitionsEntry.getKey());
        }

        Map<Long, Integer> ret = occurrencesToPartitionSet.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> Math.min(e.getValue().size(), occurrencesToNameSet.get(e.getKey()).size())));
        return ret;
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
