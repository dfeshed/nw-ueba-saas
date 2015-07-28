package fortscale.streaming;

import java.util.HashMap;
import java.util.Map;

import org.apache.samza.config.MapConfig;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fortscale.streaming.service.aggregation.AggregatorManager;
import fortscale.utils.test.category.HadoopTestCategory;
import fortscale.utils.test.category.IntegrationTestCategory;




@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/streaming-aggr-context.xml"})
@Category(HadoopTestCategory.class)
public class StreamingAggrTaskContextTestInt {

	@Test
	@Category(IntegrationTestCategory.class)
	public void testContext(){
		Map<String, String> configMap = new HashMap<String, String>();
		configMap.put(AggregatorManager.SAMZA_TASK_FORTSCALE_TIMESTAMP_FIELD_CONFIG_PATH, "timestamp");
		@SuppressWarnings("unused")
		AggregatorManager aggregatorManager = new AggregatorManager(new MapConfig(configMap), new ExtendedSamzaTaskContext(null));
	}
}
