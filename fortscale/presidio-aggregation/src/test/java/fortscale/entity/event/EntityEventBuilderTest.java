package fortscale.entity.event;

import fortscale.aggregation.feature.event.AggrEvent;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;
import java.util.concurrent.TimeoutException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/entity-event-builder-context-test.xml"})
public class EntityEventBuilderTest extends EntityEventTestBase{
	private static final String DEFAULT_ENTITY_EVENT_NAME = "testEntityEvent";
	private static final String USERNAME_CONTEXT_FIELD = "normalized_username";
	private static final String SRC_MACHINE_CONTEXT_FIELD = "normalized_src_machine";
	private static final String DST_MACHINE_CONTEXT_FIELD = "normalized_dst_machine";
	private static final String DEFAULT_BUCKET_CONF_NAME = "testBucketConf";
	private static final String DEFAULT_AGGR_FEATURE_NAME = "testAggrFeature";

	@Autowired
	private EntityEventDataStore entityEventDataStore;
	
	
	
	@Before
    public void setUp() {
		((EntityEventDataTestStore)entityEventDataStore).emptyEntityEventDataStore();
	}
	
	
	private EntityEventConf createDefaultEntityEventConf(List<String> contextFields) {
		List<String> aggregatedFeatureEventNames = new ArrayList<>();
		aggregatedFeatureEventNames.add(String.format("%s.%s", DEFAULT_BUCKET_CONF_NAME, DEFAULT_AGGR_FEATURE_NAME));
		Map<String, List<String>> aggregatedFeatureEventNamesMap = new HashMap<>();
		aggregatedFeatureEventNamesMap.put("aggregatedFeatureEventNames", aggregatedFeatureEventNames);
		JSONObject entityEventFunction = new JSONObject();
		entityEventFunction.put("clusters", new JSONObject());
		entityEventFunction.put("alphas", new JSONObject());
		JSONObject betas = new JSONObject();
		betas.put(String.format("%s.%s", DEFAULT_BUCKET_CONF_NAME, DEFAULT_AGGR_FEATURE_NAME), 0.5);
		entityEventFunction.put("betas", betas);
		return new EntityEventConf(DEFAULT_ENTITY_EVENT_NAME, contextFields, aggregatedFeatureEventNamesMap, entityEventFunction);
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructor_should_fail_when_seconds_to_wait_before_firing_is_illegal() {
		List<String> contextFields = new ArrayList<>();
		contextFields.add(USERNAME_CONTEXT_FIELD);
		EntityEventConf entityEventConf = createDefaultEntityEventConf(contextFields);
		new EntityEventBuilder(-1, entityEventConf, entityEventDataStore);
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructor_should_fail_when_entity_event_conf_is_null() {
		new EntityEventBuilder(60, null, entityEventDataStore);
	}

	@Test
	public void builder_should_create_a_new_entity_event_data_and_store_it() {
		List<String> contextFields = new ArrayList<>();
		contextFields.add(USERNAME_CONTEXT_FIELD);
		EntityEventConf entityEventConf = createDefaultEntityEventConf(contextFields);
		EntityEventBuilder builder = new EntityEventBuilder(0, entityEventConf, entityEventDataStore);

		String username = "user1";
		JSONObject context = new JSONObject();
		context.put(USERNAME_CONTEXT_FIELD, username);
		AggrEvent wrapper = createAggrEvent(createMessage(
				"F", DEFAULT_BUCKET_CONF_NAME, DEFAULT_AGGR_FEATURE_NAME, 10, 20, 1000, 800, 900, context));

		builder.updateEntityEventData(wrapper);
		List<EntityEventData> allEntityEventData = entityEventDataStore.getEntityEventDataWithModifiedAtEpochtimeLte(DEFAULT_ENTITY_EVENT_NAME, Long.MAX_VALUE);
		Assert.assertEquals(1, allEntityEventData.size());
		EntityEventData entityEventData = allEntityEventData.get(0);

		Assert.assertEquals(DEFAULT_ENTITY_EVENT_NAME, entityEventData.getEntityEventName());
		Map<String, String> expectedContext = new HashMap<>();
		expectedContext.put(USERNAME_CONTEXT_FIELD, username);
		Assert.assertEquals(expectedContext, entityEventData.getContext());
		Assert.assertEquals(String.format("%s_%s", USERNAME_CONTEXT_FIELD, username), entityEventData.getContextId());
		Assert.assertEquals(800, entityEventData.getStartTime());
		Assert.assertEquals(900, entityEventData.getEndTime());
		Set<AggrEvent> allAggrFeatureEvents = entityEventData.getIncludedAggrFeatureEvents();
		Assert.assertEquals(1, allAggrFeatureEvents.size());
		for (AggrEvent actualAggrFeatureEvent : allAggrFeatureEvents)
			Assert.assertEquals(wrapper, actualAggrFeatureEvent);
		Assert.assertFalse(entityEventData.isTransmitted());
	}

	@Test
	public void builder_should_update_an_existing_entity_event_data_and_restore_it() {
		List<String> contextFields = new ArrayList<>();
		contextFields.add(USERNAME_CONTEXT_FIELD);
		EntityEventConf entityEventConf = createDefaultEntityEventConf(contextFields);
		EntityEventBuilder builder = new EntityEventBuilder(0, entityEventConf, entityEventDataStore);

		JSONObject context = new JSONObject();
		context.put(USERNAME_CONTEXT_FIELD, "user2");
		AggrEvent wrapper1 = createAggrEvent(createMessage(
				"F", DEFAULT_BUCKET_CONF_NAME, "aggrFeatureEvent1", 10, 20, 1000, 800, 900, context));
		builder.updateEntityEventData(wrapper1);

		AggrEvent wrapper2 = createAggrEvent(createMessage(
				"P", DEFAULT_BUCKET_CONF_NAME, "aggrFeatureEvent2", 30, 0, 2000, 800, 900, context));
		builder.updateEntityEventData(wrapper2);

		List<EntityEventData> allEntityEventData = entityEventDataStore.getEntityEventDataWithModifiedAtEpochtimeLte(DEFAULT_ENTITY_EVENT_NAME, Long.MAX_VALUE);
		Assert.assertEquals(1, allEntityEventData.size());
		EntityEventData entityEventData = allEntityEventData.get(0);

		Set<AggrEvent> allAggrFeatureEvents = entityEventData.getIncludedAggrFeatureEvents();
		Assert.assertEquals(2, allAggrFeatureEvents.size());
	}

	@Test
	public void builder_should_create_two_new_entity_event_data_and_store_them() {
		List<String> contextFields = new ArrayList<>();
		contextFields.add(USERNAME_CONTEXT_FIELD);
		EntityEventConf entityEventConf = createDefaultEntityEventConf(contextFields);
		EntityEventBuilder builder = new EntityEventBuilder(0, entityEventConf, entityEventDataStore);

		JSONObject context1 = new JSONObject();
		context1.put(USERNAME_CONTEXT_FIELD, "user3");
		AggrEvent wrapper1 = createAggrEvent(createMessage(
				"F", DEFAULT_BUCKET_CONF_NAME, DEFAULT_AGGR_FEATURE_NAME, 10, 20, 1000, 800, 900, context1));
		builder.updateEntityEventData(wrapper1);

		JSONObject context2 = new JSONObject();
		context2.put(USERNAME_CONTEXT_FIELD, "user4");
		AggrEvent wrapper2 = createAggrEvent(createMessage(
				"F", DEFAULT_BUCKET_CONF_NAME, DEFAULT_AGGR_FEATURE_NAME, 30, 40, 2000, 800, 900, context2));
		builder.updateEntityEventData(wrapper2);

		List<EntityEventData> allEntityEventData = entityEventDataStore.getEntityEventDataWithModifiedAtEpochtimeLte(DEFAULT_ENTITY_EVENT_NAME, Long.MAX_VALUE);
		Assert.assertEquals(2, allEntityEventData.size());

		Set<AggrEvent> allAggrFeatureEvents1 = allEntityEventData.get(0).getIncludedAggrFeatureEvents();
		Assert.assertEquals(1, allAggrFeatureEvents1.size());
		Set<AggrEvent> allAggrFeatureEvents2 = allEntityEventData.get(1).getIncludedAggrFeatureEvents();
		Assert.assertEquals(1, allAggrFeatureEvents2.size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void update_entity_event_data_should_fail_when_aggregated_feature_event_is_null() {
		List<String> contextFields = new ArrayList<>();
		contextFields.add(USERNAME_CONTEXT_FIELD);
		EntityEventConf entityEventConf = createDefaultEntityEventConf(contextFields);
		EntityEventBuilder builder = new EntityEventBuilder(0, entityEventConf, entityEventDataStore);
		builder.updateEntityEventData(null);
	}

	@Test
	public void builder_should_not_create_entity_event_data_when_context_is_missing_fields() {
		List<String> contextFields = new ArrayList<>();
		contextFields.add(USERNAME_CONTEXT_FIELD);
		contextFields.add(SRC_MACHINE_CONTEXT_FIELD);
		EntityEventConf entityEventConf = createDefaultEntityEventConf(contextFields);
		EntityEventBuilder builder = new EntityEventBuilder(0, entityEventConf, entityEventDataStore);

		JSONObject context = new JSONObject();
		context.put(USERNAME_CONTEXT_FIELD, "user5");
		context.put(DST_MACHINE_CONTEXT_FIELD, "machine1");
		AggrEvent wrapper = createAggrEvent(createMessage(
				"F", DEFAULT_BUCKET_CONF_NAME, DEFAULT_AGGR_FEATURE_NAME, 50, 100, 1000, 250, 750, context));
		builder.updateEntityEventData(wrapper);

		List<EntityEventData> allEntityEventData = entityEventDataStore.getEntityEventDataWithModifiedAtEpochtimeLte(DEFAULT_ENTITY_EVENT_NAME, Long.MAX_VALUE);
		Assert.assertEquals(0, allEntityEventData.size());
	}

	@Test
	public void builder_should_not_update_entity_event_data_when_it_was_already_fired() {
		List<String> contextFields = new ArrayList<>();
		contextFields.add(USERNAME_CONTEXT_FIELD);
		EntityEventConf entityEventConf = createDefaultEntityEventConf(contextFields);
		EntityEventBuilder builder = new EntityEventBuilder(0, entityEventConf, entityEventDataStore);

		JSONObject context = new JSONObject();
		context.put(USERNAME_CONTEXT_FIELD, "user6");
		AggrEvent wrapper1 = createAggrEvent(createMessage(
				"P", DEFAULT_BUCKET_CONF_NAME, "aggrFeatureEvent1", 50, 0, 1000, 250, 750, context));
		builder.updateEntityEventData(wrapper1);

		List<EntityEventData> allEntityEventData = entityEventDataStore.getEntityEventDataWithModifiedAtEpochtimeLte(DEFAULT_ENTITY_EVENT_NAME, Long.MAX_VALUE);
		Assert.assertEquals(1, allEntityEventData.size());
		EntityEventData entityEventData = allEntityEventData.get(0);
		entityEventData.setTransmitted(true);
		entityEventDataStore.storeEntityEventData(entityEventData);

		AggrEvent wrapper2 = createAggrEvent(createMessage(
				"P", DEFAULT_BUCKET_CONF_NAME, "aggrFeatureEvent2", 100, 0, 2000, 250, 750, context));
		builder.updateEntityEventData(wrapper2);

		allEntityEventData = entityEventDataStore.getEntityEventDataWithModifiedAtEpochtimeLte(DEFAULT_ENTITY_EVENT_NAME, Long.MAX_VALUE);
		Assert.assertEquals(1, allEntityEventData.size());
		entityEventData = allEntityEventData.get(0);
		Assert.assertEquals(1, entityEventData.getIncludedAggrFeatureEvents().size());
		Assert.assertEquals(1, entityEventData.getNotIncludedAggrFeatureEvents().size());
	}

	@Test
	public void builder_should_fire_entity_events_on_time() throws InterruptedException, TimeoutException {
		long secondsToWaitBeforeFiring = 60;
		List<String> contextFields = new ArrayList<>();
		contextFields.add(USERNAME_CONTEXT_FIELD);
		contextFields.add(SRC_MACHINE_CONTEXT_FIELD);
		EntityEventConf entityEventConf = createDefaultEntityEventConf(contextFields);
		EntityEventBuilder builder = new EntityEventBuilder(secondsToWaitBeforeFiring, entityEventConf, entityEventDataStore);

		String username = "user7";
		JSONObject context1 = new JSONObject();
		context1.put(USERNAME_CONTEXT_FIELD, username);
		context1.put(SRC_MACHINE_CONTEXT_FIELD, "machine2");
		AggrEvent wrapper1 = createAggrEvent(createMessage(
				"F", DEFAULT_BUCKET_CONF_NAME, DEFAULT_AGGR_FEATURE_NAME, 42, 81, 500, 300, 400, context1));
		builder.updateEntityEventData(wrapper1);
		long estimatedFiringTimeInSecondsOfEntityEvent1 = (System.currentTimeMillis() / 1000) + secondsToWaitBeforeFiring;

		Thread.sleep(1000);
		JSONObject context2 = new JSONObject();
		context2.put(USERNAME_CONTEXT_FIELD, username);
		context2.put(SRC_MACHINE_CONTEXT_FIELD, "machine3");
		AggrEvent wrapper2 = createAggrEvent(createMessage(
				"P", DEFAULT_BUCKET_CONF_NAME, DEFAULT_AGGR_FEATURE_NAME, 64, 0, 500, 300, 400, context2));
		builder.updateEntityEventData(wrapper2);
		long estimatedFiringTimeInSecondsOfEntityEvent2 = (System.currentTimeMillis() / 1000) + secondsToWaitBeforeFiring;

		IEntityEventSender sender = mock(IEntityEventSender.class);
		builder.sendNewEntityEventsAndUpdateStore(estimatedFiringTimeInSecondsOfEntityEvent1, sender);
//		verify(sender, times(1)).send(any(JSONObject.class));

		builder.sendNewEntityEventsAndUpdateStore(estimatedFiringTimeInSecondsOfEntityEvent2, sender);
//		verify(sender, times(2)).send(any(JSONObject.class));

		List<EntityEventData> allEntityEventData = entityEventDataStore.getEntityEventDataWithModifiedAtEpochtimeLte(DEFAULT_ENTITY_EVENT_NAME, Long.MAX_VALUE);
		Assert.assertEquals(2, allEntityEventData.size());
		for (EntityEventData entityEventData : allEntityEventData)
			Assert.assertTrue(entityEventData.isTransmitted());
	}
}
