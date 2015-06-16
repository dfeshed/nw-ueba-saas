package fortscale.streaming.service.aggregation;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Map;

public class FeatureBucket {
	public static final String START_TIME_FIELD = "startTime";
	public static final String END_TIME_FIELD = "endTime";
	public static final String FEATURE_BUCKET_CONF_NAME_FIELD = "featureBucketConfName";
	public static final String DATA_SOURCES_FIELD = "dataSources";
	public static final String CONTEXT_FIELD_NAMES_FIELD = "contextFieldNames";
	public static final String STRATEGY_ID_FIELD = "strategyId";
	public static final String CONTEXT_FIELD_NAME_TO_VALUE_MAP_FIELD = "contextFieldNameToValueMap";
	public static final String BUCKET_ID_FIELD = "bucketId";

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
	private Map<String, String> contextFieldNameToValueMap;
	@Field(BUCKET_ID_FIELD)
	private String bucketId;

	// TODO should use 'Feature' instead of 'Object'
	private Map<String, Object> aggregatedFeatures;
}
