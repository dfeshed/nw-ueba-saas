package fortscale.ml.model.retriever;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.minidev.json.JSONObject;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Created by barak_schuster on 30/08/2017.
 */
public class AccumulatedSmartDataRetrieverConf extends AbstractDataRetrieverConf {
    public static final String ACCUMULATED_SMART_DATA_RETRIEVER_FACTORY_NAME = "accumulated_smart_data_retriever";
    private final String smartRecordConfName;
    private final long partitionsResolutionInSeconds;

    @JsonCreator
    public AccumulatedSmartDataRetrieverConf(
            @JsonProperty("timeRangeInSeconds") long timeRangeInSeconds,
            @JsonProperty("functions") List<JSONObject> functions,
            @JsonProperty("smartRecordConfName") String smartRecordConfName,
            @JsonProperty("partitionsResolutionInSeconds") long partitionsResolutionInSeconds) {
        super(timeRangeInSeconds, functions);
        Assert.hasText(smartRecordConfName,"smartRecordConfName must be not empty");
        Assert.isTrue(partitionsResolutionInSeconds>0,"partitionsResolutionInSeconds must be >0");
        this.smartRecordConfName = smartRecordConfName;
        this.partitionsResolutionInSeconds = partitionsResolutionInSeconds;
    }

    @Override
    public String getFactoryName() {
        return ACCUMULATED_SMART_DATA_RETRIEVER_FACTORY_NAME;
    }

    public String getSmartRecordConfName() {
        return smartRecordConfName;
    }

    public long getPartitionsResolutionInSeconds() {
        return partitionsResolutionInSeconds;
    }
}
