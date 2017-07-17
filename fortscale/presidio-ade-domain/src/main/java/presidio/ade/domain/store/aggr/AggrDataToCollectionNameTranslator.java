package presidio.ade.domain.store.aggr;

import presidio.ade.domain.store.AdeDataStoreCleanupParams;
import presidio.ade.domain.store.AdeToCollectionNameTranslator;

import java.util.Collection;

/**
 * Created by barak_schuster on 7/10/17.
 */
public class AggrDataToCollectionNameTranslator implements AdeToCollectionNameTranslator<AggrRecordsMetadata> {
    private static final String AGGR_COLLECTION_PREFIX = "aggr_";

    @Override
    public String toCollectionName(AggrRecordsMetadata metadata) {
        String featureName = metadata.getFeatureName();

        return String.format("%s%s", AGGR_COLLECTION_PREFIX,featureName);
    }

    @Override
    public Collection<String> toCollectionNames(AdeDataStoreCleanupParams cleanupParams) {
        //todo
        return null;
    }
}
