package org.flume.sink.mongo.persistency;


import com.mongodb.DBObject;
import fortscale.domain.core.AbstractAuditableDocument;

import java.util.List;

public interface SinkMongoRepository<T extends AbstractAuditableDocument> {

    int bulkSave(List<DBObject> events, String collectionName);

    void save(T event, String collectionName);


}
