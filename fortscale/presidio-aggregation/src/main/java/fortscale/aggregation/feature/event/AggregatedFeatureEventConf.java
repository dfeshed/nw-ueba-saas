package fortscale.aggregation.feature.event;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.functions.IAggrFeatureEventFunction;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.*;

@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.ANY,
        isGetterVisibility = JsonAutoDetect.Visibility.ANY,
        setterVisibility = JsonAutoDetect.Visibility.ANY
)
public class AggregatedFeatureEventConf implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String bucketConfName;
    private FeatureBucketConf bucketConf;
    private int numberOfBuckets;
    private int bucketsLeap;
    private Map<String, List<String>> aggregatedFeatureNamesMap;
    private IAggrFeatureEventFunction aggrFeatureEventFunction;
    private String type;

    @JsonCreator
    public AggregatedFeatureEventConf(
            @JsonProperty("name") String name,
            @JsonProperty("type") String type,
            @JsonProperty("bucketConfName") String bucketConfName,
            @JsonProperty("numberOfBuckets") int numberOfBuckets,
            @JsonProperty("bucketsLeap") int bucketsLeap,
            @JsonProperty("aggregatedFeatureNamesMap") Map<String, List<String>> aggregatedFeatureNamesMap,
            @JsonProperty("aggregatedFeatureEventFunction") IAggrFeatureEventFunction aggrFeatureEventFunction) {

        setName(name);
        setType(type);
        setBucketConfName(bucketConfName);
        setBucketConf(null);
        setNumberOfBuckets(numberOfBuckets);
        setBucketsLeap(bucketsLeap);
        setAggrFeatureEventFunction(aggrFeatureEventFunction);
        setAggregatedFeatureNamesMap(aggregatedFeatureNamesMap);
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getBucketConfName() {
        return bucketConfName;
    }

    public FeatureBucketConf getBucketConf() {
        return bucketConf;
    }

    public void setBucketConf(FeatureBucketConf bucketConf) {
        this.bucketConf = bucketConf;
    }

    public int getNumberOfBuckets() {
        return numberOfBuckets;
    }

    public int getBucketsLeap() {
        return bucketsLeap;
    }

    public Map<String, List<String>> getAggregatedFeatureNamesMap() {
        Map<String, List<String>> mapClone = new HashMap<>(aggregatedFeatureNamesMap.size());

        for (Map.Entry<String, List<String>> entry : aggregatedFeatureNamesMap.entrySet()) {
            List<String> listClone = new ArrayList<>(entry.getValue().size());
            listClone.addAll(entry.getValue());
            mapClone.put(entry.getKey(), listClone);
        }

        return mapClone;
    }

    public Set<String> getAllAggregatedFeatureNames() {
        Set<String> union = new HashSet<>();
        for (List<String> aggregatedFeatureNames : aggregatedFeatureNamesMap.values())
            union.addAll(aggregatedFeatureNames);
        return union;
    }

    public IAggrFeatureEventFunction getAggrFeatureEventFunction() {
        return aggrFeatureEventFunction;
    }

    public void setName(String name) {
        Assert.hasText(name, "name cannot be blank.");
        this.name = name;
    }

    public void setBucketConfName(String bucketConfName) {
        Assert.hasText(bucketConfName, "bucketConfName cannot be blank.");
        this.bucketConfName = bucketConfName;
    }

    public void setNumberOfBuckets(int numberOfBuckets) {
        Assert.isTrue(numberOfBuckets >= 1, "numberOfBuckets must be larger than or equal to 1.");
        this.numberOfBuckets = numberOfBuckets;
    }

    public void setBucketsLeap(int bucketsLeap) {
        Assert.isTrue(bucketsLeap >= 1, "bucketsLeap must be larger than or equal to 1.");
        this.bucketsLeap = bucketsLeap;
    }

    public void setAggregatedFeatureNamesMap(Map<String, List<String>> aggregatedFeatureNamesMap) {
        Assert.notEmpty(aggregatedFeatureNamesMap, "aggregatedFeatureNamesMap cannot be empty.");

        for (Map.Entry<String, List<String>> entry : aggregatedFeatureNamesMap.entrySet()) {
            Assert.hasText(entry.getKey(), "aggregatedFeatureNamesMap keys cannot be blank.");
            Assert.notEmpty(entry.getValue(), "aggregatedFeatureNamesMap values cannot be empty.");
            for (String aggregatedFeatureName : entry.getValue())
                Assert.hasText(aggregatedFeatureName, "aggregatedFeatureNames cannot be blank.");
        }

        this.aggregatedFeatureNamesMap = aggregatedFeatureNamesMap;
    }

    public void setAggrFeatureEventFunction(IAggrFeatureEventFunction aggrFeatureEventFunction) {
        Assert.notNull(aggrFeatureEventFunction, "aggrFeatureEventFunction cannot be null.");
        this.aggrFeatureEventFunction = aggrFeatureEventFunction;
    }

    public void setType(String type) {
        Assert.hasText(type, "type cannot be blank.");
        this.type = type;
    }
}
