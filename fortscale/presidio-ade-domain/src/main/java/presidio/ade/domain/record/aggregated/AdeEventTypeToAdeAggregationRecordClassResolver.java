

package presidio.ade.domain.record.aggregated;

import presidio.ade.domain.record.util.AdeEventTypeToAdeRecordClassResolver;

/**
 * Created by barak_schuster on 8/8/17.
 */
public class AdeEventTypeToAdeAggregationRecordClassResolver extends AdeEventTypeToAdeRecordClassResolver<AdeAggregationRecord> {
    /**
     * @param scanPackage class path to scan
     */
    public AdeEventTypeToAdeAggregationRecordClassResolver(String scanPackage) {
        super(scanPackage);
    }
}