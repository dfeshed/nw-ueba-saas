package fortscale.ml.model.builder.gaussian;

import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.ContinuousDataModel;
import fortscale.ml.model.Model;
import fortscale.ml.model.builder.IModelBuilder;
import fortscale.ml.utils.MaxValuesResult;
import fortscale.ml.utils.PartitionsReduction;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

import static fortscale.utils.ConversionUtils.convertToDouble;

public class ContinuousHistogramModelBuilder implements IModelBuilder {
    private static final String NULL_MODEL_BUILDER_DATA_ERROR_MSG = "Model builder data cannot be null.";
    private static final String MODEL_BUILDER_DATA_TYPE_ERROR_MSG = String.format(
            "Model builder data must be of type %s.", GenericHistogram.class.getSimpleName());

    @Override
    public Model build(Object modelBuilderData) {
        Map<String, Double> histogram = castModelBuilderData(modelBuilderData).getHistogramMap();
        return buildContinuousDataModel(histogram);
    }

    /**
     * @param values feature values
     * @param numOfMaxValuesSamples numOfMaxValuesSamples
     * @return ContinuousDataModel
     */
    public Model build(Collection<Double> values, int numOfMaxValuesSamples) {
        Map<String, Double> histogram = createGenericHistogram(values).getHistogramMap();
        return buildContinuousDataModel(getMaxValuesHistogram(histogram, numOfMaxValuesSamples));
    }

    protected ContinuousDataModel buildContinuousDataModel(Map<String, Double> histogram) {
        double totalCount = 0;
        double sum = 0;
        double squaredSum = 0;
        double maxValue = 0;
        for (Map.Entry<String, Double> entry : histogram.entrySet()) {
            double count = entry.getValue();
            totalCount += count;
            Double value = convertToDouble(entry.getKey());
            sum += value * count;
            squaredSum += value * value * count;
            maxValue = Math.max(maxValue, value);
        }
        double mean = sum / totalCount;
        double sd = Math.sqrt((squaredSum / totalCount) - mean * mean);
        return new ContinuousDataModel().setParameters((long)totalCount, round(mean), round(sd), round(maxValue));
    }

    protected GenericHistogram castModelBuilderData(Object modelBuilderData) {
        Assert.notNull(modelBuilderData, NULL_MODEL_BUILDER_DATA_ERROR_MSG);
        Assert.isInstanceOf(GenericHistogram.class, modelBuilderData, MODEL_BUILDER_DATA_TYPE_ERROR_MSG);
        return (GenericHistogram)modelBuilderData;
    }

    /**
     *
     * Reduce the featureValueToCount map by numOfMaxValuesSamples as a bound
     * @param histogram featureValue to count map
     * @param numOfMaxValuesSamples numOfMaxValuesSamples
     * @return featureValueToCount map
     */
    private static Map<String, Double> getMaxValuesHistogram(Map<String, Double> histogram, int numOfMaxValuesSamples) {
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

    /***
     * Create generic histogram
     * @param featureValue Collection of feature values
     * @return histogram of feature value to count
     */
    private GenericHistogram createGenericHistogram(Collection<Double> featureValue) {
        GenericHistogram reductionHistogram = new GenericHistogram();
        featureValue.forEach(aggregatedFeatureValue -> {
            reductionHistogram.add(aggregatedFeatureValue, 1d);
        });
        return reductionHistogram;
    }

    private static double round(double value) {
        return Math.round(value * 1000000) / 1000000d;
    }
}
