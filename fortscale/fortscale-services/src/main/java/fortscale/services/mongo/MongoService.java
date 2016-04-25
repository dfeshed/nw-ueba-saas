package fortscale.services.mongo;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import fortscale.utils.ConversionUtils;
import fortscale.utils.cleanup.CleanupDeletionUtil;
import fortscale.utils.cleanup.CleanupUtil;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimestampUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by Amir Keren on 22/09/15.
 */
@Service
public class MongoService extends CleanupDeletionUtil implements CleanupUtil {

    private static Logger logger = Logger.getLogger(MongoService.class);

    private final String FILTERS_DELIMITER = "%%%";
    private final String KEYVALUE_DELIMITER = ":::";
    private final String SPECIAL_DELIMITER = "!!!";
    private final String DATE_IDENTIFIER = "date";
    private final String REGEX_IDENTIFIER = "regex";
    private final String BACKUP_SUFFIX = "_backup";

    @Autowired
    private MongoTemplate mongoTemplate;

    /***
     *
     * This method deletes documents according to their date
     *
     * @param collection collection name to delete from
     * @param filters    filters including dateField and any other key=value combination
     * @param startDate  documents after that date will be deleted
     * @param endDate    documents before that date will be deleted
     * @return
     */
    @Override
    public boolean deleteEntityBetween(String collection, String filters, Date startDate, Date endDate) {
        if (!mongoTemplate.collectionExists(collection)) {
            logger.error("collection {} not found!", collection);
            return false;
        }
        logger.info("attempting to delete from collection {}", collection);
        Query query = new Query();
        boolean hasCriteria = false;
        String dateField = null;
        if (filters.contains(FILTERS_DELIMITER)) {
            for (String filter: filters.split(FILTERS_DELIMITER)) {
                String field = filter.split(KEYVALUE_DELIMITER)[0];
                String value = filter.split(KEYVALUE_DELIMITER)[1];
                if (field.equals(DATE_IDENTIFIER)) {
                    dateField = value;
                } else {
                    query.addCriteria(where(field).is(value));
                    hasCriteria = true;
                }
            }
        } else if (filters.contains(KEYVALUE_DELIMITER)) {
            String field = filters.split(KEYVALUE_DELIMITER)[0];
            String value = filters.split(KEYVALUE_DELIMITER)[1];
            if (field.equals(DATE_IDENTIFIER)) {
                dateField = value;
            } else if (field.equals(REGEX_IDENTIFIER)) {
                String key = value.split(SPECIAL_DELIMITER)[0];
                String innerValue = value.split(SPECIAL_DELIMITER)[1];
                query.addCriteria(where(key).regex(innerValue));
                hasCriteria = true;
            } else {
                query.addCriteria(where(field).is(value));
                hasCriteria = true;
            }
        } else {
            dateField = filters;
        }
        if (dateField != null) {
            DBCollection dbCollection = mongoTemplate.getCollection(collection);
            DBObject dbObject = dbCollection.findOne();
            if (dbObject == null) {
                logger.debug("collection empty - nothing to remove");
                return true;
            }
            if (!dbObject.containsField(dateField)) {
                logger.error("date field {} not found in collection {}", dateField, collection);
                return false;
            }
            long mongoTime = ConversionUtils.convertToLong(dbObject.get(dateField));
            //TODO - generalize this in the case where dateField is not in unix time
            if (startDate != null && endDate == null) {
                query.addCriteria(where(dateField).gte(convertToSecondsIfNeeded(mongoTime, startDate.getTime())));
                hasCriteria = true;
            } else if (startDate == null && endDate != null) {
                query.addCriteria(where(dateField).lte(convertToSecondsIfNeeded(mongoTime, endDate.getTime())));
                hasCriteria = true;
            } else if (startDate != null && endDate != null) {
                query.addCriteria(where(dateField).gte(convertToSecondsIfNeeded(mongoTime, startDate.getTime())).
                                                   lte(convertToSecondsIfNeeded(mongoTime, endDate.getTime())));
                hasCriteria = true;
            } else {
                logger.error("Must provide either start or end date");
                return false;
            }
        }
        if (hasCriteria) {
            logger.debug("query is {}", query.toString());
            mongoTemplate.remove(query, collection);
            return true;
        }
        logger.error("Bad parameters");
        return false;
    }

    /***
     *
     * This method helps us determine if we should perform the search in milliseconds or seconds
     *
     * @param mongoTime   sample time of an existing document in mongo
     * @param searchTime  the time we want to search by
     * @return
     */
    private long convertToSecondsIfNeeded(long mongoTime, long searchTime) {
        if (TimestampUtils.isTimestampInSeconds(mongoTime)) {
            return TimestampUtils.convertToSeconds(searchTime);
        }
        return searchTime;
    }

