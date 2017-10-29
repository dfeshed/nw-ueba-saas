package fortscale.utils.mongodb.util;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.model.InsertOneModel;
import fortscale.utils.mongodb.index.DynamicIndexingApplicationListener;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Bulk operation utility, mainly syntactic sugar and some minor workarounds
 * (like: {@link #insertUnordered(List, String)}).
 *
 * @author Barak Schuster
 * @author Lior Govrin
 */
public class MongoDbBulkOpUtil {
    private final CachedIsNewAwareAuditingHandler auditingHandler;
    private final DynamicIndexingApplicationListener dynamicIndexingApplicationListener;
    private final MongoTemplate mongoTemplate;
    private final MongoDatabase mongoDatabase;

    private static final String ID_FIELD_NAME = "_id";

    /**
     * C'tor.
     *
     * @param auditingHandler
     * @param dynamicIndexingApplicationListener a utility that creates collection indexes from document annotations
     */
    public MongoDbBulkOpUtil(
            CachedIsNewAwareAuditingHandler auditingHandler,
            DynamicIndexingApplicationListener dynamicIndexingApplicationListener,
            MongoTemplate mongoTemplate,
            MongoDatabase mongoDatabase) {

        this.auditingHandler = auditingHandler;
        this.dynamicIndexingApplicationListener = dynamicIndexingApplicationListener;
        this.mongoTemplate = mongoTemplate;
        this.mongoDatabase = mongoDatabase;
    }

    /**
     * Due to bug {@link <a href="https://jira.spring.io/browse/DATAMONGO-1743"/>}, bulk operations do not call
     * application listeners. As a result, collections are not indexed. This is a workaround until the bug is fixed.
     *
     * @param records records to be bulk inserted
     * @return null if no records received, bulk result otherwise
     */
    public BulkWriteResult insertUnordered(List<?> records, String collectionName) {
        if (CollectionUtils.isEmpty(records)) return null;
        records.forEach(auditingHandler::markAudited);
        dynamicIndexingApplicationListener.ensureDynamicIndexesExist(records.get(0).getClass(), collectionName);


        // map to mongo objects -- need to be converted to spring data code if possible
        List<InsertOneModel<Document>> documents = new ArrayList<InsertOneModel<Document>>();
        for (Object record :records) {
            DBObject sink = new BasicDBObject();
            mongoTemplate.getConverter().write(record, sink);
            Document document = new Document(sink.toMap());
            if (document.get(ID_FIELD_NAME) == null) {
                document.put(ID_FIELD_NAME, new ObjectId());
            }
            InsertOneModel insertOneModel = new InsertOneModel(document);
            documents.add(insertOneModel);
        }


        // async call to mongo
        CountDownLatch waitForMongoResponseLock = new CountDownLatch(1);
        final AtomicReference<BulkWriteResult> bulkWriteResult = new AtomicReference<BulkWriteResult>();
        mongoDatabase.getCollection(collectionName).bulkWrite(documents, new SingleResultCallback<com.mongodb.bulk.BulkWriteResult>() {

            @Override
            public void onResult(com.mongodb.bulk.BulkWriteResult result, Throwable t) {
                bulkWriteResult.set(result);
                waitForMongoResponseLock.countDown();
            }
        });


        // wait for results
        try {
            waitForMongoResponseLock.await();
        } catch (InterruptedException e) {
            throw new RuntimeException("failed to insertUnordered docs to mongo",e);
        }

        return bulkWriteResult.get();
    }
}
