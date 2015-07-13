package fortscale.streaming.service.aggregation.bucket.strategy;

import fortscale.streaming.service.aggregation.FeatureBucketConf;
import fortscale.utils.ConversionUtils;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
	@Value("${impala.table.vpn.values.data.source}")
	private String vpnSessionDataSource;

	private FeatureBucketStrategyStore featureBucketStrategyStore;
	private String strategyName;
	private long maxSessionDuration;
	private HashMap<String, List<String>> openUserSessions; //username and source ip with open sessions

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
		if (StringUtils.isNotBlank(dataSource) && dataSource.equals(vpnSessionDataSource)) {
			String username = ConversionUtils.convertToString(event.get(usernameFieldName));
			String sourceIP = ConversionUtils.convertToString(event.get(sourceIpFieldName));
			Long epochtime = ConversionUtils.convertToLong(event.get(epochtimeFieldName));
			String status = ConversionUtils.convertToString(event.get(statusFieldName));

			if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(sourceIP) && epochtime != null) {
				String strategyContextId = getStrategyContextId(username, sourceIP);
				boolean isFeatureBucketStrategyDataCreatedOrUpdated = false;
				FeatureBucketStrategyData featureBucketStrategyData = featureBucketStrategyStore.getLatestFeatureBucketStrategyData(strategyContextId, epochtime);

				// Case 1: Strategy doesn't exist - create a new one
				// Case 2: Strategy exists, but session has become inactive - create a new one
				if (featureBucketStrategyData == null || featureBucketStrategyData.getEndTime() < epochtime) {
					RemoveClosedUserSessions(username, sourceIP);
					if (status.toLowerCase().equals(successValueName)) {
						featureBucketStrategyData = new FeatureBucketStrategyData(strategyContextId, strategyName, epochtime, epochtime + maxSessionDuration);
						AddOpenUserSessions(username, sourceIP);
						isFeatureBucketStrategyDataCreatedOrUpdated = true;
					}
				}
				// Case 3: Strategy exists and the incoming event status is closed
				else if (status.toLowerCase().equals(closedValueName)) {
					featureBucketStrategyData.setEndTime(epochtime);
					RemoveClosedUserSessions(username, sourceIP);
					isFeatureBucketStrategyDataCreatedOrUpdated = true;
				}
				// Case 4: Nothing to do if exists, still active and event is not a closed event

				featureBucketStrategyStore.storeFeatureBucketStrategyData(featureBucketStrategyData);
				if (isFeatureBucketStrategyDataCreatedOrUpdated) {
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

	private String getStrategyContextId(String username, String sourceIP) {
		List<String> strategyContextIdParts = new ArrayList<>();
		strategyContextIdParts.add(VpnSessionFeatureBucketStrategyFactory.STRATEGY_TYPE);
		strategyContextIdParts.add(username);
		strategyContextIdParts.add(sourceIP);
		return StringUtils.join(strategyContextIdParts, STRATEGY_CONTEXT_ID_SEPARATOR);
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
}
