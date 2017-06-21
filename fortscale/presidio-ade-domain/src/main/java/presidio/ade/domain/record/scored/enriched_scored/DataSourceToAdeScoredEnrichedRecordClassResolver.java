package presidio.ade.domain.record.scored.enriched_scored;

import presidio.ade.domain.record.util.DataSourceToAdeRecordClassResolver;

/**
 * Created by YaronDL on 6/15/2017.
 */
public class DataSourceToAdeScoredEnrichedRecordClassResolver extends DataSourceToAdeRecordClassResolver<AdeScoredEnrichedRecord> {
    /**
     * @param scanPackage class path to scan
     */
    public DataSourceToAdeScoredEnrichedRecordClassResolver(String scanPackage) {
        super(scanPackage);
    }
}
