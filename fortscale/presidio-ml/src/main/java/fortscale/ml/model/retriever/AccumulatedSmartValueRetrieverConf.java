package fortscale.ml.model.retriever;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.minidev.json.JSONObject;
import org.springframework.util.Assert;

import java.util.List;

/**
 *
 * Created by barak_schuster on 24/08/2017.
 */
public class AccumulatedSmartValueRetrieverConf extends AbstractDataRetrieverConf {
    public static final String ACCUMULATED_SMART_VALUE_RETRIEVER_FACTORY_NAME = "accumulated_smart_value_retriever";
    private final String smartRecordConfName;
    private final String weightsModelName;
    private final long partitionsResolutionInSeconds;

    /**
     * C'tor
     * @param smartRecordConfName - name of smart records defined at {@link fortscale.smart.record.conf.SmartRecordConfService}
     * @param weightsModelName - name of the model defined at {@link fortscale.ml.model.ModelConfService}
     */
    @JsonCreator
    public AccumulatedSmartValueRetrieverConf(
            @JsonProperty("timeRangeInSeconds") long timeRangeInSeconds,
            @JsonProperty("functions") List<JSONObject> functions,
            @JsonProperty("smartRecordConfName") String smartRecordConfName,
            @JsonProperty("weightsModelName") String weightsModelName,
            @JsonProperty("partitionsResolutionInSeconds") long partitionsResolutionInSeconds) {
        super(timeRangeInSeconds, functions);
        Assert.hasText(smartRecordConfName,"smartRecordConfName must be non empty");
        Assert.isTrue(partitionsResolutionInSeconds>0,"partitionsResolutionInSeconds must be >0");
        this.smartRecordConfName = smartRecordConfName;
        this.weightsModelName = weightsModelName;
        this.partitionsResolutionInSeconds = partitionsResolutionInSeconds;
    }

    @Override
    public String getFactoryName() {
        return ACCUMULATED_SMART_VALUE_RETRIEVER_FACTORY_NAME;
    }

    public String getSmartRecordConfName() {
        return smartRecordConfName;
    }

    public String getWeightsModelName() {
        return weightsModelName;
    }

    public long getPartitionsResolutionInSeconds() {
        return partitionsResolutionInSeconds;
    }
}
