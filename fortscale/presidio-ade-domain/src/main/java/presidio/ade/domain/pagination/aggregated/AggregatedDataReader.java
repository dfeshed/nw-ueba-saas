package presidio.ade.domain.pagination.aggregated;

import com.google.common.collect.Sets;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.ScoredFeatureAggregationRecord;

import java.util.List;
import java.util.Set;

public interface AggregatedDataReader {

    /**
     * creates page iterators used to for retrieving of {@link AdeAggregationRecord} and {@link ScoredFeatureAggregationRecord}
     *
     * @param <U>                              type of records, i.e. {@link AdeAggregationRecord} and {@link ScoredFeatureAggregationRecord}
     * @param aggregatedDataPaginationParamSet
     * @param timeRange                        from - to : filter on the data timeline
     */
    <U extends AdeAggregationRecord> List<PageIterator<U>> read(Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet, TimeRange timeRange);

    /**
     * @param timeRange                        from - to : filter on the data timeline
     * @param aggregatedDataPaginationParamSet contains list of features and their type
     * @return distict context id's across features in given timerange
     */
    Set<String> findDistinctContextIds(TimeRange timeRange, Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet);

    default Set<String> findDistinctContextIds(TimeRange timeRange, AggregatedDataPaginationParam aggregatedDataPaginationParam)
    {
        return findDistinctContextIds(timeRange, Sets.newHashSet(aggregatedDataPaginationParam));
    }


    <U extends AdeAggregationRecord> List<U> readRecords(Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet, Set<String> contextIds, TimeRange timeRange);

    /**
     * setter method used for reading data in pages {@link this#read(Set, TimeRange)}
     * @param aggregatedRecordPaginationService
     */
    void setAggregatedRecordPaginationService(AggregatedRecordPaginationService aggregatedRecordPaginationService);

}
