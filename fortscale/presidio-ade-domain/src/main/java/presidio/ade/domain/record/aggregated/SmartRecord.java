package presidio.ade.domain.record.aggregated;

import fortscale.domain.feature.score.FeatureScore;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.time.TimeRange;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import presidio.ade.domain.record.AdeScoredRecord;

import java.util.Collections;
import java.util.List;

/**
 * A smart record containing the value, its score and the aggregation records that were used to calculate it.
 *
 * @author Lior Govrin
 */
@Document
public class SmartRecord extends AdeContextualAggregatedRecord implements AdeScoredRecord {
	private static final String ADE_EVENT_TYPE_PREFIX = "smart_";

	public static final String FIXED_DURATION_STRATEGY_FIELD = "fixedDurationStrategy";
	public static final String SMART_VALUE_FIELD = "smartValue";
	public static final String SMART_SCORE_FIELD = "smartScore";
	public static final String FEATURE_SCORES_FIELD = "featureScores";
	public static final String AGGREGATION_RECORDS_FIELD = "aggregationRecords";

	@Field(FIXED_DURATION_STRATEGY_FIELD)
	private FixedDurationStrategy fixedDurationStrategy;
	@Field(SMART_VALUE_FIELD)
	private double smartValue;
	@Field(SMART_SCORE_FIELD)
	@Indexed
	private double smartScore;
	@Field(FEATURE_SCORES_FIELD)
	private List<FeatureScore> featureScores;
	@Field(AGGREGATION_RECORDS_FIELD)
	private List<AdeAggregationRecord> aggregationRecords;
	@Field
	private String featureName;

	public SmartRecord(){
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
			List<AdeAggregationRecord> aggregationRecords) {

		super(timeRange.getStart(), timeRange.getEnd(), contextId);
		this.fixedDurationStrategy = fixedDurationStrategy;
		this.smartValue = smartValue;
		this.smartScore = smartScore;
		this.featureScores = featureScores;
		this.aggregationRecords = aggregationRecords;
		this.featureName = featureName;
	}

	public SmartRecord(
			TimeRange timeRange,
			String contextId,
			String featureName,
			FixedDurationStrategy fixedDurationStrategy) {

		this(timeRange, contextId, featureName, fixedDurationStrategy, 0, 0, null, null);
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

	public List<AdeAggregationRecord> getAggregationRecords() {
		return aggregationRecords;
	}

	public void setAggregationRecords(List<AdeAggregationRecord> aggregationRecords) {
		this.aggregationRecords = aggregationRecords;
	}

	@Override
	public String toString() {
		return String.format(
				"%s{start = %s, end = %s, contextId = %s, featureName = %s, " +
				"fixedDurationStrategy = %s, smartValue = %.5f, smartScore = %.5f}",
				getClass().getSimpleName(), getStartInstant().toString(), getEndInstant().toString(),
				getContextId(), getFeatureName(), getFixedDurationStrategy().toStrategyName(),
				getSmartValue(), getScore());
	}


	/**
	 * Set feature name
	 * @param featureName
	 */
	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}

	/**
	 *
	 * @return name of the aggregated feature. i.e. sum_of_xxx_daily or highest_xxx_score_daily
	 */
	public String getFeatureName() {
		return featureName;
	}
}
