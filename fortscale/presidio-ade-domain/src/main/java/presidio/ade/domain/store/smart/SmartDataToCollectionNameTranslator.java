package presidio.ade.domain.store.smart;

import presidio.ade.domain.store.AdeDataStoreCleanupParams;
import presidio.ade.domain.store.AdeToCollectionNameTranslator;

import java.util.Collection;

public class SmartDataToCollectionNameTranslator implements AdeToCollectionNameTranslator<SmartRecordsMetadata> {
    public static final String SCORE_SMART_COLLECTION_PREFIX = "scored_smart_";

    @Override
    public String toCollectionName(SmartRecordsMetadata arg) {
        return toCollectionName(arg.getConfigurationName());
    }


    public String toCollectionName(String configurationName) {
        return String.format(SCORE_SMART_COLLECTION_PREFIX + "%s", configurationName);
    }

    @Override
    public Collection<String> toCollectionNames(AdeDataStoreCleanupParams cleanupParams) {
        return null;
    }
}
