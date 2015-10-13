package fortscale.streaming.service.entity.event;

import fortscale.entity.event.EntityEventDataStore;
import fortscale.utils.ConversionUtils;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import org.apache.commons.lang.StringUtils;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.task.MessageCollector;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/entity-event-context-test.xml"})
public class EntityEventServiceTest extends EntityEventTestBase{
	private static final double DELTA = 0.00001;
	
	@Value("${streaming.entity_event.field.entity_event_type}")
    private String entityEventTypeFieldName;

	@Autowired
	private EntityEventDataStore entityEventDataStore;



	@Before
	public void setUp() {
		((EntityEventDataTestStore)entityEventDataStore).emptyEntityEventDataStore();
	}


	@Test
	public void entity_event_service_should_process_messages_correctly_and_fire_events_on_time() throws Exception {
		EntityEventService entityEventService = new EntityEventService(entityEventDataStore);

		// First group of messages is relevant only for:
		// conf1 (context is normalized_username = user1)
		JSONObject context1 = new JSONObject();
		context1.put("normalized_username", "user1");
		JSONObject message = createMessage("F", "bc1", "f1", 100, 50, 100, 0, 1, context1);
		entityEventService.process(message);
		message = createMessage("F", "bc2", "f2", 100, 49, 100, 0, 1, context1);
		entityEventService.process(message);
		message = createMessage("F", "bc3", "f3", 100, 60, 100, 0, 1, context1);
		entityEventService.process(message);
		message = createMessage("P", "bc1", "p1", 100, 0, 100, 0, 1, context1);
		entityEventService.process(message);
		// Entity event value = 50/100 * 0.9 + 60/100 * 0.8 + 100 * 0.7 = 70.93

		// Second group of messages is relevant for both:
		// conf1 (context is normalized_username = user2)
		// conf2 (context is normalized_username = user2, normalized_src_machine = machine1)
		JSONObject context2 = new JSONObject();
		context2.put("normalized_username", "user2");
		context2.put("normalized_src_machine", "machine1");
		message = createMessage("F", "bc2", "f1", 100, 70, 200, 2, 3, context2);
		entityEventService.process(message);
		message = createMessage("F", "bc2", "f2", 100, 80, 200, 2, 3, context2);
		entityEventService.process(message);
		message = createMessage("F", "bc3", "f3", 100, 79, 200, 2, 3, context2);
		entityEventService.process(message);
		message = createMessage("F", "bc4", "f4", 100, 69, 200, 2, 3, context2);
		entityEventService.process(message);
		message = createMessage("P", "bc3", "p3", 100, 0, 200, 2, 3, context2);
		entityEventService.process(message);
		message = createMessage("P", "bc4", "p4", 100, 0, 200, 2, 3, context2);
		entityEventService.process(message);
		// conf1 entity event value (only features bc2.f2 and bc3.f3) = 80/100 * 0.9 + 79/100 * 0.8 = 1.352
		// conf2 entity event value (all features) = 70/100 * 0.85 + 80/100 * 0.95 + 100 * 0.5 + 100 * 0.1 = 61.355

		// Message relevant for none of the entity event confs
		JSONObject context3 = new JSONObject();
		context3.put("normalized_username", "user3");
		message = createMessage("F", "notListedBc", "notListedAf", 100, 100, 300, 4, 5, context3);
		entityEventService.process(message);

		// Trigger the firing of entity events
		Thread.sleep(1000);
		ArgumentCaptor<OutgoingMessageEnvelope> argumentCaptor = ArgumentCaptor.forClass(OutgoingMessageEnvelope.class);
		MessageCollector collector = mock(MessageCollector.class);
		entityEventService.window(System.currentTimeMillis(), "testOutputTopic", collector);
		verify(collector, times(3)).send(argumentCaptor.capture());

		List<OutgoingMessageEnvelope> envelopes = argumentCaptor.getAllValues();
		Assert.assertNotNull(envelopes);
		Assert.assertEquals(3, envelopes.size());

		byte caseChecker = 0b000;
		for (OutgoingMessageEnvelope envelope : envelopes) {
			String entityEventString = (String)envelope.getMessage();
			JSONObject entityEventJson = (JSONObject)JSONValue.parseWithException(entityEventString);
			String entityEventType = ConversionUtils.convertToString(entityEventJson.get(entityEventTypeFieldName));
			Double entityEventValue = ConversionUtils.convertToDouble(entityEventJson.get("entity_event_value"));
			if (StringUtils.endsWith(entityEventType, "conf2")) {
				Assert.assertEquals(61.355, entityEventValue, DELTA);
				caseChecker |= 0b100;
			} else {
				@SuppressWarnings("unchecked")
				Map<String, String> context = (Map<String, String>)entityEventJson.get("context");
				if ("user1".equals(context.get("normalized_username"))) {
					Assert.assertEquals(70.93, entityEventValue, DELTA);
					caseChecker |= 0b010;
				} else {
					Assert.assertEquals(1.352, entityEventValue, DELTA);
					caseChecker |= 0b001;
				}
			}
		}

		// Make sure all 3 cases were checked
		Assert.assertEquals(0b111, caseChecker);
	}
}
