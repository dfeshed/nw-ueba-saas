package presidio.ade.sdk.aggregation_records.splitter;

import presidio.ade.domain.pagination.ScoredRecordPageIterator;
import presidio.ade.domain.record.AdeRecord;
import presidio.ade.domain.record.AdeScoredRecord;
import presidio.ade.domain.store.ScoredDataReader;

public class ScoredRecordPageIteratorFactory<T extends AdeRecord & AdeScoredRecord> {
    private final ScoredDataReader<T> scoredDataReader;
    private final int scoredRecordPageSize;

    public ScoredRecordPageIteratorFactory(ScoredDataReader<T> scoredDataReader, int scoredRecordPageSize) {
        this.scoredDataReader = scoredDataReader;
        this.scoredRecordPageSize = scoredRecordPageSize;
    }

    public ScoredRecordPageIterator<T> getScoredRecordPageIterator(ScoreAggregationRecordDetails details) {
        return new ScoredRecordPageIterator<>(
                scoredDataReader,
                details.getTimeRange(),
                details.getContextFieldNameToValueMap(),
                details.getScoredRecordAdeEventType(),
                scoredRecordPageSize);
    }
}
