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
    public static MaxValuesResult reducePartitionsMapToMaxValues(Map<Long, Double> instantToFeatureValue, Duration instantStep, int resolutionStep,  long resolutionInSeconds, int minNumOfMaxValuesSamples) {

        int resolution = (int) (resolutionInSeconds / instantStep.getSeconds());
        Map<Long, Double> maxValues = PartitionsReduction.getMaxValuesByResolution(instantToFeatureValue, resolutionInSeconds);

        while (maxValues.size() < minNumOfMaxValuesSamples && resolution / resolutionStep > 0) {
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

    /**
     *
     * Reduce the featureValueToCount map by numOfMaxValuesSamples as a bound
     * @param histogram featureValue to count map
     * @param numOfMaxValuesSamples numOfMaxValuesSamples
     * @return featureValueToCount map
     */
    public static Map<String, Double> getMaxValuesHistogram(Map<String, Double> histogram, int numOfMaxValuesSamples) {
        Comparator<Map.Entry<String, Double>> histogramKeyComparator = Comparator.comparingDouble(e -> convertToDouble(e.getKey()));
        Map<String, Double> sortedHistogram = histogram.entrySet().stream().sorted(histogramKeyComparator.reversed()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        Map<String, Double> ret = new HashMap<>();
        double totalNumOfSamples = 0;
        for (Map.Entry<String, Double> entry : sortedHistogram.entrySet()) {
            double count = entry.getValue();
            if (totalNumOfSamples + count >= numOfMaxValuesSamples) {
                ret.put(entry.getKey(), numOfMaxValuesSamples - totalNumOfSamples);
                break;
            } else {
                totalNumOfSamples += count;
                ret.put(entry.getKey(), count);
            }
        }

        return ret;
    }

}
