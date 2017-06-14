package presidio.ade.domain.store.scored;

import presidio.ade.domain.record.scored.AdeScoredRecord;
import presidio.ade.domain.store.AdeDataStoreCleanupParams;
import presidio.ade.domain.store.ToCollectionNameTranslator;

import java.util.Collection;

/**
 * Created by YaronDL on 6/13/2017.
 */
public class ScoredDataToCollectionNameTranslator implements ToCollectionNameTranslator<AdeScoredRecord> {
    @Override
    public String toCollectionName(AdeScoredRecord scoredRecord) {
        return String.format("%s_%s", scoredRecord.getAdeRecordType(), scoredRecord.getFeatureName());
    }

    @Override
    public Collection<String> toCollectionNames(AdeDataStoreCleanupParams cleanupParams) {
        return null;
    }
}
