package fortscale.ml.model.builder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.ml.model.Model;
import fortscale.ml.model.prevalance.field.TimeModel;
import fortscale.utils.logging.Logger;
import org.springframework.util.Assert;
import java.util.List;

public class TimeModelBuilder implements IModelBuilder {
    private static final Logger logger = Logger.getLogger(TimeModelBuilder.class);
    public static final String MODEL_BUILDER_TYPE = "time";

    private final int timeResolution;
    private final int bucketSize;

    @JsonCreator
    public TimeModelBuilder(@JsonProperty("timeResolution") Integer timeResolution,
                            @JsonProperty("bucketSize") Integer bucketSize) {
        Assert.notNull(timeResolution);
        Assert.notNull(bucketSize);
        Assert.isTrue(timeResolution > 0);
        Assert.isTrue(bucketSize > 0);

        this.timeResolution = timeResolution;
        this.bucketSize = bucketSize;
    }

    @Override
    public Model build(Object modelBuilderData) {
        List<Long> times = castModelBuilderData(modelBuilderData);
        TimeModel model = new TimeModel(timeResolution, bucketSize, times);
        return model;
    }

    @Override
    public double calculateScore(Object value, Model model) {
        return model.calculateScore(value);
    }

    private List<Long> castModelBuilderData(Object modelBuilderData) {
        if (modelBuilderData == null) {
            throw new IllegalArgumentException();
        }
        if (!(modelBuilderData instanceof List)) {
            String errorMsg = "got illegal modelBuilderData type - probably bad ASL";
            logger.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
        return (List<Long>) modelBuilderData;
    }
}
