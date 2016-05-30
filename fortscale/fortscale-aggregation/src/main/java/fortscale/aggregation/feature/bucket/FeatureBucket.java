package fortscale.aggregation.feature.bucket;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import fortscale.common.feature.Feature;
import fortscale.utils.time.TimeUtils;
import fortscale.utils.time.TimestampUtils;

@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class FeatureBucket {
	public static final String START_TIME_FIELD = "startTime";
	public static final String END_TIME_FIELD = "endTime";
	public static final String FEATURE_BUCKET_CONF_NAME_FIELD = "featureBucketConfName";
	public static final String DATA_SOURCES_FIELD = "dataSources";
	public static final String CONTEXT_FIELD_NAMES_FIELD = "contextFieldNames";
	public static final String CONTEXT_ID_FIELD = "contextId";
	public static final String STRATEGY_ID_FIELD = "strategyId";
	public static final String CONTEXT_FIELD_NAME_TO_VALUE_MAP_FIELD = "contextFieldNameToValueMap";
	public static final String BUCKET_ID_FIELD = "bucketId";
	public static final String CREATED_AT_FIELD_NAME = "createdAt";
	public static final String AGGREGATED_FEATURES_FIELD_NAME = "aggregatedFeatures";

	@Id
	private String id;

	@Field(START_TIME_FIELD)
	private long startTime;
	@Field(END_TIME_FIELD)
	private long endTime;
	@Field(FEATURE_BUCKET_CONF_NAME_FIELD)
	private String featureBucketConfName;
	@Field(DATA_SOURCES_FIELD)
	private List<String> dataSources;
	@Field(CONTEXT_FIELD_NAMES_FIELD)
	private List<String> contextFieldNames;
	@Field(STRATEGY_ID_FIELD)
	private String strategyId;
	@Field(CONTEXT_FIELD_NAME_TO_VALUE_MAP_FIELD)
	private Map<String, String> contextFieldNameToValueMap = new HashMap<>();
	@Field(CONTEXT_ID_FIELD)
	private String contextId;
	@Field(BUCKET_ID_FIELD)
	private String bucketId;
	
	private Date createdAt;

	private Map<String, Feature> aggregatedFeatures = new HashMap<>();

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public String getFeatureBucketConfName() {
		return featureBucketConfName;
	}

	public void setFeatureBucketConfName(String featureBucketConfName) {
		this.featureBucketConfName = featureBucketConfName;
	}

	public List<String> getDataSources() {
		return dataSources;
	}

	public void setDataSources(List<String> dataSources) {
		this.dataSources = dataSources;
	}

	public List<String> getContextFieldNames() {
		return contextFieldNames;
	}

	public void setContextFieldNames(List<String> contextFieldNames) {
		this.contextFieldNames = contextFieldNames;
	}

	public String getStrategyId() {
		return strategyId;
	}

	public void setStrategyId(String strategyId) {
		this.strategyId = strategyId;
	}

	public Map<String, String> getContextFieldNameToValueMap() {
		return contextFieldNameToValueMap;
	}
	
	public void addToContextFieldNameToValueMap(String contextFieldName, String contextValue) {
		contextFieldNameToValueMap.put(contextFieldName, contextValue);
	}

	public void setContextFieldNameToValueMap(Map<String, String> contextFieldNameToValueMap) {
		this.contextFieldNameToValueMap = contextFieldNameToValueMap;
	}

	public String getContextId() {
		return contextId;
	}

	public void setContextId(String contextId) {
		this.contextId = contextId;
	}

	public String getBucketId() {
		return bucketId;
	}

	public void setBucketId(String bucketId) {
		this.bucketId = bucketId;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Map<String, Feature> getAggregatedFeatures() {
		return aggregatedFeatures;
	}

	public void setAggregatedFeatures(Map<String, Feature> aggregatedFeatures) {
		this.aggregatedFeatures = aggregatedFeatures;
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "FeatureBucket{" +
				"startTime=" + TimeUtils.getFormattedTime(TimestampUtils.convertToMilliSeconds(startTime)) +
				", endTime=" + TimeUtils.getFormattedTime(TimestampUtils.convertToMilliSeconds(endTime)) +
				", id='" + id + '\'' +
				", bucketId='" + bucketId + '\'' +
				'}';
	}
}
