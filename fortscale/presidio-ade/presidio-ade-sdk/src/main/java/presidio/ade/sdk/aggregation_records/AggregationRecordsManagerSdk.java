package presidio.ade.sdk.aggregation_records;

import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.record.accumulator.AccumulatedAggregationFeatureRecord;
import presidio.ade.sdk.scored_enriched_records.ScoredEnrichedRecordsManagerSdk;

import java.util.List;
import java.util.Map;

/**
 * Provides the ADE's consumers with APIs related to Aggregation Records.
 *
 * @author Lior Govrin
 */
public interface AggregationRecordsManagerSdk {

	/**
	 * Get a list of {@link AccumulatedAggregationFeatureRecord}s with the given feature name, context ID and time range.
	 * NOTE: Accumulations are not a product the ADE should expose. it should be calculated separately.
	 * it  is only for temporal usage. do not add any more calls to this method!!!
	 * @param featureName the name of the aggregation feature that is accumulated
	 * @param contextId   the context ID (i.e. username)
	 * @param timeRange   the start and end instants of the records
	 * @return a list of {@link AccumulatedAggregationFeatureRecord}s
	 */
	@Deprecated
	List<AccumulatedAggregationFeatureRecord> getAccumulatedAggregatedFeatureEvents(
			String featureName, String contextId, TimeRange timeRange);

	/**
	 *
	 * @return mapping of aggregation name to adeEventType. can be used in order to retrieve the right parameters for {@link ScoredEnrichedRecordsManagerSdk#findScoredEnrichedRecords(List, String, Double)}
	 */
	Map<String,List<String>> getAggregationNameToAdeEventTypeMap();

	/**
	 * syntactic suger for {@link this#getAggregationNameToAdeEventTypeMap()}
	 * @param scoreAggregationName
	 * @return adeEventTypes for scoreAggregationName
	 */
	default List<String> getScoreAggregationNameAdeEventTypes(String scoreAggregationName)
	{
		return getAggregationNameToAdeEventTypeMap().get(scoreAggregationName);
	}

	Map<String,String> getAggregationNameToFeatureBucketConfNameMap();

	/**
	 * NOTE: featurebuckets are not a product the ADE should expose. it should be calculated separately.
	 * it  is only for temporal usage. do not add any more calls to this method!!!
	 *
	 * @param contextId context to retrieve feature buckets for
	 * @param bucketConfName indicates the name of the bucket
	 * @param timeRange filtering by time (gte start, lt end)
	 * @return feature buckets by filtering params
	 */
	@Deprecated
	List<FeatureBucket> findFeatureBuckets(String contextId, String bucketConfName, TimeRange timeRange);

}
