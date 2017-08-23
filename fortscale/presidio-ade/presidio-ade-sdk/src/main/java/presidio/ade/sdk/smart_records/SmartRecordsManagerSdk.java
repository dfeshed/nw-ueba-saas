package presidio.ade.sdk.smart_records;

import fortscale.domain.SMART.EntityEvent;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.record.aggregated.SmartRecord;

/**
 * Provides the ADE's consumers with APIs related to Smart Records.
 *
 * @author Lior Govrin
 */
public interface SmartRecordsManagerSdk {
	/**
	 * Get an iterator over {@link EntityEvent}s with the given time
	 * range and a score greater than or equal to the given threshold.
	 *
	 * @param timeRange      the start and end instants of the records
	 * @param pageSize       the number of records in each page
	 * @param scoreThreshold the scores should be greater than or equal to this threshold
	 * @return an iterator over {@link EntityEvent}s
	 */
	PageIterator<SmartRecord> getSmartRecords(TimeRange timeRange, int pageSize, int scoreThreshold);
}
