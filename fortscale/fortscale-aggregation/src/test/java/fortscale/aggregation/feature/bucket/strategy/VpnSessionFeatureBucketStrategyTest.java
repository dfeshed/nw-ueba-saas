package fortscale.aggregation.feature.bucket.strategy;

import com.fasterxml.jackson.databind.JsonMappingException;
import fortscale.common.event.EventMessage;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		String sourceIp = "1.1.1.1";
		long epochtime = 1435737600;
		String status = "SUCCESS";
		FeatureBucketStrategyStore store = new FeatureBucketStrategyInMemoryStore();
		FeatureBucketStrategy strategy = createStrategyWithFactory(store, createDefaultParams());
		JSONObject event = createDataSourceEvent(username, sourceIp, epochtime, status);
		JSONObject eventAfterMaxSessionDuration = createDataSourceEvent(username, sourceIp, epochtime + MAX_SESSION_DURATION + 1, status);
		String strategyContextId = String.format("%s_%s", VpnSessionFeatureBucketStrategyFactory.STRATEGY_TYPE, username, sourceIp);
		FeatureBucketStrategyData strategyData1 = new FeatureBucketStrategyData(strategyContextId, DEFAULT_STRATEGY_NAME, epochtime, epochtime + MAX_SESSION_DURATION);
		FeatureBucketStrategyData strategyData2 = new FeatureBucketStrategyData(strategyContextId, DEFAULT_STRATEGY_NAME, epochtime + MAX_SESSION_DURATION + 1, (epochtime + MAX_SESSION_DURATION + 1) + MAX_SESSION_DURATION);

		FeatureBucketStrategyData actual = strategy.update(new EventMessage(event));
		assertEqualData(strategyData1, actual);

		actual = strategy.update(new EventMessage(eventAfterMaxSessionDuration));
		assertEqualData(strategyData2, actual);
	}

	@Test
	public void update_method_when_strategy_exists_closed_event() throws Exception {
		String username = "user1";
		String sourceIp = "1.1.1.1";
		long epochtime = 1435737600;
		String openStatus = "SUCCESS";
		String closeStatus = "CLOSED";
		FeatureBucketStrategyStore store = new FeatureBucketStrategyInMemoryStore();
		FeatureBucketStrategy strategy = createStrategyWithFactory(store, createDefaultParams());
		JSONObject openEvent = createDataSourceEvent(username, sourceIp, epochtime, openStatus);
		JSONObject closeEvent = createDataSourceEvent(username, sourceIp, epochtime + 1, closeStatus);
		String strategyContextId = String.format("%s_%s", VpnSessionFeatureBucketStrategyFactory.STRATEGY_TYPE, username, sourceIp);

		FeatureBucketStrategyData actual = strategy.update(new EventMessage(openEvent));
		FeatureBucketStrategyData expected = new FeatureBucketStrategyData(strategyContextId, DEFAULT_STRATEGY_NAME, epochtime, epochtime + MAX_SESSION_DURATION);
		assertEqualData(expected, actual);

		actual = strategy.update(new EventMessage(closeEvent));
		long expectedStartTime = epochtime;
		long expectedEndTime = epochtime + 2;
		expected = new FeatureBucketStrategyData(strategyContextId, DEFAULT_STRATEGY_NAME, expectedStartTime, expectedEndTime);
		assertEqualData(expected, actual);
	}

	@Test
	public void update_method_when_strategy_exists_not_closed_event() throws Exception {
		String username = "user1";
		String sourceIp = "1.1.1.1";
		long epochtime = 1435737600;
		String status = "SUCCESS";
		FeatureBucketStrategyStore store = new FeatureBucketStrategyInMemoryStore();
		FeatureBucketStrategy strategy = createStrategyWithFactory(store, createDefaultParams());
		JSONObject event = createDataSourceEvent(username, sourceIp, epochtime, status);
		String strategyContextId = String.format("%s_%s", VpnSessionFeatureBucketStrategyFactory.STRATEGY_TYPE, username);

		FeatureBucketStrategyData actual = strategy.update(new EventMessage(event)); // Creating the session
		FeatureBucketStrategyData expected = new FeatureBucketStrategyData(strategyContextId, DEFAULT_STRATEGY_NAME, epochtime, epochtime + MAX_SESSION_DURATION);
		assertEqualData(expected, actual);

		event = createDataSourceEvent(username, sourceIp, epochtime + (MAX_SESSION_DURATION / 2), status);
		actual = strategy.update(new EventMessage(event)); // 2nd call for the same session. should return null
		Assert.assertNull(actual);
	}

	@Test
	public void update_open_event_second_after_closed() throws Exception {
		String username = "user1";
		String sourceIp = "1.1.1.1";
		long epochtime = 1435737600;
		String openStatus = "SUCCESS";
		String closeStatus = "CLOSED";

		FeatureBucketStrategyStore store = new FeatureBucketStrategyInMemoryStore();
		FeatureBucketStrategy strategy = createStrategyWithFactory(store, createDefaultParams());
		JSONObject openEvent1 = createDataSourceEvent(username, sourceIp, epochtime, openStatus);
		JSONObject closeEvent = createDataSourceEvent(username, sourceIp, epochtime + 1, closeStatus);
		JSONObject openEvent2 = createDataSourceEvent(username, sourceIp, epochtime + 2, openStatus);

		FeatureBucketStrategyData openStrategyData = strategy.update(new EventMessage(openEvent1)); // Creating the session
		Assert.assertEquals(epochtime, openStrategyData.getStartTime());
		Assert.assertEquals(epochtime + 14400, openStrategyData.getEndTime());

		FeatureBucketStrategyData closedStrategyData = strategy.update(new EventMessage(closeEvent)); // Closing the session
		Assert.assertEquals(openStrategyData, closedStrategyData);
		Assert.assertEquals(epochtime, closedStrategyData.getStartTime());
		Assert.assertEquals(epochtime + 2, closedStrategyData.getEndTime());

		FeatureBucketStrategyData newOpenedStrategyData = strategy.update(new EventMessage(openEvent2)); // Openning another session
		Assert.assertEquals(epochtime + 2, newOpenedStrategyData.getStartTime());
		Assert.assertEquals(epochtime + 14400 + 2, newOpenedStrategyData.getEndTime());
	}

	@Test
	public void get_strategy_data() throws Exception {
		String username = "user1";
		String sourceIp1 = "1.1.1.1";
		String sourceIp2 = "2.2.2.2";
		long epochtime = 1435737600;
		JSONObject success_event1 = createDataSourceEvent(username, sourceIp1, epochtime, "SUCCESS");
		JSONObject success_event2 = createDataSourceEvent(username, sourceIp2, epochtime, "SUCCESS");
		JSONObject close_event1 = createDataSourceEvent(username, sourceIp1, epochtime + (MAX_SESSION_DURATION / 2), "CLOSED");
		JSONObject close_event2 = createDataSourceEvent(username, sourceIp2, epochtime + (MAX_SESSION_DURATION / 2), "CLOSED");

		String strategyContextId = String.format("%s_%s", VpnSessionFeatureBucketStrategyFactory.STRATEGY_TYPE, username);
		FeatureBucketStrategyStore store = new FeatureBucketStrategyInMemoryStore();
		FeatureBucketStrategy strategy = createStrategyWithFactory(store, createDefaultParams());
		Map<String, String> contextMap1 = new HashMap<>();
		contextMap1.put(VpnSessionFeatureBucketStrategy.SOURCE_IP_CONTEXT_FIELD_NAME, sourceIp1);
		contextMap1.put(VpnSessionFeatureBucketStrategy.SOURCE_IP_CONTEXT_FIELD_NAME, username);
		FeatureBucketStrategyData activeStrategyData1 = new FeatureBucketStrategyData(strategyContextId, DEFAULT_STRATEGY_NAME, epochtime, epochtime + MAX_SESSION_DURATION, contextMap1);
		Map<String, String> contextMap2 = new HashMap<>();
		contextMap2.put(VpnSessionFeatureBucketStrategy.SOURCE_IP_CONTEXT_FIELD_NAME, sourceIp2);
		contextMap2.put(VpnSessionFeatureBucketStrategy.SOURCE_IP_CONTEXT_FIELD_NAME, username);
		FeatureBucketStrategyData activeStrategyData2 = new FeatureBucketStrategyData(strategyContextId, DEFAULT_STRATEGY_NAME, epochtime, epochtime + MAX_SESSION_DURATION, contextMap2);

		List<FeatureBucketStrategyData> actual = strategy.getFeatureBucketStrategyData(null, new EventMessage(close_event1), 123);
		Assert.assertEquals(0, actual == null ? 0 : actual.size());

		strategy.update(new EventMessage(success_event1));
		actual = strategy.getFeatureBucketStrategyData(null, new EventMessage(success_event1), epochtime + 1);
		Assert.assertEquals(1, actual.size());
		assertEqualData(activeStrategyData1, actual.get(0));

		strategy.update(new EventMessage(success_event2));
		actual = strategy.getFeatureBucketStrategyData(null, new EventMessage(success_event2), epochtime + 1);
		Assert.assertEquals(2, actual.size());
		assertEqualData(activeStrategyData1, actual.get(0));
		assertEqualData(activeStrategyData2, actual.get(1));

		strategy.update(new EventMessage(close_event2));

		actual = strategy.getFeatureBucketStrategyData(null, new EventMessage(close_event1), epochtime + (MAX_SESSION_DURATION / 2) + 1);
		Assert.assertEquals(1, actual.size());
		assertEqualData(activeStrategyData1, actual.get(0));

		strategy.update(new EventMessage(close_event1));
		actual = strategy.getFeatureBucketStrategyData(null, new EventMessage(close_event1), epochtime + (MAX_SESSION_DURATION / 2) + 1);
		Assert.assertEquals(0, actual.size());
	}

	@Test
	public void get_contextId_form_strategyId() throws Exception {
		String username = "user1";
		String sourceIp1 = "1.1.1.1";
		long epochtime = 1435737600;
		JSONObject success_event1 = createDataSourceEvent(username, sourceIp1, epochtime, "SUCCESS");

		String strategyContextId1 = String.format("%s_%s", VpnSessionFeatureBucketStrategyFactory.STRATEGY_TYPE, username);
		FeatureBucketStrategyStore store = new FeatureBucketStrategyInMemoryStore();
		FeatureBucketStrategy strategy = createStrategyWithFactory(store, createDefaultParams());

		strategy.update(new EventMessage(success_event1));
		List<FeatureBucketStrategyData> actual = strategy.getFeatureBucketStrategyData(null, new EventMessage(success_event1), epochtime + 1);
		String strategyId = actual.get(0).getStrategyId();
		String actualContextId = strategy.getStrategyContextIdFromStrategyId(strategyId);
		Assert.assertEquals(strategyContextId1, actualContextId);
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

		FeatureBucketStrategyFactory factory = new VpnSessionFeatureBucketStrategyFactoryForTest(store);
		VpnSessionFeatureBucketStrategy strategy = (VpnSessionFeatureBucketStrategy) factory.createFeatureBucketStrategy(strategyJson);

		return strategy;
	}

	/*
	 * Create and return a new event of the default data source defined
	 */
	private JSONObject createDataSourceEvent(String username, String sourceIp, long epochtime, String status) {
		JSONObject event = new JSONObject();
		event.put("normalized_username", username);
		event.put("source_ip", sourceIp);
		event.put("date_time_unix", epochtime);
		event.put("status", status);
		event.put("data_source", "vpn_session");

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
