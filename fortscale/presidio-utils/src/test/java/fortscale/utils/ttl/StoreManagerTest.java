package fortscale.utils.ttl;

import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import fortscale.utils.ttl.record.TtlData;
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
import org.springframework.data.mongodb.core.mapping.Document;
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
public class StoreManagerTest {

    @Autowired
    private StoreManager storeManager;
    @Autowired
    private StoreManagerAwareTest storeManagerAwareTest;
    @Autowired
    private MongoTemplate mongoTemplate;

    private final static String COLLECTION_NAME_TEST = "collectionNameTest";
    private final static String COLLECTION_NAME_DEFAULT_TTL_TEST = "collectionNameDefaultTTlTest";

    /**
     * Create 5 records and 2 collections(collectionNameTest, collectionNameDefaultTTlTest) .
     * 1. collectionNameTest contains 1 record.
     * 2. collectionNameDefaultTTlTest contains 4 records.
     */
    @Before
    public void setup() {
        mongoTemplate.getCollectionNames().forEach(collection -> mongoTemplate.dropCollection(collection));

        StoreManagerRecordTest storeManagerRecordTest1 = new StoreManagerRecordTest("test1", Instant.EPOCH, Instant.EPOCH.plus(Duration.ofDays(1)));
        StoreManagerRecordTest storeManagerRecordTest2 = new StoreManagerRecordTest("test2", Instant.EPOCH, Instant.EPOCH.plus(Duration.ofDays(1)));
        StoreManagerRecordTest storeManagerRecordTest3 = new StoreManagerRecordTest("test3", Instant.EPOCH.plus(Duration.ofDays(2)), Instant.EPOCH.plus(Duration.ofDays(3)));
        StoreManagerRecordTest storeManagerRecordTest4 = new StoreManagerRecordTest("test4", Instant.EPOCH.plus(Duration.ofDays(3)), Instant.EPOCH.plus(Duration.ofDays(4)));
        StoreManagerRecordTest storeManagerRecordTest5 = new StoreManagerRecordTest("test5", Instant.EPOCH.plus(Duration.ofDays(4)), Instant.EPOCH.plus(Duration.ofDays(5)));

        storeManagerAwareTest.save(storeManagerRecordTest1, COLLECTION_NAME_TEST, Duration.ofDays(4), Duration.ofDays(5));
        storeManagerAwareTest.saveWithDefaultTtl(storeManagerRecordTest2, COLLECTION_NAME_DEFAULT_TTL_TEST);
        storeManagerAwareTest.saveWithDefaultTtl(storeManagerRecordTest3, COLLECTION_NAME_DEFAULT_TTL_TEST);
        storeManagerAwareTest.saveWithDefaultTtl(storeManagerRecordTest4, COLLECTION_NAME_DEFAULT_TTL_TEST);
        storeManagerAwareTest.saveWithDefaultTtl(storeManagerRecordTest5, COLLECTION_NAME_DEFAULT_TTL_TEST);
    }

    /**
     * Test save and cleanupCollection methods
     */
    @Test
    public void TtlStoreManagerTest() {
        TtlSaveTest();
        TtlCleanupTest();
    }

    /**
     * Test save method:
     * 1. with default ttl and cleanup interval values
     * 2. defined ttl and cleanup interval values
     */
    public void TtlSaveTest() {

        //2 collections in the same store
        int numOfTtlDataRecords = 2;

        //assert ttlData list, which saved to mongo.
        Set<String> collectionNames = mongoTemplate.getCollectionNames();
        // 3 collections: collectionNameTest, collectionNameDefaultTTlTest, management_ttl.
        Assert.assertTrue(collectionNames.size() == 4);

        String collectionName = TtlData.class.getAnnotation(Document.class).collection();
        List<TtlData> ttlDataList = mongoTemplate.findAll(TtlData.class, collectionName);
        assertTtlData(ttlDataList, numOfTtlDataRecords);
    }


    /**
     * Test clean up collections.
     */
    public void TtlCleanupTest() {
        storeManager.cleanupCollections(Instant.EPOCH.plus(Duration.ofDays(5)));

        //StoreManagerRecordTest2 record deleted:
        // 1. ttl default duration is 2 days(48 hours).
        // 2. cleanup run on day 6.
        // 3. records, who has start instant less or equal than day 4 (6-2), should be deleted.
        List<StoreManagerRecordTest> defaultTtlRecords = mongoTemplate.findAll(StoreManagerRecordTest.class, COLLECTION_NAME_DEFAULT_TTL_TEST);
        Assert.assertTrue(defaultTtlRecords.size() == 2);

        //StoreManagerRecordTest1 record deleted due to cleanup interval:
        List<StoreManagerRecordTest> records = mongoTemplate.findAll(StoreManagerRecordTest.class, COLLECTION_NAME_TEST);
        Assert.assertTrue(records.size() == 0);
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
                Assert.assertTrue(ttlData.getCleanupInterval().equals(Duration.ofDays(5)));

            } else if (ttlData.getCollectionName().equals(COLLECTION_NAME_DEFAULT_TTL_TEST)) {
                Assert.assertTrue(ttlData.getTtlDuration().equals(Duration.ofDays(2)));
                Assert.assertTrue(ttlData.getCleanupInterval().equals(Duration.ofDays(1)));
            }
        }
    }


    @Configuration
    @Import({MongodbTestConfig.class, StoreManagerConfig.class})
    @EnableSpringConfigured
    public static class StoreManagerTestConfig {

        @Autowired
        private MongoTemplate mongoTemplate;

        @Bean
        public static TestPropertiesPlaceholderConfigurer StoreManagereTestConfigurer() {
            Properties properties = new Properties();
            properties.put("spring.application.name", "test-app-name");
            properties.put("presidio.default.ttl.duration", "PT48H");
            properties.put("presidio.default.cleanup.interval", "PT24H");
            return new TestPropertiesPlaceholderConfigurer(properties);
        }

        @Bean
        StoreManagerAwareTest storeManagerAwareTest() {
            return new StoreManagerAwareTest(mongoTemplate);
        }
    }

}
