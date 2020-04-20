package presidio.ade.domain.store.accumulator;

import presidio.ade.domain.store.AdeDataStoreCleanupParams;
import presidio.ade.domain.store.AdeToCollectionNameTranslator;

import java.util.Collection;


public class AccumulatedDataToCollectionNameTranslator implements AdeToCollectionNameTranslator<AccumulatedRecordsMetaData> {
    private static final String AGGR_COLLECTION_PREFIX = "accm_";

    @Override
    public String toCollectionName(AccumulatedRecordsMetaData metadata) {
        String featureName = metadata.getFeatureName();

        return String.format("%s%s", AGGR_COLLECTION_PREFIX, featureName);
    }

    @Override
    public Collection<String> toCollectionNames(AdeDataStoreCleanupParams cleanupParams) {
        return null;
    }
}
