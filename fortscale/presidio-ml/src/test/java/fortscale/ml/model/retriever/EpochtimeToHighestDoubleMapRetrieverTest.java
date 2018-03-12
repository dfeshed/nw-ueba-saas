package fortscale.ml.model.retriever;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.bucket.FeatureBucketReader;
import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.feature.Feature;
import fortscale.ml.model.AggregatedFeatureValuesData;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.time.TimeRange;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static fortscale.utils.fixedduration.FixedDurationStrategy.DAILY;
import static java.time.Duration.ofHours;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Lior Govrin
 */
public class EpochtimeToHighestDoubleMapRetrieverTest {
    // 2017-01-01T00:00:00Z - 2017-04-01T00:00:00Z (90 days)
    private static final TimeRange timeRange = new TimeRange(1483228800, 1491004800);
    private static final String FEATURE_BUCKET_CONF_NAME = "myFeatureBucketConf";
    private static final String FEATURE_NAME = "myFeature";
    private static final String CONTEXT_ID = "avi.cohen";

    private FeatureBucketReader featureBucketReader;
    private BucketConfigurationService bucketConfigurationService;
    private EpochtimeToHighestDoubleMapRetrieverConf conf;

    @Before
    public void before() {
        featureBucketReader = mock(FeatureBucketReader.class);
        bucketConfigurationService = mock(BucketConfigurationService.class);
        conf = mock(EpochtimeToHighestDoubleMapRetrieverConf.class);
        FeatureBucketConf featureBucketConf = mock(FeatureBucketConf.class);
        when(bucketConfigurationService.getBucketConf(eq(FEATURE_BUCKET_CONF_NAME))).thenReturn(featureBucketConf);
        when(conf.getTimeRangeInSeconds()).thenReturn(7776000L); // 90 days
        when(conf.getFunctionConfs()).thenReturn(Collections.emptyList());
        when(conf.getFeatureBucketConfName()).thenReturn(FEATURE_BUCKET_CONF_NAME);
        when(conf.getFeatureName()).thenReturn(FEATURE_NAME);
        when(featureBucketConf.getName()).thenReturn(FEATURE_BUCKET_CONF_NAME);
    }

    @Test
    public void test_retrieval_of_data_with_lesser_epochtime_resolution_than_required() {
        when(conf.getEpochtimeResolutionInSeconds()).thenReturn(3600L); // 1 hour
        when(featureBucketReader.getFeatureBuckets(eq(FEATURE_BUCKET_CONF_NAME), eq(CONTEXT_ID), eq(timeRange)))
                .thenReturn(generateFeatureBuckets(DAILY, new CyclicIntegerIterator(10, 20, 30), ofHours(3)));
        AggregatedFeatureValuesData actualData = retrieve();
        Assert.assertEquals(ofHours(1), actualData.getInstantStep());
        TreeMap<Instant, Double> actualTreeMap = actualData.getInstantToAggregatedFeatureValues();
        Assert.assertEquals(720, actualTreeMap.size()); // 90 days * 8 buckets a day
        Assert.assertEquals(generateExpected(new CyclicIntegerIterator(10, 20, 30), ofHours(3)), actualTreeMap);
    }

    @Test
    public void test_retrieval_of_data_with_required_epochtime_resolution() {
        when(conf.getEpochtimeResolutionInSeconds()).thenReturn(14400L); // 4 hours
        when(featureBucketReader.getFeatureBuckets(eq(FEATURE_BUCKET_CONF_NAME), eq(CONTEXT_ID), eq(timeRange)))
                .thenReturn(generateFeatureBuckets(DAILY, new CyclicIntegerIterator(10), ofHours(4)));
        AggregatedFeatureValuesData actualData = retrieve();
        Assert.assertEquals(ofHours(4), actualData.getInstantStep());
        TreeMap<Instant, Double> actualTreeMap = actualData.getInstantToAggregatedFeatureValues();
        Assert.assertEquals(540, actualTreeMap.size()); // 90 days * 6 buckets a day
        Assert.assertEquals(generateExpected(new CyclicIntegerIterator(10), ofHours(4)), actualTreeMap);
    }

