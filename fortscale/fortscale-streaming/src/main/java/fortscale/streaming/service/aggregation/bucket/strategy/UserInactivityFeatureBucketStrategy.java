package fortscale.streaming.service.aggregation.bucket.strategy;

import fortscale.streaming.service.aggregation.FeatureBucketConf;
import fortscale.utils.ConversionUtils;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import java.util.*;

@Configurable(preConstruction = true)
public class UserInactivityFeatureBucketStrategy implements FeatureBucketStrategy {
	private static final String STRATEGY_CONTEXT_ID_SEPARATOR = "_";

	@Value("${impala.table.fields.data.source}")
	private String dataSourceFieldName;
	@Value("${impala.table.fields.normalized.username}")
	private String usernameFieldName;
	@Value("${impala.table.fields.epochtime}")
	private String epochtimeFieldName;

	private FeatureBucketStrategyStore featureBucketStrategyStore;
	private String strategyName;
	private List<String> dataSources;
	private long inactivityDurationInMinutes;
	private long endTimeDeltaInMinutes;
	private Map<String, List<NextBucketEndTimeListenerData>> username2listenersListMap = new HashMap<>();

	public UserInactivityFeatureBucketStrategy(String strategyName, List<String> dataSources, long inactivityDurationInMinutes, long endTimeDeltaInMinutes) {
		// Validate input
		Assert.isTrue(StringUtils.isNotBlank(strategyName));
		Assert.notEmpty(dataSources);
		Assert.isTrue(inactivityDurationInMinutes > 0);
		Assert.isTrue(endTimeDeltaInMinutes >= 0);

		this.featureBucketStrategyStore = null;
		this.strategyName = strategyName;
		this.dataSources = dataSources;
		this.inactivityDurationInMinutes = inactivityDurationInMinutes;
		this.endTimeDeltaInMinutes = endTimeDeltaInMinutes;
	}

	public void setFeatureBucketStrategyStore(FeatureBucketStrategyStore featureBucketStrategyStore) {
		Assert.notNull(featureBucketStrategyStore);
		this.featureBucketStrategyStore = featureBucketStrategyStore;
	}

	@Override
	public FeatureBucketStrategyData update(JSONObject event) {
		// Get the event's data source
		String dataSource = ConversionUtils.convertToString(event.get(dataSourceFieldName));

		// Make sure this strategy contains the event's data source
		if (StringUtils.isNotBlank(dataSource) && dataSources.contains(dataSource)) {
			String username = ConversionUtils.convertToString(event.get(usernameFieldName));
			Long epochtime = ConversionUtils.convertToLong(event.get(epochtimeFieldName));

			if (StringUtils.isNotBlank(username) && epochtime != null) {
				String strategyContextId = getStrategyContextId(username);
				boolean isFeatureBucketStrategyDataCreated = false;
				boolean isFeatureBucketStrategyDataUpdated = false;
				FeatureBucketStrategyData featureBucketStrategyData = featureBucketStrategyStore.getLatestFeatureBucketStrategyData(strategyContextId, epochtime);

				// Case 1: Strategy doesn't exist - create a new one
				// Case 2: Strategy exists, but session has become inactive - create a new one
				if (featureBucketStrategyData == null || featureBucketStrategyData.getEndTime() + getInactivityDurationInSeconds() < epochtime) {
					featureBucketStrategyData = new FeatureBucketStrategyData(strategyContextId, strategyName, epochtime, epochtime + getEndTimeDeltaInSeconds());
					isFeatureBucketStrategyDataCreated = true;
				}
				// Case 3: Strategy exists and the incoming event updates its end time
				else if (featureBucketStrategyData.getEndTime() < epochtime) {
					featureBucketStrategyData.setEndTime(epochtime + getEndTimeDeltaInSeconds());
					isFeatureBucketStrategyDataUpdated = true;
				}

				featureBucketStrategyStore.storeFeatureBucketStrategyData(featureBucketStrategyData);

				if(isFeatureBucketStrategyDataCreated) {
					notifyListneres(username, featureBucketStrategyData);
				}

				if (isFeatureBucketStrategyDataUpdated || isFeatureBucketStrategyDataCreated) {
					return featureBucketStrategyData;
				} else {
					return null;
				}
			}
		}

		return null;
	}

