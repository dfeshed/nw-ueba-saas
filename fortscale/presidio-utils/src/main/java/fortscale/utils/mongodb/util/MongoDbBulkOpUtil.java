package fortscale.utils.mongodb.util;

import com.mongodb.BulkWriteResult;
import fortscale.utils.mongodb.index.DynamicIndexApplicationListener;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.data.auditing.IsNewAwareAuditingHandler;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

/**
 * Bulk operation utility, mainly syntactic sugar and some minor workarounds (like: {@link #insertUnordered(List, String)}
 * Created by barak_schuster on 7/19/17.
 */
public class MongoDbBulkOpUtil {
    private final ObjectFactory<IsNewAwareAuditingHandler> auditingHandlerFactory;
    private DynamicIndexApplicationListener dynamicIndexApplicationListener;
    private MongoTemplate mongoTemplate;

    /**
     * C'tor
     * @param dynamicIndexApplicationListener a utility that creates indexes to collection by annotations regardless to pre-configured collection name
     * @param mongoTemplate you know...
     * @param auditingHandlerFactory
     */
    public MongoDbBulkOpUtil(DynamicIndexApplicationListener dynamicIndexApplicationListener, MongoTemplate mongoTemplate, ObjectFactory<IsNewAwareAuditingHandler> auditingHandlerFactory) {
        this.dynamicIndexApplicationListener = dynamicIndexApplicationListener;
        this.mongoTemplate = mongoTemplate;
        this.auditingHandlerFactory = auditingHandlerFactory;
    }

    /**
     * due to bug {@link <a href="https://jira.spring.io/browse/DATAMONGO-1743"/>} bulk operations does not call applications listeners
     * as a result, collections are not indexed.
     * this is a work around till bug is resolved
     * @param records records to be bulk inserted
     * @param collectionName
     * @return null if no records received. bulk result otherwise
     */
    public BulkWriteResult insertUnordered(List <?> records, String collectionName) {
        BulkWriteResult result;
        if(records == null)
        {
            return null;
        }
        if (records.isEmpty())
        {
            return null;
        }
        records.forEach(record -> auditingHandlerFactory.getObject().markAudited(record));
        dynamicIndexApplicationListener.createCollectionIndexesForClass(collectionName,records.get(0).getClass());
        result = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED,collectionName).insert(records).execute();

        return result;
    }
}
