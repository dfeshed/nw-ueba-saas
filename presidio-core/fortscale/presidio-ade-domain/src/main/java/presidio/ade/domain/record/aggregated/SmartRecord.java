package presidio.ade.domain.record.aggregated;

import fortscale.domain.feature.score.FeatureScore;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.mongodb.index.DynamicIndexing;
import fortscale.utils.time.TimeRange;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import presidio.ade.domain.record.AdeScoredRecord;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A smart record containing the value, its score and the aggregation records that were used to calculate it.
 *
 * @author Lior Govrin
 */
@Document
@DynamicIndexing(compoundIndexes = {
		@CompoundIndex(name = "ctxStartScr", def = "{'contextId': 1, 'startInstant': 1, 'smartScore': 1}"),
		@CompoundIndex(name = "startScr", def = "{'startInstant': 1, 'smartScore': 1}")
})
public class SmartRecord extends AdeContextualAggregatedRecord implements AdeScoredRecord {
	private static final String ADE_EVENT_TYPE_PREFIX = "smart_";

	public static final String FIXED_DURATION_STRATEGY_FIELD = "fixedDurationStrategy";
	public static final String SMART_VALUE_FIELD = "smartValue";
	public static final String SMART_SCORE_FIELD = "smartScore";
	public static final String FEATURE_SCORES_FIELD = "featureScores";
	public static final String SMART_AGGREGATION_RECORDS_FIELD = "smartAggregationRecords";
	public static final String FEATURE_NAME_FIELD = "featureName";
	public static final String CONTEXT_FIELD = "context";

	@Field(FIXED_DURATION_STRATEGY_FIELD)
	private FixedDurationStrategy fixedDurationStrategy;
	@Field(SMART_VALUE_FIELD)
	private double smartValue;
	@Field(SMART_SCORE_FIELD)
	private double smartScore;
	@Field(FEATURE_SCORES_FIELD)
	private List<FeatureScore> featureScores;
	@Field(SMART_AGGREGATION_RECORDS_FIELD)
	private List<SmartAggregationRecord> smartAggregationRecords;
	@Field(FEATURE_NAME_FIELD)
	private String featureName;
	@Field(CONTEXT_FIELD)
	private Map<String, String> context;

	public SmartRecord() {
		super();
	}

	public SmartRecord(
			TimeRange timeRange,
			String contextId,
			String featureName,
			FixedDurationStrategy fixedDurationStrategy,
			double smartValue,
			double smartScore,
			List<FeatureScore> featureScores,
			List<SmartAggregationRecord> smartAggregationRecords,
			Map<String, String> context) {

		super(timeRange.getStart(), timeRange.getEnd(), contextId);
		this.fixedDurationStrategy = fixedDurationStrategy;
		this.smartValue = smartValue;
		this.smartScore = smartScore;
		this.featureScores = featureScores;
		this.smartAggregationRecords = smartAggregationRecords;
		this.featureName = featureName;
		this.context = context;
	}

	public SmartRecord(
			TimeRange timeRange,
			String contextId,
			String featureName,
			FixedDurationStrategy fixedDurationStrategy,
			Map<String, String> context) {

		this(timeRange, contextId, featureName, fixedDurationStrategy, 0, 0, null, null, context);
	}

	@Override
	public String getAdeEventType() {
		return ADE_EVENT_TYPE_PREFIX + getFeatureName();
	}

	@Override
	public List<String> getDataSources() {
		return Collections.emptyList();
	}

	public FixedDurationStrategy getFixedDurationStrategy() {
		return fixedDurationStrategy;
	}

	public void setFixedDurationStrategy(FixedDurationStrategy fixedDurationStrategy) {
		this.fixedDurationStrategy = fixedDurationStrategy;
	}

	public double getSmartValue() {
		return smartValue;
	}

	public void setSmartValue(double smartValue) {
		this.smartValue = smartValue;
	}

	@Override
	public Double getScore() {
		return smartScore;
	}

	public void setScore(Double smartScore) {
		this.smartScore = smartScore;
	}

	@Override
	public List<FeatureScore> getFeatureScoreList() {
		return featureScores;
	}

	public void setFeatureScoreList(List<FeatureScore> featureScores) {
		this.featureScores = featureScores;
	}

	public List<SmartAggregationRecord> getSmartAggregationRecords() {
		return smartAggregationRecords;
	}

	public void setSmartAggregationRecords(List<SmartAggregationRecord> smartAggregationRecords) {
		this.smartAggregationRecords = smartAggregationRecords;
	}

	@Override
	public String getFeatureName() {
		return featureName;
	}

	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}

	public Map<String, String> getContext() {
		return context;
	}

	public void setContext(Map<String, String> context) {
		this.context = context;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
