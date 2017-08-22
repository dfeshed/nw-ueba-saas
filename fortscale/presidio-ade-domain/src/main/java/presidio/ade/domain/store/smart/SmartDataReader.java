package presidio.ade.domain.store.smart;

import fortscale.domain.SMART.EntityEvent;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.record.aggregated.SmartRecord;

import java.util.List;

/**
 * Exposes the methods that read from the {@link SmartRecord}s store.
 *
 * @author Lior Govrin
 */
public interface SmartDataReader {
	List<EntityEvent> readSmartRecords(TimeRange timeRange, int scoreThreshold);
}
