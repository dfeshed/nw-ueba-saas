package fortscale.ml.utils;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Map;
import java.util.TreeMap;

public class MaxValuesResult {
    private long resolutionInSeconds;
    private Map<Long, Double> maxValues;

    public MaxValuesResult(long resolutionInSeconds, Map<Long, Double> maxValues) {
        this.resolutionInSeconds = resolutionInSeconds;
        this.maxValues = maxValues;
    }

    public long getResolutionInSeconds() {
        return resolutionInSeconds;
    }

    public Map<Long, Double> getMaxValues() {
        return maxValues;
    }

    public void setMaxValues(TreeMap<Long, Double> maxValues) {
        this.maxValues = maxValues;
    }


    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}