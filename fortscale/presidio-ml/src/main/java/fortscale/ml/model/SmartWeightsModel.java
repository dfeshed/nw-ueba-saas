package fortscale.ml.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import fortscale.smart.record.conf.ClusterConf;
import org.springframework.util.Assert;

import java.util.List;

@JsonAutoDetect(
        fieldVisibility = Visibility.ANY,
        getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE,
        isGetterVisibility = Visibility.NONE
)
public class SmartWeightsModel implements Model {
    private List<ClusterConf> clusterConfs;
    private long numOfPartitions;

    //This empty constructor is needed for mongo
    public SmartWeightsModel(){}

    public SmartWeightsModel setClusterConfs(List<ClusterConf> clusterConfs) {
        clusterConfs.forEach(Assert::notNull);
        this.clusterConfs = clusterConfs;
        return this;
    }

    @Override
    public long getNumOfSamples() {
        return 0;
    }

    public List<ClusterConf> getClusterConfs() {
        return clusterConfs;
    }

    public SmartWeightsModel setNumOfPartitions(long numOfPartitions) {
        this.numOfPartitions = numOfPartitions;
        return this;
    }
}
