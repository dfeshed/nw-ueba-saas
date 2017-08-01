package presidio.ade.domain.store.enriched;

import fortscale.domain.core.EnrichedRecordsMetadata;
import presidio.ade.domain.store.AdeDataStoreCleanupParams;
import presidio.ade.domain.store.AdeToCollectionNameTranslator;

import java.util.Collection;
import java.util.Collections;

/**
 * Translator from enriched data to collection name.
 * <p>
 * Created by barak_schuster on 5/18/17.
 */
public class EnrichedDataAdeToCollectionNameTranslator implements AdeToCollectionNameTranslator<EnrichedRecordsMetadata> {
    private static final String ENRICHED_COLLECTION_PREFIX = "enriched_";

    @Override
    public String toCollectionName(EnrichedRecordsMetadata arg) {
        return toCollectionName(arg.getAdeEventType());
    }


    public String toCollectionName(String adeEventType) {
        return String.format(ENRICHED_COLLECTION_PREFIX + "%s", adeEventType);
    }


    @Override
    public Collection<String> toCollectionNames(AdeDataStoreCleanupParams cleanupParams) {
        return Collections.singletonList(toCollectionName(cleanupParams.getAdeEventType()));
    }
}
