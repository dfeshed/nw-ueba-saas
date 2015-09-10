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

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class AggregatedFeatureEventConf implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private String bucketConfName;
	private FeatureBucketConf bucketConf;
	private int numberOfBuckets;
	private int bucketsLeap;
	private long waitAfterBucketCloseSeconds;
	private String anomalyType;
	private String evidencesFilterStrategy;
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
			@JsonProperty("anomalyType") String anomalyType,
			@JsonProperty("evidencesFilterStrategy") String evidencesFilterStrategy,
			@JsonProperty("aggregatedFeatureNamesMap") Map<String, List<String>> aggregatedFeatureNamesMap,
			@JsonProperty("aggregatedFeatureEventFunction") JSONObject aggregatedFeatureEventFunction) {

		init(name, type, bucketConfName, numberOfBuckets, bucketsLeap, waitAfterBucketCloseSeconds,
				anomalyType, evidencesFilterStrategy, aggregatedFeatureNamesMap, aggregatedFeatureEventFunction, false);

	}

	private void init(
			String name,
			String type,
			String bucketConfName,
			int numberOfBuckets,
			int bucketsLeap,
			long waitAfterBucketCloseSeconds,
			String anomalyType,
			String evidencesFilterStrategy,
			Map<String, List<String>> aggregatedFeatureNamesMap,
			JSONObject aggregatedFeatureEventFunction,
			boolean fireEventsAlsoForEmptyBucketTicks) {

		Assert.isTrue(StringUtils.isNotBlank(name));
		Assert.isTrue(StringUtils.isNotBlank(type));
		Assert.isTrue(StringUtils.isNotBlank(bucketConfName));
		Assert.isTrue(numberOfBuckets >= 1);
		Assert.isTrue(bucketsLeap >= 1);
		Assert.isTrue(waitAfterBucketCloseSeconds >= 0);
		Assert.isTrue(StringUtils.isNotBlank(anomalyType));
		Assert.isTrue(StringUtils.isNotBlank(evidencesFilterStrategy));
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
		this.anomalyType = anomalyType;
		this.evidencesFilterStrategy = evidencesFilterStrategy;
		this.aggregatedFeatureNamesMap = aggregatedFeatureNamesMap;
		this.aggregatedFeatureEventFunction = aggregatedFeatureEventFunction;
		this.fireEventsAlsoForEmptyBucketTicks = fireEventsAlsoForEmptyBucketTicks;

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

	public String getEvidencesFilterStrategy() { return evidencesFilterStrategy; }

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
		this.name = name;
	}

	public void setBucketConfName(String bucketConfName) {
		this.bucketConfName = bucketConfName;
	}

	public void setNumberOfBuckets(int numberOfBuckets) {
		this.numberOfBuckets = numberOfBuckets;
	}

	public void setBucketsLeap(int bucketsLeap) {
		this.bucketsLeap = bucketsLeap;
	}

	public void setWaitAfterBucketCloseSeconds(long waitAfterBucketCloseSeconds) {
		this.waitAfterBucketCloseSeconds = waitAfterBucketCloseSeconds;
	}

	public void setAnomalyType(String anomalyType) {
		this.anomalyType = anomalyType;
	}

	public void setEvidencesFilterStrategy(String evidencesFilterStrategy) {
		this.evidencesFilterStrategy = evidencesFilterStrategy;
	}

	public void setAggregatedFeatureNamesMap(Map<String, List<String>> aggregatedFeatureNamesMap) {
		this.aggregatedFeatureNamesMap = aggregatedFeatureNamesMap;
	}

	public void setAggregatedFeatureEventFunction(JSONObject aggregatedFeatureEventFunction) {
		this.aggregatedFeatureEventFunction = aggregatedFeatureEventFunction;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setOutputBucketStrategy(String outputBucketStrategy) {
		this.outputBucketStrategy = outputBucketStrategy;
	}

	public void setFireEventsAlsoForEmptyBucketTicks(boolean fireEventsAlsoForEmptyBucketTicks) {
		this.fireEventsAlsoForEmptyBucketTicks = fireEventsAlsoForEmptyBucketTicks;
	}
}
