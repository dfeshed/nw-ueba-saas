package fortscale.aggregation.feature.bucket;


import fortscale.aggregation.feature.bucket.metrics.FeatureBucketAggregatorMetricsContainer;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyData;
import fortscale.aggregation.feature.functions.AggrFeatureFeatureToMaxRelatedFuncTestUtils;
import fortscale.common.feature.*;
import fortscale.utils.recordreader.RecordReaderFactory;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import fortscale.utils.recordreader.transformation.Transformation;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import fortscale.utils.test.category.ModuleTestCategory;
import fortscale.utils.time.TimeRange;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.ade.domain.record.AdeRecord;
import presidio.ade.domain.record.AdeRecordReaderFactory;
import presidio.ade.domain.record.enriched.dlpfile.AdeScoredDlpFileRecord;
import presidio.ade.domain.record.enriched.dlpfile.EnrichedDlpFileRecord;

import java.time.Instant;
import java.util.*;

import static junit.framework.Assert.assertTrue;


@RunWith(SpringRunner.class)
@Category(ModuleTestCategory.class)
public class FeatureBucketsServiceTest {

    @Autowired
    private BucketConfigurationService bucketConfigurationService;
    @MockBean
    private FeatureBucketAggregatorMetricsContainer featureBucketAggregatorMetricsContainer;

    private FeatureBucketsAggregatorInMemory featureBucketsAggregatorStore;
    private FeatureBucketAggregator featureBucketAggregator;
    private FeatureBucketStrategyData strategyData;
    private List<String> contextFieldNames;
    private List<AdeRecord> adeScoredDlpFileRecords;

    private static final long DEFAULT_END_TIME_DELTA_IN_SECONDS = 300; // 5 minutes
    private static final String STRATEGY_EVENT_CONTEXT_ID = "fixed_duration_hourly";
    private static final String STRATEGY_NAME = "fixed_duration_hourly";

    private static final String BUCKET_ID1 = "fixed_duration_hourly_1435737600###context.userId###normalized_username_test1###normalized_username_dlpfile_hourly";
    private static final String BUCKET_ID2 = "fixed_duration_hourly_1435737600###context.userId###normalized_username_test2###normalized_username_dlpfile_hourly";
    private static final String BUCKET_ID3 = "fixed_duration_hourly_1435737600###context.userId###normalized_username_test3###source_path_to_highest_score_dlpfile_hourly";

    @Before
    public void initialize() {
        featureBucketsAggregatorStore = new FeatureBucketsAggregatorInMemory();
        featureBucketAggregatorInitialize(featureBucketsAggregatorStore);
        strategyDataInitialize();
        contextFieldNamesInitialize();
        adeRecordsInitialize();
    }

    @Test
    public void testFeatureBucketAggregator() {
        featureBucketAggregator.aggregate(adeScoredDlpFileRecords, contextFieldNames, strategyData);
        List<FeatureBucket> featureBuckets = featureBucketsAggregatorStore.getAllFeatureBuckets();
        Map<String, FeatureBucket> featureBucketsExpectedResults = buildExpectedResult();
        checkExpectedResults(featureBucketsExpectedResults, featureBuckets);
    }

    /**
     * Create featureBucketAggregator
     */
    public void featureBucketAggregatorInitialize(FeatureBucketsAggregatorStore featureBucketsAggregatorStore) {
        Map<String, Transformation<?>> transformations = new HashMap<>();
        Collection<RecordReaderFactory> recordReaderFactories = new ArrayList<>();
        recordReaderFactories.add(new AdeRecordReaderFactory());
        RecordReaderFactoryService recordReaderFactoryService = new RecordReaderFactoryService(recordReaderFactories, transformations);
        featureBucketAggregator = new FeatureBucketAggregator(featureBucketsAggregatorStore, bucketConfigurationService, recordReaderFactoryService, featureBucketAggregatorMetricsContainer);
    }

    /**
     * Create strategyData
     */
    public void strategyDataInitialize() {
        long epochtime = 1435737600;
        TimeRange timeRange = new TimeRange(epochtime,epochtime + DEFAULT_END_TIME_DELTA_IN_SECONDS);
        strategyData = new FeatureBucketStrategyData(STRATEGY_EVENT_CONTEXT_ID, STRATEGY_NAME, timeRange, new HashMap<>());
    }

