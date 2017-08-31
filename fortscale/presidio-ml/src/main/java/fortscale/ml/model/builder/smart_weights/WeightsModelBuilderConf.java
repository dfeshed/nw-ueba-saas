package fortscale.ml.model.builder.smart_weights;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.ml.model.builder.IModelBuilderConf;
import org.springframework.util.Assert;

/**
 * Created by barak_schuster on 30/08/2017.
 */
public class WeightsModelBuilderConf implements IModelBuilderConf {
    public static final String WEIGHTS_MODEL_BUILDER = "weights_model_builder";
    private static final int DEFAULT_NUM_OF_SIMULATIONS = 100;

    private String smartRecordConfName;
    private int numOfSimulations;

    @JsonCreator
    public WeightsModelBuilderConf(
            @JsonProperty("smartRecordConfName") String smartRecordConfName,
            @JsonProperty("numOfSimulations") Integer numOfSimulations) {
        if (numOfSimulations == null) {
            numOfSimulations = DEFAULT_NUM_OF_SIMULATIONS;
        }
        Assert.hasText(smartRecordConfName, "smartRecordConfName must has text");
        Assert.isTrue(numOfSimulations > 0,String.format("numOfSimulations must be more than 0 for smartConfName=%s",smartRecordConfName));
        this.smartRecordConfName = smartRecordConfName;
        this.numOfSimulations = numOfSimulations;
    }
    public String getSmartRecordConfName() {
        return smartRecordConfName;
    }

    public int getNumOfSimulations() {
        return numOfSimulations;
    }

    @Override
    public String getFactoryName() {
        return WEIGHTS_MODEL_BUILDER;
    }
}
