package fortscale.ml.model.retriever;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.minidev.json.JSONObject;

import java.util.List;

/**
 * Created by barak_schuster on 10/16/17.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class ContextSequentialReducedHistogramRetrieverConf extends ContextHistogramRetrieverConf {

    public static final String CONTEXT_HISTOGRAM_SEQUENTIAL_REDUCED_RETRIEVER = "context_histogram_sequential_retriever";
    private final long sequencingResolutionInSeconds;

    @JsonCreator
    public ContextSequentialReducedHistogramRetrieverConf(
            @JsonProperty("timeRangeInSeconds") long timeRangeInSeconds,
            @JsonProperty("functions") List<JSONObject> functions,
            @JsonProperty("featureBucketConfName") String featureBucketConfName,
            @JsonProperty("featureName") String featureName,
            @JsonProperty("sequencingResolutionInSeconds") long sequencingResolutionInSeconds,
            @JsonProperty("partitionsResolutionInSeconds") long partitionsResolutionInSeconds) {
        super(timeRangeInSeconds, functions, featureBucketConfName, featureName, partitionsResolutionInSeconds);
        this.sequencingResolutionInSeconds = sequencingResolutionInSeconds;
    }

    @Override
    public String getFactoryName() {
        return CONTEXT_HISTOGRAM_SEQUENTIAL_REDUCED_RETRIEVER;
    }

    public long getSequencingResolutionInSeconds() {
        return sequencingResolutionInSeconds;
    }
}
