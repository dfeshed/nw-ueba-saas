package presidio.output.processor.services.alert;

import fortscale.domain.SMART.EntityEvent;
import fortscale.utils.pagination.PageIterator;

/**
 * Created by efratn on 24/07/2017.
 */
public interface AlertService {

    /**
     * Convert the received smarts into alerts and persist them
     * @param smarts- paged smarts list
     */
    void generateAlerts(PageIterator<EntityEvent> smarts);
}
