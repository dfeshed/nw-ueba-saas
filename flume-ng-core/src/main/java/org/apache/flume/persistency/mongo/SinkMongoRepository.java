package org.apache.flume.persistency.mongo;


import com.mongodb.DBObject;
import fortscale.domain.core.AbstractDocument;
import org.apache.flume.Event;

import java.util.List;

public interface SinkMongoRepository<T extends AbstractDocument> {

    int bulkSave(List<DBObject> events, String collectionName);

    void save(T event, String collectionName);

    void saveFlumeEvent(Event event, String collectionName);
}
