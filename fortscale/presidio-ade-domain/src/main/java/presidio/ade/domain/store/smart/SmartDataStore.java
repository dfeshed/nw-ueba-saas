package presidio.ade.domain.store.smart;

import fortscale.domain.SMART.EntityEvent;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.store.enriched.EnrichedRecordsMetadata;

import java.util.List;
import java.util.Set;

/**
 * Created by efratn on 23/07/2017.
 */
public interface SmartDataStore {

    List<EntityEvent> readSmarts(TimeRange timeRange, int scoreThreshold);

}
