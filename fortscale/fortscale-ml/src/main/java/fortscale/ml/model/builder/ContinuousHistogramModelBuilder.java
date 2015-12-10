package fortscale.ml.model.builder;

import fortscale.aggregation.feature.util.GenericHistogram;
import fortscale.ml.model.Model;
import fortscale.ml.model.prevalance.field.ContinuousDataModel;
import org.springframework.util.Assert;

import java.util.Map;

import static fortscale.utils.ConversionUtils.convertToDouble;

public class ContinuousHistogramModelBuilder implements IModelBuilder {
    public static final String MODEL_BUILDER_TYPE = "continuous_data_histogram";
    private static final int DEFAULT_NUM_OF_DIGITS_AFTER_POINT = 3;

    @Override
    public Model build(Object modelBuilderData) {
        Map<String, Double> histogram = castModelBuilderData(modelBuilderData).getHistogramMap();

        // Calculate mean
        double totalCount = 0;
        double sum = 0;
        for (Map.Entry<String, Double> entry : histogram.entrySet()) {
            double count = entry.getValue();
            totalCount += count;
            sum += convertToDouble(entry.getKey()) * count;
        }
        double mean = sum / totalCount;

        // Calculate standard deviation
        sum = 0;
        for (Map.Entry<String, Double> entry : histogram.entrySet()) {
            sum += Math.pow(convertToDouble(entry.getKey()) - mean, 2) * entry.getValue();
        }
        double sd = Math.sqrt(sum / totalCount);

        ContinuousDataModel model = new ContinuousDataModel();
        mean = round(mean, DEFAULT_NUM_OF_DIGITS_AFTER_POINT);
        sd = round(sd, DEFAULT_NUM_OF_DIGITS_AFTER_POINT);
        model.setParameters((long)totalCount, mean, sd);
        return model;
    }

    @Override
    public double calculateScore(Object value, Model model) {
        return model.calculateScore(value);
    }

    private GenericHistogram castModelBuilderData(Object modelBuilderData) {
        Assert.notNull(modelBuilderData, "Model builder data cannot be null.");

        String errorMsg = String.format("Model builder data must be of type %s.",
                GenericHistogram.class.getSimpleName());
        Assert.isInstanceOf(GenericHistogram.class, modelBuilderData, errorMsg);

        return (GenericHistogram)modelBuilderData;
    }

    private static double round(double value, int numOfDigitsAfterPoint) {
        if (numOfDigitsAfterPoint < 0) {
            numOfDigitsAfterPoint = DEFAULT_NUM_OF_DIGITS_AFTER_POINT;
        }

        try {
            double helper = Math.pow(10.0, numOfDigitsAfterPoint);
            return Math.round(value * helper) / helper;
        } catch (Exception e) {
            return value;
        }
    }
}
