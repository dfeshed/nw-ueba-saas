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
public class CategoryRarityGlobalModel implements Model{
    private List<Double> occurrencesToNumOfUsersList;
    private long maxNumOfPartitions;
    private Long numOfSamples;


    @JsonCreator
    public CategoryRarityGlobalModel(@JsonProperty("occurrencesToNumOfUsersList") List<Double> occurrencesToNumOfUsersList,
                                     @JsonProperty("maxNumOfPartitions") Long maxNumOfPartitions,
                                     @JsonProperty("numOfSamples") Long numOfSamples){
        this.occurrencesToNumOfUsersList = occurrencesToNumOfUsersList;
        this.maxNumOfPartitions = maxNumOfPartitions;
        this.numOfSamples = numOfSamples;
    }


    public List<Double> getOccurrencesToNumOfUsersList() {
        return occurrencesToNumOfUsersList;
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
                .append(that.occurrencesToNumOfUsersList, occurrencesToNumOfUsersList)
                .append(that.maxNumOfPartitions, maxNumOfPartitions).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(numOfSamples).append(occurrencesToNumOfUsersList)
                .append(maxNumOfPartitions).hashCode();
    }
}
