package fortscale.ml.model;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class CategoryRarityGlobalModel implements Model, OccurrencesToNumOfDistinctFeatureValuesModel{
    private List<Double> occurrencesToNumOfDistinctFeatureValuesList;
    private long maxNumOfPartitions;
    private Long numOfSamples;


    @JsonCreator
    public CategoryRarityGlobalModel(@JsonProperty("occurrencesToNumOfDistinctFeatureValuesList") List<Double> occurrencesToNumOfDistinctFeatureValuesList,
                                     @JsonProperty("maxNumOfPartitions") Long maxNumOfPartitions,
                                     @JsonProperty("numOfSamples") Long numOfSamples){
        this.occurrencesToNumOfDistinctFeatureValuesList = occurrencesToNumOfDistinctFeatureValuesList;
        this.maxNumOfPartitions = maxNumOfPartitions;
        this.numOfSamples = numOfSamples;
    }

    @Override
    public List<Double> getOccurrencesToNumOfDistinctFeatureValuesList() {
        return occurrencesToNumOfDistinctFeatureValuesList;
    }

    public long getMaxNumOfPartitions() {
        return maxNumOfPartitions;
    }

    @Override
    public long getNumOfSamples() {
        return numOfSamples;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CategoryRarityGlobalModel)) return false;
        CategoryRarityGlobalModel that = (CategoryRarityGlobalModel)o;
        return new EqualsBuilder().append(that.numOfSamples, numOfSamples)
                .append(that.occurrencesToNumOfDistinctFeatureValuesList, occurrencesToNumOfDistinctFeatureValuesList)
                .append(that.maxNumOfPartitions, maxNumOfPartitions).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(numOfSamples).append(occurrencesToNumOfDistinctFeatureValuesList)
                .append(maxNumOfPartitions).hashCode();
    }
}
