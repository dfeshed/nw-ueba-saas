package fortscale.aggregation.feature.bucket;


import fortscale.aggregation.feature.functions.AggrFeatureFuncService;
import fortscale.aggregation.feature.functions.IAggrFeatureFunctionsService;
import fortscale.utils.recordreader.RecordReaderFactory;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import fortscale.utils.recordreader.transformation.Transformation;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import presidio.ade.domain.record.AdeRecordReader;
import presidio.ade.domain.record.AdeRecordReaderFactory;
import presidio.ade.domain.record.enriched.dlpfile.AdeScoredDlpFileRecord;
import presidio.ade.domain.record.enriched.dlpfile.EnrichedDlpFileRecord;

import java.time.Instant;
import java.util.*;

/**
 * Created by amira on 22/06/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class BucketConfigurationServiceTest {

    @Autowired
    BucketConfigurationService bch;

    @Value("${impala.table.fields.data.source}")
    private String dataSourceFieldName;

    private RecordReaderFactoryService recordReaderFactoryService;
    private AdeScoredDlpFileRecord adeRecord;

    @Before
    public void initialize() {
        featureBucketsServiceInitialize();
        adeRecordInitialize();
    }

    public void featureBucketsServiceInitialize() {
        IAggrFeatureFunctionsService aggrFeatureFunctionsService = new AggrFeatureFuncService();
        Map<String, Transformation<?>> transformations = new HashMap<>();
        Collection<RecordReaderFactory> recordReaderFactories = new ArrayList<>();
        recordReaderFactories.add(new AdeRecordReaderFactory());
        recordReaderFactoryService = new RecordReaderFactoryService(recordReaderFactories, transformations);
    }

    public void adeRecordInitialize() {
        EnrichedDlpFileRecord enrichedDlpFileRecord = new EnrichedDlpFileRecord(Instant.now());
        enrichedDlpFileRecord.setUserId("normalized_username_test1");
        adeRecord = new AdeScoredDlpFileRecord(Instant.now(), "date_time","dlpfile", 80.0, new ArrayList<>(), enrichedDlpFileRecord);
    }

    @Test
    public void testGetRelatedBucketConfs() {
        AdeRecordReader reader = (AdeRecordReader) recordReaderFactoryService.getRecordReader(adeRecord);
        List<String> contextFieldNames = new ArrayList<>();
        contextFieldNames.add("context.userId");
        List<FeatureBucketConf> bc = bch.getRelatedBucketConfs(reader, "fixed_duration_hourly", contextFieldNames);
        Assert.assertEquals(1, bc.size());
        FeatureBucketConf fbc = bc.get(0);
        Assert.assertEquals("normalized_username_dlpfile_hourly", fbc.getName());
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