	@Override
	public List<FeatureBucketStrategyData> getFeatureBucketStrategyData(FeatureBucketConf featureBucketConf, JSONObject event, long epochtimeInSec) {
		List<FeatureBucketStrategyData> strategyDataList = new ArrayList<>();

		String username = (String)event.get(usernameFieldName);
		if (StringUtils.isNotBlank(username)) {
			String strategyContextId = getStrategyContextId(username);
			FeatureBucketStrategyData strategyData = featureBucketStrategyStore.getLatestFeatureBucketStrategyData(strategyContextId, epochtimeInSec);

			if (strategyData != null) {
				strategyDataList.add(strategyData);
			}
		}

		return strategyDataList;
	}


	private String getStrategyContextId(String username) {
		List<String> strategyContextIdParts = new ArrayList<>();

		strategyContextIdParts.add(AbstractUserInactivityFeatureBucketStrategyFactory.STRATEGY_TYPE);
		strategyContextIdParts.addAll(dataSources);
		strategyContextIdParts.add(Long.toString(inactivityDurationInMinutes));
		strategyContextIdParts.add(username);

		return StringUtils.join(strategyContextIdParts, STRATEGY_CONTEXT_ID_SEPARATOR);
	}

	private long getInactivityDurationInSeconds() {
		return inactivityDurationInMinutes * 60;
	}

	private long getEndTimeDeltaInSeconds() {
		return endTimeDeltaInMinutes * 60;
	}

	/**
	 * Returns strategy data of the bucket tick which starts after the given startAfterEpochtimeInSeconds for the given context.
	 * @param bucketConf
	 * @param context
	 * @param startAfterEpochtimeInSeconds
	 */
	@Override
	public FeatureBucketStrategyData getNextBucketStrategyData(FeatureBucketConf bucketConf, Map<String, String> context, long startAfterEpochtimeInSeconds) {
		List<FeatureBucketStrategyData> strategyDatas = getFeatureBucketStrategyData(bucketConf, new JSONObject(context), startAfterEpochtimeInSeconds +1);
		return strategyDatas.size()>0 ? strategyDatas.get(0) : null;
	}

	/**
	 * Register the listener to be called when a new strategy data (a.k.a 'bucket tick') is created for the given context and
	 * which its start time is after the given startAfterEpochtimeInSeconds.
	 * @param bucketConf
	 * @param context
	 * @param listener
	 * @param startAfterEpochtimeInSeconds
	 */
	@Override
	public void notifyWhenNextBucketEndTimeIsKnown(FeatureBucketConf bucketConf, Map<String, String> context, NextBucketEndTimeListener listener, long startAfterEpochtimeInSeconds) {
		if(listener!=null) {
			String username = context.get(usernameFieldName);
			if(username==null || StringUtils.isEmpty(username)) {
				return;
			}
			List<NextBucketEndTimeListenerData> listeners = username2listenersListMap.get(username);
			if(listeners==null) {
				listeners = new ArrayList<>();
				username2listenersListMap.put(username, listeners);
			}
			listeners.add(new NextBucketEndTimeListenerData(startAfterEpochtimeInSeconds, listener));
		}
	}

	private void notifyListneres(String username, FeatureBucketStrategyData strategyData) {
		List<NextBucketEndTimeListenerData> listeners = username2listenersListMap.get(username);
		Collection<NextBucketEndTimeListenerData> listenerDatasToRemove = new ArrayList<>();
		if(listeners!=null) {
			for(NextBucketEndTimeListenerData listenerData : listeners) {
				if(listenerData.startAfterEpochtimeInSeconds < strategyData.getStartTime()) {
					listenerData.listener.nextBucketEndTimeUpdate(strategyData);
					listenerDatasToRemove.add(listenerData);
				}
			}
			listeners.removeAll(listenerDatasToRemove);
			if(listeners.isEmpty()) {
				username2listenersListMap.remove(username);
			}
		}
	}

	private class NextBucketEndTimeListenerData {
		long startAfterEpochtimeInSeconds;
		NextBucketEndTimeListener listener;

		private NextBucketEndTimeListenerData(long startAfterEpochtimeInSeconds, NextBucketEndTimeListener listener) {
			this.startAfterEpochtimeInSeconds = startAfterEpochtimeInSeconds;
			this.listener = listener;
		}

	}
}

