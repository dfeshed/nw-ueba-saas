package presidio.ade.processes.shell.feature.aggregation.buckets;

import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketStore;
import fortscale.aggregation.feature.bucket.FeatureBucketUtils;
import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.feature.Feature;
import fortscale.common.shell.command.PresidioCommands;
import fortscale.common.util.GenericHistogram;
import fortscale.utils.pagination.ContextIdToNumOfItems;
import fortscale.utils.shell.BootShim;
import fortscale.utils.shell.BootShimConfig;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import fortscale.utils.store.record.StoreMetadataProperties;
import fortscale.utils.test.category.ModuleTestCategory;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import fortscale.utils.time.TimeRange;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.shell.core.CommandResult;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import presidio.ade.domain.record.enriched.file.EnrichedFileRecord;
import presidio.ade.domain.store.AdeDataStoreCleanupParams;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedRecordsMetadata;
import presidio.ade.processes.shell.feature.aggregation.buckets.config.ModelFeatureAggregationBucketsConfiguration;
import presidio.monitoring.services.MetricCollectingService;
import presidio.monitoring.services.export.MetricsExporter;

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
    private static final String ADE_EVENT_TYPE = "file";

    @Autowired
    private EnrichedDataStore enrichedDataStore;
    @Autowired
    private FeatureBucketStore featureBucketStore;
    @Autowired
    private BootShim bootShim;


    @Test
    public void sanityTest() {
        Instant startTime = Instant.now().truncatedTo(ChronoUnit.DAYS);
        Instant endTime = startTime.plus(1, ChronoUnit.DAYS);
        TimeRange timeRange = new TimeRange(startTime, endTime);
        String username = "sanityTestUser";
        generateAndPersistAdeEnrichedRecords(timeRange, username);
        CommandResult commandResult = bootShim.getShell().executeCommand(String.format("run --schema %s --start_date %s --end_date %s --fixed_duration_strategy 3600", ADE_EVENT_TYPE.toUpperCase(), startTime.toString(), endTime.toString()));
        Assert.assertTrue(commandResult.isSuccess());
        String contextId = FeatureBucketUtils.buildContextId(Collections.singletonMap("userId", username));
        List<FeatureBucket> featureBucketList = featureBucketStore.getFeatureBuckets(
                "normalized_username_dlpfile_daily", Collections.singleton(contextId), timeRange);
        Assert.assertEquals(1, featureBucketList.size());
        FeatureBucket featureBucket = featureBucketList.get(0);

        Feature feature = featureBucket.getAggregatedFeatures().get("sum_of_moved_files_to_shared_drive_size");
        Assert.assertNull(feature);


        feature = featureBucket.getAggregatedFeatures().get("copied_to_shared_drive_src_paths_histogram");
        Assert.assertNotNull(feature);
        Assert.assertTrue(feature.getValue() instanceof GenericHistogram);
        GenericHistogram genericHistogram = (GenericHistogram) feature.getValue();
        Assert.assertEquals(1, genericHistogram.getHistogramMap().size());
        Assert.assertEquals(1, genericHistogram.get("/home/test_source_path"), 0.0);

        feature = featureBucket.getAggregatedFeatures().get("copied_files_to_shared_drive_counter");
        Assert.assertNotNull(featureBucket.getAggregatedFeatures().toString(), feature);
        Assert.assertTrue(feature.getValue() instanceof AggrFeatureValue);
        AggrFeatureValue aggrFeatureValue = (AggrFeatureValue) feature.getValue();
        Assert.assertEquals(1.0D, aggrFeatureValue.getValue());

        feature = featureBucket.getAggregatedFeatures().get("sum_of_copied_files_to_shared_drive_size");
        Assert.assertNotNull(feature);
        Assert.assertTrue(feature.getValue() instanceof AggrFeatureValue);
        aggrFeatureValue = (AggrFeatureValue) feature.getValue();
        Assert.assertEquals(1000D, aggrFeatureValue.getValue());

    }


    /**
     * Create adeRecords
     */
    public void generateAndPersistAdeEnrichedRecords(TimeRange timeRange, String username) {
        AdeDataStoreCleanupParams cleanupParams = new AdeDataStoreCleanupParams(timeRange.getStart(), timeRange.getEnd(), ADE_EVENT_TYPE);
        enrichedDataStore.cleanup(cleanupParams);
        Instant startTime = timeRange.getStart();
        EnrichedFileRecord enrichedFileRecord = new EnrichedFileRecord(startTime);
        enrichedFileRecord.setUserId(username);
        enrichedFileRecord.setOperationType("FILE_COPY");
        enrichedFileRecord.setDstDriveShared(true);
        enrichedFileRecord.setSrcDriveShared(false);
        enrichedFileRecord.setFileSize(1000L);
        enrichedFileRecord.setAbsoluteSrcFilePath("/home/test_source_path");

        List<EnrichedFileRecord> enrichedFileRecordList = Collections.singletonList(enrichedFileRecord);
        EnrichedRecordsMetadata enrichedRecordsMetadata = new EnrichedRecordsMetadata(ADE_EVENT_TYPE, startTime, startTime.plus(1, ChronoUnit.SECONDS));
        enrichedDataStore.store(enrichedRecordsMetadata, enrichedFileRecordList, new StoreMetadataProperties());
        List<ContextIdToNumOfItems> contextIdToNumOfItemsList = enrichedDataStore.aggregateContextToNumOfEvents(enrichedRecordsMetadata, "userId", true);
        Assert.assertEquals(1, contextIdToNumOfItemsList.size());
    }

    @Configuration
    @Import({MongodbTestConfig.class,
            PresidioCommands.class,
            BootShimConfig.class
    })
    public static class ModelFeatureAggregationBucketsServiceTestConfiguration extends ModelFeatureAggregationBucketsConfiguration {
        @MockBean
        MetricCollectingService metricCollectingService;
        @MockBean
        MetricsExporter metricsExporter;

        @Bean
        public static TestPropertiesPlaceholderConfigurer modelFeatureAggregationBucketsServiceTestProp() {
            Properties properties = new Properties();
            properties.put("fortscale.model.aggregation.bucket.conf.json.file.name", "classpath:config/asl/model/buckets/model_buckets_test.json");
            properties.put("spring.application.name", "test-app-name");
            properties.put("presidio.default.ttl.duration", "PT48H");
            properties.put("presidio.default.cleanup.interval", "PT24H");
            properties.put("model-feature-aggregation.pageIterator.pageSize", 1000);
            properties.put("model-feature-aggregation.pageIterator.maxGroupSize", 1000);
            return new TestPropertiesPlaceholderConfigurer(properties);
        }
    }
}
