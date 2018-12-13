package presidio.ade.sdk.aggregation_records.splitter;

import fortscale.common.feature.MultiKeyFeature;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.pagination.ScoredRecordPageIterator;
import presidio.ade.domain.record.AdeRecord;
import presidio.ade.domain.record.AdeScoredRecord;
import presidio.ade.domain.store.ScoredDataReader;

public class ScoredDataReaderViewer<T extends AdeRecord & AdeScoredRecord> {
    private final ScoredDataReader<T> scoredDataReader;
    private final int scoredRecordScoreThreshold;
    private final int scoredRecordPageSize;

    public ScoredDataReaderViewer(
            ScoredDataReader<T> scoredDataReader, int scoredRecordScoreThreshold, int scoredRecordPageSize) {

        this.scoredDataReader = scoredDataReader;
        this.scoredRecordScoreThreshold = scoredRecordScoreThreshold;
        this.scoredRecordPageSize = scoredRecordPageSize;
    }

    public PageIterator<T> getScoredRecordPageIterator(ScoreAggregationRecordDetails scoreAggregationRecordDetails) {
        return new ScoredRecordPageIterator<>(
                scoredDataReader,
                scoreAggregationRecordDetails.getTimeRange(),
                scoreAggregationRecordDetails.getContextFieldNameToValueMap(),
                scoredRecordScoreThreshold,
                scoreAggregationRecordDetails.getScoredRecordAdeEventType(),
                scoredRecordPageSize);
    }

    public AdeScoredRecord getFirstScoredRecord(
            TimeRange timeRange,
            String scoredRecordAdeEventType,
            MultiKeyFeature contextFieldNameToValueMap,
            MultiKeyFeature additionalFieldNameToValueMap) {

        return scoredDataReader.readFirstScoredRecord(
                timeRange,
                scoredRecordAdeEventType,
                contextFieldNameToValueMap,
                additionalFieldNameToValueMap,
                scoredRecordScoreThreshold);
    }

    public AdeScoredRecord getLastScoredRecord(
            TimeRange timeRange,
            String scoredRecordAdeEventType,
            MultiKeyFeature contextFieldNameToValueMap,
            MultiKeyFeature additionalFieldNameToValueMap) {

        return scoredDataReader.readLastScoredRecord(
                timeRange,
                scoredRecordAdeEventType,
                contextFieldNameToValueMap,
                additionalFieldNameToValueMap,
                scoredRecordScoreThreshold);
    }
}
