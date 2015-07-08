package fortscale.streaming.service.aggregation.bucket.strategy;

import com.fasterxml.jackson.databind.JsonMappingException;
import fortscale.streaming.service.aggregation.bucket.strategy.samza.FeatureBucketStrategyFactorySamza;
import fortscale.streaming.service.aggregation.bucket.strategy.samza.UserInactivityFeatureBucketStrategyFactorySamza;
import fortscale.streaming.service.aggregation.bucket.strategy.samza.VpnSessionFeatureBucketStrategyFactorySamza;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/bucketconf-context-test.xml" })
public class VpnSessionFeatureBucketStrategyTest {
	private static final String DEFAULT_STRATEGY_NAME = "vpn_session";
	private static final long MAX_SESSION_DURATION = 14400; // 4 hours

	@Test
	public void update_method_when_strategy_exists_and_inactive() throws Exception {
		String username = "user1";
		String srcMachine = "machine1";
		long epochtime = 1435737600;
		String status = "SUCCESS";
		FeatureBucketStrategyStore store = new FeatureBucketStrategyInMemoryStore();
		FeatureBucketStrategy strategy = createStrategyWithFactory(store, createDefaultParams());
		JSONObject event = createDataSourceEvent(username, srcMachine, epochtime, status);
		JSONObject eventAfterMaxSessionDuration = createDataSourceEvent(username, srcMachine, epochtime + MAX_SESSION_DURATION + 1, status);
		String strategyContextId = String.format("%s_%s_%s", VpnSessionFeatureBucketStrategyFactory.STRATEGY_TYPE, username, srcMachine);
		FeatureBucketStrategyData strategyData1 = new FeatureBucketStrategyData(strategyContextId, DEFAULT_STRATEGY_NAME, epochtime, epochtime + MAX_SESSION_DURATION);
		FeatureBucketStrategyData strategyData2 = new FeatureBucketStrategyData(strategyContextId, DEFAULT_STRATEGY_NAME, epochtime + MAX_SESSION_DURATION + 1, (epochtime + MAX_SESSION_DURATION + 1) + MAX_SESSION_DURATION);

		FeatureBucketStrategyData actual = strategy.update(event);
		assertEqualData(strategyData1, actual);

		actual = strategy.update(eventAfterMaxSessionDuration);
		assertEqualData(strategyData2, actual);
	}

	@Test
	public void update_method_when_strategy_exists_closed_event() throws Exception {
		String username = "user1";
		String srcMachine = "machine1";
		long epochtime = 1435737600;
		String openStatus = "SUCCESS";
		String closeStatus = "CLOSED";
		FeatureBucketStrategyStore store = new FeatureBucketStrategyInMemoryStore();
		FeatureBucketStrategy strategy = createStrategyWithFactory(store, createDefaultParams());
		JSONObject openEvent = createDataSourceEvent(username, srcMachine, epochtime, openStatus);
		JSONObject closeEvent = createDataSourceEvent(username, srcMachine, epochtime + 1, closeStatus);
		String strategyContextId = String.format("%s_%s_%s", VpnSessionFeatureBucketStrategyFactory.STRATEGY_TYPE, username, srcMachine);
		FeatureBucketStrategyData activeStrategyData = new FeatureBucketStrategyData(strategyContextId, DEFAULT_STRATEGY_NAME, epochtime, epochtime + MAX_SESSION_DURATION);

		FeatureBucketStrategyData actual = strategy.update(openEvent);
		FeatureBucketStrategyData expected = new FeatureBucketStrategyData(strategyContextId, DEFAULT_STRATEGY_NAME, epochtime, epochtime + MAX_SESSION_DURATION);
		assertEqualData(expected, actual);

		actual = strategy.update(closeEvent);
		expected = new FeatureBucketStrategyData(strategyContextId, DEFAULT_STRATEGY_NAME, epochtime, epochtime + 1);
		assertEqualData(expected, actual);
	}

	@Test
	public void update_method_when_strategy_exists_not_closed_event() throws Exception {
		String username = "user1";
		String srcMachine = "machine1";
		long epochtime = 1435737600;
		String status = "SUCCESS";
		FeatureBucketStrategyStore store = new FeatureBucketStrategyInMemoryStore();
		FeatureBucketStrategy strategy = createStrategyWithFactory(store, createDefaultParams());
		JSONObject event = createDataSourceEvent(username, srcMachine, epochtime, status);
		String strategyContextId = String.format("%s_%s_%s", VpnSessionFeatureBucketStrategyFactory.STRATEGY_TYPE, username, srcMachine);
		FeatureBucketStrategyData activeStrategyData = new FeatureBucketStrategyData(strategyContextId, DEFAULT_STRATEGY_NAME, epochtime, epochtime + MAX_SESSION_DURATION);

		FeatureBucketStrategyData actual = strategy.update(event); // Creating the session
		FeatureBucketStrategyData expected = new FeatureBucketStrategyData(strategyContextId, DEFAULT_STRATEGY_NAME, epochtime, epochtime + MAX_SESSION_DURATION);
		assertEqualData(expected, actual);

		event = createDataSourceEvent(username, srcMachine, epochtime + (MAX_SESSION_DURATION / 2), status);
		actual = strategy.update(event); // 2nd call for the same session. should return null
		Assert.assertNull(actual);
	}

