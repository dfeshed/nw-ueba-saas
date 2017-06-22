package presidio.ade.domain.store.enriched;

import presidio.ade.domain.store.AdeDataStoreCleanupParams;
import presidio.ade.domain.store.ToCollectionNameTranslator;

import java.util.Collection;

/**
 * Translator from enriched data to collection name.
 * <p>
 * Created by barak_schuster on 5/18/17.
 */
public class EnrichedDataToCollectionNameTranslator implements ToCollectionNameTranslator<EnrichedRecordsMetadata> {
    public static final String ENRICHED_COLLECTION_PREFIX = "enriched";

    @Override
    public String toCollectionName(EnrichedRecordsMetadata arg) {
        return toCollectionName(arg.getDataSource());
    }


    public String toCollectionName(String dataSource) {
        return String.format(ENRICHED_COLLECTION_PREFIX + "_%s", dataSource);
    }


    @Override
    public Collection<String> toCollectionNames(AdeDataStoreCleanupParams cleanupParams) {
        return null;
    }
}