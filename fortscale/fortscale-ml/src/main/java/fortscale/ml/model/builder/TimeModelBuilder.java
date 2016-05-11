package fortscale.ml.model.builder;

import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.Model;
import fortscale.ml.model.TimeModel;
import org.springframework.util.Assert;

public class TimeModelBuilder implements IModelBuilder {
    private static final String NULL_MODEL_BUILDER_DATA_ERROR_MSG = "Model builder data cannot be null.";
    private static final String MODEL_BUILDER_DATA_TYPE_ERROR_MSG = String.format(
            "Model builder data must be of type %s.", GenericHistogram.class.getSimpleName());

    private final int timeResolution;
    private final int bucketSize;
    private final int maxRareTimestampCount;

    public TimeModelBuilder(TimeModelBuilderConf config) {
        timeResolution = config.getTimeResolution();
        bucketSize = config.getBucketSize();
        maxRareTimestampCount = config.getMaxRareTimestampCount();
    }

    @Override
    public Model build(Object modelBuilderData) {
        TimeModel timeModel = new TimeModel();
        timeModel.init(
                timeResolution, bucketSize, maxRareTimestampCount,
                castModelBuilderData(modelBuilderData).getHistogramMap());
        return timeModel;
    }

    private GenericHistogram castModelBuilderData(Object modelBuilderData) {
        Assert.notNull(modelBuilderData, NULL_MODEL_BUILDER_DATA_ERROR_MSG);
        Assert.isInstanceOf(GenericHistogram.class, modelBuilderData, MODEL_BUILDER_DATA_TYPE_ERROR_MSG);
        return (GenericHistogram)modelBuilderData;
    }
}
