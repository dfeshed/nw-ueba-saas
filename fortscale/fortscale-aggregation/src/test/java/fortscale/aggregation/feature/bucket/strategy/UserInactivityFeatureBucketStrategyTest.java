package fortscale.aggregation.feature.bucket.strategy;

import com.fasterxml.jackson.databind.JsonMappingException;
import fortscale.common.event.EventMessage;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/bucketconf-context-test.xml"})
public class UserInactivityFeatureBucketStrategyTest {
	private static final String DEFAULT_STRATEGY_NAME = "user_inactivity_default_strategy";
	private static final String DEFAULT_DATA_SOURCE = "ssh";
	private static final long DEFAULT_INACTIVITY_DURATION_IN_MINUTES = 240; // 4 hours
	private static final long DEFAULT_END_TIME_DELTA_IN_SECONDS = 300; // 5 minutes

	@Test
	public void update_method_should_create_new_data_but_should_not_update_the_end_time_afterwards() throws Exception {
		FeatureBucketStrategyStore store = new FeatureBucketStrategyInMemoryStore(null);
		FeatureBucketStrategy strategy = createStrategyWithFactory(store, createDefaultParams());

		String username = "user1";
		String strategyContextId = String.format("%s_%s_%d_%s",
			AbstractUserInactivityFeatureBucketStrategyFactory.STRATEGY_TYPE, DEFAULT_DATA_SOURCE, DEFAULT_INACTIVITY_DURATION_IN_MINUTES, username);

		long epochtime = 1435737600;
		JSONObject event = createDefaultDataSourceEvent(username, epochtime);

//		when(store.getLatestFeatureBucketStrategyData(strategyContextId, epochtime)).thenReturn(null);
		FeatureBucketStrategyData actual = strategy.update(new EventMessage(event));

		FeatureBucketStrategyData expected = new FeatureBucketStrategyData(
			strategyContextId, DEFAULT_STRATEGY_NAME, epochtime, epochtime + DEFAULT_END_TIME_DELTA_IN_SECONDS);
		assertEqualData(expected, actual);

		// Add event within the end time delta
		epochtime += DEFAULT_END_TIME_DELTA_IN_SECONDS / 2;
		event = createDefaultDataSourceEvent(username, epochtime);

//		when(store.getLatestFeatureBucketStrategyData(strategyContextId, epochtime)).thenReturn(actual);
		actual = strategy.update(new EventMessage(event));

		Assert.assertNull(actual);
	}

	@Test
	public void getStrategyConextId_from_StrategyId() throws Exception{
		FeatureBucketStrategyStore store = new FeatureBucketStrategyInMemoryStore(null);
		FeatureBucketStrategy strategy = createStrategyWithFactory(store, createDefaultParams());

		String username = "user2";
		String strategyContextId = String.format("%s_%s_%d_%s",
				AbstractUserInactivityFeatureBucketStrategyFactory.STRATEGY_TYPE, DEFAULT_DATA_SOURCE, DEFAULT_INACTIVITY_DURATION_IN_MINUTES, username);

		long startEpochtime = 1435737600;
		JSONObject event = createDefaultDataSourceEvent(username, startEpochtime);

		FeatureBucketStrategyData actual = strategy.update(new EventMessage(event));
		String strategyId = actual.getStrategyId();
		String actualContextId = strategy.getStrategyContextIdFromStrategyId(strategyId);
		Assert.assertEquals(strategyContextId, actualContextId);

	}

	@Test
	public void update_method_should_create_new_data_and_update_the_end_time_afterwards() throws Exception {
		FeatureBucketStrategyStore store = new FeatureBucketStrategyInMemoryStore(null);
		FeatureBucketStrategy strategy = createStrategyWithFactory(store, createDefaultParams());

		String username = "user2";
		String strategyContextId = String.format("%s_%s_%d_%s",
			AbstractUserInactivityFeatureBucketStrategyFactory.STRATEGY_TYPE, DEFAULT_DATA_SOURCE, DEFAULT_INACTIVITY_DURATION_IN_MINUTES, username);

		long startEpochtime = 1435737600;
		JSONObject event = createDefaultDataSourceEvent(username, startEpochtime);

//		when(store.getLatestFeatureBucketStrategyData(strategyContextId, startEpochtime)).thenReturn(null);
		FeatureBucketStrategyData actual = strategy.update(new EventMessage(event));

		FeatureBucketStrategyData expected = new FeatureBucketStrategyData(
			strategyContextId, DEFAULT_STRATEGY_NAME, startEpochtime, startEpochtime + DEFAULT_END_TIME_DELTA_IN_SECONDS);
		assertEqualData(expected, actual);

		// Add event after the end time delta, but before the inactivity duration
		long epochtime = startEpochtime + ((DEFAULT_END_TIME_DELTA_IN_SECONDS + (DEFAULT_INACTIVITY_DURATION_IN_MINUTES * 60)) / 2);
		event = createDefaultDataSourceEvent(username, epochtime);

//		when(store.getLatestFeatureBucketStrategyData(strategyContextId, epochtime)).thenReturn(actual);
		actual = strategy.update(new EventMessage(event));

		expected = new FeatureBucketStrategyData(
			strategyContextId, DEFAULT_STRATEGY_NAME, startEpochtime, epochtime + DEFAULT_END_TIME_DELTA_IN_SECONDS);
		assertEqualData(expected, actual);
	}

