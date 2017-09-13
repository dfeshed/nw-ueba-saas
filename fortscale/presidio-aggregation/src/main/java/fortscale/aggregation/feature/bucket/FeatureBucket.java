package fortscale.aggregation.feature.bucket;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import fortscale.common.feature.Feature;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE,isGetterVisibility = Visibility.NONE, setterVisibility=Visibility.NONE)
@Document
@CompoundIndexes({@CompoundIndex(name = "context_pagination", def = "{'contextId': -1, 'startInstant': -1}")})public class FeatureBucket {
	public static final String START_TIME_FIELD = "startTime";
	public static final String END_TIME_FIELD = "endTime";
	public static final String FEATURE_BUCKET_CONF_NAME_FIELD = "featureBucketConfName";
	public static final String CONTEXT_FIELD_NAMES_FIELD = "contextFieldNames";
	public static final String CONTEXT_ID_FIELD = "contextId";
	public static final String STRATEGY_ID_FIELD = "strategyId";
	public static final String CONTEXT_FIELD_NAME_TO_VALUE_MAP_FIELD = "contextFieldNameToValueMap";
	public static final String BUCKET_ID_FIELD = "bucketId";
	public static final String CREATED_AT_FIELD = "createdAt";

	@Id
	private String id;

	@Field(START_TIME_FIELD)
	@Indexed
	private Instant startTime;
	@Field(END_TIME_FIELD)
	private Instant endTime;
	@Field(FEATURE_BUCKET_CONF_NAME_FIELD)
	private String featureBucketConfName;
	@Field(CONTEXT_FIELD_NAMES_FIELD)
	private List<String> contextFieldNames;
	@Field(STRATEGY_ID_FIELD)
	private String strategyId;
	@Field(CONTEXT_FIELD_NAME_TO_VALUE_MAP_FIELD)
	private Map<String, String> contextFieldNameToValueMap = new HashMap<>();
	@Field(CONTEXT_ID_FIELD)
	private String contextId;
	@Field(BUCKET_ID_FIELD)
	@Indexed(unique = true)
	private String bucketId;
	@Field(CREATED_AT_FIELD)
	private Date createdAt;

	private Map<String, Feature> aggregatedFeatures = new HashMap<>();

	public Instant getStartTime() {
		return startTime;
	}

	public void setStartTime(Instant startTime) {
		this.startTime = startTime;
	}

	public Instant getEndTime() {
		return endTime;
	}

	public void setEndTime(Instant endTime) {
		this.endTime = endTime;
	}

	public String getFeatureBucketConfName() {
		return featureBucketConfName;
	}

	public void setFeatureBucketConfName(String featureBucketConfName) {
		this.featureBucketConfName = featureBucketConfName;
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

	/**
	 * featureBucket is considered as synced if id is filled
	 * @return true if synced, false otherwise
     */
	public boolean isFeatureBucketSynced()
	{
		return id != null;
	}

	@Override
	public String toString() {
		return "FeatureBucket{" +
				"startTime=" + startTime +
				", endTime=" + endTime +
				", id='" + id + '\'' +
				", bucketId='" + bucketId + '\'' +
				'}';
	}
}
