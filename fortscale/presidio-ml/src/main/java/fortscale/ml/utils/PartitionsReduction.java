package fortscale.ml.utils;

import fortscale.utils.data.Pair;

import java.time.Duration;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static fortscale.utils.ConversionUtils.convertToDouble;

public class PartitionsReduction {

    /**
     * Reduce instantToFeatureValue map to max values.
     * Use resolutionInSeconds as an initial resolution and decrease it by resolutionStep until maxValues map will contain at least minNumOfMaxValuesSamples.
     *
     * @param instantToFeatureValue start instant to featureValue map
     * @param resolutionStep resolutionStep
     * @param instantStep instantStep (e.g: 3600 seconds)
     * @param resolutionInSeconds initial resolution
     * @param minNumOfMaxValuesSamples minNumOfMaxValuesSamples
     * @return  map of instant to max feature value and final resolution
     */
    public static MaxValuesResult reducePartitionsMapToMaxValues(Map<Long, Double> instantToFeatureValue, Duration instantStep, int resolutionStep,  long resolutionInSeconds, int minNumOfMaxValuesSamples, int minResolution) {
        int resolution = (int) (resolutionInSeconds / instantStep.getSeconds());
        Map<Long, Double> maxValues = PartitionsReduction.getMaxValuesByResolution(instantToFeatureValue, resolutionInSeconds);

        while (maxValues.size() < minNumOfMaxValuesSamples && resolution / resolutionStep >= minResolution) {
            resolution = resolution / resolutionStep;
            resolutionInSeconds = resolution * instantStep.getSeconds();
            maxValues = PartitionsReduction.getMaxValuesByResolution(instantToFeatureValue, resolutionInSeconds);
        }

        return new MaxValuesResult(resolutionInSeconds, maxValues);
    }
    /**
     * Split instantToFeatureValue by resolution and get max value of each part.
     *
     * @param instantToFeatureValue start instant to featureValue treeMap
     * @param resolutionInSeconds   resolutionInSeconds
     * @return map of instant and max feature value
     */
    private static Map<Long, Double> getMaxValuesByResolution(Map<Long, Double> instantToFeatureValue, long resolutionInSeconds) {
        Map<Integer, Pair<Long, Double>> instantToMaxValueMap = new HashMap<>();

        instantToFeatureValue.forEach((instant, featureValue) -> {
            int bucketIndex = (int) ((instant / resolutionInSeconds));
            Pair<Long, Double> instantToValuePair = instantToMaxValueMap.get(bucketIndex);
            if ((instantToValuePair == null || instantToValuePair.getValue() < featureValue)) {
                instantToMaxValueMap.put(bucketIndex, new Pair<>(instant, featureValue));
            }
        });

        return instantToMaxValueMap.values().stream().collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

}
