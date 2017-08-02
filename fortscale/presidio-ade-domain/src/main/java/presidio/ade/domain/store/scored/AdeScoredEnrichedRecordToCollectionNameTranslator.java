package presidio.ade.domain.store.scored;

import presidio.ade.domain.record.scored.AdeScoredRecord;
import presidio.ade.domain.record.scored.enriched_scored.AdeScoredEnrichedRecord;
import presidio.ade.domain.store.AdeDataStoreCleanupParams;
import presidio.ade.domain.store.AdeToCollectionNameTranslator;

import java.util.Collection;

/**
 * Created by YaronDL on 6/13/2017.
 */
public class AdeScoredEnrichedRecordToCollectionNameTranslator implements AdeToCollectionNameTranslator<AdeScoredEnrichedRecord> {
    @Override
    public String toCollectionName(AdeScoredEnrichedRecord scoredRecord) {
        return scoredRecord.getAdeEventType().replaceAll("\\.","_");
    }

    @Override
    public Collection<String> toCollectionNames(AdeDataStoreCleanupParams cleanupParams) {
        return null;
    }
}
