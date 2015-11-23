package fortscale.ml.model.retriever;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.ml.model.data.type.ContinuousDataHistogram;

import java.util.Map;

public class ContinuousDataHistogramExponentialDecay implements IDataRetrieverFunction {
    public static final String DATA_RETRIEVER_FUNCTION_TYPE = "continuous_data_histogram_exponential_decay";

    private final float base;
    private final long timeRangeIntervalInSeconds;

    @JsonCreator
    public ContinuousDataHistogramExponentialDecay(@JsonProperty("base") float base,
                                                   @JsonProperty("timeRangeIntervalInSeconds") long timeRangeIntervalInSeconds) {
        this.base = base;
        this.timeRangeIntervalInSeconds = timeRangeIntervalInSeconds;
    }

    @Override
    public ContinuousDataHistogram execute(Object data, long timeRelativeToNow) {
        ContinuousDataHistogram histogram = (ContinuousDataHistogram) data;
        ContinuousDataHistogram res = new ContinuousDataHistogram();
        double decayFactor = Math.pow(base, Math.floor(timeRelativeToNow / timeRangeIntervalInSeconds));
        for (Map.Entry<Double, Double> entry : histogram.getMap().entrySet()) {
            res.add(entry.getKey(), entry.getValue() * decayFactor);
        }
        return res;
    }
}
