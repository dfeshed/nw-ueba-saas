package fortscale.ml.model.builder;

import fortscale.aggregation.feature.util.GenericHistogram;
import fortscale.ml.model.Model;
import fortscale.ml.model.prevalance.field.TimeModel;
import org.springframework.util.Assert;

public class TimeModelBuilder implements IModelBuilder {
    private static final String NULL_MODEL_BUILDER_DATA_ERROR_MSG = "Model builder data cannot be null.";
    private static final String MODEL_BUILDER_DATA_TYPE_ERROR_MSG = String.format(
            "Model builder data must be of type %s.", GenericHistogram.class.getSimpleName());

    private final int timeResolution;
    private final int bucketSize;
    private final int minEvents;
    private final int maxRareTimestampCount;
    private final int maxNumOfRareTimestamps;

    public TimeModelBuilder(TimeModelBuilderConf config) {
        timeResolution = config.getTimeResolution();
        bucketSize = config.getBucketSize();
        minEvents = config.getMinEvents();
        maxRareTimestampCount = config.getMaxRareTimestampCount();
        maxNumOfRareTimestamps = config.getMaxNumOfRareTimestamps();
    }

    @Override
    public Model build(Object modelBuilderData) {
        return new TimeModel(
                timeResolution,
                bucketSize,
                minEvents,
                maxRareTimestampCount,
                maxNumOfRareTimestamps,
                castModelBuilderData(modelBuilderData).getHistogramMap()
        );
    }

    @Override
    public double calculateScore(Object value, Model model) {
        return model.calculateScore(value);
    }

    private GenericHistogram castModelBuilderData(Object modelBuilderData) {
        Assert.notNull(modelBuilderData, NULL_MODEL_BUILDER_DATA_ERROR_MSG);
        Assert.isInstanceOf(GenericHistogram.class, modelBuilderData, MODEL_BUILDER_DATA_TYPE_ERROR_MSG);
        return (GenericHistogram)modelBuilderData;
    }
}
