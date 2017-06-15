package presidio.ade.domain.store;

import java.util.Collection;

/**
 * determines what should be the collection name for a given data record.
 *
 * Created by barak_schuster on 5/18/17.
 */
public interface ToCollectionNameTranslator<T> {
	/**
	 * translates arg to relevant mongodb collection name
	 *
	 * @param arg i.e. conf name
	 * @return mongodb collection name
	 */
	String toCollectionName(T arg);

	/**
	 * @param cleanupParams deletion filtering params
	 * @return all relevant mongodb collection names to be cleaned for given cleanup filters
	 */
	Collection<String> toCollectionNames(AdeDataStoreCleanupParams cleanupParams);
}