    /**
     * Create contextFieldNames
     */
    public void contextFieldNamesInitialize() {
        contextFieldNames = new ArrayList<>();
        contextFieldNames.add("context.userId");
    }

    /**
     * Create adeRecords
     */
    public void adeRecordsInitialize() {
        adeScoredDlpFileRecords = new ArrayList<>();
        EnrichedDlpFileRecord enrichedDlpFileRecord = new EnrichedDlpFileRecord(Instant.now());
        enrichedDlpFileRecord.setUserId("normalized_username_test1");
        AdeScoredDlpFileRecord adeRecord1 = new AdeScoredDlpFileRecord(Instant.now(), "date_time","dlpfile", 80.0, new ArrayList<>(), enrichedDlpFileRecord);

        enrichedDlpFileRecord.setUserId("normalized_username_test2");
        AdeScoredDlpFileRecord adeRecord2 = new AdeScoredDlpFileRecord(Instant.now(), "date_time","dlpfile", 10.0, new ArrayList<>(), enrichedDlpFileRecord);
        AdeScoredDlpFileRecord adeRecord3 = new AdeScoredDlpFileRecord(Instant.now(), "date_time","dlpfile", 70.0, new ArrayList<>(), enrichedDlpFileRecord);
        AdeScoredDlpFileRecord adeRecord4 = new AdeScoredDlpFileRecord(Instant.now(), "date_time","dlpfile", 60.0, new ArrayList<>(), enrichedDlpFileRecord);

        enrichedDlpFileRecord.setUserId("normalized_username_test3");
        enrichedDlpFileRecord.setSrcMachineId("pc1");
        enrichedDlpFileRecord.setSourcePath("source_path_test");
        AdeScoredDlpFileRecord adeRecord5 = new AdeScoredDlpFileRecord(Instant.now(), "source_path","dlpfile", 50.0, new ArrayList<>(), enrichedDlpFileRecord);
        AdeScoredDlpFileRecord adeRecord6 = new AdeScoredDlpFileRecord(Instant.now(), "source_path","dlpfile", 90.0, new ArrayList<>(), enrichedDlpFileRecord);

        adeScoredDlpFileRecords.add(adeRecord1);
        adeScoredDlpFileRecords.add(adeRecord2);
        adeScoredDlpFileRecords.add(adeRecord3);
        adeScoredDlpFileRecords.add(adeRecord4);
        adeScoredDlpFileRecords.add(adeRecord5);
        adeScoredDlpFileRecords.add(adeRecord6);
    }

    /**
     * Compare test results to expected result
     */
    public void checkExpectedResults(Map<String, FeatureBucket> featureBucketsExpectedResults, List<FeatureBucket> featureBuckets) {
        for (FeatureBucket featureBucket : featureBuckets) {
            String bucketId = featureBucket.getBucketId();
            if (!featureBucketsExpectedResults.containsKey(bucketId)) {
                assertTrue(false);
            } else {
                FeatureBucket expectedFeatureBucket = featureBucketsExpectedResults.get(bucketId);
                Map<String, Feature> expectedAggregatedFeatures = expectedFeatureBucket.getAggregatedFeatures();
                Map<String, Feature> aggregatedFeatures = featureBucket.getAggregatedFeatures();
                for (Map.Entry<String, Feature> aggregatedFeature : aggregatedFeatures.entrySet()) {
                    if (!expectedAggregatedFeatures.containsKey(aggregatedFeature.getKey())) {
                        assertTrue(false);
                    } else {
                        Feature expectedFeature = expectedAggregatedFeatures.get(aggregatedFeature.getKey());
                        Feature feature = aggregatedFeature.getValue();


                        if (!expectedFeature.getName().equals(feature.getName())) {
                            assertTrue(false);
                        } else {
                            MultiKeyHistogram multiKeyHistogram = (MultiKeyHistogram) feature.getValue();
                            MultiKeyHistogram expectedAggrFeatureValue = (MultiKeyHistogram) expectedFeature.getValue();
                            if (!isEqual(multiKeyHistogram,expectedAggrFeatureValue)) {
                                assertTrue(false);
                            }
                        }
                        expectedAggregatedFeatures.remove(aggregatedFeature.getKey());
                    }
                }
                featureBucketsExpectedResults.remove(bucketId);
            }
        }
        if (featureBucketsExpectedResults.size() != 0) {
            assertTrue(false);
        }
    }

