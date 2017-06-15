package presidio.ade.domain.record.enriched;

import presidio.ade.domain.record.util.DataSourceToAdeRecordClassResolver;

/**
 * Created by YaronDL on 6/14/2017.
 */
public class DataSourceToAdeEnrichedRecordClassResolver extends DataSourceToAdeRecordClassResolver<EnrichedRecord> {
    /**
     * @param scanPackage class path to scan
     */
    public DataSourceToAdeEnrichedRecordClassResolver(String scanPackage) {
        super(scanPackage);
    }
}