    @Test
    public void test_retrieval_of_data_with_larger_epochtime_resolution_than_required() {
        when(conf.getEpochtimeResolutionInSeconds()).thenReturn(10800L); // 3 hours
        when(featureBucketReader.getFeatureBuckets(eq(FEATURE_BUCKET_CONF_NAME), eq(CONTEXT_ID), eq(timeRange)))
                .thenReturn(generateFeatureBuckets(DAILY, new CyclicIntegerIterator(10, 20, 30), ofHours(1)));
        AggregatedFeatureValuesData actualData = retrieve();
        Assert.assertEquals(ofHours(3), actualData.getInstantStep());
        TreeMap<Instant, Double> actualTreeMap = actualData.getInstantToAggregatedFeatureValues();
        Assert.assertEquals(720, actualTreeMap.size()); // 90 days * 8 buckets a day
        Assert.assertEquals(generateExpected(new CyclicIntegerIterator(30), ofHours(3)), actualTreeMap);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void test_retrieval_of_a_single_feature() {
        new EpochtimeToHighestDoubleMapRetriever(featureBucketReader, bucketConfigurationService, conf)
                .retrieve(CONTEXT_ID, new Date(), new Feature(FEATURE_NAME, "1483228800"));
    }

    private AggregatedFeatureValuesData retrieve() {
        Object data = new EpochtimeToHighestDoubleMapRetriever(featureBucketReader, bucketConfigurationService, conf)
                .retrieve(CONTEXT_ID, Date.from(timeRange.getEnd()))
                .getData();
        Assert.assertNotNull(data);
        Assert.assertEquals(AggregatedFeatureValuesData.class, data.getClass());
        return (AggregatedFeatureValuesData)data;
    }

    private static List<FeatureBucket> generateFeatureBuckets(
            FixedDurationStrategy fixedDurationStrategy,
            CyclicIntegerIterator cyclicIntegerIterator,
            Duration epochtimeResolution) {

        Instant currentStartInstant = timeRange.getStart();
        Instant lastEndInstant = timeRange.getEnd();
        List<FeatureBucket> featureBuckets = new LinkedList<>();

        while (currentStartInstant.isBefore(lastEndInstant)) {
            Instant currentBucketStartInstant = currentStartInstant;
            Instant currentEndInstant = currentStartInstant.plus(fixedDurationStrategy.toDuration());
            Map<String, Double> epochtimeToHighestDoubleMap = new HashMap<>();

            while (currentBucketStartInstant.isBefore(currentEndInstant)) {
                String epochtime = String.valueOf(currentBucketStartInstant.getEpochSecond());
                String key = String.format("%s#%s", FEATURE_NAME, epochtime);
                epochtimeToHighestDoubleMap.put(key, cyclicIntegerIterator.next().doubleValue());
                currentBucketStartInstant = currentBucketStartInstant.plus(epochtimeResolution);
            }

            Feature feature = new Feature(FEATURE_NAME, new AggrFeatureValue(epochtimeToHighestDoubleMap, 0L));
            FeatureBucket featureBucket = new FeatureBucket();
            featureBucket.setStartTime(currentStartInstant);
            featureBucket.setEndTime(currentEndInstant);
            featureBucket.setAggregatedFeatures(Collections.singletonMap(FEATURE_NAME, feature));
            featureBuckets.add(featureBucket);
            currentStartInstant = currentEndInstant;
        }

        return featureBuckets;
    }

    private static TreeMap<Instant, Double> generateExpected(
            CyclicIntegerIterator cyclicIntegerIterator,
            Duration epochtimeResolution) {

        Instant currentBucketStartInstant = timeRange.getStart();
        Instant lastBucketEndInstant = timeRange.getEnd();
        TreeMap<Instant, Double> expected = new TreeMap<>();

        while (currentBucketStartInstant.isBefore(lastBucketEndInstant)) {
            expected.put(currentBucketStartInstant, cyclicIntegerIterator.next().doubleValue());
            currentBucketStartInstant = currentBucketStartInstant.plus(epochtimeResolution);
        }

        return expected;
    }

    private static final class CyclicIntegerIterator implements Iterator<Integer> {
        private int[] integers;
        private int nextIndex;

        public CyclicIntegerIterator(int... integers) {
            if (integers == null || integers.length == 0) {
                throw new IllegalArgumentException("integers cannot be null or empty.");
            }

            this.integers = integers;
            this.nextIndex = 0;
        }

        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        public Integer next() {
            int next = integers[nextIndex];
            nextIndex++;
            if (nextIndex == integers.length) nextIndex = 0;
            return next;
        }
    }
}
