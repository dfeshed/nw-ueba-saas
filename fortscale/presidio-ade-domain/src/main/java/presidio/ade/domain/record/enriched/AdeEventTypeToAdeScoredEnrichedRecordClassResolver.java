package presidio.ade.domain.record.enriched;

import presidio.ade.domain.record.util.AdeEventTypeToAdeRecordClassResolver;

/**
 * Created by YaronDL on 6/15/2017.
 */
public class AdeEventTypeToAdeScoredEnrichedRecordClassResolver extends AdeEventTypeToAdeRecordClassResolver<AdeScoredEnrichedRecord> {
    /**
     * @param scanPackage class path to scan
     */
    public AdeEventTypeToAdeScoredEnrichedRecordClassResolver(String scanPackage) {
        super(scanPackage);
    }
}
