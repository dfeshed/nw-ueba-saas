package fortscale.aggregation.feature.event;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.*;

@JsonAutoDetect(fieldVisibility = Visibility.NONE, getterVisibility = Visibility.ANY, setterVisibility = Visibility.ANY, isGetterVisibility = Visibility.ANY)
public class AggregatedFeatureEventConf implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private String bucketConfName;
	private FeatureBucketConf bucketConf;
	private int numberOfBuckets;
	private int bucketsLeap;
	private long waitAfterBucketCloseSeconds;
	private String anomalyType;
	private Map<String, List<String>> aggregatedFeatureNamesMap;
	private JSONObject aggregatedFeatureEventFunction;
	private String type;
	private String outputBucketStrategy = null;
	private boolean fireEventsAlsoForEmptyBucketTicks = false;

	@JsonCreator
	public AggregatedFeatureEventConf(
			@JsonProperty("name") String name,
			@JsonProperty("type") String type,
			@JsonProperty("bucketConfName") String bucketConfName,
			@JsonProperty("numberOfBuckets") int numberOfBuckets,
			@JsonProperty("bucketsLeap") int bucketsLeap,
			@JsonProperty("waitAfterBucketCloseSeconds") long waitAfterBucketCloseSeconds,
			@JsonProperty("aggregatedFeatureNamesMap") Map<String, List<String>> aggregatedFeatureNamesMap,
			@JsonProperty("aggregatedFeatureEventFunction") JSONObject aggregatedFeatureEventFunction) {

		setName(name);
		setType(type);
		setBucketConfName(bucketConfName);
		setBucketConf(null);
		setNumberOfBuckets(numberOfBuckets);
		setBucketsLeap(bucketsLeap);
		setWaitAfterBucketCloseSeconds(waitAfterBucketCloseSeconds);
		setAggregatedFeatureEventFunction(aggregatedFeatureEventFunction);
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

	public long getWaitAfterBucketCloseSeconds() {
		return waitAfterBucketCloseSeconds;
	}

	public String getAnomalyType() { return anomalyType; }

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

	public String getOutputBucketStrategy() {
		return outputBucketStrategy;
	}

	public boolean getFireEventsAlsoForEmptyBucketTicks() {
		return fireEventsAlsoForEmptyBucketTicks;
	}

	public void setName(String name) {
		Assert.isTrue(StringUtils.isNotBlank(name));
		this.name = name;
	}

	public void setBucketConfName(String bucketConfName) {
		Assert.isTrue(StringUtils.isNotBlank(bucketConfName));
		this.bucketConfName = bucketConfName;
	}

	public void setNumberOfBuckets(int numberOfBuckets) {
		Assert.isTrue(numberOfBuckets >= 1);
		this.numberOfBuckets = numberOfBuckets;
	}

	public void setBucketsLeap(int bucketsLeap) {
		Assert.isTrue(bucketsLeap >= 1);
		this.bucketsLeap = bucketsLeap;
	}

	public void setWaitAfterBucketCloseSeconds(long waitAfterBucketCloseSeconds) {
		Assert.isTrue(waitAfterBucketCloseSeconds >= 0);
		this.waitAfterBucketCloseSeconds = waitAfterBucketCloseSeconds;
	}

	public void setAnomalyType(String anomalyType) {
//		Assert.isTrue(StringUtils.isNotBlank(anomalyType));
		this.anomalyType = anomalyType;
	}


	public void setAggregatedFeatureNamesMap(Map<String, List<String>> aggregatedFeatureNamesMap) {
		Assert.notEmpty(aggregatedFeatureNamesMap);
		for (Map.Entry<String, List<String>> entry : aggregatedFeatureNamesMap.entrySet()) {
			Assert.isTrue(StringUtils.isNotBlank(entry.getKey()));
			Assert.notEmpty(entry.getValue());
			for (String aggregatedFeatureName : entry.getValue()) {
				Assert.isTrue(StringUtils.isNotBlank(aggregatedFeatureName));
			}
		}
		this.aggregatedFeatureNamesMap = aggregatedFeatureNamesMap;
	}

	public void setAggregatedFeatureEventFunction(JSONObject aggregatedFeatureEventFunction) {
		Assert.notNull(aggregatedFeatureEventFunction);
		this.aggregatedFeatureEventFunction = aggregatedFeatureEventFunction;
	}

	public void setType(String type) {
		Assert.isTrue(StringUtils.isNotBlank(type));
		this.type = type;
	}

	public void setOutputBucketStrategy(String outputBucketStrategy) {
		this.outputBucketStrategy = outputBucketStrategy;
	}

	public void setFireEventsAlsoForEmptyBucketTicks(boolean fireEventsAlsoForEmptyBucketTicks) {
		this.fireEventsAlsoForEmptyBucketTicks = fireEventsAlsoForEmptyBucketTicks;
	}
}
