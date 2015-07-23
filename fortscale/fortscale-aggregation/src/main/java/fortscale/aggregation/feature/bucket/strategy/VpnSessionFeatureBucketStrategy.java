package fortscale.aggregation.feature.bucket.strategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.utils.ConversionUtils;

@Configurable(preConstruction = true)
public class VpnSessionFeatureBucketStrategy implements FeatureBucketStrategy {
	private static final String STRATEGY_CONTEXT_ID_SEPARATOR = "_";

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
	private HashMap<String, List<String>> openUserSessions; //username and source ip with open sessions
	private Map<UserNameAndSourceIp, List<NextBucketEndTimeListenerData>> usernameAndSourceIp2listenersListMap = new HashMap<>();

	public VpnSessionFeatureBucketStrategy(String strategyName, long maxSessionDuration) {
		// Validate input
		Assert.isTrue(StringUtils.isNotBlank(strategyName));
		Assert.isTrue(maxSessionDuration > 0);

		this.featureBucketStrategyStore = null;
		this.strategyName = strategyName;
		this.maxSessionDuration = maxSessionDuration;
		this.openUserSessions = new HashMap<String, List<String>>();
	}

	public void setFeatureBucketStrategyStore(FeatureBucketStrategyStore featureBucketStrategyStore) {
		Assert.notNull(featureBucketStrategyStore);
		this.featureBucketStrategyStore = featureBucketStrategyStore;
	}

	@Override
	public FeatureBucketStrategyData update(JSONObject event) {
		// Get the event's data source
		String dataSource = ConversionUtils.convertToString(event.get(dataSourceFieldName));
		if (StringUtils.isNotBlank(dataSource) && containsCaseInsensitive(dataSource, vpnDataSources)) {
			String username = ConversionUtils.convertToString(event.get(usernameFieldName));
			String sourceIP = ConversionUtils.convertToString(event.get(sourceIpFieldName));
			Long epochtime = ConversionUtils.convertToLong(event.get(epochtimeFieldName));
			String status = ConversionUtils.convertToString(event.get(statusFieldName));

			if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(sourceIP) && epochtime != null) {
				String strategyContextId = getStrategyContextId(username, sourceIP);
				boolean isFeatureBucketStrategyDataCreated = false;
				boolean isFeatureBucketStrategyDataUpdated = false;
				FeatureBucketStrategyData featureBucketStrategyData = featureBucketStrategyStore.getLatestFeatureBucketStrategyData(strategyContextId, epochtime);

				// Case 1: Strategy doesn't exist - create a new one
				// Case 2: Strategy exists, but session has become inactive - create a new one
				if (featureBucketStrategyData == null || featureBucketStrategyData.getEndTime() < epochtime) {
					RemoveClosedUserSessions(username, sourceIP);
					if (status.equalsIgnoreCase(successValueName)) {
						featureBucketStrategyData = new FeatureBucketStrategyData(strategyContextId, strategyName, epochtime, epochtime + maxSessionDuration);
						AddOpenUserSessions(username, sourceIP);
						isFeatureBucketStrategyDataCreated = true;
					}
				}
				// Case 3: Strategy exists and the incoming event status is closed
				else if (status.equalsIgnoreCase(closedValueName)) {
					featureBucketStrategyData.setEndTime(epochtime);
					RemoveClosedUserSessions(username, sourceIP);
					isFeatureBucketStrategyDataUpdated = true;
				}
				// Case 4: Nothing to do if exists, still active and event is not a closed event

				featureBucketStrategyStore.storeFeatureBucketStrategyData(featureBucketStrategyData);


				if(isFeatureBucketStrategyDataCreated) {
					notifyListeners(username, sourceIP, featureBucketStrategyData);
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
		Assert.notNull(event);
		List<FeatureBucketStrategyData> strategyDataList = new ArrayList<>();
		String username = ConversionUtils.convertToString(event.get(usernameFieldName));

		if (openUserSessions.containsKey(username)) {
			for (String sourceIp:openUserSessions.get(username)) {
				String strategyContextId = getStrategyContextId(username, sourceIp);
				strategyDataList.add(featureBucketStrategyStore.getLatestFeatureBucketStrategyData(strategyContextId, epochtimeInSec));
			}
		}

		return strategyDataList;
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
		FeatureBucketStrategyData strategyData = null;

		if (openUserSessions.containsKey(userNameAndSourceIp.username)) {
			for (String sourceIp:openUserSessions.get(userNameAndSourceIp.username)) {
				if(sourceIp.equals(userNameAndSourceIp.sourceIp)) {
					String strategyContextId = getStrategyContextId(userNameAndSourceIp.username, userNameAndSourceIp.sourceIp);
					strategyData = featureBucketStrategyStore.getLatestFeatureBucketStrategyData(strategyContextId, startAfterEpochtimeInSeconds + 1);
				}
			}
		}

		return strategyData;
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

	private String getStrategyContextId(String username, String sourceIP) {
		List<String> strategyContextIdParts = new ArrayList<>();
		strategyContextIdParts.add(VpnSessionFeatureBucketStrategyFactory.STRATEGY_TYPE);
		strategyContextIdParts.add(username);
		strategyContextIdParts.add(sourceIP);
		return StringUtils.join(strategyContextIdParts, STRATEGY_CONTEXT_ID_SEPARATOR);
	}


	private UserNameAndSourceIp getUserNameAndSourceIpFromStrategyId(String strategyId) throws IllegalArgumentException{
		Assert.notNull(strategyId);
		String[] strings = StringUtils.splitByWholeSeparator(strategyId, STRATEGY_CONTEXT_ID_SEPARATOR);
		if(strings.length !=4 || !strings[0].equals(VpnSessionFeatureBucketStrategyFactory.STRATEGY_TYPE) ) {
			throw new IllegalArgumentException(String.format("strategyId parameter does not match strategy ID format: %s", strategyId));
		}
		try {
			Long.parseLong(strings[3]); // Validating that the forth element is long (end time), getting exception if not.
			return new UserNameAndSourceIp(strings[1],strings[2]);
		} catch (Exception e) {
			throw new IllegalArgumentException(String.format("strategyId parameter does not match strategy ID format: %s", strategyId));
		}
	}

	private void AddOpenUserSessions(String username, String sourceIP) {
		if (!openUserSessions.containsKey(username)) {
			openUserSessions.put(username, new ArrayList<String>());
		}
			openUserSessions.get(username).add(sourceIP);
		}

	private void RemoveClosedUserSessions(String username, String sourceIP) {
		if (openUserSessions.containsKey(username)) {
			List<String> userSources = openUserSessions.get(username);
			userSources.remove(sourceIP);
			if (userSources.size() == 0) {
				openUserSessions.remove(username);
			}
		}
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
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			UserNameAndSourceIp that = (UserNameAndSourceIp) o;

			if (username != null ? !username.equals(that.username) : that.username != null) return false;
			return !(sourceIp != null ? !sourceIp.equals(that.sourceIp) : that.sourceIp != null);

		}

		@Override
		public int hashCode() {
			int result = username != null ? username.hashCode() : 0;
			result = 31 * result + (sourceIp != null ? sourceIp.hashCode() : 0);
			return result;
		}
	}
}