	@Test
	public void update_method_should_create_new_data_twice() throws Exception {
		FeatureBucketStrategyStore store = new FeatureBucketStrategyInMemoryStore(null);
		FeatureBucketStrategy strategy = createStrategyWithFactory(store, createDefaultParams());

		String username = "user3";
		String strategyContextId = String.format("%s_%s_%d_%s",
			AbstractUserInactivityFeatureBucketStrategyFactory.STRATEGY_TYPE, DEFAULT_DATA_SOURCE, DEFAULT_INACTIVITY_DURATION_IN_MINUTES, username);

		long startEpochtime1 = 1435737600;
		JSONObject event = createDefaultDataSourceEvent(username, startEpochtime1);

//		when(store.getLatestFeatureBucketStrategyData(strategyContextId, startEpochtime1)).thenReturn(null);
		FeatureBucketStrategyData actual = strategy.update(new EventMessage(event));

		FeatureBucketStrategyData expected = new FeatureBucketStrategyData(
			strategyContextId, DEFAULT_STRATEGY_NAME, startEpochtime1, startEpochtime1 + DEFAULT_END_TIME_DELTA_IN_SECONDS);
		assertEqualData(expected, actual);

		// Add event after the inactivity duration
		long startEpochtime2 = startEpochtime1 + DEFAULT_END_TIME_DELTA_IN_SECONDS + (DEFAULT_INACTIVITY_DURATION_IN_MINUTES * 60) + 1;
		event = createDefaultDataSourceEvent(username, startEpochtime2);

//		when(store.getLatestFeatureBucketStrategyData(strategyContextId, startEpochtime2)).thenReturn(actual);
		actual = strategy.update(new EventMessage(event));

		expected = new FeatureBucketStrategyData(
			strategyContextId, DEFAULT_STRATEGY_NAME, startEpochtime2, startEpochtime2 + DEFAULT_END_TIME_DELTA_IN_SECONDS);
		assertEqualData(expected, actual);
	}

	/*
	 * Create and return a JSON object containing the
	 * default parameters defined at the beginning of the class.
	 */
	private JSONObject createDefaultParams() {
		JSONArray dataSources = new JSONArray();
		dataSources.add(DEFAULT_DATA_SOURCE);

		JSONObject params = new JSONObject();
		params.put("dataSources", dataSources);
		params.put("inactivityDurationInMinutes", DEFAULT_INACTIVITY_DURATION_IN_MINUTES);
		params.put("endTimeDeltaInMinutes", DEFAULT_END_TIME_DELTA_IN_SECONDS / 60);

		return params;
	}

	/*
	 * Create and return a new user inactivity feature bucket strategy
	 * with the given parameters, using the corresponding factory.
	 */
	private FeatureBucketStrategy createStrategyWithFactory(FeatureBucketStrategyStore store, JSONObject params) throws JsonMappingException {
		StrategyJson strategyJson = Mockito.mock(StrategyJson.class);
		Mockito.when(strategyJson.getParams()).thenReturn(params);
		Mockito.when(strategyJson.getName()).thenReturn(DEFAULT_STRATEGY_NAME);

		FeatureBucketStrategyFactory factory = new UserInactivityFeatureBucketStrategyFactoryForTest(store);
		UserInactivityFeatureBucketStrategy strategy = (UserInactivityFeatureBucketStrategy) factory.createFeatureBucketStrategy(strategyJson);

		return strategy;
	}

	/*
	 * Create and return a new event of the default data source defined
	 * at the beginning of the class, with the given username and epochtime.
	 */
	private JSONObject createDefaultDataSourceEvent(String username, long epochtime) {
		JSONObject event = new JSONObject();
		event.put("data_source", DEFAULT_DATA_SOURCE);
		event.put("normalized_username", username);
		event.put("date_time_unix", epochtime);

		return event;
	}

	/*
	 * Make sure that all the fields in the expected feature bucket strategy
	 * data are equal to those in the actual feature bucket strategy data.
	 */
	private void assertEqualData(FeatureBucketStrategyData expected, FeatureBucketStrategyData actual) {
		Assert.assertEquals(expected.getStrategyEventContextId(), actual.getStrategyEventContextId());
		Assert.assertEquals(expected.getStrategyName(), actual.getStrategyName());
		Assert.assertEquals(expected.getStartTime(), actual.getStartTime());
		Assert.assertEquals(expected.getEndTime(), actual.getEndTime());
		Assert.assertEquals(expected.getStrategyId(), actual.getStrategyId());
	}
}
