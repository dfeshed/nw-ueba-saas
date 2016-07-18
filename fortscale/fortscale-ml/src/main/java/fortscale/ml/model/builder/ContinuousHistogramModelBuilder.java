package fortscale.ml.model.builder;

import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.ContinuousDataModel;
import fortscale.ml.model.Model;
import org.springframework.util.Assert;

import java.util.Map;

import static fortscale.utils.ConversionUtils.convertToDouble;

public class ContinuousHistogramModelBuilder implements IModelBuilder {
    private static final String NULL_MODEL_BUILDER_DATA_ERROR_MSG = "Model builder data cannot be null.";
    private static final String MODEL_BUILDER_DATA_TYPE_ERROR_MSG = String.format(
            "Model builder data must be of type %s.", GenericHistogram.class.getSimpleName());

    @Override
    public Model build(Object modelBuilderData) {
        Map<String, Double> histogram = castModelBuilderData(modelBuilderData).getHistogramMap();

        // Calculate mean and maxValue
        double totalCount = 0;
        double sum = 0;
        double maxValue = 0;
        for (Map.Entry<String, Double> entry : histogram.entrySet()) {
            double count = entry.getValue();
            totalCount += count;
            Double value = convertToDouble(entry.getKey());
            sum += value * count;
            maxValue = Math.max(maxValue, value);
        }
        double mean = sum / totalCount;

        // Calculate standard deviation
        sum = 0;
        for (Map.Entry<String, Double> entry : histogram.entrySet()) {
            sum += Math.pow(convertToDouble(entry.getKey()) - mean, 2) * entry.getValue();
        }
        double sd = Math.sqrt(sum / totalCount);

        ContinuousDataModel model = new ContinuousDataModel();
        model.setParameters((long)totalCount, round(mean), round(sd), round(maxValue));
        return model;
    }

    private GenericHistogram castModelBuilderData(Object modelBuilderData) {
        Assert.notNull(modelBuilderData, NULL_MODEL_BUILDER_DATA_ERROR_MSG);
        Assert.isInstanceOf(GenericHistogram.class, modelBuilderData, MODEL_BUILDER_DATA_TYPE_ERROR_MSG);
        return (GenericHistogram)modelBuilderData;
    }

    private static double round(double value) {
        return Math.round(value * 1000) / 1000d;
    }
}
