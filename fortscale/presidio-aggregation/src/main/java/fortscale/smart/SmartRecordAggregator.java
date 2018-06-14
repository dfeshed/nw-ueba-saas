package fortscale.smart;

import fortscale.smart.record.conf.SmartRecordConf;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.record.aggregated.*;

import java.util.*;

/**
 * Aggregates {@link AdeAggregationRecord}s with a specific {@link FixedDurationStrategy} and
 * {@link TimeRange} to their corresponding {@link SmartRecord}s, according to the context ID.
 *
 * @author Lior Govrin
 */
public class SmartRecordAggregator {
	private static final Logger logger = Logger.getLogger(SmartRecordAggregator.class);

	private SmartRecordConf smartRecordConf;
	private FixedDurationStrategy fixedDurationStrategy;
	private TimeRange timeRange;
	private Double threshold;
	private Map<String, SmartRecord> contextIdToSmartRecordMap;

	/**
	 * C'tor.
	 *
	 * @param smartRecordConf       the configuration of the {@link SmartRecord}s that are created
	 * @param fixedDurationStrategy the expected {@link FixedDurationStrategy} of the {@link AdeAggregationRecord}s
	 * @param timeRange             the expected {@link TimeRange} of the {@link AdeAggregationRecord}s
	 * @param threshold             only {@link AdeAggregationRecord}s whose values / scores are larger
	 *                              than this threshold will be included in the {@link SmartRecord}s
	 */
	public SmartRecordAggregator(
			SmartRecordConf smartRecordConf,
			FixedDurationStrategy fixedDurationStrategy,
			TimeRange timeRange,
			Double threshold) {

		if (!timeRange.getStart().plus(fixedDurationStrategy.toDuration()).equals(timeRange.getEnd())) {
			String msg = String.format("Fixed duration strategy %s does not fit with time range %s.",
					fixedDurationStrategy.toStrategyName(), timeRange.toString());
			logger.error(msg);
			throw new IllegalArgumentException(msg);
		}

		this.smartRecordConf = smartRecordConf;
		this.fixedDurationStrategy = fixedDurationStrategy;
		this.timeRange = timeRange;
		this.threshold = threshold;
		this.contextIdToSmartRecordMap = new HashMap<>();
	}

	/**
	 * Add the given {@link AdeAggregationRecord}s to their corresponding
	 * {@link SmartRecord}s. Create new smart records for new context IDs.
	 *
	 * @param newAggregationRecords the {@link AdeAggregationRecord}s
	 */
	public void updateSmartRecords(Collection<AdeAggregationRecord> newAggregationRecords) {
		for (AdeAggregationRecord newAggregationRecord : newAggregationRecords) {
			if (isAggregationRecordTimeRangeValid(newAggregationRecord)) {
				SmartRecord smartRecord = getSmartRecord(newAggregationRecord);
				List<SmartAggregationRecord> existingAggregationRecords = smartRecord.getSmartAggregationRecords();

				if (existingAggregationRecords == null) {
					existingAggregationRecords = new LinkedList<>();
					smartRecord.setSmartAggregationRecords(existingAggregationRecords);
				}

				if (doesAggregationRecordAlreadyExist(newAggregationRecord, existingAggregationRecords)) {
					// TODO: Add metric
					logger.error("Context ID {} already has an aggregation record of type {} between {}. " +
							"Ignoring new aggregation record of same type between same time range.",
							smartRecord.getContextId(), newAggregationRecord.getFeatureName(), timeRange);
				} else if (doesAggregationRecordPassThreshold(newAggregationRecord)) {
					// TODO: Add metric
					existingAggregationRecords.add(new SmartAggregationRecord(newAggregationRecord));
				} else {
					// TODO: Add metric
					logger.debug("Discarding aggregation record of type {} between {}, " +
							"because it did not pass the threshold {}. Context ID = {}.",
							newAggregationRecord.getFeatureName(), timeRange, threshold, smartRecord.getContextId());
				}
			} else {
				// TODO: Add metric
				logger.error("Ignoring aggregation record {} with start instant {} " +
						"and end instant {}, because the expected time range is {}.", newAggregationRecord,
						newAggregationRecord.getStartInstant(), newAggregationRecord.getEndInstant(), timeRange);
			}
		}
	}

	/**
	 * @return all the {@link SmartRecord}s aggregated so far
	 */
	public Collection<SmartRecord> getSmartRecords() {
		return contextIdToSmartRecordMap.values();
	}

	private boolean isAggregationRecordTimeRangeValid(AdeAggregationRecord aggregationRecord) {
		return aggregationRecord.getStartInstant().equals(timeRange.getStart()) &&
				aggregationRecord.getEndInstant().equals(timeRange.getEnd());
	}

	private SmartRecord getSmartRecord(AdeAggregationRecord aggregationRecord) {
		Map<String, String> aggregationRecordContext = aggregationRecord.getContext();
		Map<String, String> smartRecordContext = new HashMap<>();

		for (Map.Entry<String, List<String>> entry : smartRecordConf.getContextToFieldsMap().entrySet()) {
			for (String field : entry.getValue()) {
				String value = aggregationRecordContext.get(field);

				if (value != null) {
					smartRecordContext.put(entry.getKey(), value);
					break;
				}
			}
		}

		String contextId = AdeContextualAggregatedRecord.getAggregatedFeatureContextId(smartRecordContext);
		return contextIdToSmartRecordMap.computeIfAbsent(contextId, key ->
				new SmartRecord(timeRange, key, smartRecordConf.getName(), fixedDurationStrategy, smartRecordContext));
	}

	private boolean doesAggregationRecordAlreadyExist(
			AdeAggregationRecord newAggregationRecord, List<SmartAggregationRecord> existingAggregationRecords) {

		return existingAggregationRecords.stream()
				.map(SmartAggregationRecord::getAggregationRecord)
				.map(AdeAggregationRecord::getFeatureName)
				.anyMatch(featureName -> featureName.equals(newAggregationRecord.getFeatureName()));
	}

	private boolean doesAggregationRecordPassThreshold(AdeAggregationRecord aggregationRecord) {
		if (threshold != null) {
			if (aggregationRecord.getClass().equals(AdeAggregationRecord.class)) {
				return aggregationRecord.getFeatureValue() > threshold;
			} else if (aggregationRecord.getClass().equals(ScoredFeatureAggregationRecord.class)) {
				return ((ScoredFeatureAggregationRecord)aggregationRecord).getScore() > threshold;
			}
		}

		return true;
	}
}
