package presidio.ade.domain.store;

import fortscale.utils.mongodb.util.ToCollectionNameTranslator;

import java.util.Collection;

/**
 * determines what should be the collection name for a given data record.
 *
 * Created by barak_schuster on 5/18/17.
 */
public interface AdeToCollectionNameTranslator<T> extends ToCollectionNameTranslator<T>{

	/**
	 * @param cleanupParams deletion filtering params
	 * @return all relevant mongodb collection names to be cleaned for given cleanup filters
	 */
	Collection<String> toCollectionNames(AdeDataStoreCleanupParams cleanupParams);
}
