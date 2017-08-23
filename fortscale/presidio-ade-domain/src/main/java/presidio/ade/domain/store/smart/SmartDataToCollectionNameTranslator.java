package presidio.ade.domain.store.smart;

import presidio.ade.domain.store.AdeDataStoreCleanupParams;
import presidio.ade.domain.store.AdeToCollectionNameTranslator;

import java.util.Collection;

public class SmartDataToCollectionNameTranslator implements AdeToCollectionNameTranslator<SmartRecordsMetadata> {
    public static final String SMART_COLLECTION_PREFIX = "smart_";

    @Override
    public String toCollectionName(SmartRecordsMetadata arg) {
        return toCollectionName(arg.getConfigurationName());
    }


    public String toCollectionName(String configurationName) {
        return String.format(SMART_COLLECTION_PREFIX + "%s", configurationName);
    }

    @Override
    public Collection<String> toCollectionNames(AdeDataStoreCleanupParams cleanupParams) {
        return null;
    }
}
