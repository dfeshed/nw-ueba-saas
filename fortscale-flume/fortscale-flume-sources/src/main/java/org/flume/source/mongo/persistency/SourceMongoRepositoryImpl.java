package org.flume.source.mongo.persistency;


import fortscale.domain.core.AbstractAuditableDocument;
import org.flume.domain.AbstractDocument;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Instant;
import java.util.List;


public class SourceMongoRepositoryImpl implements SourceMongoRepository {

    private final MongoTemplate mongoTemplate;

    public SourceMongoRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<AbstractAuditableDocument> findByDateTimeBetween(String collectionName, Instant startDate, Instant endDate, int pageNum, int pageSize) {
        final Query timeQuery = new Query(Criteria.where(AbstractDocument.DATE_TIME_FIELD_NAME).gte(startDate).lt(endDate)).with(new PageRequest(pageNum, pageSize));
        return mongoTemplate.find(timeQuery, AbstractAuditableDocument.class, collectionName);
    }
}
