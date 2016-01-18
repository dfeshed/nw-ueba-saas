package fortscale.aggregation.feature.bucket.strategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.common.event.Event;
import fortscale.utils.ConversionUtils;

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
	private String stratetgyContextIdPrefix;
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
		this.stratetgyContextIdPrefix = buildContextIdPrefix();
	}

	public void setFeatureBucketStrategyStore(FeatureBucketStrategyStore featureBucketStrategyStore) {
		Assert.notNull(featureBucketStrategyStore);
		this.featureBucketStrategyStore = featureBucketStrategyStore;
	}

	@Override
	public FeatureBucketStrategyData update(Event event) {
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
					notifyListeners(username, featureBucketStrategyData);
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
	public List<FeatureBucketStrategyData> getFeatureBucketStrategyData(FeatureBucketConf featureBucketConf, Event event, long epochtimeInSec) {
		String username = (String)event.get(usernameFieldName);
		return getFeatureBucketStrategyData(featureBucketConf, username, epochtimeInSec);
	}

	private List<FeatureBucketStrategyData> getFeatureBucketStrategyData(FeatureBucketConf featureBucketConf, String username, long epochtimeInSec) {
		List<FeatureBucketStrategyData> strategyDataList = new ArrayList<>();

		if (StringUtils.isNotBlank(username)) {
			String strategyContextId = getStrategyContextId(username);
			FeatureBucketStrategyData strategyData = featureBucketStrategyStore.getLatestFeatureBucketStrategyData(strategyContextId, epochtimeInSec);

			if (strategyData != null) {
				strategyDataList.add(strategyData);
			}
		}

		return strategyDataList;
	}

	private String buildContextIdPrefix() {
		List<String> strategyContextIdParts = new ArrayList<>();

		strategyContextIdParts.add(AbstractUserInactivityFeatureBucketStrategyFactory.STRATEGY_TYPE);
		strategyContextIdParts.addAll(dataSources);
		strategyContextIdParts.add(Long.toString(inactivityDurationInMinutes));

		return StringUtils.join(strategyContextIdParts, STRATEGY_CONTEXT_ID_SEPARATOR);
	}


	private String getStrategyContextId(String username) {
		List<String> strategyContextIdParts = new ArrayList<>();

		strategyContextIdParts.add(stratetgyContextIdPrefix);
		strategyContextIdParts.add(username);

		return StringUtils.join(strategyContextIdParts, STRATEGY_CONTEXT_ID_SEPARATOR);
	}

	private String getUserNameFromStrategyId(String strategyId) throws IllegalArgumentException{
		return	getStrategyParts(strategyId)[0];
	}

	/**
	 *
	 * @param strategyId the strategy ID to parse
	 * @return an array that contains:
	 *     String[0] - user name
	 *     String[1] - start time
	 *     String[2] - strategyContextId
	 * @throws IllegalArgumentException
	 */
	private String[] getStrategyParts(String strategyId) throws IllegalArgumentException{
		Assert.notNull(strategyId);
		String[] strings = new String[3];
		try {

			String prefix = strategyId.substring(0, stratetgyContextIdPrefix.length());

			// User
			int lastIndexOfSeperator = strategyId.lastIndexOf(STRATEGY_CONTEXT_ID_SEPARATOR);
			strings[0] = strategyId.substring(stratetgyContextIdPrefix.length()+1, lastIndexOfSeperator);

			// Start Time
			strings[1] = strategyId.substring(lastIndexOfSeperator+1);

			// Strategy Context ID
			strings[2] = new StringBuilder(prefix).append(STRATEGY_CONTEXT_ID_SEPARATOR).append(strings[0]).toString();

		} catch (Exception e) {
			throw new IllegalArgumentException(String.format("strategyId parameter does not match strategy ID format: %s", strategyId), e);
		}

		if(!strings[2].equals(getStrategyContextId(strings[0]))) {
			throw new IllegalArgumentException(String.format("strategyId parameter does not match strategy ID format: %s", strategyId));
		}

		return strings;
	}

	/**
	 * @param strategyId
	 * @return the strategy context of the given startegyId
	 * @throws IllegalArgumentException
	 */
	@Override
	public String getStrategyContextIdFromStrategyId(String strategyId) throws IllegalArgumentException{
		return getStrategyParts(strategyId)[2];
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
	 * @param strategyId
	 * @param startAfterEpochtimeInSeconds
	 */
	@Override
	public FeatureBucketStrategyData getNextBucketStrategyData(FeatureBucketConf bucketConf, String strategyId, long startAfterEpochtimeInSeconds)
			throws IllegalArgumentException{
		String username = getUserNameFromStrategyId(strategyId);
		List<FeatureBucketStrategyData> strategyDatas = getFeatureBucketStrategyData(bucketConf, username, startAfterEpochtimeInSeconds + 1);
		return strategyDatas.size()>0 ? strategyDatas.get(0) : null;
	}

	/**
	 * Register the listener to be called when a new strategy data (a.k.a 'bucket tick') is created for the given context and
	 * which its start time is after the given startAfterEpochtimeInSeconds.
	 * @param bucketConf
	 * @param strategyId
	 * @param listener
	 * @param startAfterEpochtimeInSeconds
	 */
	@Override
	public void notifyWhenNextBucketEndTimeIsKnown(FeatureBucketConf bucketConf, String strategyId, NextBucketEndTimeListener listener, long startAfterEpochtimeInSeconds)
			throws IllegalArgumentException{
		Assert.notNull(listener);
		String username = getUserNameFromStrategyId(strategyId);
		List<NextBucketEndTimeListenerData> listeners = username2listenersListMap.get(username);
		if(listeners==null) {
			listeners = new ArrayList<>();
			username2listenersListMap.put(username, listeners);
		}
		listeners.add(new NextBucketEndTimeListenerData(startAfterEpochtimeInSeconds, listener));
	}



	private void notifyListeners(String username, FeatureBucketStrategyData strategyData) {
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

