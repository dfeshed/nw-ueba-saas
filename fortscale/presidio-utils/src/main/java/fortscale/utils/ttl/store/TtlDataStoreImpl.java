package fortscale.utils.ttl.store;

import fortscale.utils.ttl.record.TtlData;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;


public class TtlDataStoreImpl implements TtlDataStore {

    private MongoTemplate mongoTemplate;
    private static final String MANAGEMENT_TTL_TABLE = "management_ttl";

    public TtlDataStoreImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    @Override
    public List<TtlData> getTtlDataList(String appName) {
        Criteria appNameFilter = Criteria.where(TtlData.APPLICATION_NAME_FIELD).is(appName);
        Query query = new Query().addCriteria(appNameFilter);
        return mongoTemplate.find(query, TtlData.class, MANAGEMENT_TTL_TABLE);
    }

    @Override
    public void save(TtlData ttlData) {
        mongoTemplate.save(ttlData, MANAGEMENT_TTL_TABLE);
    }


}
