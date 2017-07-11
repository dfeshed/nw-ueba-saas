package presidio.ade.domain.record.scored.enriched_scored;

import presidio.ade.domain.record.util.AdeEventTypeToAdeRecordClassResolver;

/**
 * Created by YaronDL on 6/15/2017.
 */
public class DataSourceToAdeScoredEnrichedRecordClassResolver extends AdeEventTypeToAdeRecordClassResolver<AdeScoredEnrichedRecord> {
    /**
     * @param scanPackage class path to scan
     */
    public DataSourceToAdeScoredEnrichedRecordClassResolver(String scanPackage) {
        super(scanPackage);
    }
}
