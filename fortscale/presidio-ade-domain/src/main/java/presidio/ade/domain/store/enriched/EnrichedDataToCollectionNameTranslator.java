package presidio.ade.domain.store.enriched;

import presidio.ade.domain.store.ToCollectionNameTranslator;

import java.util.Collection;

/**
 * Translator from enriched data to collection name.
 *
 * Created by barak_schuster on 5/18/17.
 */
public class EnrichedDataToCollectionNameTranslator implements ToCollectionNameTranslator<EnrichedRecordsMetadata> {
	public static final String ENRICHED_COLLECTION_PREFIX = "enriched";

	@Override
	public String toCollectionName(EnrichedRecordsMetadata arg) {
		return String.format(ENRICHED_COLLECTION_PREFIX + "_%s", arg.getDataSource());
	}

	@Override
	public Collection<String> toCollectionNames(EnrichedDataStoreCleanupParams cleanupParams) {
		return null;
	}
}
