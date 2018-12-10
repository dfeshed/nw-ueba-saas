package presidio.ade.sdk.aggregation_records.splitter;

import fortscale.common.feature.MultiKeyFeature;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.pagination.ScoredRecordPageIterator;
import presidio.ade.domain.record.AdeRecord;
import presidio.ade.domain.record.AdeScoredRecord;
import presidio.ade.domain.store.ScoredDataReader;

import java.time.Instant;

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

    public Instant getFirstStartInstant(
            TimeRange timeRange, MultiKeyFeature contextFieldNameToValueMap, String scoredRecordAdeEventType) {

        return scoredDataReader.readFirstStartInstant(
                timeRange, contextFieldNameToValueMap, scoredRecordScoreThreshold, scoredRecordAdeEventType);
    }

    public Instant getLastStartInstant(
            TimeRange timeRange, MultiKeyFeature contextFieldNameToValueMap, String scoredRecordAdeEventType) {

        return scoredDataReader.readLastStartInstant(
                timeRange, contextFieldNameToValueMap, scoredRecordScoreThreshold, scoredRecordAdeEventType);
    }
}
