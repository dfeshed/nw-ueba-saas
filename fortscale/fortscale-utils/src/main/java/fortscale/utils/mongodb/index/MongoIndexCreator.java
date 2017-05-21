package fortscale.utils.mongodb.index;

/**
 * adds indexes to collection. would not add index if collection already containing those indexes
 * Created by barak_schuster on 5/21/17.
 */
public interface MongoIndexCreator {

    /**
     * ensure indexes to given store
     * @param collectionName
     * @param store
     */
    void ensureIndexes(String collectionName, MongoIndexedStore store);
}
