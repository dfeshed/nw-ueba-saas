package fortscale.streaming.service.aggregation.entity.event;

import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/bucketconf-context-test.xml"})
public class EntityEventConfServiceTest {
	@Autowired
	EntityEventConfService entityEventConfService;

	@Test
	public void service_should_deserialize_entity_event_definitions_from_json_file() {
		// Check global params
		Map<String, Object> globalParams = entityEventConfService.getGlobalParams();
		Assert.assertNotNull(globalParams);
		Assert.assertEquals(2, globalParams.size());
		Assert.assertEquals(60, globalParams.get("secondsToWaitBeforeFiring"));
		Assert.assertEquals(1800, globalParams.get("fireEventsEverySeconds"));

		// Check entity event definitions
		List<EntityEventConf> entityEventDefinitions = entityEventConfService.getEntityEventDefinitions();
		Assert.assertNotNull(entityEventDefinitions);
		Assert.assertEquals(2, entityEventDefinitions.size());

		JSONObject expectedParams = new JSONObject();
		expectedParams.put("functionParam", "valueOfFunctionParam");

		for (int i = 1; i <= entityEventDefinitions.size(); i++) {
			EntityEventConf definition = entityEventDefinitions.get(i - 1);

			// Check name
			Assert.assertEquals(String.format("entityEventDefinition%d", i), definition.getName());

			// Check context fields
			List<String> contextFields = definition.getContextFields();
			Assert.assertNotNull(contextFields);
			Assert.assertEquals(1, contextFields.size());
			Assert.assertEquals("normalized_username", contextFields.get(0));

			// Check aggregated feature event names map
			Map<String, List<String>> aggregatedFeatureEventNamesMap = definition.getAggregatedFeatureEventNamesMap();
			Assert.assertNotNull(aggregatedFeatureEventNamesMap);
			Assert.assertEquals(1, aggregatedFeatureEventNamesMap.size());
			List<String> aggregatedFeatureEventNames = aggregatedFeatureEventNamesMap.get("functionArgument");
			Assert.assertNotNull(aggregatedFeatureEventNames);
			Assert.assertEquals(1, aggregatedFeatureEventNames.size());
			Assert.assertEquals(String.format("bucketConf%d.aggregatedFeatureEvent%d", i, i), aggregatedFeatureEventNames.get(0));

			// Check entity event function
			JSONObject entityEventFunction = definition.getEntityEventFunction();
			Assert.assertNotNull(entityEventFunction);
			Assert.assertEquals(2, entityEventFunction.size());
			Assert.assertEquals(String.format("type%d", i), entityEventFunction.get("type"));
			Assert.assertEquals(expectedParams, entityEventFunction.get("params"));
		}
	}
}
