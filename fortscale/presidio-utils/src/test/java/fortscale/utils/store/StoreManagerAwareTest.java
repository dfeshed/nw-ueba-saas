package fortscale.utils.store;


import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Duration;
import java.time.Instant;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * StoreManagerAwareTest store used for StoreManagerTest.
 */
public class StoreManagerAwareTest implements StoreManagerAware {

    private StoreManager storeManager;

    private MongoTemplate mongoTemplate;

    public StoreManagerAwareTest(MongoTemplate mongoTemplate){
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void setStoreManager(StoreManager storeManager) {
        this.storeManager = storeManager;
    }

    @Override
    public void remove(String collectionName, Instant until) {
        Query query = new Query()
                .addCriteria(where(StoreManagerRecordTest.START_FIELD).lt(until));
        mongoTemplate.remove(query, collectionName);
    }

    @Override
    public void remove(String collectionName, Instant start, Instant end){
        Query query = new Query()
                .addCriteria(where(StoreManagerRecordTest.START_FIELD).gte(start).lt(end));
        mongoTemplate.remove(query, collectionName);
    }

    public void save(StoreManagerRecordTest storeManagerRecordTest, String collectionName, Duration ttl, Duration cleanupInterval) {
        mongoTemplate.insert(storeManagerRecordTest, collectionName);
        storeManager.registerWithTtl(getStoreName(), collectionName, ttl, cleanupInterval);
    }

    public void saveWithDefaultTtl(StoreManagerRecordTest storeManagerRecordTest, String collectionName) {
        mongoTemplate.insert(storeManagerRecordTest,collectionName);
        storeManager.registerWithTtl(getStoreName(), collectionName);
    }

    public void register(StoreManagerRecordTest storeManagerRecordTest, String collectionName) {
        mongoTemplate.insert(storeManagerRecordTest, collectionName);
        storeManager.register(getStoreName(), collectionName);
    }


}
