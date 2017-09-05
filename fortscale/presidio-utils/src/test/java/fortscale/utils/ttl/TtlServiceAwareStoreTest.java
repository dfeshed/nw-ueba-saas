package fortscale.utils.ttl;


import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Duration;
import java.time.Instant;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * TtlServiceAwareStoreTest store used for TtlServiceTest.
 */
public class TtlServiceAwareStoreTest implements TtlServiceAware {

    private TtlService ttlService;

    private MongoTemplate mongoTemplate;

    public TtlServiceAwareStoreTest(MongoTemplate mongoTemplate){
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void setTtlService(TtlService ttlService) {
        this.ttlService = ttlService;
    }

    @Override
    public void remove(String collectionName, Instant until) {
        Query query = new Query()
                .addCriteria(where(TtlServiceRecordTest.START_FIELD).lte(until));
        mongoTemplate.remove(query, collectionName);
    }

    public void save(TtlServiceRecordTest ttlServiceRecordTest, String collectionName, Duration ttl, Duration cleanupInterval) {
        mongoTemplate.insert(ttlServiceRecordTest, collectionName);
        ttlService.save(getStoreName(), collectionName, ttl, cleanupInterval);
    }

    public void saveWithDefaultTtl(TtlServiceRecordTest ttlServiceRecordTest, String collectionName) {
        mongoTemplate.insert(ttlServiceRecordTest,collectionName);
        ttlService.save(getStoreName(), collectionName);
    }

    @Override
    public String getStoreName() {
        return "testStoreName";
    }

}