    /**
     * Build expected results
     */
    public Map<String, FeatureBucket> buildExpectedResult() {
        Map<String, FeatureBucket> featureBuckets = new HashMap<>();

        FeatureBucket featureBucket1 = new FeatureBucket();
        featureBucket1.setBucketId(BUCKET_ID1);
        Map<String, Feature> aggregatedFeatures = new HashMap<>();
        double total = 1d;
        MultiKeyHistogram multiKeyHistogram = createMultiKeyHistogram(new HashMap<>(), 80.0, total);
        Feature feature = new Feature("highest_date_time_score", multiKeyHistogram);
        aggregatedFeatures.put("highest_date_time_score", feature);
        featureBucket1.setAggregatedFeatures(aggregatedFeatures);


        FeatureBucket featureBucket2 = new FeatureBucket();
        featureBucket2.setBucketId(BUCKET_ID2);
        aggregatedFeatures = new HashMap<>();
        total = 2;
        multiKeyHistogram = createMultiKeyHistogram(new HashMap<>(), 70.0, total);
        feature = new Feature("highest_date_time_score", multiKeyHistogram);
        aggregatedFeatures.put("highest_date_time_score", feature);
        featureBucket2.setAggregatedFeatures(aggregatedFeatures);

        FeatureBucket featureBucket3 = new FeatureBucket();
        featureBucket3.setBucketId(BUCKET_ID3);
        aggregatedFeatures = new HashMap<>();
        total = 2;
        Map<String, FeatureValue> featureNameToValue = new HashMap<>();
        featureNameToValue.put("context.sourcePath",new FeatureStringValue("source_path_test"));
        featureNameToValue.put("context.srcMachineId",new FeatureStringValue("pc1"));
        multiKeyHistogram = createMultiKeyHistogram(featureNameToValue, 90.0, total);
        feature = new Feature("srcpath_and_srcmachine_to_highest_score_map", multiKeyHistogram);
        aggregatedFeatures.put("srcpath_and_srcmachine_to_highest_score_map", feature);
        featureBucket3.setAggregatedFeatures(aggregatedFeatures);

        featureBuckets.put(BUCKET_ID1, featureBucket1);
        featureBuckets.put(BUCKET_ID2, featureBucket2);
        featureBuckets.put(BUCKET_ID3, featureBucket3);
        return featureBuckets;
    }

    private MultiKeyHistogram createMultiKeyHistogram(Map<String, FeatureValue> featureNameToValue, double count, double total){
        MultiKeyHistogram multiKeyHistogram = new MultiKeyHistogram();
        MultiKeyFeature multiKeyFeature = new MultiKeyFeature();
        featureNameToValue.forEach(multiKeyFeature::add);
        multiKeyHistogram.setMax(multiKeyFeature, count);
        multiKeyHistogram.setTotal(total);
        return multiKeyHistogram;
    }


    public boolean isEqual(MultiKeyHistogram m1, MultiKeyHistogram m2) {
        if (m1.getTotal() != m2.getTotal() ||
                m1.getHistogram().size() != m1.getHistogram().size()) {
            return false;
        }

        for (Map.Entry<MultiKeyFeature, Double> m1Entry : m1.getHistogram().entrySet()) {
            if (!m2.getHistogram().get(m1Entry.getKey()).equals(m1Entry.getValue())) {
                return false;
            }
        }

        return true;
    }


    @Configuration
    @Import({
            BucketConfigurationServiceConfig.class
    })
    public static class springConfig {

        @Bean
        public static TestPropertiesPlaceholderConfigurer abc() {
            Properties properties = new Properties();
            properties.put("impala.table.fields.data.source", "dlpfile");
            properties.put("fortscale.aggregation.bucket.conf.json.file.name", "classpath:fortscale/config/asl/buckets/BucketConfigurationServiceTest.json");
            properties.put("fortscale.aggregation.bucket.conf.json.overriding.files.path", "file:home/cloudera/fortscale/config/asl/buckets/overriding/*.json");
            properties.put("fortscale.aggregation.bucket.conf.json.additional.files.path", "file:home/cloudera/fortscale/config/asl/buckets/overriding/*.json");

            return new TestPropertiesPlaceholderConfigurer(properties);
        }
    }

}
