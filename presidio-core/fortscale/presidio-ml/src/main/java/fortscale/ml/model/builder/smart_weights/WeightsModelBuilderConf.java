package fortscale.ml.model.builder.smart_weights;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.ml.model.builder.IModelBuilderConf;
import org.apache.commons.lang3.Validate;

/**
 * @author Barak Schuster.
 * @author Lior Govrin.
 */
public class WeightsModelBuilderConf implements IModelBuilderConf {
    public static final String WEIGHTS_MODEL_BUILDER = "weights_model_builder";
    public static final int DEFAULT_NUM_OF_SIMULATIONS = 300;

    private String smartRecordConfName;
    private int numOfSimulations;

    @JsonCreator
    public WeightsModelBuilderConf(
            @JsonProperty("smartRecordConfName") String smartRecordConfName,
            @JsonProperty("numOfSimulations") Integer numOfSimulations) {

        if (numOfSimulations == null) {
            numOfSimulations = DEFAULT_NUM_OF_SIMULATIONS;
        }

        Validate.notBlank(smartRecordConfName, "smartRecordConfName cannot be blank.");
        Validate.isTrue(numOfSimulations > 0, "numOfSimulations must be greater than 0 (smartRecordConfName = %s).", smartRecordConfName);
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
