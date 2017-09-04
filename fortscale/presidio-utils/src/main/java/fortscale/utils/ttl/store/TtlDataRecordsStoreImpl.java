package fortscale.utils.ttl.store;

import fortscale.utils.ttl.record.TtlData;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;


public class TtlDataRecordsStoreImpl implements TtlDataRecordsStore {

    private MongoTemplate mongoTemplate;
    private static final String TTL_TABLE_PREFIX = "management_ttl";

    public TtlDataRecordsStoreImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    @Override
    public List<TtlData> findTtlData(String appName) {
        Criteria appNameFilter = Criteria.where(TtlData.APPLICATION_NAME_FIELD).is(appName);
        Query query = new Query().addCriteria(appNameFilter);
        return mongoTemplate.find(query, TtlData.class, TTL_TABLE_PREFIX);
    }

    @Override
    public void save(TtlData ttlData) {
        mongoTemplate.save(ttlData, TTL_TABLE_PREFIX);
    }


}
