package presidio.ade.sdk.aggregation_records.splitter;

import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.common.feature.MultiKeyFeature;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.pagination.ScoredRecordPageIterator;
import presidio.ade.domain.record.AdeAggregationReader;
import presidio.ade.domain.record.AdeRecord;
import presidio.ade.domain.record.AdeScoredRecord;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.ScoredFeatureAggregationRecord;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.domain.store.ScoredDataReader;

import java.util.List;

/**
 * This factory creates an appropriate {@link ScoredRecordPageIterator} over pages of the underlying
 * {@link AdeScoredRecord}s of a given {@link AdeAggregationRecord} (i.e. score aggregation record).
 */
public class ScoredRecordPageIteratorFactory {
    private final AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
    private final RecordReaderFactoryService recordReaderFactoryService;
    private final ScoredDataReader<AdeScoredEnrichedRecord> scoredEnrichedDataReader;
    private final ScoredDataReader<ScoredFeatureAggregationRecord> scoredFeatureAggregationDataReader;
    private final int scoredEnrichedRecordPageSize;
    private final int scoredFeatureAggregationRecordPageSize;

    public ScoredRecordPageIteratorFactory(
            AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService,
            RecordReaderFactoryService recordReaderFactoryService,
            ScoredDataReader<AdeScoredEnrichedRecord> scoredEnrichedDataReader,
            ScoredDataReader<ScoredFeatureAggregationRecord> scoredFeatureAggregationDataReader,
            int scoredEnrichedRecordPageSize,
            int scoredFeatureAggregationRecordPageSize) {

        this.aggregatedFeatureEventsConfService = aggregatedFeatureEventsConfService;
        this.recordReaderFactoryService = recordReaderFactoryService;
        this.scoredEnrichedDataReader = scoredEnrichedDataReader;
        this.scoredFeatureAggregationDataReader = scoredFeatureAggregationDataReader;
        this.scoredEnrichedRecordPageSize = scoredEnrichedRecordPageSize;
        this.scoredFeatureAggregationRecordPageSize = scoredFeatureAggregationRecordPageSize;
    }

    public PageIterator<? extends AdeRecord> getScoredRecordPageIterator(AdeAggregationRecord scoreAggregationRecord) {
        String adeEventType = getScoredRecordAdeEventType(scoreAggregationRecord);

        if (adeEventType.startsWith(AdeScoredEnrichedRecord.EVENT_TYPE_PREFIX)) {
            return new ScoredRecordPageIterator<>(
                    scoredEnrichedDataReader,
                    getTimeRange(scoreAggregationRecord),
                    getContextFieldNameToValueMap(scoreAggregationRecord),
                    adeEventType,
                    scoredEnrichedRecordPageSize);
        } else if (adeEventType.startsWith(AdeAggregationRecord.ADE_AGGR_EVENT_TYPE_PREFIX)) {
            return new ScoredRecordPageIterator<>(
                    scoredFeatureAggregationDataReader,
                    getTimeRange(scoreAggregationRecord),
                    getContextFieldNameToValueMap(scoreAggregationRecord),
                    adeEventType,
                    scoredFeatureAggregationRecordPageSize);
        } else {
            throw new IllegalArgumentException(String.format("ADE event type %s is not supported.", adeEventType));
        }
    }

    private String getScoredRecordAdeEventType(AdeAggregationRecord scoreAggregationRecord) {
        return aggregatedFeatureEventsConfService
                .getAggregatedFeatureEventConf(scoreAggregationRecord.getFeatureName())
                .getBucketConf()
                .getAdeEventTypes()
                // Assume feature buckets are built from exactly one ADE event type.
                .get(0);
    }

    private TimeRange getTimeRange(AdeAggregationRecord scoreAggregationRecord) {
        return new TimeRange(scoreAggregationRecord.getStartInstant(), scoreAggregationRecord.getEndInstant());
    }

    private MultiKeyFeature getContextFieldNameToValueMap(AdeAggregationRecord scoreAggregationRecord) {
        List<String> contextFieldNames = aggregatedFeatureEventsConfService
                .getAggregatedFeatureEventConf(scoreAggregationRecord.getFeatureName())
                .getBucketConf()
                .getContextFieldNames();
        AdeAggregationReader scoreAggregationRecordReader = (AdeAggregationReader)recordReaderFactoryService
                .getRecordReader(scoreAggregationRecord);
        MultiKeyFeature contextFieldNameToValueMap = new MultiKeyFeature();
        contextFieldNames.forEach(contextFieldName -> {
            String contextFieldValue = scoreAggregationRecordReader.getContext(contextFieldName);
            contextFieldNameToValueMap.add(contextFieldName, contextFieldValue);
        });
        return contextFieldNameToValueMap;
    }
}
