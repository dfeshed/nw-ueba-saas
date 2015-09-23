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

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by Amir Keren on 22/09/15.
 */
public class MongoUtil implements CustomUtil {

    private static Logger logger = Logger.getLogger(MongoUtil.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    /***
     *
     * This method deletes documents according to their date
     *
     * @param collection collection name to delete from
     * @param dateField  date field that will be used for the filter query
     * @param startDate  documents after that date will be deleted
     * @param endDate    documents before that date will be deleted
     * @return
     */
    @Override
    public boolean deleteEntityBetween(String collection, String dateField, Date startDate, Date endDate) {
        logger.info("attempting to delete from collection {}", collection);
        Query query;
        //TODO - generalize this in the case where dateField is not in unix time
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

    /***
     *
     * This method drops all collections in the database
     *
     * @param doValidate  flag to determine should we perform validations
     * @return
     */
    public boolean dropAllCollections(boolean doValidate) {
        Collection<String> collectionNames = getAllCollectionsWithPrefix("");
        //system collection - ignore
        collectionNames.remove("system.indexes");
        logger.debug("found {} collections to drop", collectionNames.size());
        return dropCollections(collectionNames, doValidate);
    }

    /***
     *
     * This method drops the given list of collections from the database
     *
     * @param collectionNames a list of collection names to drop
     * @param doValidate      flag to determine should we perform validations
     * @return
     */
    public boolean dropCollections(Collection<String> collectionNames, boolean doValidate) {
        int numberOfCollectionsDropped = 0;
        logger.debug("attempting to drop {} collections from mongo", collectionNames.size());
        for (String collectionName: collectionNames) {
            if (dropCollection(collectionName, doValidate)) {
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

    /***
     *
     * This method drops a single collection from the database
     *
     * @param collectionName  collection name to drop
     * @param doValidate      flag to determine should we perform validations
     * @return
     */
    private boolean dropCollection(String collectionName, boolean doValidate) {
        try {
            mongoTemplate.dropCollection(collectionName);
        } catch (Exception ex) {
            logger.error("failed to drop collection " + collectionName);
            return false;
        }
        if (doValidate) {
            //verify drop
            if (mongoTemplate.collectionExists(collectionName)) {
                logger.error("failed to drop collection " + collectionName);
                return false;
            }
            logger.info("dropped collection {}", collectionName);
        }
        return true;
    }

    /***
     *
     * This method gets all of the documents starting with the given prefix
     *
     * @param prefix  run with empty prefix to get all collections
     * @return
     */
    public Collection<String> getAllCollectionsWithPrefix(String prefix) {
        logger.debug("getting all collections");
        Collection<String> collectionNames = mongoTemplate.getCollectionNames();
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

    /***
     *
     * This methods attempts to restore an entire collection from a backup collection
     *
     * @param collectionName        the collection to drop
     * @param backupCollectionName  the backup collection name to rename back
     * @return
     */
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