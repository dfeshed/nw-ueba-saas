package fortscale.ml.model.retriever;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.minidev.json.JSONObject;
import org.springframework.util.Assert;

import java.util.List;

@JsonAutoDetect(
        fieldVisibility = Visibility.ANY,
        getterVisibility = Visibility.NONE,
        isGetterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE
)
public class EpochtimeToHighestDoubleMapRetrieverConf extends AbstractDataRetrieverConf {
    public static final String EPOCHTIME_TO_HIGHEST_DOUBLE_MAP_RETRIEVER = "epochtime_to_highest_double_map_retriever";

    private final String featureBucketConfName;
    private final String featureName;
    private final long epochtimeResolutionInSeconds;

    @JsonCreator
    public EpochtimeToHighestDoubleMapRetrieverConf(
            @JsonProperty("timeRangeInSeconds") long timeRangeInSeconds,
            @JsonProperty("functions") List<JSONObject> functions,
            @JsonProperty("featureBucketConfName") String featureBucketConfName,
            @JsonProperty("featureName") String featureName,
            @JsonProperty("epochtimeResolutionInSeconds") long epochtimeResolutionInSeconds) {

        super(timeRangeInSeconds, functions);
        Assert.hasText(featureBucketConfName, "featureBucketConfName cannot be blank.");
        Assert.hasText(featureName, "featureName cannot be blank.");
        Assert.isTrue(epochtimeResolutionInSeconds > 0, "epochtimeResolutionInSeconds must be positive.");
        this.featureBucketConfName = featureBucketConfName;
        this.featureName = featureName;
        this.epochtimeResolutionInSeconds = epochtimeResolutionInSeconds;
    }

    @Override
    public String getFactoryName() {
        return EPOCHTIME_TO_HIGHEST_DOUBLE_MAP_RETRIEVER;
    }

    public String getFeatureBucketConfName() {
        return featureBucketConfName;
    }

    public String getFeatureName() {
        return featureName;
    }

    public long getEpochtimeResolutionInSeconds() {
        return epochtimeResolutionInSeconds;
    }
}
