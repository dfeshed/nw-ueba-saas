package fortscale.utils.store;

import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import fortscale.utils.store.record.StoreMetadataProperties;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import fortscale.utils.store.record.StoreMetadata;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

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
    @Value("#{T(java.time.Duration).parse('${presidio.default.ttl.duration}')}")
    private Duration defaultTtl;
    @Value("#{T(java.time.Duration).parse('${presidio.default.cleanup.interval}')}")
    private Duration defaultCleanupInterval;

    private final static String COLLECTION_NAME_TEST = "collectionNameTest";
    private final static String COLLECTION_NAME_DEFAULT_TTL_TEST = "collectionNameDefaultTTlTest";


    @Before
    public void setup() {
        mongoTemplate.getCollectionNames().forEach(collection -> mongoTemplate.dropCollection(collection));
    }

    /**
     * Test registerWithTtl and cleanupCollection methods
     */
    @Test
    public void TtlStoreManagerTest() {
        //2 collections in the same store
        int numOfTtlDataRecords = 2;
        Instant start = Instant.EPOCH;
        Instant end = start.plus(Duration.ofDays(10));
        Duration ttl = Duration.ofDays(4);
        Duration cleanupInterval = Duration.ofDays(5);

        createStoreManagerRecordsWithTtl(start, end, ttl, cleanupInterval);
        AssertTtlData(numOfTtlDataRecords, ttl, cleanupInterval);
        AssertTtlCleanup(start.plus(Duration.ofDays(4)), cleanupInterval);
    }


    /**
     * Test register and cleanupCollection methods
     */
    @Test
    public void cleanupStoreManagerTest() {
        Instant start = Instant.EPOCH;
        Instant end = start.plus(Duration.ofDays(1));
        createStoreManagerRecordsForTimeRangeCleanup(start, end);
        AssertCleanupInTimeRange(start.plus(Duration.ofHours(2)), start.plus(Duration.ofHours(3)));
    }

    /**
     * Test save method:
     * 1. with default ttl and cleanup interval values
     * 2. defined ttl and cleanup interval values
     */
    public void AssertTtlData(int numOfTtlDataRecords, Duration ttl, Duration cleanupInterval) {
        String collectionName = StoreMetadata.class.getAnnotation(Document.class).collection();
        List<StoreMetadata> storeMetadataList = mongoTemplate.findAll(StoreMetadata.class, collectionName);

        Assert.assertTrue(storeMetadataList.size() == numOfTtlDataRecords);
        for (StoreMetadata storeMetadata : storeMetadataList) {
            if (storeMetadata.getCollectionName().equals(COLLECTION_NAME_TEST)) {
                Assert.assertTrue(storeMetadata.getTtlDuration().equals(ttl));
                Assert.assertTrue(storeMetadata.getCleanupInterval().equals(cleanupInterval));

            } else if (storeMetadata.getCollectionName().equals(COLLECTION_NAME_DEFAULT_TTL_TEST)) {
                Assert.assertTrue(storeMetadata.getTtlDuration().equals(defaultTtl));
                Assert.assertTrue(storeMetadata.getCleanupInterval().equals(defaultCleanupInterval));
            }
        }
    }


    /**
     * Test cleanup collections with until instant, depends on cleanupInterval.
     */
    public void AssertTtlCleanup(Instant until, Duration cleanupInterval) {
        long numOfDefaultTtlRecords = mongoTemplate.count(new Query(), COLLECTION_NAME_DEFAULT_TTL_TEST);
        long numOfRecords = mongoTemplate.count(new Query(), COLLECTION_NAME_TEST);

        storeManager.cleanupCollections(new StoreMetadataProperties(), until);

        List<StoreManagerRecordTest> defaultTtlRecords = mongoTemplate.findAll(StoreManagerRecordTest.class, COLLECTION_NAME_DEFAULT_TTL_TEST);
        if (until.getEpochSecond() % defaultCleanupInterval.getSeconds() == 0) {
            defaultTtlRecords.forEach(defaultTtlRecord -> {
                Assert.assertTrue(defaultTtlRecord.getStart().isAfter(until.minus(defaultTtl)) || defaultTtlRecord.getStart().equals(until.minus(defaultTtl)));
            });
        } else {
            Assert.assertTrue(numOfDefaultTtlRecords == mongoTemplate.count(new Query(), COLLECTION_NAME_DEFAULT_TTL_TEST));
        }

        List<StoreManagerRecordTest> records = mongoTemplate.findAll(StoreManagerRecordTest.class, COLLECTION_NAME_TEST);
        if (until.getEpochSecond() % cleanupInterval.getSeconds() == 0) {
            defaultTtlRecords.forEach(defaultTtlRecord -> {
                Assert.assertTrue(defaultTtlRecord.getStart().isAfter(until.minus(defaultTtl)) || defaultTtlRecord.getStart().equals(until.minus(defaultTtl)));
            });
        } else {
            Assert.assertTrue(numOfRecords == mongoTemplate.count(new Query(), COLLECTION_NAME_TEST));
        }

    }

    /**
     * Test cleanup collections between time range.
     */
    public void AssertCleanupInTimeRange(Instant start, Instant end) {
        storeManager.cleanupCollections(new StoreMetadataProperties(), start, end);

        List<StoreManagerRecordTest> defaultTtlRecords = mongoTemplate.findAll(StoreManagerRecordTest.class, COLLECTION_NAME_DEFAULT_TTL_TEST);
        List<StoreManagerRecordTest> records = mongoTemplate.findAll(StoreManagerRecordTest.class, COLLECTION_NAME_TEST);
        records.addAll(defaultTtlRecords);

        records.forEach(record -> {
            Assert.assertTrue(record.getStart().isAfter(end) || record.getStart().equals(end) || record.getStart().isBefore(start));
        });

    }


    /**
     * create record with mentioned ttl and cleanupInterval and default ttl and cleanupInterval for cleanup test with until instant
     *
     * @param start           start instant
     * @param end             end instant
     * @param ttl             ttl
     * @param cleanupInterval cleanupInterval
     */
    public void createStoreManagerRecordsWithTtl(Instant start, Instant end, Duration ttl, Duration cleanupInterval) {
        Duration interval = Duration.ofDays(1);
        while (start.isBefore(end)) {
            StoreManagerRecordTest record = new StoreManagerRecordTest("test" + start.toString(), start, start.plus(interval));
            StoreManagerRecordTest recordDefaultTtl = new StoreManagerRecordTest("testDefaultTtl" + start.toString(), start, start.plus(interval));
            storeManagerAwareTest.save(record, COLLECTION_NAME_TEST, ttl, cleanupInterval);
            storeManagerAwareTest.saveWithDefaultTtl(recordDefaultTtl, COLLECTION_NAME_DEFAULT_TTL_TEST);
            start = start.plus(interval);
        }
    }

    /**
     * create records without ttl and cleanupInterval and with default ttl and cleanupInterval for cleanup test between time range
     *
     * @param start start instant
     * @param end   end instant
     */
    public void createStoreManagerRecordsForTimeRangeCleanup(Instant start, Instant end) {
        Duration interval = Duration.ofHours(1);
        while (start.isBefore(end)) {
            StoreManagerRecordTest record = new StoreManagerRecordTest("test" + start.toString(), start, start.plus(interval));
            StoreManagerRecordTest recordDefaultTtl = new StoreManagerRecordTest("testDefaultTtl" + start.toString(), start, start.plus(interval));
            storeManagerAwareTest.register(record, COLLECTION_NAME_TEST);
            storeManagerAwareTest.saveWithDefaultTtl(recordDefaultTtl, COLLECTION_NAME_DEFAULT_TTL_TEST);
            start = start.plus(interval);
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