	@Test
	public void get_strategy_data() throws Exception {
		String username = "user1";
		String srcMachine1 = "machine1";
		String srcMachine2 = "machine2";
		long epochtime = 1435737600;
		JSONObject success_event1 = createDataSourceEvent(username, srcMachine1, epochtime, "SUCCESS");
		JSONObject success_event2 = createDataSourceEvent(username, srcMachine2, epochtime, "SUCCESS");
		JSONObject close_event1 = createDataSourceEvent(username, srcMachine1, epochtime + (MAX_SESSION_DURATION / 2), "CLOSED");
		JSONObject close_event2 = createDataSourceEvent(username, srcMachine2, epochtime + (MAX_SESSION_DURATION / 2), "CLOSED");

		String strategyContextId1 = String.format("%s_%s_%s", VpnSessionFeatureBucketStrategyFactory.STRATEGY_TYPE, username, srcMachine1);
		String strategyContextId2 = String.format("%s_%s_%s", VpnSessionFeatureBucketStrategyFactory.STRATEGY_TYPE, username, srcMachine2);
		FeatureBucketStrategyStore store = new FeatureBucketStrategyInMemoryStore();
		FeatureBucketStrategy strategy = createStrategyWithFactory(store, createDefaultParams());
		FeatureBucketStrategyData activeStrategyData1 = new FeatureBucketStrategyData(strategyContextId1, DEFAULT_STRATEGY_NAME, epochtime, epochtime + MAX_SESSION_DURATION);
		FeatureBucketStrategyData activeStrategyData2 = new FeatureBucketStrategyData(strategyContextId2, DEFAULT_STRATEGY_NAME, epochtime, epochtime + MAX_SESSION_DURATION);

		List<FeatureBucketStrategyData> actual = strategy.getFeatureBucketStrategyData(null, close_event1, 123);
		Assert.assertEquals(0, actual.size());

		strategy.update(success_event1);
		actual = strategy.getFeatureBucketStrategyData(null, success_event1, epochtime + 1);
		Assert.assertEquals(1, actual.size());
		assertEqualData(activeStrategyData1, actual.get(0));

		strategy.update(success_event2);
		actual = strategy.getFeatureBucketStrategyData(null, success_event2, epochtime + 1);
		Assert.assertEquals(2, actual.size());
		assertEqualData(activeStrategyData1, actual.get(0));
		assertEqualData(activeStrategyData2, actual.get(1));

		strategy.update(close_event2);

		actual = strategy.getFeatureBucketStrategyData(null, close_event1, epochtime + (MAX_SESSION_DURATION / 2));
		Assert.assertEquals(1, actual.size());
		assertEqualData(activeStrategyData1, actual.get(0));

		strategy.update(close_event1);
		actual = strategy.getFeatureBucketStrategyData(null, close_event1, 123);
		Assert.assertEquals(0, actual.size());
	}

	/*
	 * Create and return a JSON object containing the
	 * default parameters defined at the beginning of the class.
	 */
	private JSONObject createDefaultParams() {
		JSONObject params = new JSONObject();
		params.put("maxSessionDuration", MAX_SESSION_DURATION);

		return params;
	}

	/*
	 * Create and return a new vpnsession feature bucket strategy
	 * with the given parameters, using the corresponding factory.
	 */
	private FeatureBucketStrategy createStrategyWithFactory(FeatureBucketStrategyStore store, JSONObject params)
			throws JsonMappingException {
		StrategyJson strategyJson = mock(StrategyJson.class);
		when(strategyJson.getParams()).thenReturn(params);
		when(strategyJson.getName()).thenReturn(DEFAULT_STRATEGY_NAME);

		FeatureBucketStrategyFactory factory = new VpnSessionFeatureBucketStrategyFactorySamza();
		VpnSessionFeatureBucketStrategy strategy = (VpnSessionFeatureBucketStrategy) factory.createFeatureBucketStrategy(strategyJson);
		strategy.setFeatureBucketStrategyStore(store);

		return strategy;
	}

	/*
	 * Create and return a new event of the default data source defined
	 */
	private JSONObject createDataSourceEvent(String username, String srcMachine, long epochtime, String status) {
		JSONObject event = new JSONObject();
		event.put("normalized_username", username);
		event.put("normalized_src_machine", srcMachine);
		event.put("date_time_unix", epochtime);
		event.put("status", status);
		event.put("data_source", DEFAULT_STRATEGY_NAME);

		return event;
	}

	/*
	 * Make sure that all the fields in the expected feature bucket strategy
	 * data are equal to those in the actual feature bucket strategy data.
	 */
	private void assertEqualData(FeatureBucketStrategyData expected, FeatureBucketStrategyData actual) {
		Assert.assertEquals(expected.getStrategyContextId(), actual.getStrategyContextId());
		Assert.assertEquals(expected.getStrategyName(), actual.getStrategyName());
		Assert.assertEquals(expected.getStartTime(), actual.getStartTime());
		Assert.assertEquals(expected.getEndTime(), actual.getEndTime());
		Assert.assertEquals(expected.getStrategyId(), actual.getStrategyId());
	}
}