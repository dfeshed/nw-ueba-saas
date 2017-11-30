package fortscale.ml.model.retriever;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.minidev.json.JSONObject;
import org.springframework.util.Assert;

import java.util.List;


public class AccumulatedContextSmartValueRetrieverConf extends AbstractDataRetrieverConf {
    public static final String ACCUMULATED_CONTEXT_SMART_VALUE_RETRIEVER_FACTORY_NAME = "accumulated_context_smart_value_retriever";
    private final String smartRecordConfName;
    private final String weightsModelName;

    /**
     * C'tor
     * @param smartRecordConfName - name of smart records defined at {@link fortscale.smart.record.conf.SmartRecordConfService}
     * @param weightsModelName - name of the model defined at {@link fortscale.ml.model.ModelConfService}
     */
    @JsonCreator
    public AccumulatedContextSmartValueRetrieverConf(
            @JsonProperty("timeRangeInSeconds") long timeRangeInSeconds,
            @JsonProperty("functions") List<JSONObject> functions,
            @JsonProperty("smartRecordConfName") String smartRecordConfName,
            @JsonProperty("weightsModelName") String weightsModelName) {
        super(timeRangeInSeconds, functions);
        Assert.hasText(smartRecordConfName,"smartRecordConfName must be non empty");
        this.smartRecordConfName = smartRecordConfName;
        this.weightsModelName = weightsModelName;
    }

    @Override
    public String getFactoryName() {
        return ACCUMULATED_CONTEXT_SMART_VALUE_RETRIEVER_FACTORY_NAME;
    }

    public String getSmartRecordConfName() {
        return smartRecordConfName;
    }

    public String getWeightsModelName() {
        return weightsModelName;
    }

}
