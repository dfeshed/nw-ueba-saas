package fortscale.ml.model;


import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.List;

@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class CategoryRarityGlobalModel implements Model{
    private List<Double> occurrencesToNumOfUsersList;
    private long maxNumOfPartitions;
    private Long numOfSamples;


    public CategoryRarityGlobalModel(List<Double> occurrencesToNumOfUsersList,
                                     Long maxNumOfPartitions, Long numOfSamples){
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
}
