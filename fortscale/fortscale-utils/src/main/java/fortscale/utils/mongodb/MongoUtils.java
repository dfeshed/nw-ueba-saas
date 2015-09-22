package fortscale.utils.mongodb;

import com.mongodb.DBCollection;
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
public class MongoUtils {

    private static Logger logger = Logger.getLogger(MongoUtils.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    public boolean deleteMongoEntityBetween(DAO toDelete, Date startDate, Date endDate) {
        logger.info("attempting to delete {} from mongo", toDelete.daoObject.getSimpleName());
        Query query;
        if (startDate != null && endDate == null) {
            query = new Query(where(toDelete.queryField).gte(startDate.getTime()));
        } else if (startDate == null && endDate != null) {
            query = new Query(where(toDelete.queryField).lte(endDate.getTime()));
        } else {
            query = new Query(where(toDelete.queryField).gte(startDate.getTime()).lte(endDate.getTime()));
        }
        logger.debug("query is {}", query.toString());
        long recordsFound = mongoTemplate.count(query, toDelete.daoObject);
        logger.info("found {} records", recordsFound);
        if (recordsFound > 0) {
            mongoTemplate.remove(query, toDelete.daoObject);
        }
        return true;
    }

    public boolean dropAllCollections() {
        Collection<String> collectionNames = getAllMongoCollectionsWithPrefix("");
        logger.debug("found {} collections to drop", collectionNames.size());
        return dropMongoCollections(collectionNames);
    }

    private boolean dropMongoCollections(Collection<String> collectionNames) {
        int numberOfCollectionsDropped = 0;
        logger.debug("attempting to drop {} collections from mongo", collectionNames.size());
        for (String collectionName: collectionNames) {
            mongoTemplate.dropCollection(collectionName);
            //verify drop
            if (mongoTemplate.collectionExists(collectionName)) {
                String message = "failed to drop collection " + collectionName;
                logger.warn(message);
                //monitor.warn(getMonitorId(), getStepName(), message);
            } else {
                logger.info("dropped collection {}", collectionName);
                numberOfCollectionsDropped++;
            }
        }
        if (numberOfCollectionsDropped == collectionNames.size()) {
            logger.info("dropped all {} collections", collectionNames.size());
            return true;
        }
        logError(String.format("failed to drop all %s collections, dropped only %s", collectionNames.size(),
                numberOfCollectionsDropped));
        return false;
    }

    //run with empty prefix to get all collections
    private Collection<String> getAllMongoCollectionsWithPrefix(String prefix) {
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

    public boolean restoreMongoForEntity(DAO toRestore, String backupCollectionName) {
        boolean success = false;
        logger.debug("check if backup collection exists");
        if (mongoTemplate.collectionExists(backupCollectionName)) {
            DBCollection backupCollection = mongoTemplate.getCollection(backupCollectionName);
            logger.debug("drop collection");
            mongoTemplate.dropCollection(toRestore.queryField);
            //verify drop
            if (mongoTemplate.collectionExists(toRestore.queryField)) {
                logger.debug("dropping failed, abort");
                return success;
            }
            logger.debug("renaming backup collection");
            backupCollection.rename(toRestore.queryField);
            if (mongoTemplate.collectionExists(toRestore.queryField)) {
                //verify restore
                logger.info("snapshot restored");
                success = true;
                return success;
            } else {
                logError("snapshot failed to restore - could not rename collection");
            }
        } else {
            logError(String.format("snapshot failed to restore - no backup collection %s found", backupCollectionName));
            return success;
        }
        logError("snapshot failed to restore - manually rename backup collection");
        return success;
    }

    public boolean clearMongoOfEntity(DAO toDelete) {
        boolean success;
        logger.info("attempting to delete {} from mongo", toDelete.daoObject.getSimpleName());
        mongoTemplate.remove(new Query(), toDelete.daoObject);
        long recordsFound = mongoTemplate.count(new Query(), toDelete.daoObject);
        if (recordsFound > 0) {
            success = false;
            logError("failed to remove documents");
        } else {
            success = true;
            logger.info("successfully removed all documents");
        }
        return success;
    }

    private void logError(String message) {
        logger.error(message);
        //monitor.error(getMonitorId(), getStepName(), message);
    }

    private class DAO {

        public Class daoObject;
        public String queryField;

        public DAO(Class daoObject, String queryField) {
            this.daoObject = daoObject;
            this.queryField = queryField;
        }

    }

}