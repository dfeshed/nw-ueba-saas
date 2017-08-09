package presidio.ade.domain.store.scored;

import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.domain.store.AdeDataStoreCleanupParams;
import presidio.ade.domain.store.AdeToCollectionNameTranslator;

import java.util.Collection;

/**
 * Created by YaronDL on 6/13/2017.
 */
public class AdeScoredEnrichedRecordToCollectionNameTranslator implements AdeToCollectionNameTranslator<AdeScoredEnrichedRecord> {
    @Override
    public String toCollectionName(AdeScoredEnrichedRecord scoredRecord) {
        String adeEventType = scoredRecord.getAdeEventType();
        return toCollectionName(adeEventType);
    }

    public String toCollectionName(String adeEventType) {
        return adeEventType.replaceAll("\\.","_");
    }

    @Override
    public Collection<String> toCollectionNames(AdeDataStoreCleanupParams cleanupParams) {
        return null;
    }
}
