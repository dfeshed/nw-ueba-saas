package presidio.ade.domain.record.enriched;

import presidio.ade.domain.record.util.AdeEventTypeToAdeRecordClassResolver;

/**
 * Created by YaronDL on 6/14/2017.
 */
public class AdeEventTypeToAdeEnrichedRecordClassResolver extends AdeEventTypeToAdeRecordClassResolver<EnrichedRecord> {
    /**
     * @param scanPackage class path to scan
     */
    public AdeEventTypeToAdeEnrichedRecordClassResolver(String scanPackage) {
        super(scanPackage);
    }
}
