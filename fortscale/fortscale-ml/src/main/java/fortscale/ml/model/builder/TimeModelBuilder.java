package fortscale.ml.model.builder;

import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.Model;
import fortscale.ml.model.prevalance.field.TimeModel;
import org.springframework.util.Assert;

public class TimeModelBuilder implements IModelBuilder {
    private static final String NULL_MODEL_BUILDER_DATA_ERROR_MSG = "Model builder data cannot be null.";
    private static final String MODEL_BUILDER_DATA_TYPE_ERROR_MSG = String.format(
            "Model builder data must be of type %s.", GenericHistogram.class.getSimpleName());

    private final int timeResolution;
    private final int bucketSize;

    public TimeModelBuilder(TimeModelBuilderConf config) {
        timeResolution = config.getTimeResolution();
        bucketSize = config.getBucketSize();
    }

    @Override
    public Model build(Object modelBuilderData) {
        return new TimeModel(
                timeResolution,
                bucketSize,
                castModelBuilderData(modelBuilderData).getHistogramMap()
        );
    }

    private GenericHistogram castModelBuilderData(Object modelBuilderData) {
        Assert.notNull(modelBuilderData, NULL_MODEL_BUILDER_DATA_ERROR_MSG);
        Assert.isInstanceOf(GenericHistogram.class, modelBuilderData, MODEL_BUILDER_DATA_TYPE_ERROR_MSG);
        return (GenericHistogram)modelBuilderData;
    }
}
