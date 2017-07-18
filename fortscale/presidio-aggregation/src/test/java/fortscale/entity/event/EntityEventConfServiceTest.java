package fortscale.entity.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/entity-event-conf-service-context.xml"})
public class EntityEventConfServiceTest {
	@Autowired
	EntityEventConfService entityEventConfService;

	@Test
	public void service_should_deserialize_entity_event_definitions_from_json_file() throws Exception {
		// Check global params
		Map<String, Object> globalParams = entityEventConfService.getGlobalParams();
		Assert.assertNotNull(globalParams);
		Assert.assertEquals(2, globalParams.size());
		Assert.assertEquals(1, globalParams.get("secondsToWaitBeforeFiring"));
		Assert.assertEquals(3, globalParams.get("fireEventsEverySeconds"));

		// Check entity event definitions
		List<EntityEventConf> entityEventDefinitions = entityEventConfService.getEntityEventDefinitions();
		Assert.assertNotNull(entityEventDefinitions);
		Assert.assertEquals(2, entityEventDefinitions.size());
		for (EntityEventConf entityEventConf : entityEventDefinitions) {
			Assert.assertNotNull(entityEventConf);
			JSONObject entityEventFunction = entityEventConf.getEntityEventFunction();
			Assert.assertNotNull(entityEventFunction);
			JokerFunction jokerFunction = (new ObjectMapper()).readValue(entityEventFunction.toJSONString(), JokerFunction.class);
			Assert.assertNotNull(jokerFunction);
		}
	}
}
