package fortscale.ml.model.retriever.function;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.common.util.GenericHistogram;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ContinuousDataHistogramExponentialDecay implements IDataRetrieverFunction {
    public static final String DATA_RETRIEVER_FUNCTION_TYPE = "continuous_data_histogram_exponential_decay";

    private final float base;
    private final long timeRangeIntervalInSeconds;

    @JsonCreator
    public ContinuousDataHistogramExponentialDecay(
            @JsonProperty("base") float base,
            @JsonProperty("timeRangeIntervalInSeconds") long timeRangeIntervalInSeconds) {

        this.base = base;
        this.timeRangeIntervalInSeconds = timeRangeIntervalInSeconds;
    }

    @Override
    public Object execute(Object data, Date dataTime, Date currentTime) {
        GenericHistogram oldHistogram = (GenericHistogram)data;
        GenericHistogram newHistogram = new GenericHistogram();

        long timeDifferenceInSeconds = TimeUnit.MILLISECONDS.toSeconds(currentTime.getTime() - dataTime.getTime());
        double decayFactor = Math.pow(base, timeDifferenceInSeconds / timeRangeIntervalInSeconds);

        for (Map.Entry<String, Double> entry : oldHistogram.getHistogramMap().entrySet()) {
            newHistogram.add(entry.getKey(), entry.getValue() * decayFactor);
        }

        return newHistogram;
    }
}
