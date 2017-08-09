package presidio.ade.domain.store.smart;

import fortscale.domain.SMART.EntityEvent;
import fortscale.utils.time.TimeRange;

import java.util.List;

/**
 * Created by efratn on 23/07/2017.
 */
public interface SmartDataStore {

    List<EntityEvent> readSmarts(TimeRange timeRange, int scoreThreshold);

}
