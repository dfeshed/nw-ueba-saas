package presidio.ade.domain.pagination;

import fortscale.common.feature.MultiKeyFeature;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.record.AdeRecord;
import presidio.ade.domain.record.AdeScoredRecord;
import presidio.ade.domain.store.ScoredDataReader;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * An iterator over pages of {@link AdeScoredRecord}s.
 *
 * @param <T> The type of {@link AdeScoredRecord}s.
 */
public class ScoredRecordPageIterator<T extends AdeRecord & AdeScoredRecord> implements PageIterator<T> {
    private final ScoredDataReader<T> scoredDataReader;
    private final TimeRange timeRange;
    private final MultiKeyFeature contextFieldNameToValueMap;
    private final int scoreThreshold;
    private final String adeEventType;
    private final int pageSize;

    private int nextPageIndex;
    private int numberOfPages;

    public ScoredRecordPageIterator(
            ScoredDataReader<T> scoredDataReader,
            TimeRange timeRange,
            MultiKeyFeature contextFieldNameToValueMap,
            int scoreThreshold,
            String adeEventType,
            int pageSize) {

        this.scoredDataReader = scoredDataReader;
        this.timeRange = timeRange;
        this.contextFieldNameToValueMap = contextFieldNameToValueMap;
        this.scoreThreshold = scoreThreshold;
        this.adeEventType = adeEventType;
        this.pageSize = pageSize;

        long count = scoredDataReader.countScoredRecords(
                timeRange,
                contextFieldNameToValueMap,
                scoreThreshold,
                adeEventType);
        nextPageIndex = 0;
        numberOfPages = (int)((count + pageSize - 1) / pageSize);
    }

    @Override
    public boolean hasNext() {
        return nextPageIndex < numberOfPages;
    }

    @Override
    public List<T> next() {
        if (nextPageIndex == numberOfPages) throw new NoSuchElementException("No more scored record pages.");
        List<T> scoredRecords = scoredDataReader.readScoredRecords(
                timeRange,
                contextFieldNameToValueMap,
                scoreThreshold,
                adeEventType,
                pageSize * nextPageIndex,
                pageSize);
        ++nextPageIndex;
        return scoredRecords;
    }
}
