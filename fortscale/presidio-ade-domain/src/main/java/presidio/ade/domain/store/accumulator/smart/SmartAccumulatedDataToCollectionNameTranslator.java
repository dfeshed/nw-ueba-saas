package presidio.ade.domain.store.accumulator.smart;

import presidio.ade.domain.store.AdeDataStoreCleanupParams;
import presidio.ade.domain.store.AdeToCollectionNameTranslator;

import java.util.Collection;


public class SmartAccumulatedDataToCollectionNameTranslator implements AdeToCollectionNameTranslator<SmartAccumulatedRecordsMetaData> {
    private static final String SMART_ACCUMULATION_COLLECTION_PREFIX = "accm_smart_";

    @Override
    public String toCollectionName(SmartAccumulatedRecordsMetaData arg) {
        return toCollectionName(arg.getConfigurationName());
    }


    public String toCollectionName(String configurationName) {
        return String.format(SMART_ACCUMULATION_COLLECTION_PREFIX + "%s", configurationName);
    }


    @Override
    public Collection<String> toCollectionNames(AdeDataStoreCleanupParams cleanupParams) {
        return null;
    }
}
