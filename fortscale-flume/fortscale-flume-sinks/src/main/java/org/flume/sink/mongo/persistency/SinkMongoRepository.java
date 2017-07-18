package org.flume.sink.mongo.persistency;


import com.mongodb.DBObject;

import java.util.List;

public interface SinkMongoRepository {

    int bulkSave(List<DBObject> events, String collectionName);

    void save(DBObject event, String collectionName);


}
