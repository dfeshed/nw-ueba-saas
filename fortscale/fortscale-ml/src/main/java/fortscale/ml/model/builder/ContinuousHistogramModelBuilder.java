package fortscale.ml.model.builder;

import fortscale.ml.model.Model;
import fortscale.ml.model.prevalance.field.ContinuousDataModel;
import fortscale.utils.logging.Logger;

import java.util.Map;

public class ContinuousHistogramModelBuilder implements IModelBuilder {
    private static final Logger logger = Logger.getLogger(ContinuousHistogramModelBuilder.class);
    public static final String MODEL_BUILDER_TYPE = "continuous_data_histogram";

    @Override
    public Model build(Object modelBuilderData) {
        Map<Double, Double> histogram = castModelBuilderData(modelBuilderData);

        // Calculate mean
        double totalCount = 0;
        double sum = 0;
        for (Map.Entry<Double, Double> entry : histogram.entrySet()) {
            double count = entry.getValue();
            totalCount += count;
            sum += entry.getKey() * count;
        }
        double mean = sum / totalCount;

        // Calculate standard deviation
        sum = 0;
        for (Map.Entry<Double, Double> entry : histogram.entrySet())
            sum += Math.pow(entry.getKey() - mean, 2) * entry.getValue();
        double sd = Math.sqrt(sum / totalCount);

        ContinuousDataModel model = new ContinuousDataModel();
        model.setParameters((long) totalCount, mean, sd);
        return model;
    }

    private Map<Double, Double> castModelBuilderData(Object modelBuilderData) {
        if (modelBuilderData == null) {
            throw new IllegalArgumentException();
        }
        if (!(modelBuilderData instanceof Map)) {
            String errorMsg = "got illegal modelBuilderData type - probably bad ASL";
            logger.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
        return (Map<Double, Double>) modelBuilderData;
    }
}
