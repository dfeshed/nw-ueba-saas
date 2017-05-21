package fortscale.utils.mongodb.index;

import org.springframework.data.mongodb.core.index.Index;

import java.util.Set;

/**
 * this store would be indexed by the {@link MongoIndexCreator#ensureIndexes(String, MongoIndexedStore)}
 * Created by barak_schuster on 5/21/17.
 */
public interface MongoIndexedStore {
    Set<Index> getIndexes();
}
