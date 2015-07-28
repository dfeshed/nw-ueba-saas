package fortscale.aggregation.feature.event;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.*;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class AggregatedFeatureEventConf implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private String bucketConfName;
	private FeatureBucketConf bucketConf;
	private int numberOfBuckets;
	private int bucketsLeap;
	private long waitAfterBucketCloseSeconds;
	private Map<String, List<String>> aggregatedFeatureNamesMap;
	private JSONObject aggregatedFeatureEventFunction;
	private String type;

	public AggregatedFeatureEventConf(
			@JsonProperty("name") String name,
			@JsonProperty("type") String type,
			@JsonProperty("bucketConfName") String bucketConfName,
			@JsonProperty("numberOfBuckets") int numberOfBuckets,
			@JsonProperty("bucketsLeap") int bucketsLeap,
			@JsonProperty("waitAfterBucketCloseSeconds") long waitAfterBucketCloseSeconds,
			@JsonProperty("aggregatedFeatureNamesMap") Map<String, List<String>> aggregatedFeatureNamesMap,
			@JsonProperty("aggregatedFeatureEventFunction") JSONObject aggregatedFeatureEventFunction) {

		Assert.isTrue(StringUtils.isNotBlank(name));
		Assert.isTrue(StringUtils.isNotBlank(type));
		Assert.isTrue(StringUtils.isNotBlank(bucketConfName));
		Assert.isTrue(numberOfBuckets >= 1);
		Assert.isTrue(bucketsLeap >= 1);
		Assert.isTrue(waitAfterBucketCloseSeconds >= 0);
		Assert.notNull(aggregatedFeatureEventFunction);
		Assert.notEmpty(aggregatedFeatureNamesMap);
		for (Map.Entry<String, List<String>> entry : aggregatedFeatureNamesMap.entrySet()) {
			Assert.isTrue(StringUtils.isNotBlank(entry.getKey()));
			Assert.notEmpty(entry.getValue());
			for (String aggregatedFeatureName : entry.getValue()) {
				Assert.isTrue(StringUtils.isNotBlank(aggregatedFeatureName));
			}
		}

		this.name = name;
		this.type = type;
		this.bucketConfName = bucketConfName;
		this.bucketConf = null;
		this.numberOfBuckets = numberOfBuckets;
		this.bucketsLeap = bucketsLeap;
		this.waitAfterBucketCloseSeconds = waitAfterBucketCloseSeconds;
		this.aggregatedFeatureNamesMap = aggregatedFeatureNamesMap;
		this.aggregatedFeatureEventFunction = aggregatedFeatureEventFunction;
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

	public long getWaitAfterBucketCloseSeconds() {
		return waitAfterBucketCloseSeconds;
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
		for (List<String> aggregatedFeatureNames : aggregatedFeatureNamesMap.values()) {
			union.addAll(aggregatedFeatureNames);
		}

		return union;
	}

	public JSONObject getAggregatedFeatureEventFunction() {
		return aggregatedFeatureEventFunction;
	}
}
