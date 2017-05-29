package fortscale.utils.mongodb.index;

/**
 * adds indexes to collection. would not add index if collection already containing those indexes
 * if collection pre-existed but did not contain some of the indexes, they will be added
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
