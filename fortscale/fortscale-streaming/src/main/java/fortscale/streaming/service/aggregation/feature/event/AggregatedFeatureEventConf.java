package fortscale.streaming.service.aggregation.feature.event;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.streaming.service.aggregation.FeatureBucketConf;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class AggregatedFeatureEventConf implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private String bucketConfName;
	private FeatureBucketConf bucketConf;
	private int numberOfBuckets;
	private int bucketsLeap;
	private long waitAfterBucketCloseSeconds;
	private List<String> aggregatedFeatureNamesList;
	private Map<String, String> aggregatedFeatureNamesMap;
	private JSONObject aggregatedFeatureEventFunction;

	public AggregatedFeatureEventConf(
			@JsonProperty("name") String name,
			@JsonProperty("bucketConfName") String bucketConfName,
			@JsonProperty("numberOfBuckets") int numberOfBuckets,
			@JsonProperty("bucketsLeap") int bucketsLeap,
			@JsonProperty("waitAfterBucketCloseSeconds") long waitAfterBucketCloseSeconds,
			@JsonProperty("aggregatedFeatureNamesList") List<String> aggregatedFeatureNamesList,
			@JsonProperty("aggregatedFeatureNamesMap") Map<String, String> aggregatedFeatureNamesMap,
			@JsonProperty("aggregatedFeatureEventFunction") JSONObject aggregatedFeatureEventFunction) {

		Assert.isTrue(StringUtils.isNotBlank(name));
		Assert.isTrue(StringUtils.isNotBlank(bucketConfName));
		Assert.isTrue(numberOfBuckets >= 1);
		Assert.isTrue(bucketsLeap >= 1);
		Assert.isTrue(waitAfterBucketCloseSeconds >= 0);
		Assert.notNull(aggregatedFeatureEventFunction);

		// Either the list or the map is required, but not both
		if (aggregatedFeatureNamesList != null) {
			Assert.notEmpty(aggregatedFeatureNamesList);
			Assert.isNull(aggregatedFeatureNamesMap);
		} else {
			Assert.notNull(aggregatedFeatureNamesMap);
			Assert.notEmpty(aggregatedFeatureNamesMap);
		}

		this.name = name;
		this.bucketConfName = bucketConfName;
		this.bucketConf = null;
		this.numberOfBuckets = numberOfBuckets;
		this.bucketsLeap = bucketsLeap;
		this.waitAfterBucketCloseSeconds = waitAfterBucketCloseSeconds;
		this.aggregatedFeatureNamesList = aggregatedFeatureNamesList;
		this.aggregatedFeatureNamesMap = aggregatedFeatureNamesMap;
		this.aggregatedFeatureEventFunction = aggregatedFeatureEventFunction;
	}

	public String getName() {
		return name;
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

	public List<String> getAggregatedFeatureNamesList() {
		return aggregatedFeatureNamesList;
	}

	public Map<String, String> getAggregatedFeatureNamesMap() {
		return aggregatedFeatureNamesMap;
	}

	public JSONObject getAggregatedFeatureEventFunction() {
		return aggregatedFeatureEventFunction;
	}
}
