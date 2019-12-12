package presidio.input.sdk.impl.repositories;

import com.mongodb.client.result.DeleteResult;
import fortscale.domain.core.AbstractAuditableDocument;
import fortscale.domain.core.AbstractDocument;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import presidio.sdk.api.domain.AbstractInputDocument;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

public class DataSourceRepositoryImpl implements DataSourceRepository {
    private static final String MAX_TIME_AGGREGATION_FIELD_NAME = "maxTime";
    private final MongoTemplate mongoTemplate;
    private final MongoDbBulkOpUtil mongoDbBulkOpUtil;

    public DataSourceRepositoryImpl(MongoTemplate mongoTemplate, MongoDbBulkOpUtil mongoDbBulkOpUtil) {
        this.mongoTemplate = mongoTemplate;
        this.mongoDbBulkOpUtil = mongoDbBulkOpUtil;
    }

    @Override
    public List<? extends AbstractAuditableDocument> getDataSourceDataBetweenDates(String collectionName, Instant startTime, Instant endTime) {
        final Query query = new Query(createDateCriteria(startTime, endTime));
        return mongoTemplate.find(query, AbstractAuditableDocument.class, collectionName);
    }

    @Override
    public int cleanDataSourceDataBetweenDates(String collectionName, Instant startTime, Instant endTime) {
        final Query query = new Query(createDateCriteria(startTime, endTime));
        DeleteResult removeResult = mongoTemplate.remove(query, AbstractAuditableDocument.class, collectionName);
        return (int)removeResult.getDeletedCount();
    }

    @Override
    public int cleanDataSourceDataUntilDate(String collectionName, Instant endTime) {
        final Query query = new Query(Criteria.where(AbstractAuditableDocument.DATE_TIME_FIELD_NAME).lt(endTime));
        DeleteResult removeResult = mongoTemplate.remove(query, AbstractAuditableDocument.class, collectionName);
        return (int)removeResult.getDeletedCount();
    }

    @Override
    public void insertDataSource(String collectionName, List<? extends AbstractAuditableDocument> documents) {
        mongoDbBulkOpUtil.insertUnordered(documents, collectionName);
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

    @Override
    public <U extends AbstractInputDocument> List<U> readRecords(String collectionName, Instant startDate, Instant endDate, int numOfItemsToSkip, int pageSize, Map<String, Object> filter,  List<String> projectionFields) {
        Query query = getQuery(startDate, endDate, numOfItemsToSkip, pageSize);
        query = createFilterCriteria(query, filter);
        addFieldsProjection(projectionFields, query);
        query.with(new Sort(Sort.Direction.ASC, AbstractInputDocument.DATE_TIME_FIELD_NAME));
        List<U> recordList = mongoTemplate.find(query,(Class<U>)  AbstractInputDocument.class, collectionName);
        return recordList;

    }

    public long count(String collectionName, Instant startDate, Instant endDate) {
        Query query = new Query(createDateCriteria(startDate, endDate));
        return mongoTemplate.count(query, collectionName);
    }

    public long count(String collectionName, Instant startDate, Instant endDate, Map<String, Object> filter, List<String> projectionFields) {
        Query query = new Query(createDateCriteria(startDate, endDate));
        query = createFilterCriteria(query, filter);
        addFieldsProjection(projectionFields, query);
        return mongoTemplate.count(query, collectionName);
    }

    public Map<String, Instant> aggregateKeysMaxTime(Instant startDate, Instant endDate, String fieldPath, long skip, long limit, String collectionName, boolean allowDiskUse){
        List<AggregationOperation> aggregationOperations = new LinkedList<>();
        aggregationOperations.add(match(
                new Criteria().andOperator(createDateCriteria(startDate, endDate), where(fieldPath).exists(true))));

        aggregationOperations.add(group(fieldPath).max(AbstractAuditableDocument.DATE_TIME_FIELD_NAME).as(MAX_TIME_AGGREGATION_FIELD_NAME));

        if (skip >= 0 && limit > 0) {
            aggregationOperations.add(sort(Sort.Direction.ASC, AbstractDocument.ID_FIELD));
            aggregationOperations.add(skip(skip));
            aggregationOperations.add(limit(limit));
        }

        Aggregation aggregation = newAggregation(aggregationOperations).withOptions(Aggregation.newAggregationOptions().
                allowDiskUse(allowDiskUse).build());

        List<Document> aggrResult = mongoTemplate
                .aggregate(aggregation, collectionName, Document.class)
                .getMappedResults();

        Map<String, Instant> ret = aggrResult.stream().collect(Collectors.toMap(document -> document.getString(AbstractDocument.ID_FIELD), document -> document.getDate(MAX_TIME_AGGREGATION_FIELD_NAME).toInstant()));
        return ret;
    }


    private Query getQuery(Instant startDate, Instant endDate, int numOfItemsToSkip, int pageSize) {
        Criteria dateTimeCriteria = createDateCriteria(startDate, endDate);
        return new Query(dateTimeCriteria).skip(numOfItemsToSkip).limit(pageSize);
    }

    private Criteria createDateCriteria(Instant startTime, Instant endTime) {
        return Criteria.where(AbstractAuditableDocument.DATE_TIME_FIELD_NAME).gte(startTime).lt(endTime);
    }


    private Query createFilterCriteria(Query query, Map<String, Object> filter) {
        if(!filter.isEmpty()){
            filter.forEach((key, value) -> {
                query.addCriteria(Criteria.where(key).is(value));
            });
        }
        return query;
    }

    private void addFieldsProjection(List<String> projectionFields, Query query) {
        if(!projectionFields.isEmpty()){
            projectionFields.add("_class");
            for(String projectionField : projectionFields) {
                query.fields().include(projectionField);
            }
        }
    }
}
