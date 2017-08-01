package presidio.ade.sdk.aggregation_records;

import fortscale.accumulator.aggregation.event.AccumulatedAggregatedFeatureEvent;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;

import java.util.List;

/**
 * Provides the ADE's consumers with APIs related to Aggregation Records.
 *
 * @author Lior Govrin
 */
public interface AggregationRecordsManagerSdk {
	/**
	 * Get a list of {@link AdeAggregationRecord}s with the given feature name, context ID and time range.
	 *
	 * @param featureName the name of the aggregation feature
	 * @param contextId   the context ID (i.e. username)
	 * @param timeRange   the start and end instants of the records
	 * @return a list of {@link AdeAggregationRecord}s
	 */
	List<AdeAggregationRecord> getAggregationRecords(
			String featureName, String contextId, TimeRange timeRange);

	/**
	 * Get a list of {@link AccumulatedAggregatedFeatureEvent}s with the given feature name, context ID and time range.
	 * TODO: Replace with new POJO.
	 *
	 * @param featureName the name of the aggregation feature that is accumulated
	 * @param contextId   the context ID (i.e. username)
	 * @param timeRange   the start and end instants of the records
	 * @return a list of {@link AccumulatedAggregatedFeatureEvent}s
	 */
	List<AccumulatedAggregatedFeatureEvent> getAccumulatedAggregatedFeatureEvents(
			String featureName, String contextId, TimeRange timeRange);
}
