package presidio.ade.domain.store.scored;

import presidio.ade.domain.record.scored.AdeScoredRecord;
import presidio.ade.domain.store.AdeDataStoreCleanupParams;
import presidio.ade.domain.store.AdeToCollectionNameTranslator;

import java.util.Collection;

/**
 * Created by YaronDL on 6/13/2017.
 */
public class ScoredDataAdeToCollectionNameTranslator implements AdeToCollectionNameTranslator<AdeScoredRecord> {
    @Override
    public String toCollectionName(AdeScoredRecord scoredRecord) {
        return scoredRecord.getAdeEventType().replaceAll("\\.","_");
    }

    @Override
    public Collection<String> toCollectionNames(AdeDataStoreCleanupParams cleanupParams) {
        return null;
    }
}
