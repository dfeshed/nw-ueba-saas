package fortscale.aggregation.feature.bucket.strategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.common.event.Event;
import fortscale.utils.ConversionUtils;

@Configurable(preConstruction = true)
public class VpnSessionFeatureBucketStrategy implements FeatureBucketStrategy {
	private static final String STRATEGY_CONTEXT_ID_SEPARATOR = "_";
	public static final String USERNAME_CONTEXT_FIELD_NAME = "username";
	public static final String SOURCE_IP_CONTEXT_FIELD_NAME = "source_ip";

	@Value("${impala.table.fields.normalized.username}")
	private String usernameFieldName;
	@Value("${impala.table.fields.source_ip}")
	private String sourceIpFieldName;
	@Value("${impala.table.fields.epochtime}")
	private String epochtimeFieldName;
	@Value("${impala.table.vpn.fields.status}")
	private String statusFieldName;
	@Value("${impala.table.vpn.values.status.success}")
	private String successValueName;
	@Value("${impala.table.vpn.values.status.closed}")
	private String closedValueName;
	@Value("${impala.table.fields.data.source}")
	private String dataSourceFieldName;
	@Value("${impala.table.vpn.values.data.sources}")
	private String[] vpnDataSources;

	private FeatureBucketStrategyStore featureBucketStrategyStore;
	private String strategyName;
	private long maxSessionDuration;
	private Map<UserNameAndSourceIp, List<NextBucketEndTimeListenerData>> usernameAndSourceIp2listenersListMap = new HashMap<>();

	public VpnSessionFeatureBucketStrategy(String strategyName, long maxSessionDuration) {
		// Validate input
		Assert.isTrue(StringUtils.isNotBlank(strategyName));
		Assert.isTrue(maxSessionDuration > 0);

		this.featureBucketStrategyStore = null;
		this.strategyName = strategyName;
		this.maxSessionDuration = maxSessionDuration;
	}

	public void setFeatureBucketStrategyStore(FeatureBucketStrategyStore featureBucketStrategyStore) {
		Assert.notNull(featureBucketStrategyStore);
		this.featureBucketStrategyStore = featureBucketStrategyStore;
	}

	@Override
	public FeatureBucketStrategyData update(Event event) {
		// Get the event's data source
		String dataSource = ConversionUtils.convertToString(event.get(dataSourceFieldName));
		if (StringUtils.isNotBlank(dataSource) && containsCaseInsensitive(dataSource, vpnDataSources)) {
			String username = ConversionUtils.convertToString(event.get(usernameFieldName));
			String sourceIP = ConversionUtils.convertToString(event.get(sourceIpFieldName));
			Long epochtime = ConversionUtils.convertToLong(event.get(epochtimeFieldName));
			String status = ConversionUtils.convertToString(event.get(statusFieldName));

			if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(sourceIP) && epochtime != null) {
				String strategyEventContextId = getStrategyEventContextId(username);
				boolean isFeatureBucketStrategyDataCreated = false;
				boolean isFeatureBucketStrategyDataUpdated = false;
				Map<String, String> contextMap = new HashMap<>();
				contextMap.put(SOURCE_IP_CONTEXT_FIELD_NAME, sourceIP);
				contextMap.put(USERNAME_CONTEXT_FIELD_NAME, username);
				FeatureBucketStrategyData featureBucketStrategyData = featureBucketStrategyStore.getLatestFeatureBucketStrategyData(strategyEventContextId, epochtime, contextMap);

				// Case 1: Strategy doesn't exist - create a new one
				// Case 2: Strategy exists, but session has become inactive - create a new one
				if (featureBucketStrategyData == null || featureBucketStrategyData.getEndTime() <= epochtime) {
					if (status.equalsIgnoreCase(successValueName)) {
						featureBucketStrategyData = new FeatureBucketStrategyData(strategyEventContextId, strategyName, epochtime, epochtime + maxSessionDuration, contextMap);
						isFeatureBucketStrategyDataCreated = true;
					}
				}
				// Case 3: Strategy exists and the incoming event status is closed
				else if (status.equalsIgnoreCase(closedValueName)) {
					//since the end time is part of the session we add one second.
					featureBucketStrategyData.setEndTime(epochtime+1);
					isFeatureBucketStrategyDataUpdated = true;
				}
				// Case 4: Nothing to do if exists, still active and event is not a closed event


				if(isFeatureBucketStrategyDataCreated) {
					notifyListeners(username, sourceIP, featureBucketStrategyData);
				}

				if (isFeatureBucketStrategyDataUpdated || isFeatureBucketStrategyDataCreated) {
					featureBucketStrategyStore.storeFeatureBucketStrategyData(featureBucketStrategyData);
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
		Assert.notNull(event);
		String username = ConversionUtils.convertToString(event.get(usernameFieldName));

		String strategyEventContextId = getStrategyEventContextId(username);
		return featureBucketStrategyStore.getFeatureBucketStrategyDataContainsEventTime(strategyEventContextId, epochtimeInSec);
	}

	/**
	 * Returns strategy data of the bucket tick which starts after the given startAfterEpochtimeInSeconds for the given context.
	 *
	 * @param bucketConf
	 * @param strategyId
	 * @param startAfterEpochtimeInSeconds
	 */
	@Override
	public FeatureBucketStrategyData getNextBucketStrategyData(FeatureBucketConf bucketConf, String strategyId, long startAfterEpochtimeInSeconds) throws IllegalArgumentException{
		UserNameAndSourceIp userNameAndSourceIp = getUserNameAndSourceIpFromStrategyId(strategyId);

		String strategyEventContextId = getStrategyEventContextId(userNameAndSourceIp.username);
		Map<String, String> contextMap = new HashMap<>();
		contextMap.put(SOURCE_IP_CONTEXT_FIELD_NAME, userNameAndSourceIp.sourceIp);
		contextMap.put(USERNAME_CONTEXT_FIELD_NAME, userNameAndSourceIp.username);
		return featureBucketStrategyStore.getLatestFeatureBucketStrategyData(strategyEventContextId, startAfterEpochtimeInSeconds + 1, contextMap);
	}

	/**
	 * Register the listener to be called when a new strategy data (a.k.a 'bucket tick') is created for the given context and
	 * which its start time is after the given startAfterEpochtimeInSeconds.
	 *
	 * @param bucketConf
	 * @param strategyId
	 * @param listener
	 * @param startAfterEpochtimeInSeconds
	 */
	@Override
	public void notifyWhenNextBucketEndTimeIsKnown(FeatureBucketConf bucketConf, String strategyId, NextBucketEndTimeListener listener, long startAfterEpochtimeInSeconds)
			throws IllegalArgumentException{
		Assert.notNull(listener);
		Assert.isTrue(startAfterEpochtimeInSeconds>946684800); // Sat, 01 Jan 2000 00:00:00 GMT

		UserNameAndSourceIp userNameAndSourceIp = getUserNameAndSourceIpFromStrategyId(strategyId);

		List<NextBucketEndTimeListenerData> listeners = usernameAndSourceIp2listenersListMap.get(userNameAndSourceIp);
		if(listeners==null) {
			listeners = new ArrayList<>();
			usernameAndSourceIp2listenersListMap.put(userNameAndSourceIp, listeners);
		}
		listeners.add(new NextBucketEndTimeListenerData(startAfterEpochtimeInSeconds, listener));
	}


	private void notifyListeners(String username, String sourceIpAddress, FeatureBucketStrategyData strategyData) {
		UserNameAndSourceIp userNameAndSourceIp = new UserNameAndSourceIp(username, sourceIpAddress);
		List<NextBucketEndTimeListenerData> listeners = usernameAndSourceIp2listenersListMap.get(userNameAndSourceIp);
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
				usernameAndSourceIp2listenersListMap.remove(userNameAndSourceIp);
			}
		}
	}

