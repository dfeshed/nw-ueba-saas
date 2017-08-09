package presidio.ade.domain.store.accumulator;

import presidio.ade.domain.store.AdeDataStoreCleanupParams;
import presidio.ade.domain.store.AdeToCollectionNameTranslator;
import presidio.ade.domain.store.aggr.AggrRecordsMetadata;

import java.util.Collection;


public class AccumulatedDataToCollectionNameTranslator implements AdeToCollectionNameTranslator<AggrRecordsMetadata> {
    private static final String AGGR_COLLECTION_PREFIX = "accm_";

    @Override
    public String toCollectionName(AggrRecordsMetadata metadata) {
        String featureName = metadata.getFeatureName();

        return String.format("%s%s", AGGR_COLLECTION_PREFIX, featureName);
    }

    @Override
    public Collection<String> toCollectionNames(AdeDataStoreCleanupParams cleanupParams) {
        return null;
    }
}
