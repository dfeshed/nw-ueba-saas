package org.apache.flume.persistency.mongo;

import com.mongodb.BulkWriteResult;
import com.mongodb.DBObject;
import fortscale.domain.core.AbstractDocument;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.BulkOperationException;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

import java.util.List;


public class SinkMongoRepositoryImpl<T extends AbstractDocument> implements SinkMongoRepository<T> {

    private static final Logger logger = LoggerFactory.getLogger(SinkMongoRepositoryImpl.class);

    private final MongoTemplate mongoTemplate;

    public SinkMongoRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public int bulkSave(List<DBObject> events, String collectionName) {
        try {
            if (events.isEmpty()) {
                return 0;
            }
            BulkWriteResult bulkOpResult = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, collectionName)
                    .insert(events).execute();
            final int insertedCount = bulkOpResult.getInsertedCount();
            if (bulkOpResult.isAcknowledged()) {
                logger.debug("inserted={} documents into collection={} in bulk insert", insertedCount, collectionName);
            } else {
                logger.error("bulk insert into collection={} wasn't acknowledged", collectionName);
            }
            return insertedCount;
        } catch (BulkOperationException e) {
            logger.error("failed to perform bulk insert into collection={}", collectionName, e);
            throw e;
        }

    }

    @Override
    public void save(T event, String collectionName) {
        mongoTemplate.save(event, collectionName);
    }

    @Override
    public void ensureIndex(String collectionName, String indexFieldName) {
        final Index indexDefinition = new Index(indexFieldName, Sort.Direction.ASC);
        mongoTemplate.indexOps(collectionName).ensureIndex(indexDefinition);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("mongoTemplate", mongoTemplate)
                .toString();
    }
}
