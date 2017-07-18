package fortscale.entity.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;
import java.util.Properties;

@RunWith(SpringJUnit4ClassRunner.class)
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


	@Configuration
	public static class EntityEventConfServiceTestConfiguration{

		@Bean
		public EntityEventGlobalParamsConfService getEntityEventGlobalParamsConfService(){
			return new EntityEventGlobalParamsConfService();
		}

		@Bean
		public EntityEventConfService getEntityEventConfService(){
			return new EntityEventConfService();
		}

		@Bean
		public static TestPropertiesPlaceholderConfigurer abc() {
			Properties properties = new Properties();
			properties.put("fortscale.entity.event.definitions.json.file.path", "classpath:entity_events_test.json");
			properties.put("fortscale.entity.event.definitions.conf.json.overriding.files.path", "");
			properties.put("fortscale.entity.event.global.params.json.file.path","entity_events_global_params_test.json");
			properties.put("fortscale.entity.event.global.params.conf.json.overriding.files.path", "");

			return new TestPropertiesPlaceholderConfigurer(properties);
		}
	}
}
