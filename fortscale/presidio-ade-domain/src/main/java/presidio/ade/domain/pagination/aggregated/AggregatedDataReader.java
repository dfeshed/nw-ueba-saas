package presidio.ade.domain.pagination.aggregated;

import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.ScoredFeatureAggregationRecord;

import java.util.List;
import java.util.Set;

public interface AggregatedDataReader {
    /**
     * creates page iterators used for retrieving of {@link AdeAggregationRecord} and {@link ScoredFeatureAggregationRecord}
     *
     * @param <U>                              type of records, i.e. {@link AdeAggregationRecord} and {@link ScoredFeatureAggregationRecord}
     * @param aggregatedDataPaginationParamSet contains list of features and their type
     * @param timeRange                        from - to : filter on the data timeline
     */
    <U extends AdeAggregationRecord> List<PageIterator<U>> read(Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet, TimeRange timeRange);

    /**
     * creates page iterators used for retrieving of {@link AdeAggregationRecord} and {@link ScoredFeatureAggregationRecord}
     *
     * @param aggregatedDataPaginationParamSet contains list of features and their type
     * @param timeRange                        from - to : filter on the data timeline
     * @param threshold                        only aggregation records with a value / score larger than this threshold will be included
     * @param <U>                              type of records, i.e. {@link AdeAggregationRecord} and {@link ScoredFeatureAggregationRecord}
     */
    <U extends AdeAggregationRecord> List<PageIterator<U>> read(Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet, TimeRange timeRange, double threshold);

    /**
     * @param timeRange                        from - to : filter on the data timeline
     * @param aggregatedDataPaginationParamSet contains list of features and their type
     * @return distinct context id's across features in given time range
     */
    Set<String> findDistinctContextIds(TimeRange timeRange, Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet);

    /**
     * @param timeRange                        from - to : filter on the data timeline
     * @param aggregatedDataPaginationParamSet contains list of features and their type
     * @param threshold                        only aggregation records with a value / score larger than this threshold will be included
     * @return distinct context id's across features in given time range
     */
    Set<String> findDistinctContextIds(TimeRange timeRange, Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet, double threshold);

    /**
     * @param aggregatedDataPaginationParamSet contains list of features and their type
     * @param contextIds                       distinct context id's across features
     * @param timeRange                        from - to : filter on the data timeline
     * @param <U>                              type of records, i.e. {@link AdeAggregationRecord} and {@link ScoredFeatureAggregationRecord}
     * @return the corresponding aggregation records in the given time range
     */
    <U extends AdeAggregationRecord> List<U> readRecords(Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet, Set<String> contextIds, TimeRange timeRange);

    /**
     * @param aggregatedDataPaginationParamSet contains list of features and their type
     * @param contextIds                       distinct context id's across features
     * @param timeRange                        from - to : filter on the data timeline
     * @param threshold                        only aggregation records with a value / score larger than this threshold will be included
     * @param <U>                              type of records, i.e. {@link AdeAggregationRecord} and {@link ScoredFeatureAggregationRecord}
     * @return the corresponding aggregation records in the given time range
     */
    <U extends AdeAggregationRecord> List<U> readRecords(Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet, Set<String> contextIds, TimeRange timeRange, double threshold);

    /**
     * setter method used for reading data in pages
     *
     * @param aggregatedRecordPaginationService the pagination service
     * @see #read(Set, TimeRange)
     * @see #read(Set, TimeRange, double)
     */
    void setAggregatedRecordPaginationService(AggregatedRecordPaginationService aggregatedRecordPaginationService);
}