    /***
     *
     * This method drops all collections in the database
     *
     * @param doValidate  flag to determine should we perform validations
     * @return
     */
    @Override
    public boolean deleteAllEntities(boolean doValidate) {
        Collection<String> collectionNames = getAllEntities();
        //ignore the following collections
        collectionNames.remove("system.indexes");
        collectionNames.remove("systemConfiguration");
        collectionNames.remove("analyst");
        collectionNames.remove("analystAuth");
        collectionNames.remove("geoIp");
        collectionNames.remove("fortscale_configuration");
        logger.debug("found {} collections to drop", collectionNames.size());
        return deleteEntities(collectionNames, doValidate);
    }

    /***
     *
     * This method returns all the collections in the database
     *
     * @return
     */
    @Override
    public Collection<String> getAllEntities() {
        return mongoTemplate.getCollectionNames();
    }

    /***
     *
     * This method drops the given list of collections from the database
     *
     * @param collectionNames a list of collection names to drop
     * @param doValidate      flag to determine should we perform validations
     * @return
     */
    @Override
    public boolean deleteEntities(Collection<String> collectionNames, boolean doValidate) {
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
     * This methods attempts to restore an entire collection from a backup collection
     *
     * @param collectionName        the collection to drop
     * @param backupCollectionName  the backup collection name to rename back
     * @return
     */
    private boolean restoreCollection(String collectionName, String backupCollectionName) {
        final String TEMPSUFFIX = "_clean-job-temp-suffix";
        boolean success = false;
        //sanity check
        logger.debug("verify that collections exist");
        if (!mongoTemplate.collectionExists(collectionName) || !mongoTemplate.collectionExists(backupCollectionName)) {
            logger.error("no origin or backup collection found");
            return success;
        }
        DBCollection deleteCollection = mongoTemplate.getCollection(collectionName);
        DBCollection backupCollection = mongoTemplate.getCollection(backupCollectionName);
        String tempCollectionName = collectionName + TEMPSUFFIX;
        logger.debug("verify that destination collection temp name doesn't exist");
        //sanity check - shouldn't be found
        if (mongoTemplate.collectionExists(tempCollectionName)) {
            logger.info("temp collection {} already exists, dropping...", tempCollectionName);
            mongoTemplate.dropCollection(tempCollectionName);
            if (mongoTemplate.collectionExists(tempCollectionName)) {
                logger.error("failed to drop temp collection, manually drop it before continuing");
                return success;
            }
        }
        logger.debug("renaming origin collection {} to {}", collectionName, tempCollectionName);
        deleteCollection.rename(tempCollectionName);
        //verify rename
        if (mongoTemplate.collectionExists(collectionName) || !mongoTemplate.collectionExists(tempCollectionName)) {
            logger.error("renaming failed, abort");
            return success;
        }
        logger.debug("renaming backup collection {} to {}", backupCollection, collectionName);
        backupCollection.rename(collectionName);
        //verify rename
        if (mongoTemplate.collectionExists(collectionName) && !mongoTemplate.collectionExists(backupCollectionName)) {
            logger.info("snapshot restored");
            success = true;
            //remove old collection
            mongoTemplate.dropCollection(tempCollectionName);
            if (mongoTemplate.collectionExists(tempCollectionName)) {
                logger.warn("failed to drop old collection {} - drop manually", tempCollectionName);
            }
            return success;
        }
        logger.error("snapshot failed to restore - manually rename backup collection");
        return success;
    }

    /***
     *
     * This method searches for all collections starting with prefix and restores / deletes them
     *
     * @param prefix
     * @return
     */
    public boolean restoreSnapshot(String prefix) {
        logger.info("trying to restore mongo to snapshot");
        int restored = 0, toRestore = 0;
        Collection<String> collectionNames = getAllEntities();
        for (String collectionName: collectionNames) {
            if (collectionName.startsWith(prefix)) {
                logger.debug("target collection found - {}", collectionName);
                toRestore++;
                String backupCollectionName = collectionName + BACKUP_SUFFIX;
                logger.debug("looking for backup collection {}", backupCollectionName);
                if (mongoTemplate.collectionExists(backupCollectionName)) {
                    logger.debug("backup collection found, attempting to restore");
                    if (restoreCollection(collectionName, backupCollectionName)) {
                        logger.debug("restore successful");
                        restored++;
                    } else {
                        logger.warn("failed to restore {} to {}", collectionName, backupCollectionName);
                    }
                } else {
                    logger.debug("backup collection not found, attempting to delete {}", collectionName);
                    mongoTemplate.dropCollection(collectionName);
                    if (!mongoTemplate.collectionExists(collectionName)) {
                        logger.debug("delete successful");
                        restored++;
                    } else {
                        logger.warn("failed to delete {}", collectionName);
                    }
                }
            }
        }
        if (restored != toRestore) {
            logger.error("failed to restore all {} collections, restored only {}", toRestore, restored);
            return false;
        }
        logger.info("all {} collections successfully restored / deleted", toRestore);
        return true;
    }

}