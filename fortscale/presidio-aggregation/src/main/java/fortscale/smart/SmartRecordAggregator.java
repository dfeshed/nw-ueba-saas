package fortscale.smart;

import fortscale.smart.record.conf.SmartRecordConf;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.ScoredFeatureAggregationRecord;
import presidio.ade.domain.record.aggregated.SmartRecord;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
				List<AdeAggregationRecord> existingAggregationRecords = smartRecord.getAggregationRecords();

				if (existingAggregationRecords == null) {
					existingAggregationRecords = new LinkedList<>();
					smartRecord.setAggregationRecords(existingAggregationRecords);
				}

				if (doesAggregationRecordAlreadyExist(newAggregationRecord, existingAggregationRecords)) {
					logger.error("Context ID {} already has an aggregation record of type {} between {}. " +
							"Ignoring new aggregation record of same type between same time range.",
							smartRecord.getContextId(), newAggregationRecord.getFeatureName(), timeRange);
				} else if (doesAggregationRecordPassThreshold(newAggregationRecord)) {
					existingAggregationRecords.add(newAggregationRecord);
				} else {
					logger.debug("Discarding aggregation record of type {} between {}, " +
							"because it did not pass the threshold {}. Context ID = {}.",
							newAggregationRecord.getFeatureName(), timeRange, threshold, smartRecord.getContextId());
				}
			} else {
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
		String contextId = aggregationRecord.getContextId();
		return contextIdToSmartRecordMap.computeIfAbsent(contextId, key -> {
			Map<String, String> aggregationRecordContext = aggregationRecord.getContext();
			Map<String, String> context = smartRecordConf.getContexts().stream()
					.collect(Collectors.toMap(Function.identity(), aggregationRecordContext::get));
			return new SmartRecord(timeRange, contextId, smartRecordConf.getName(), fixedDurationStrategy, context);
		});
	}

	private boolean doesAggregationRecordAlreadyExist(
			AdeAggregationRecord newAggregationRecord, List<AdeAggregationRecord> existingAggregationRecords) {

		return existingAggregationRecords.stream()
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
