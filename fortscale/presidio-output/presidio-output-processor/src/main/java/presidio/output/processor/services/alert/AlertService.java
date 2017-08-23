package presidio.output.processor.services.alert;

import fortscale.utils.pagination.PageIterator;
import presidio.ade.domain.record.aggregated.SmartRecord;

/**
 * Created by efratn on 24/07/2017.
 */
public interface AlertService {

    /**
     * Convert the received smarts into alerts and persist them
     * @param smarts- paged smarts list
     */
    void generateAlerts(PageIterator<SmartRecord> smarts);
}
