package fortscale.utils.mongodb.util;

import com.mongodb.BulkWriteResult;
import fortscale.utils.mongodb.index.DynamicIndexingApplicationListener;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.CollectionUtils;

import java.util.List;

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

    /**
     * C'tor.
     *
     * @param auditingHandler
     * @param dynamicIndexingApplicationListener a utility that creates collection indexes from document annotations
     */
    public MongoDbBulkOpUtil(
            CachedIsNewAwareAuditingHandler auditingHandler,
            DynamicIndexingApplicationListener dynamicIndexingApplicationListener,
            MongoTemplate mongoTemplate) {

        this.auditingHandler = auditingHandler;
        this.dynamicIndexingApplicationListener = dynamicIndexingApplicationListener;
        this.mongoTemplate = mongoTemplate;
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
        return mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, collectionName).insert(records).execute();
    }
}
