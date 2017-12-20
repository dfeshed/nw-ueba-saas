package presidio.ade.processes.shell.accumulate;

import fortscale.accumulator.aggregation.Accumulator;
import fortscale.aggregation.creator.AggregationRecordsCreator;
import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketAggregator;
import fortscale.aggregation.feature.bucket.FeatureBucketsAggregatorInMemory;
import fortscale.aggregation.feature.bucket.metrics.FeatureBucketAggregatorMetricsContainer;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyData;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import fortscale.utils.time.TimeRange;
import fortscale.utils.time.TimeService;
import presidio.ade.domain.record.AdeRecord;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.enriched.EnrichedRecord;

import java.time.Instant;
import java.util.*;

/**
 * Created by maria_dorohin on 7/30/17.
 */
public class AccumulateAggregationsBucketServiceImpl implements AccumulateAggregationsBucketService {

    private FeatureBucketAggregatorMetricsContainer featureBucketAggregatorMetricsContainer;
    private FeatureBucketAggregator featureBucketAggregator;
    private FeatureBucketsAggregatorInMemory featureBucketsInMemory;
    private AggregationRecordsCreator aggregationsCreator;
    private BucketConfigurationService bucketConfigurationService;
    private RecordReaderFactoryService recordReaderFactoryService;

    public AccumulateAggregationsBucketServiceImpl(AggregationRecordsCreator aggregationsCreator, BucketConfigurationService bucketConfigurationService, RecordReaderFactoryService recordReaderFactoryService, FeatureBucketAggregatorMetricsContainer featureBucketAggregatorMetricsContainer) {
        this.aggregationsCreator = aggregationsCreator;
        this.bucketConfigurationService = bucketConfigurationService;
        this.recordReaderFactoryService = recordReaderFactoryService;
        this.featureBucketAggregatorMetricsContainer = featureBucketAggregatorMetricsContainer;
        initFeatureBucketAggregator();
    }

    private void initFeatureBucketAggregator() {
        this.featureBucketsInMemory = new FeatureBucketsAggregatorInMemory();
        this.featureBucketAggregator = new FeatureBucketAggregator(featureBucketsInMemory, bucketConfigurationService, recordReaderFactoryService, featureBucketAggregatorMetricsContainer);
    }

    public void aggregateAndAccumulate(PageIterator<EnrichedRecord> pageIterator, List<String> contextTypes, FixedDurationStrategy featureBucketStrategy, Accumulator accumulatorService) {

        while (pageIterator.hasNext()) {
            List<? extends AdeRecord> adeRecords = pageIterator.next();
            Map<Instant, List<AdeRecord>> startDateToRecords = getStartDateToRecordsOrderedMap(adeRecords, featureBucketStrategy);

            Iterator<Map.Entry<Instant, List<AdeRecord>>> entries = startDateToRecords.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<Instant, List<AdeRecord>> entry = entries.next();

                featureBucketAggregator.aggregate(entry.getValue(), contextTypes, createFeatureBucketStrategyData(entry.getKey(), featureBucketStrategy));

                // Should be closed if it is not last entry or if it is last page in iterator
                if (entries.hasNext() || !pageIterator.hasNext()) {
                    //close buckets
                    List<FeatureBucket> featureBuckets = closeAggregationBuckets();
                    List<AdeAggregationRecord> adeAggregationRecords = aggregationsCreator.createAggregationRecords(featureBuckets);
                    accumulatorService.accumulate(adeAggregationRecords);
                }
            }
        }
    }

    /**
     * Close buckets
     *
     * @return FeatureBucket list
     */
    private List<FeatureBucket> closeAggregationBuckets() {
        List<FeatureBucket> featureBuckets = featureBucketsInMemory.getAllFeatureBuckets();
        initFeatureBucketAggregator();
        return featureBuckets;
    }

    /**
     * create FeatureBucketStrategyData for aggregator.
     *
     * @param startDate             - start date
     * @param featureBucketStrategy - strategy
     * @return FeatureBucketStrategyData
     */
    private FeatureBucketStrategyData createFeatureBucketStrategyData(Instant startDate, FixedDurationStrategy featureBucketStrategy) {
        TimeRange timeRange = new TimeRange(startDate, startDate.plus(featureBucketStrategy.toDuration()));
        String strategyName = featureBucketStrategy.toStrategyName();
        return new FeatureBucketStrategyData(strategyName, strategyName, timeRange);
    }

    /**
     * create startDate to AdeRecords ordered map.
     * The map ordered by startDate
     * e.g:
     * 01-01-2017T00:00:00 , list of records that startInstant between 01-01-2017T00:00:00 - 01-01-2017T01:00:00
     * 01-01-2017T01:00:00 , list of records that startInstant between 01-01-2017T01:00:00 - 01-01-2017T02:00:00
     *
     * @return ordered map of startDate to AdeRecords
     */
    private Map<Instant, List<AdeRecord>> getStartDateToRecordsOrderedMap(List<? extends AdeRecord> adeRecords, FixedDurationStrategy featureBucketStrategy) {

        Map<Instant, List<AdeRecord>> startDateToRecords = new TreeMap<>();

        for (AdeRecord adeRecord : adeRecords) {
            Instant start = adeRecord.getStartInstant();

            start = TimeService.floorTime(start, featureBucketStrategy.toDuration());

            if (startDateToRecords.containsKey(start)) {
                startDateToRecords.get(start).add(adeRecord);
            } else {
                List<AdeRecord> records = new ArrayList<>();
                records.add(adeRecord);
                startDateToRecords.put(start, records);
            }
        }
        return startDateToRecords;
    }


}
