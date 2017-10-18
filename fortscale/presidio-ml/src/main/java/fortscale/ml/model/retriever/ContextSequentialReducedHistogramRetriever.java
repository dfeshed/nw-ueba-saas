package fortscale.ml.model.retriever;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketReader;
import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.ModelBuilderData;
import fortscale.utils.time.TimeRange;
import fortscale.utils.time.TimestampUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * retrieves context histogram data.
 * if data is sequential in given resolution, it will be reduced and counted as 1.
 *
 * example for sequential data:
 * context X1 occurred with feature value F1 5 times per a hour, in 6 different values times of day
 * expected result would be a reduced histogram with count value of 1 for the whole day.
 *
 * Created by barak_schuster on 10/16/17.
 */
public class ContextSequentialReducedHistogramRetriever extends ContextHistogramRetriever {
    public static final double SEQUENTIAL_BUCKETS_REDUCED_VALUE = 1.0D;
    private final ContextSequentialReducedHistogramRetrieverConf config;
    private ChronoField sequencingResolution;

    public ContextSequentialReducedHistogramRetriever(ContextSequentialReducedHistogramRetrieverConf config, BucketConfigurationService bucketConfigurationService, FeatureBucketReader featureBucketReader) {
        super(config, bucketConfigurationService, featureBucketReader);
        this.config = config;
        this.sequencingResolution = config.getSequencingResolution();
    }

    protected ModelBuilderData doRetrieve(String contextId, Date endTime, String featureValue) {
        long endTimeInSeconds = TimestampUtils.convertToSeconds(endTime.getTime());
        long startTimeInSeconds = endTimeInSeconds - timeRangeInSeconds;
        TimeRange retrievedBucketsTimeRange = new TimeRange(startTimeInSeconds, endTimeInSeconds);

        List<FeatureBucket> featureBuckets = featureBucketReader.getFeatureBuckets(
                featureBucketConf.getName(), contextId,
                retrievedBucketsTimeRange);

        if (featureBuckets.isEmpty()) return new ModelBuilderData(ModelBuilderData.NoDataReason.NO_DATA_IN_DATABASE);

        GenericHistogram reductionHistogram = new GenericHistogram();

        Map<Long, List<FeatureBucket>> sequencingResolutionToBuckets =
                groupBucketsBySequencingResolution(featureBuckets);

        for (Long bucketsSequenceNumber: sequencingResolutionToBuckets.keySet()) {
            List<FeatureBucket> sequentialFeatureBuckets = sequencingResolutionToBuckets.get(bucketsSequenceNumber);
            GenericHistogram seqReductionHistogram = new GenericHistogram();
            createReductionHistogram(endTime,featureValue,sequentialFeatureBuckets,seqReductionHistogram);
            seqReductionHistogram = reduceSequentialHistogramValues(seqReductionHistogram);
            reductionHistogram.add(seqReductionHistogram);
        }

        long distinctDays = calcNumOfDistinctDaysOfFeatureBuckets(featureBuckets);
        reductionHistogram.setDistinctDays(distinctDays);

        return getModelBuilderData(reductionHistogram);
    }

    /**
     *
     * @param featureBuckets
     * @return grouped buckets by resolution. i.e. if {@link #sequencingResolution} is ChronoField.EPOCH_DAY and all the buckets are Hourly - they will be grouped by their day (start-time field's day).
     * if {@link #sequencingResolution} is smaller then the buckets resolution and grouping is in smaller resolution, the the grouping will probably be all of the startTimes of the bucket. currently there is no validation to assure it would not happen.
     */
    Map<Long, List<FeatureBucket>> groupBucketsBySequencingResolution(List<FeatureBucket> featureBuckets) {
        return featureBuckets.stream().collect(Collectors.groupingBy(x -> LocalDateTime.ofInstant(x.getStartTime(), ZoneId.of("UTC")).getLong(sequencingResolution)));
    }

    private GenericHistogram reduceSequentialHistogramValues(GenericHistogram seqReductionHistogram) {
        Map<String, Double> seqReductionHistogramMap = seqReductionHistogram.getHistogramMap();
        seqReductionHistogramMap.replaceAll((k,v)-> SEQUENTIAL_BUCKETS_REDUCED_VALUE);
        seqReductionHistogram = new GenericHistogram(seqReductionHistogramMap);
        return seqReductionHistogram;
    }

}
