package presidio.input.sdk.impl.repositories;

import com.mongodb.WriteResult;
import fortscale.domain.core.AbstractAuditableDocument;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import presidio.sdk.api.domain.AbstractInputDocument;

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

    @Override
    public <U extends AbstractInputDocument> List<U> readRecords(String collectionName, Instant startDate, Instant endDate, int numOfItemsToSkip, int pageSize) {
        Query query = getQuery(startDate, endDate, numOfItemsToSkip, pageSize);

        query.with(new Sort(Sort.Direction.ASC, AbstractInputDocument.DATE_TIME_FIELD_NAME));

        List<U> recordList = mongoTemplate.find(query, (Class<U>) AbstractInputDocument.class, collectionName);
        return recordList;

    }

    public long count(String collectionName, Instant startDate, Instant endDate) {
        Query query = new Query(createDateCriteria(startDate, endDate));
        return mongoTemplate.count(query, collectionName);
    }

    private Query getQuery(Instant startDate, Instant endDate, int numOfItemsToSkip, int pageSize) {
        Criteria dateTimeCriteria = createDateCriteria(startDate, endDate);
        return new Query(dateTimeCriteria).skip(numOfItemsToSkip).limit(pageSize);
    }

    private Criteria createDateCriteria(Instant startTime, Instant endTime) {
        return Criteria.where(AbstractAuditableDocument.DATE_TIME_FIELD_NAME).gte(startTime).lt(endTime);
    }
}
