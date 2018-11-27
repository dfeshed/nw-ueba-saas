package presidio.ade.sdk.aggregation_records.splitter;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyData;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.aggregation.feature.functions.AggrFeatureFuncService;
import fortscale.aggregation.feature.functions.IAggrFeatureEventFunction;
import fortscale.common.feature.MultiKeyFeature;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import fortscale.utils.time.TimeRange;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.Validate;
import presidio.ade.domain.record.AdeAggregationReader;
import presidio.ade.domain.record.AdeScoredRecord;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.AggregatedFeatureType;
import presidio.ade.domain.record.aggregated.ScoredFeatureAggregationRecord;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;

import java.util.List;

public class ScoreAggregationRecordDetails {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final TimeRange timeRange;
    private final AggregatedFeatureEventConf aggregatedFeatureEventConf;
    private final String featureBucketConfName;
    private final String scoredRecordAdeEventType;
    private final Class<? extends AdeScoredRecord> scoredRecordClass;
    private final MultiKeyFeature contextFieldNameToValueMap;
    private final FeatureBucketStrategyData featureBucketStrategyData;
    private final IAggrFeatureEventFunction aggrFeatureEventFunction;

    public ScoreAggregationRecordDetails(
            AdeAggregationRecord scoreAggregationRecord,
            AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService,
            RecordReaderFactoryService recordReaderFactoryService) {

        assertScoreAggregationRecord(scoreAggregationRecord);
        timeRange = new TimeRange(scoreAggregationRecord.getStartInstant(), scoreAggregationRecord.getEndInstant());
        aggregatedFeatureEventConf = aggregatedFeatureEventsConfService.getAggregatedFeatureEventConf(scoreAggregationRecord.getFeatureName());
        AdeAggregationReader adeAggregationReader = (AdeAggregationReader)recordReaderFactoryService.getRecordReader(scoreAggregationRecord);
        FeatureBucketConf featureBucketConf = aggregatedFeatureEventConf.getBucketConf();
        featureBucketConfName = featureBucketConf.getName();
        List<String> adeEventTypes = featureBucketConf.getAdeEventTypes();
        String message = "Feature buckets built from more than one scored record ADE event type are not supported.";
        Validate.isTrue(adeEventTypes.size() == 1, message);
        scoredRecordAdeEventType = adeEventTypes.get(0);
        List<String> contextFieldNames = featureBucketConf.getContextFieldNames();
        String strategyName = featureBucketConf.getStrategyName();
        scoredRecordClass = resolveScoredRecordClass(scoredRecordAdeEventType);
        contextFieldNameToValueMap = new MultiKeyFeature();
        contextFieldNames.forEach(contextFieldName -> {
            String contextFieldValue = adeAggregationReader.getContext(contextFieldName);
            contextFieldNameToValueMap.add(contextFieldName, contextFieldValue);
        });
        featureBucketStrategyData = new FeatureBucketStrategyData(strategyName, strategyName, timeRange);
        JSONObject jsonObject = aggregatedFeatureEventConf.getAggregatedFeatureEventFunction();
        aggrFeatureEventFunction = AggrFeatureFuncService.deserializeAggrFeatureEventFunction(jsonObject);
    }

    public TimeRange getTimeRange() {
        return timeRange;
    }

    public AggregatedFeatureEventConf getAggregatedFeatureEventConf() {
        return aggregatedFeatureEventConf;
    }

    public String getFeatureBucketConfName() {
        return featureBucketConfName;
    }

    public String getScoredRecordAdeEventType() {
        return scoredRecordAdeEventType;
    }

    public Class<? extends AdeScoredRecord> getScoredRecordClass() {
        return scoredRecordClass;
    }

    public MultiKeyFeature getContextFieldNameToValueMap() {
        return contextFieldNameToValueMap;
    }

    public FeatureBucketStrategyData getFeatureBucketStrategyData() {
        return featureBucketStrategyData;
    }

    public IAggrFeatureEventFunction getAggrFeatureEventFunction() {
        return aggrFeatureEventFunction;
    }

    private static void assertScoreAggregationRecord(AdeAggregationRecord scoreAggregationRecord) {
        Validate.isTrue(scoreAggregationRecord.getAggregatedFeatureType() == AggregatedFeatureType.SCORE_AGGREGATION,
                "%s is not a score aggregation record.", scoreAggregationRecord.getFeatureName());
    }

    private static Class<? extends AdeScoredRecord> resolveScoredRecordClass(String scoredRecordAdeEventType) {
        if (scoredRecordAdeEventType.startsWith(AdeScoredEnrichedRecord.EVENT_TYPE_PREFIX)) {
            return AdeScoredEnrichedRecord.class;
        } else if (scoredRecordAdeEventType.startsWith(AdeAggregationRecord.ADE_AGGR_EVENT_TYPE_PREFIX)) {
            return ScoredFeatureAggregationRecord.class;
        } else {
            String s = String.format("Scored record ADE event type %s is not supported.", scoredRecordAdeEventType);
            throw new IllegalArgumentException(s);
        }
    }
}
