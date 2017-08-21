package fortscale.smart;

import fortscale.smart.record.conf.SmartRecordConf;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.SmartRecord;

import java.util.*;

/**
 * @author Lior Govrin
 */
public class SmartRecordAggregator {
	private SmartRecordConf smartRecordConf;
	private FixedDurationStrategy fixedDurationStrategy;
	private TimeRange timeRange;
	private Map<String, SmartRecord> contextIdToSmartRecordMap;

	public SmartRecordAggregator(
			SmartRecordConf smartRecordConf,
			FixedDurationStrategy fixedDurationStrategy,
			TimeRange timeRange) {

		this.smartRecordConf = smartRecordConf;
		this.fixedDurationStrategy = fixedDurationStrategy;
		this.timeRange = timeRange;
		this.contextIdToSmartRecordMap = new HashMap<>();
	}

	public void updateSmartRecords(Collection<AdeAggregationRecord> newAggregationRecords) {
		for (AdeAggregationRecord newAggregationRecord : newAggregationRecords) {
			String contextId = newAggregationRecord.getContextId();
			ensureSmartRecordOfContextIdExists(contextId);
			SmartRecord smartRecord = contextIdToSmartRecordMap.get(contextId);
			List<AdeAggregationRecord> existingAggregationRecords = smartRecord.getAggregationRecords();
			if (existingAggregationRecords == null) existingAggregationRecords = new LinkedList<>();
			existingAggregationRecords.add(newAggregationRecord);
		}
	}

	public Collection<SmartRecord> getSmartRecords() {
		return contextIdToSmartRecordMap.values();
	}

	private void ensureSmartRecordOfContextIdExists(String contextId) {
		if (!contextIdToSmartRecordMap.containsKey(contextId)) {
			contextIdToSmartRecordMap.put(contextId, new SmartRecord(
					timeRange, contextId, smartRecordConf.getName(), fixedDurationStrategy
			));
		}
	}
}
