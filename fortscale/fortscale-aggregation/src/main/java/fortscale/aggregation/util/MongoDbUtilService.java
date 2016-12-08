package fortscale.aggregation.util;

import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.UncategorizedMongoDbException;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.HashSet;
import java.util.Set;


public class MongoDbUtilService implements InitializingBean {
    private static final Logger logger = Logger.getLogger(MongoDbUtilService.class);
    public static final String COLLECTION_ALREADY_EXISTS_ERR_MSG = "collection already exists";

    @Autowired
    private MongoTemplate mongoTemplate;

    private Set<String> collectionNames;

    @Override
    public void afterPropertiesSet() throws Exception {
        collectionNames = new HashSet<>(mongoTemplate.getCollectionNames());
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

}
