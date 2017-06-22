package presidio.input.sdk.impl.repositories;

import com.mongodb.WriteResult;
import fortscale.domain.core.AbstractAuditableDocument;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import presidio.sdk.api.domain.DlpFileDataDocument;

import java.time.Instant;
import java.util.List;

public class DataSourceRepositoryImpl implements DataSourceRepository {

    private final MongoTemplate mongoTemplate;

    public DataSourceRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<? extends AbstractAuditableDocument> getDataSourceDataBetweenDates(String collectionName, Instant startTime, Instant endTime) {
        final Query query = new Query(createDateCriteria(startTime, endTime));
        return mongoTemplate.find(query, AbstractAuditableDocument.class, collectionName);
    }

    @Override
    public int cleanDataSourceDataBetweenDates(String collectionName, Instant startTime, Instant endTime) {
        final Query query = new Query(createDateCriteria(startTime, endTime));
        WriteResult removeResult = mongoTemplate.remove(query, AbstractAuditableDocument.class, collectionName);
        return removeResult.getN();
    }


    @Override
    public void insertDataSource(String collectionName, List<? extends AbstractAuditableDocument> documents) {
        mongoTemplate.insert(documents, collectionName);
    }

    @Override
    public void cleanCollection(String collectionName) {
        mongoTemplate.dropCollection(collectionName);
    }

    private Criteria createDateCriteria(Instant startTime, Instant endTime) {
        return Criteria.where(DlpFileDataDocument.DATE_TIME_FIELD_NAME).gte(startTime).lt(endTime);
    }
}
