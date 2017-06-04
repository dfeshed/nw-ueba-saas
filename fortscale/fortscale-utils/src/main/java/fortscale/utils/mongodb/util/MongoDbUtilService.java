package fortscale.utils.mongodb.util;

import com.mongodb.MongoInternalException;
import fortscale.utils.logging.Logger;
import org.springframework.data.mongodb.UncategorizedMongoDbException;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;


public class MongoDbUtilService {
    public static final String COLLECTION_ALREADY_EXISTS_ERR_MSG = "collection already exists";
    public static final String TOO_LARGE_OBJECT_MONGO_ERR_MSG = "is larger than MaxDocumentSize";
    private static final Logger logger = Logger.getLogger(MongoDbUtilService.class);
    private MongoTemplate mongoTemplate;

    private Set<String> collectionNames;

    public MongoDbUtilService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.collectionNames = new HashSet<>(mongoTemplate.getCollectionNames());
    }

    public void createCollectionIfNotExists(String collectionName) {
        if (!collectionNames.contains(collectionName)) {
            createCollection(collectionName);
        }
    }
    public boolean collectionExists(String collectionName) {
        return collectionNames.contains(collectionName);
    }

    /**
     * creates mongodb collection.
     * collection might be created by other processes...there for, if collection already exists ->
     * just adds it to in memory collection name set
     *
     * @param collectionName collection to create
     */
    public void createCollection(String collectionName) {
        try {
            mongoTemplate.createCollection(collectionName);
        }
        catch (UncategorizedMongoDbException e)
        {
            if(e.getMessage().contains(COLLECTION_ALREADY_EXISTS_ERR_MSG))
            {
                logger.debug("collection={} already exists. not creating.",collectionName);
            }
            else
            {
                throw e;
            }
        }
        collectionNames.add(collectionName);
    }

    public void dropCollection(String collectionName) {
        mongoTemplate.dropCollection(collectionName);
        collectionNames.remove(collectionName);
    }

    public Set<String> getCollections(){
        return new HashSet<>(collectionNames);
    }

    /**
     * mongo BSON size is limited. in case this limit is exceeded, write an error to log
     * @see <a href="https://docs.mongodb.com/manual/reference/limits/#bson-documents">https://docs.mongodb.com/manual/reference/limits/#bson-documents</a>
     * @param collectionName collection we try to write into
     * @param object POJO we tried to write
     * @param e exception raised to validated
     * @param failureMetrics metrics to be updated in case of too large document
     * @return true if it is exception cause by too large document
     */
    public boolean isTooLargeDocumentMongoException(String collectionName, Object object, Exception e, AtomicLong failureMetrics) {
        Throwable cause = e.getCause();
        if(cause!=null) {
            if (cause instanceof MongoInternalException) {
                if (cause.getMessage().contains(TOO_LARGE_OBJECT_MONGO_ERR_MSG)) {
                    failureMetrics.incrementAndGet();
                    logger.error("can't save object={} to collection={} cause it is too large", object, collectionName, e);
                    return true;
                }
            }
        }
        return false;
    }

}
