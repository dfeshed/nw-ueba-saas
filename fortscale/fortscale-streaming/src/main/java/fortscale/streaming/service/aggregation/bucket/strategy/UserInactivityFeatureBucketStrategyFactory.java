package fortscale.streaming.service.aggregation.bucket.strategy;

import com.fasterxml.jackson.databind.JsonMappingException;
import fortscale.streaming.service.aggregation.bucket.strategy.samza.FeatureBucketStrategyFactorySamza;
import fortscale.utils.ConversionUtils;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UserInactivityFeatureBucketStrategyFactory implements FeatureBucketStrategyFactorySamza {
	public static final String STRATEGY_TYPE = "user_inactivity";

	private static final String DATA_SOURCES_JSON_PARAM_FIELD_NAME = "dataSources";
	private static final String INACTIVITY_DURATION_IN_MINUTES_JSON_PARAM_FIELD_NAME = "inactivityDurationInMinutes";
	private static final String END_TIME_DELTA_IN_MINUTES_JSON_PARAM_FIELD_NAME = "endTimeDeltaInMinutes";

	private static final String DATA_SOURCES_FIELD_ERROR_MSG = String.format("Field '%s' must contain a non-empty list of data sources", DATA_SOURCES_JSON_PARAM_FIELD_NAME);
	private static final String INACTIVITY_DURATION_IN_MINUTES_FIELD_ERROR_MSG = String.format("Params must contain field '%s' with a valid long value", INACTIVITY_DURATION_IN_MINUTES_JSON_PARAM_FIELD_NAME);
	private static final String END_TIME_DELTA_IN_MINUTES_FIELD_ERROR_MSG = String.format("Params must contain field '%s' with a valid long value", END_TIME_DELTA_IN_MINUTES_JSON_PARAM_FIELD_NAME);

	private List<UserInactivityFeatureBucketStrategy> featureBucketStrategies = new ArrayList<>();

	@Override
	public void setStrategyStore(FeatureBucketStrategyStore featureBucketStrategyStore) {
		for (UserInactivityFeatureBucketStrategy featureBucketStrategy : featureBucketStrategies) {
			featureBucketStrategy.setFeatureBucketStrategyStore(featureBucketStrategyStore);
		}
	}

	@Override
	public FeatureBucketStrategy createFeatureBucketStrategy(StrategyJson strategyJson) throws JsonMappingException {
		// Get Inactivity strategy parameters
		JSONObject params = strategyJson.getParams();

		// Get json array of data sources
		JSONArray dataSourcesJsonArray = (JSONArray)params.get(DATA_SOURCES_JSON_PARAM_FIELD_NAME);
		Assert.notEmpty(dataSourcesJsonArray, DATA_SOURCES_FIELD_ERROR_MSG);

		// Iterate the data sources in the json array and add them to a new list
		Iterator<Object> dataSourcesIterator = dataSourcesJsonArray.iterator();
		List<String> dataSources = new ArrayList<>();

		while (dataSourcesIterator.hasNext()) {
			String dataSource = ConversionUtils.convertToString(dataSourcesIterator.next());
			Assert.isTrue(StringUtils.isNotBlank(dataSource), "Data source name cannot be blank");
			dataSources.add(dataSource);
		}

		// Get inactivity duration in minutes
		Long inactivityDurationInMinutes = ConversionUtils.convertToLong(params.get(INACTIVITY_DURATION_IN_MINUTES_JSON_PARAM_FIELD_NAME));
		Assert.notNull(inactivityDurationInMinutes, INACTIVITY_DURATION_IN_MINUTES_FIELD_ERROR_MSG);

		// Get end time delta in minutes
		Long endTimeDeltaInMinutes = ConversionUtils.convertToLong(params.get(END_TIME_DELTA_IN_MINUTES_JSON_PARAM_FIELD_NAME));
		Assert.notNull(endTimeDeltaInMinutes, END_TIME_DELTA_IN_MINUTES_FIELD_ERROR_MSG);

		UserInactivityFeatureBucketStrategy featureBucketStrategy = new UserInactivityFeatureBucketStrategy(strategyJson.getName(), dataSources, inactivityDurationInMinutes, endTimeDeltaInMinutes);
		featureBucketStrategies.add(featureBucketStrategy);
		return featureBucketStrategy;
	}
}
