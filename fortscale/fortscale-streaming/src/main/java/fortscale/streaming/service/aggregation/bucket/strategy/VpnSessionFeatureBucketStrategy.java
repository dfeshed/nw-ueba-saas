package fortscale.streaming.service.aggregation.bucket.strategy;

import fortscale.streaming.service.aggregation.FeatureBucketConf;
import fortscale.utils.ConversionUtils;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@Configurable(preConstruction = true)
public class VpnSessionFeatureBucketStrategy implements FeatureBucketStrategy {
	private static final String STRATEGY_CONTEXT_ID_SEPARATOR = "_";

	@Value("${impala.table.fields.data.source}")
	private String dataSourceFieldName;
	@Value("${impala.table.fields.username}")
	private String usernameFieldName;
	@Value("${impala.table.fields.src_machine}")
	private String srcMachineFieldName;
	@Value("${impala.table.fields.epochtime}")
	private String epochtimeFieldName;

	private FeatureBucketStrategyStore featureBucketStrategyStore;
	private String strategyName;
	private List<String> dataSources;
	private long maxSessionDuration;

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
	public FeatureBucketStrategyData update(JSONObject event) {
		// Get the event's data source
		//String dataSource = ConversionUtils.convertToString(event.get(dataSourceFieldName));

		// Make sure this strategy contains the event's data source
		//if (StringUtils.isNotBlank(dataSource) && dataSources.contains(dataSource)) {
		String username = ConversionUtils.convertToString(event.get(usernameFieldName));
		String srcMachine = ConversionUtils.convertToString(event.get(srcMachineFieldName));
		Long epochtime = ConversionUtils.convertToLong(event.get(epochtimeFieldName));

		if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(srcMachine) && epochtime != null) {
			String strategyContextId = getStrategyContextId(username);
			FeatureBucketStrategyData featureBucketStrategyData = featureBucketStrategyStore.getLatestFeatureBucketStrategyData(strategyContextId, epochtime);

			// Case 1: Strategy doesn't exist - create a new one
			// Case 2: Strategy exists, but session has become inactive - create a new one
			if (featureBucketStrategyData == null || featureBucketStrategyData.getEndTime() + inactivityDurationInSeconds() < epochtime) {
				featureBucketStrategyData = new FeatureBucketStrategyData(strategyContextId, strategyName, epochtime, epochtime + endTimeDeltaInSeconds());
			}
			// Case 3: Strategy exists and the incoming event updates its end time
			else if (featureBucketStrategyData.getEndTime() < epochtime) {
				featureBucketStrategyData.setEndTime(epochtime + endTimeDeltaInSeconds());
			}

			featureBucketStrategyStore.storeFeatureBucketStrategyData(featureBucketStrategyData);
			return featureBucketStrategyData;
		}
		return null;
	}

	@Override
	public List<FeatureBucketStrategyData> getFeatureBucketStrategyData(FeatureBucketConf featureBucketConf, JSONObject event, long epochtimeInSec) {
		List<FeatureBucketStrategyData> strategyDataList = new ArrayList<>();

		String dataSource = ConversionUtils.convertToString(event.get(dataSourceFieldName));
		String username = ConversionUtils.convertToString(event.get(usernameFieldName));

		if (StringUtils.isNotBlank(dataSource) && dataSources.contains(dataSource) && StringUtils.isNotBlank(username)) {
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
		strategyContextIdParts.add(VpnSessionFeatureBucketStrategyFactory.STRATEGY_TYPE);
		strategyContextIdParts.add(username);
		return StringUtils.join(strategyContextIdParts, STRATEGY_CONTEXT_ID_SEPARATOR);
	}
}
