package presidio.ade.domain.pagination.aggregated;

import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;

import java.util.List;
import java.util.Set;

public class AggregatedRecordsPageIterator<U extends AdeAggregationRecord> implements PageIterator<U> {

    private final Set<String> contextIds;

    private final AggregatedDataReader dataReader;
    private final TimeRange timeRange;
    private final Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet;

    public AggregatedRecordsPageIterator(AggregatedDataReader dataReader, Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet, Set<String> contextIds, TimeRange timeRange) {
        this.dataReader = dataReader;
        this.aggregatedDataPaginationParamSet = aggregatedDataPaginationParamSet;
        this.contextIds = contextIds;
        this.timeRange = timeRange;
    }


    /**
     *
     * @return false, since each page holds all the data for set of contexts
     */
    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public List<U> next() {
        List<U> readRecords = this.dataReader.readRecords(this.aggregatedDataPaginationParamSet, this.contextIds,this.timeRange);
        return readRecords;
    }
}
