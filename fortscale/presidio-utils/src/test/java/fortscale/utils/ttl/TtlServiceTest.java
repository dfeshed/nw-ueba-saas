package fortscale.utils.ttl;

import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import fortscale.utils.ttl.record.TtlData;
import fortscale.utils.ttl.store.TtlDataStore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Created by maria_dorohin on 9/4/17.
 */
@Configuration
@RunWith(SpringRunner.class)
public class TtlServiceTest {

    @Autowired
    private TtlService ttlService;
    @Autowired
    private TtlServiceAwareStoreTest ttlServiceAwareStoreTest;
    @Autowired
    private TtlDataStore ttlDataStore;
    @Autowired
    private MongoTemplate mongoTemplate;

    private final static String COLLECTION_NAME_TEST = "collectionNameTest";
    private final static String COLLECTION_NAME_DEFAULT_TTL_TEST = "collectionNameDefaultTTlTest";
    private final static String COLLECTION_NAME_MANAGEMENT_TTL = "management_ttl";

    /**
     * Create 5 records and 2 collections(collectionNameTest, collectionNameDefaultTTlTest) .
     * 1. collectionNameTest contains 1 record.
     * 2. collectionNameDefaultTTlTest contains 4 records.
     */
    @Before
    public void setup() {
        TtlServiceRecordTest ttlServiceRecordTest1 = new TtlServiceRecordTest("test1", Instant.EPOCH, Instant.EPOCH.plus(Duration.ofDays(1)));
        TtlServiceRecordTest ttlServiceRecordTest2 = new TtlServiceRecordTest("test2", Instant.EPOCH, Instant.EPOCH.plus(Duration.ofDays(1)));
        TtlServiceRecordTest ttlServiceRecordTest3 = new TtlServiceRecordTest("test3", Instant.EPOCH.plus(Duration.ofDays(2)), Instant.EPOCH.plus(Duration.ofDays(3)));
        TtlServiceRecordTest ttlServiceRecordTest4 = new TtlServiceRecordTest("test4", Instant.EPOCH.plus(Duration.ofDays(3)), Instant.EPOCH.plus(Duration.ofDays(4)));
        TtlServiceRecordTest ttlServiceRecordTest5 = new TtlServiceRecordTest("test5", Instant.EPOCH.plus(Duration.ofDays(4)), Instant.EPOCH.plus(Duration.ofDays(5)));

        ttlServiceAwareStoreTest.save(ttlServiceRecordTest1, COLLECTION_NAME_TEST, Duration.ofDays(4), Duration.ofDays(3));
        ttlServiceAwareStoreTest.saveWithDefaultTtl(ttlServiceRecordTest2, COLLECTION_NAME_DEFAULT_TTL_TEST);
        ttlServiceAwareStoreTest.saveWithDefaultTtl(ttlServiceRecordTest3, COLLECTION_NAME_DEFAULT_TTL_TEST);
        ttlServiceAwareStoreTest.saveWithDefaultTtl(ttlServiceRecordTest4, COLLECTION_NAME_DEFAULT_TTL_TEST);
        ttlServiceAwareStoreTest.saveWithDefaultTtl(ttlServiceRecordTest5, COLLECTION_NAME_DEFAULT_TTL_TEST);
    }

    /**
     * Test save method:
     * 1. with default ttl and cleanup interval values
     * 2. defined ttl and cleanup interval values
     */
    @Test
    public void TtlServiceSaveTest() {

        //2 collections in the same store
        int numOfTtlDataRecords = 2;

        //assert inMemory list.
        List<TtlData> ttlDataRecords = ttlDataStore.getTtlDataList();
        assertTtlData(ttlDataRecords, numOfTtlDataRecords);

        //assert ttlData list, which saved to mongo.
        Set<String> collectionNames = mongoTemplate.getCollectionNames();
        // 3 collections: collectionNameTest, collectionNameDefaultTTlTest, management_ttl.
        Assert.assertTrue(collectionNames.size() == 3);
        List<TtlData> ttlDataList = mongoTemplate.findAll(TtlData.class, COLLECTION_NAME_MANAGEMENT_TTL);
        assertTtlData(ttlDataList, numOfTtlDataRecords);
    }


    /**
     * Test clean up collections.
     */
    @Test
    public void TtlServiceCleanupTest() {
        ttlService.cleanupCollections(Instant.EPOCH.plus(Duration.ofDays(4)));

        //ttlServiceRecordTest2 and ttlServiceRecordTest3 records deleted:
        // 1. ttl default duration is 2 days(48 hours).
        // 2. cleanup run on day 5.
        // 3. records, who has start instant equal or less than day 3 (5-2), should be deleted.
        List<TtlServiceRecordTest> defaultTtlRecords = mongoTemplate.findAll(TtlServiceRecordTest.class, COLLECTION_NAME_DEFAULT_TTL_TEST);
        Assert.assertTrue(defaultTtlRecords.size() == 2);

        //ttlServiceRecordTest1 record was not deleted:
        // 1. cleanup interval is 3 days.
        // 2. cleanup run on day 5.
        List<TtlServiceRecordTest> records = mongoTemplate.findAll(TtlServiceRecordTest.class, COLLECTION_NAME_TEST);
        Assert.assertTrue(records.size() == 1);
    }


    /**
     * Assert TtlData
     *
     * @param ttlDataRecords      list of TtlData
     * @param numOfTtlDataRecords num of saved records
     */
    private void assertTtlData(List<TtlData> ttlDataRecords, int numOfTtlDataRecords) {

        Assert.assertTrue(ttlDataRecords.size() == numOfTtlDataRecords);
        for (TtlData ttlData : ttlDataRecords) {
            if (ttlData.getCollectionName().equals(COLLECTION_NAME_TEST)) {
                Assert.assertTrue(ttlData.getTtlDuration().equals(Duration.ofDays(4)));
                Assert.assertTrue(ttlData.getCleanupInterval().equals(Duration.ofDays(3)));

            } else if (ttlData.getCollectionName().equals(COLLECTION_NAME_DEFAULT_TTL_TEST)) {
                Assert.assertTrue(ttlData.getTtlDuration().equals(Duration.ofDays(2)));
                Assert.assertTrue(ttlData.getCleanupInterval().equals(Duration.ofDays(1)));
            }
        }
    }


    @Configuration
    @Import({MongodbTestConfig.class, TtlServiceConfig.class})
    @EnableSpringConfigured
    public static class TtlServiceTestConfig {

        @Autowired
        private MongoTemplate mongoTemplate;

        @Bean
        public static TestPropertiesPlaceholderConfigurer TtlServiceTestConfigurer() {
            Properties properties = new Properties();
            properties.put("spring.application.name", "test-app-name");
            properties.put("presidio.default.ttl.duration", "PT48H");
            properties.put("presidio.default.cleanup.interval", "PT24H");
            return new TestPropertiesPlaceholderConfigurer(properties);
        }

        @Bean
        TtlServiceAwareStoreTest ttlServiceAwareStoreTest() {
            return new TtlServiceAwareStoreTest(mongoTemplate);
        }
    }

}
