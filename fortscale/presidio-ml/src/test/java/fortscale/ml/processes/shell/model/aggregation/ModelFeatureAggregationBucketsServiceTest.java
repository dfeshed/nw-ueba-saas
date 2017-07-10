package fortscale.ml.processes.shell.model.aggregation;

import fortscale.aggregation.feature.bucket.*;
import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.feature.Feature;
import fortscale.common.util.GenericHistogram;
import fortscale.utils.pagination.ContextIdToNumOfItems;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import fortscale.utils.test.category.ModuleTestCategory;
import fortscale.utils.test.mongodb.FongoTestConfig;
import fortscale.utils.time.TimeRange;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import presidio.ade.domain.record.enriched.EnrichedDlpFileRecord;
import presidio.ade.domain.store.AdeDataStoreCleanupParams;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStoreConfig;
import presidio.ade.domain.store.enriched.EnrichedRecordsMetadata;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Created by YaronDL on 7/5/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Category(ModuleTestCategory.class)
public class ModelFeatureAggregationBucketsServiceTest {
    private static final String DATA_SOURCE = "dlpfile";

    @Autowired
    private ModelFeatureAggregationBucketsService modelFeatureAggregationBucketsService;
    @Autowired
    private EnrichedDataStore enrichedDataStore;
    @Autowired
    FeatureBucketStore featureBucketStore;


    @Test
    public void sanityTest(){
        Instant startTime = Instant.now().truncatedTo(ChronoUnit.DAYS);
        Instant endTime = startTime.plus(1,ChronoUnit.DAYS);
        TimeRange timeRange = new TimeRange(startTime,endTime);
        String username = "sanityTestUser";
        generateAndPersistAdeEnrichedRecords(timeRange,username);
        modelFeatureAggregationBucketsService.execute(timeRange,DATA_SOURCE);
        String contextId = FeatureBucketUtils.buildContextId(Collections.singletonMap("normalized_username", username));
        List<FeatureBucket> featureBucketList = featureBucketStore.getFeatureBuckets(
                "normalized_username_dlpfile_daily", Collections.singleton(contextId), timeRange);
        Assert.assertEquals(1,featureBucketList.size());
        FeatureBucket featureBucket = featureBucketList.get(0);

        Feature feature = featureBucket.getAggregatedFeatures().get("sum_of_moved_files_to_removable_device_size");
        Assert.assertNull(feature);

        feature = featureBucket.getAggregatedFeatures().get("copied_files_from_remote_device_counter");
        Assert.assertNotNull(feature);
        Assert.assertTrue(feature.getValue() instanceof AggrFeatureValue);
        AggrFeatureValue aggrFeatureValue = (AggrFeatureValue) feature.getValue();
        Assert.assertEquals(1.0D,aggrFeatureValue.getValue());

        feature = featureBucket.getAggregatedFeatures().get("sum_of_copied_files_from_remote_device_size");
        Assert.assertNotNull(feature);
        Assert.assertTrue(feature.getValue() instanceof AggrFeatureValue);
        aggrFeatureValue = (AggrFeatureValue) feature.getValue();
        Assert.assertEquals(1000D,aggrFeatureValue.getValue());

        feature = featureBucket.getAggregatedFeatures().get("src_network_folder_paths_histogram");
        Assert.assertNotNull(feature);
        Assert.assertTrue(feature.getValue() instanceof GenericHistogram);
        GenericHistogram genericHistogram = (GenericHistogram) feature.getValue();
        Assert.assertEquals(1,genericHistogram.getHistogramMap().size());
        Assert.assertEquals(1,genericHistogram.get("/home/test_source_path"),0.0);
    }


    /**
     * Create adeRecords
     */
    public void generateAndPersistAdeEnrichedRecords(TimeRange timeRange,String username) {
        AdeDataStoreCleanupParams cleanupParams = new AdeDataStoreCleanupParams(timeRange.getStart(),timeRange.getEnd(),DATA_SOURCE);
        enrichedDataStore.cleanup(cleanupParams);
        Instant startTime = timeRange.getStart();
        EnrichedDlpFileRecord enrichedDlpFileRecord = new EnrichedDlpFileRecord(startTime);
        enrichedDlpFileRecord.setNormalized_username(username);
        enrichedDlpFileRecord.setWas_classified(false);
        enrichedDlpFileRecord.setNormalized_src_machine(String.format("%s_pc",username));
        enrichedDlpFileRecord.setEvent_type("copy");
        enrichedDlpFileRecord.setSource_drive_type("remote");
        enrichedDlpFileRecord.setFile_size(1000);
        enrichedDlpFileRecord.setSource_path("/home/test_source_path");

        List<EnrichedDlpFileRecord> enrichedDlpFileRecordList = Collections.singletonList(enrichedDlpFileRecord);
        EnrichedRecordsMetadata enrichedRecordsMetadata = new EnrichedRecordsMetadata(DATA_SOURCE,startTime,startTime.plus(1,ChronoUnit.SECONDS));
        enrichedDataStore.store(enrichedRecordsMetadata,enrichedDlpFileRecordList);
        List<ContextIdToNumOfItems> contextIdToNumOfItemsList = enrichedDataStore.aggregateContextToNumOfEvents(enrichedRecordsMetadata,"normalized_username");
        Assert.assertEquals(1,contextIdToNumOfItemsList.size());
    }

    @Configuration
    @Import({FongoTestConfig.class,
            ModelAggregationBucketConfigurationServiceConfig.class,
            EnrichedDataStoreConfig.class,
            InMemoryFeatureBucketAggregatorConfig.class,
            FeatureBucketStoreMongoConfig.class,
    })
    public static class ModelFeatureAggregationBucketsServiceTestConfiguration {
        @Autowired
        private BucketConfigurationService bucketConfigurationService;
        @Autowired
        private EnrichedDataStore enrichedDataStore;
        @Autowired
        private InMemoryFeatureBucketAggregator featureBucketAggregator;
        @Autowired
        FeatureBucketStore featureBucketStore;

        @Bean
        public ModelFeatureAggregationBucketsService getModelFeatureAggregationBucketsService(){
            return new ModelFeatureAggregationBucketsService(bucketConfigurationService,enrichedDataStore,featureBucketAggregator,featureBucketStore);
        }

        @Bean
        public static TestPropertiesPlaceholderConfigurer abc() {
            Properties properties = new Properties();
            properties.put("impala.table.fields.data.source", "dlpfile");
            properties.put("fortscale.model.aggregation.bucket.conf.json.file.name", "classpath:fortscale/config/asl/model/buckets/model_buckets_test.json");
            properties.put("mongo.db.name","model_feature_aggregation");

            return new TestPropertiesPlaceholderConfigurer(properties);
        }
    }
}
