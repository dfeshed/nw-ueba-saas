package fortscale.utils.mongodb;

import com.mongodb.DBCollection;
import fortscale.utils.CustomUtil;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by Amir Keren on 22/09/15.
 */
public class MongoUtil implements CustomUtil {

    private static Logger logger = Logger.getLogger(MongoUtil.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public boolean deleteEntityBetween(String collection, String dateField, Date startDate, Date endDate) {
        logger.info("attempting to delete from collection {}", collection);
        Query query;
        if (startDate != null && endDate == null) {
            query = new Query(where(dateField).gte(startDate.getTime()));
        } else if (startDate == null && endDate != null) {
            query = new Query(where(dateField).lte(endDate.getTime()));
        } else {
            query = new Query(where(dateField).gte(startDate.getTime()).lte(endDate.getTime()));
        }
        logger.debug("query is {}", query.toString());
        long recordsFound = mongoTemplate.count(query, collection);
        logger.info("found {} records", recordsFound);
        if (recordsFound > 0) {
            mongoTemplate.remove(query, collection);
        }
        return true;
    }

    public boolean dropAllCollections() {
        Collection<String> collectionNames = getAllCollectionsWithPrefix("");
        logger.debug("found {} collections to drop", collectionNames.size());
        return dropCollections(collectionNames);
    }

    public boolean dropCollections(Collection<String> collectionNames) {
        int numberOfCollectionsDropped = 0;
        logger.debug("attempting to drop {} collections from mongo", collectionNames.size());
        for (String collectionName: collectionNames) {
            if (dropCollection(collectionName)) {
                numberOfCollectionsDropped++;
            }
        }
        if (numberOfCollectionsDropped == collectionNames.size()) {
            logger.info("dropped all {} collections", collectionNames.size());
            return true;
        }
        logger.error("failed to drop all {} collections, dropped only {}", collectionNames.size(),
                numberOfCollectionsDropped);
        return false;
    }

    private boolean dropCollection(String collectionName) {
        mongoTemplate.dropCollection(collectionName);
        //verify drop
        if (mongoTemplate.collectionExists(collectionName)) {
            logger.warn("failed to drop collection " + collectionName);
            return false;
        }
        logger.info("dropped collection {}", collectionName);
        return true;
    }

    //run with empty prefix to get all collections
    private Collection<String> getAllCollectionsWithPrefix(String prefix) {
        logger.debug("getting all collections");
        Set<String> collectionNames = mongoTemplate.getCollectionNames();
        logger.debug("found {} collections", collectionNames.size());
        if (prefix.isEmpty()) {
            return collectionNames;
        }
        Iterator<String> it = collectionNames.iterator();
        logger.debug("filtering out collections not starting with {}", prefix);
        while (it.hasNext()) {
            String collectionName = it.next();
            if (!collectionName.startsWith(prefix)) {
                it.remove();
            }
        }
        logger.info("found {} collections with prefix {}", collectionNames.size(), prefix);
        return collectionNames;
    }

    @Override
    public boolean restoreSnapshot(String collectionName, String backupCollectionName) {
        boolean success = false;
        logger.debug("check if backup collection exists");
        if (mongoTemplate.collectionExists(backupCollectionName)) {
            DBCollection backupCollection = mongoTemplate.getCollection(backupCollectionName);
            logger.debug("drop collection");
            mongoTemplate.dropCollection(collectionName);
            //verify drop
            if (mongoTemplate.collectionExists(collectionName)) {
                logger.debug("dropping failed, abort");
                return success;
            }
            logger.debug("renaming backup collection");
            backupCollection.rename(collectionName);
            if (mongoTemplate.collectionExists(collectionName)) {
                //verify restore
                logger.info("snapshot restored");
                success = true;
                return success;
            } else {
                logger.error("snapshot failed to restore - could not rename collection");
            }
        } else {
            logger.error("snapshot failed to restore - no backup collection {} found", backupCollectionName);
            return success;
        }
        logger.error("snapshot failed to restore - manually rename backup collection");
        return success;
    }

}