package presidio.ade.domain.store.translators;

import presidio.ade.domain.store.input.ADEInputCleanupParams;

import java.util.Collection;

/**
 * determines what should be the collection name for given data record.
 * Created by barak_schuster on 5/18/17.
 */
public interface DataCollectionTranslator<T> {

    /**
     * translates arg to relevant mongodb collection name
     *
     * @param arg i.e. conf name
     * @return mongodb collection name
     */
    String toCollectionName(T arg);

    /**
     * @return all relevant mongodb collection names to be cleaned for given cleanup filters
     * @param cleanupParams deletion filtering params
     */
    Collection<String> toCollectionNames(ADEInputCleanupParams cleanupParams);

}
