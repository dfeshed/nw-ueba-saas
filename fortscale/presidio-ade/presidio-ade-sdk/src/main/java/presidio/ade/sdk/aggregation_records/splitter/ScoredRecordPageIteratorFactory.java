package presidio.ade.sdk.aggregation_records.splitter;

import fortscale.utils.pagination.PageIterator;
import presidio.ade.domain.pagination.ScoredRecordPageIterator;
import presidio.ade.domain.record.AdeRecord;
import presidio.ade.domain.record.AdeScoredRecord;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.ScoredFeatureAggregationRecord;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.domain.store.ScoredDataReader;

/**
 * This factory creates an appropriate {@link ScoredRecordPageIterator} over pages of the underlying
 * {@link AdeScoredRecord}s of a given {@link AdeAggregationRecord} (i.e. score aggregation record).
 */
public class ScoredRecordPageIteratorFactory {
    private final ScoredDataReader<AdeScoredEnrichedRecord> scoredEnrichedDataReader;
    private final ScoredDataReader<ScoredFeatureAggregationRecord> scoredFeatureAggregationDataReader;
    private final int scoredEnrichedRecordPageSize;
    private final int scoredFeatureAggregationRecordPageSize;

    public ScoredRecordPageIteratorFactory(
            ScoredDataReader<AdeScoredEnrichedRecord> scoredEnrichedDataReader,
            ScoredDataReader<ScoredFeatureAggregationRecord> scoredFeatureAggregationDataReader,
            int scoredEnrichedRecordPageSize,
            int scoredFeatureAggregationRecordPageSize) {

        this.scoredEnrichedDataReader = scoredEnrichedDataReader;
        this.scoredFeatureAggregationDataReader = scoredFeatureAggregationDataReader;
        this.scoredEnrichedRecordPageSize = scoredEnrichedRecordPageSize;
        this.scoredFeatureAggregationRecordPageSize = scoredFeatureAggregationRecordPageSize;
    }

    public PageIterator<? extends AdeRecord> getScoredRecordPageIterator(
            ScoreAggregationRecordDetails scoreAggregationRecordDetails) {

        Class<? extends AdeScoredRecord> scoredRecordClass = scoreAggregationRecordDetails.getScoredRecordClass();

        if (scoredRecordClass.equals(AdeScoredEnrichedRecord.class)) {
            return new ScoredRecordPageIterator<>(
                    scoredEnrichedDataReader,
                    scoreAggregationRecordDetails.getTimeRange(),
                    scoreAggregationRecordDetails.getContextFieldNameToValueMap(),
                    scoreAggregationRecordDetails.getScoredRecordAdeEventType(),
                    scoredEnrichedRecordPageSize);
        } else if (scoredRecordClass.equals(ScoredFeatureAggregationRecord.class)) {
            return new ScoredRecordPageIterator<>(
                    scoredFeatureAggregationDataReader,
                    scoreAggregationRecordDetails.getTimeRange(),
                    scoreAggregationRecordDetails.getContextFieldNameToValueMap(),
                    scoreAggregationRecordDetails.getScoredRecordAdeEventType(),
                    scoredFeatureAggregationRecordPageSize);
        } else {
            String s = String.format("Scored record class %s is not supported.", scoredRecordClass.getSimpleName());
            throw new IllegalArgumentException(s);
        }
    }
}