	private String getStrategyEventContextId(String username) {
		List<String> strategyContextIdParts = new ArrayList<>();
		strategyContextIdParts.add(VpnSessionFeatureBucketStrategyFactory.STRATEGY_TYPE);
		strategyContextIdParts.add(username);
		return StringUtils.join(strategyContextIdParts, STRATEGY_CONTEXT_ID_SEPARATOR);
	}

	private String[] getStrategyIdParts(String strategyId) throws IllegalArgumentException{
		Assert.notNull(strategyId);
		String[] strings = new String[3];
		strings[0] = strategyId.substring(0,VpnSessionFeatureBucketStrategyFactory.STRATEGY_TYPE.length());
		int lastIndexOfSeperator = strategyId.lastIndexOf(STRATEGY_CONTEXT_ID_SEPARATOR);
		strings[2] = strategyId.substring(lastIndexOfSeperator+1); // startTime
		strings[1] = strategyId.substring(VpnSessionFeatureBucketStrategyFactory.STRATEGY_TYPE.length()+1, lastIndexOfSeperator);
		if(!strings[0].equals(VpnSessionFeatureBucketStrategyFactory.STRATEGY_TYPE) ) {
			throw new IllegalArgumentException(String.format("strategyId parameter does not match strategy ID format: %s", strategyId));
		}
		try {
			Long.parseLong(strings[2]); // Validating that the forth element is long (startTime), getting exception if not.
			return strings;
		} catch (Exception e) {
			throw new IllegalArgumentException(String.format("strategyId parameter does not match strategy ID format: %s", strategyId));
		}
	}

	private UserNameAndSourceIp getUserNameAndSourceIpFromStrategyId(String strategyId) throws IllegalArgumentException{
		String[] strategyIdParts = getStrategyIdParts(strategyId);
		return new UserNameAndSourceIp(strategyIdParts[1],strategyIdParts[2]);
	}

	/**
	 * @param strategyId
	 * @return the strategy context of the given startegyId
	 * @throws IllegalArgumentException
	 */
	@Override
	public String getStrategyContextIdFromStrategyId(String strategyId) throws IllegalArgumentException {
		String[] strategyIdParts = getStrategyIdParts(strategyId);
		return StringUtils.join(strategyIdParts, STRATEGY_CONTEXT_ID_SEPARATOR, 0, 2);
	}

	
	private boolean containsCaseInsensitive(String str, String[] arr){
		for (String arr_string : arr){
			if (arr_string.equalsIgnoreCase(str)){
				return true;
			}
		}
		return false;
	}

	private class NextBucketEndTimeListenerData {
		long startAfterEpochtimeInSeconds;
		NextBucketEndTimeListener listener;

		private NextBucketEndTimeListenerData(long startAfterEpochtimeInSeconds, NextBucketEndTimeListener listener) {
			this.startAfterEpochtimeInSeconds = startAfterEpochtimeInSeconds;
			this.listener = listener;
		}

	}

	private class UserNameAndSourceIp{
		String username;
		String sourceIp;
		private UserNameAndSourceIp(String username, String sourceIp) {
			this.username = username;
			this.sourceIp = sourceIp;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (obj == this) {
				return true;
			}
			if (obj.getClass() != getClass()) {
				return false;
			}

			UserNameAndSourceIp that = (UserNameAndSourceIp) obj;
			
			return new EqualsBuilder().append(this.username, that.username).append(this.sourceIp, that.sourceIp).isEquals();
		}

		@Override
		public int hashCode() {
			return username.hashCode();
		}
	}
}
