package presidio.ade.domain.pagination.aggregated;

import fortscale.utils.time.TimeRange;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.ScoredFeatureAggregationRecord;
import presidio.ade.domain.store.aggr.AggrRecordsMetadata;

import java.util.List;
import java.util.Set;

public interface AggregatedDataReader {

    /**
     * creates page iterators used to for retrieving of {@link AdeAggregationRecord} and {@link ScoredFeatureAggregationRecord}
     *
     * @param recordsMetadata    indicates of features to be read
     * @param contextIds         contextIds to create pageIterators on
     * @param contextType        i.e. userId
     * @param timeRange          from - to fileter on the data timeline
     * @param pageSize           num of events in each page
     * @param totalNumOfItems    total num of events in the all pages
     * @param totalAmountOfPages
     * @param <U>                type of records, i.e. {@link AdeAggregationRecord} and {@link ScoredFeatureAggregationRecord}
     * @return
     */
    <U extends AdeAggregationRecord> List<AggregatedRecordsPageIterator<U>> read(Set<AggrRecordsMetadata> recordsMetadata, Set<String> contextIds, String contextType, TimeRange timeRange, int pageSize, int totalNumOfItems, int totalAmountOfPages);

    Set<String> findDistinctContextIds(TimeRange timeRange, Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet);


    <U extends AdeAggregationRecord> List<U>readRecords(Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet, Set<String> contextIds, TimeRange timeRange);
}
