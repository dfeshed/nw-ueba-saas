package fortscale.ml.model.builder;

import fortscale.common.feature.CategoricalFeatureValue;
import fortscale.ml.model.CategoryRarityModel;
import fortscale.ml.model.Model;
import fortscale.ml.model.metrics.CategoryRarityModelBuilderMetricsContainer;
import fortscale.utils.data.Pair;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CategoryRarityModelBuilder implements IModelBuilder {
    private static final String MODEL_BUILDER_DATA_TYPE_ERROR_MSG = String.format(
            "Model builder data must be of type %s.", CategoricalFeatureValue.class.getSimpleName());

    private int numOfBuckets;
    private int entriesToSaveInModel;
    private long partitionsResolutionInSeconds;
    private CategoryRarityModelBuilderMetricsContainer categoryRarityModelBuilderMetricsContainer;
    private List<String> filter;

    public CategoryRarityModelBuilder(CategoryRarityModelBuilderConf config, CategoryRarityModelBuilderMetricsContainer categoryRarityModelBuilderMetricsContainer) {
        numOfBuckets = config.getNumOfBuckets();
        entriesToSaveInModel = config.getEntriesToSaveInModel();
        partitionsResolutionInSeconds = config.getPartitionsResolutionInSeconds();
        filter = config.getFilter();
        this.categoryRarityModelBuilderMetricsContainer = categoryRarityModelBuilderMetricsContainer;
    }

    @Override
    public Model build(Object modelBuilderData) {
        CategoricalFeatureValue categoricalFeatureValue = getCategoricalFeatureValue(modelBuilderData);
        Map<Pair<String, Long/*name,partitionId*/>, Double/*at most 1*/> sequenceReduction = calcSequenceReduceData(categoricalFeatureValue);
        Map<String, Integer> featureValueToNumOfOccurrences = getFeatureValueToNumOfOccurrences(sequenceReduction);
        CategoryRarityModel categoryRarityModel = new CategoryRarityModel();
        long numOfPartitions = sequenceReduction.keySet().stream().map(Pair::getValue).distinct().count();
        long numDistinctFeatures = featureValueToNumOfOccurrences.size();
        Map<Long, Integer> occurrencesToNumOfDistinctPartitions = calcOccurrencesToNumOfDistinctPartitions(sequenceReduction);
        Map<Long, Integer> occurrencesToNumOfDistinctFeatureValues = calcOccurrencesToNumOfDistinctFeatureValues(featureValueToNumOfOccurrences);
        categoryRarityModel.init(occurrencesToNumOfDistinctPartitions, occurrencesToNumOfDistinctFeatureValues,
                numOfBuckets, numOfPartitions, numDistinctFeatures);
        saveTopEntriesInModel(featureValueToNumOfOccurrences, categoryRarityModel);
        categoryRarityModelBuilderMetricsContainer.updateMetric(featureValueToNumOfOccurrences.size(), numOfPartitions, categoryRarityModel.getOccurrencesToNumOfPartitionsList());
        return categoryRarityModel;
    }

    //an accumulative histogram
    private Map<Long, Integer> calcOccurrencesToNumOfDistinctFeatureValues(Map<String, Integer> featureValueToNumOfOccurrences){
        Map<Long, Integer> ret = new HashMap<>();
        int maxOccurrence = 0;
        for(Integer occurrence: featureValueToNumOfOccurrences.values()){
            ret.compute((long)occurrence, (k,v) -> v == null ? 1 : v+1);
            if(occurrence>maxOccurrence){
                maxOccurrence = occurrence;
            }
        }

        int prevCount = 0;
        for(int occurrence = 1; occurrence <=maxOccurrence; occurrence++){
            int finalPrevCount = prevCount;
            prevCount = ret.compute((long)occurrence, (k, v) -> v == null ? finalPrevCount : v + finalPrevCount);
        }
        return ret;
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
            result.put(resultKey, Math.min(entry.getValue(),1));
        }

        return result;
    }

    Map<Long, Integer> calcOccurrencesToNumOfDistinctPartitions(Map<Pair<String, Long>, Double> sequenceReducedData) {
        if(sequenceReducedData.isEmpty()){
            return Collections.emptyMap();
        }

        Map<String, Map<Long, Double>> nameToPartitionsMap = new HashMap<>();
        //filter features (e.g: "N/A" features)
        sequenceReducedData.entrySet().forEach(entry -> {
            String name = entry.getKey().getKey();
            if (!filter.contains(name)) {
                Map<Long, Double> partitionsMap = nameToPartitionsMap.computeIfAbsent(name, k -> new HashMap<>());
                partitionsMap.put(entry.getKey().getValue(), entry.getValue());
            }
        });

        //due to the filter the map might be empty.
        if(nameToPartitionsMap.isEmpty()){
            return Collections.emptyMap();
        }

        Map<Integer, Map<Long, Double>> occurrencesToPartitionMap = new HashMap<>();
        for (Map.Entry<String, Map<Long, Double>> nameToPartitionsEntry : nameToPartitionsMap.entrySet()) {
            int numOfOccurrences = (int) Math.ceil(nameToPartitionsEntry.getValue().values().stream().mapToDouble(v->v).sum());
            // filling occurrencesToPartitionSet
            Map<Long, Double> occurrencesPartitionMap = occurrencesToPartitionMap.computeIfAbsent(numOfOccurrences, k -> new HashMap<>());
            for (Map.Entry<Long, Double> entry: nameToPartitionsEntry.getValue().entrySet()){
                occurrencesPartitionMap.compute(entry.getKey(), (k,v) -> v == null ? entry.getValue() : Math.min(v + entry.getValue(),1));
            }
        }

        int maxOccurrences = occurrencesToPartitionMap.keySet().stream().mapToInt(v->v).max().orElseThrow(NoSuchElementException::new);
        Map<Long, Double> partitionMap = new HashMap<>();
        Map<Long, Integer> ret = new HashMap<>();
        for(int i = 1; i<=maxOccurrences; i++){
            Map<Long, Double> occurrencePartitionMap = occurrencesToPartitionMap.get(i);
            if(occurrencePartitionMap != null){
                for (Map.Entry<Long, Double> entry: occurrencePartitionMap.entrySet()){
                    partitionMap.compute(entry.getKey(), (k,v) -> v == null ? entry.getValue() : Math.min(v + entry.getValue(), 1));
                }
            }
            double numOfDifferentPartitions = partitionMap.values().stream().mapToDouble(v->v).sum();
            ret.put((long)i, (int) Math.ceil(numOfDifferentPartitions));
        }

        return ret;
    }

    Map<String, Integer> getFeatureValueToNumOfOccurrences(Map<Pair<String, Long/*name,partitionId*/>, Double/*at most 1*/> sequenceReduction) {
        Map<String, Double> map = new HashMap<>();
        sequenceReduction.forEach((key, value) -> {
                    String name = key.getKey();
                    if (map.get(name) != null) {
                        map.put(name, map.get(name) + value);
                    } else {
                        map.put(name, 1D);
                    }
                }
        );

        Map<String, Integer> ret = new HashMap<>();
        map.forEach((key, value)-> ret.put(key, value.intValue()));

        return ret;
    }

    private CategoricalFeatureValue getCategoricalFeatureValue(Object modelBuilderData) {
        Assert.isInstanceOf(CategoricalFeatureValue.class, modelBuilderData, MODEL_BUILDER_DATA_TYPE_ERROR_MSG);
        return (CategoricalFeatureValue) modelBuilderData;
    }

    private void saveTopEntriesInModel(Map<String, Integer> countMap, CategoryRarityModel model) {
        countMap.entrySet().stream()
                .sorted((entry1, entry2) -> Long.compare(entry2.getValue(), entry1.getValue()))
                .limit(entriesToSaveInModel)
                .forEach(entry -> model.setFeatureCount(entry.getKey(), entry.getValue()));
        model.setNumberOfEntriesToSaveInModel(entriesToSaveInModel);
    }
}
