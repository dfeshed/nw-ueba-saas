package fortscale.ml.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.Duration;
import java.util.Map;

@JsonAutoDetect(
        fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE)
public class PartitionsDataModel implements PartitionedDataModel {
    private Map<Long, Double> instantToValue;
    private long resolutionInSeconds;
    private Duration instantStep;
    private long numOfPartitions;


    public PartitionsDataModel(Map<Long, Double> instantToValue, long resolutionInSeconds, Duration instantStep, long numOfPartitions) {
        this.instantToValue = instantToValue;
        this.resolutionInSeconds = resolutionInSeconds;
        this.instantStep = instantStep;
        this.numOfPartitions = numOfPartitions;
    }

    @Override
    public long getNumOfSamples() {
        return instantToValue.size();
    }

    public Map<Long, Double> getInstantToValue() {
        return instantToValue;
    }

    public long getResolutionInSeconds() {
        return resolutionInSeconds;
    }

    public long getNumOfPartitions() {
        return numOfPartitions;
    }

    public Duration getInstantStep() {
        return instantStep;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PartitionsDataModel)) return false;
        PartitionsDataModel that = (PartitionsDataModel) o;
        return instantToValue.equals(that.instantToValue) &&
                resolutionInSeconds == that.resolutionInSeconds;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(instantToValue)
                .append(resolutionInSeconds)
                .toHashCode();
    }

}
